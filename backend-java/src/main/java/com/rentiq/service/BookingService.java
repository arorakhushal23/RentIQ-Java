package com.rentiq.service;

import com.rentiq.exception.InvalidBookingException;
import com.rentiq.exception.VehicleNotAvailableException;
import com.rentiq.model.Booking;
import com.rentiq.model.BookingRequest;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

/**
 * Service class for handling vehicle booking operations in the RentIQ system.
 * Integrates:
 * - ArrayList: dynamic booking storage.
 * - HashMap: O(1) Booking ID to Booking lookup with duplicate prevention.
 * - LinkedHashMap: insertion-ordered booking history.
 * - TreeMap: booking records sorted by Booking ID.
 * - Queue (FIFO): asynchronous pending booking request scheduling.
 * - Deque (ArrayDeque): front/rear priority booking management.
 * - PriorityQueue: priority-based request scheduling (VIP > Gold > Regular with sequence tie-breaking).
 */
public class BookingService {
    private List<Booking> bookings;
    private Map<String, Booking> bookingMap;
    private LinkedHashMap<String, Booking> bookingHistory;

    // Queue implementations
    private Queue<BookingRequest> pendingBookingQueue;       // FIFO Queue
    private Deque<BookingRequest> bookingRequestDeque;       // Dual-ended Deque
    private PriorityQueue<BookingRequest> priorityBookingQueue; // Priority Queue

    private StringBuffer bookingAuditLog;

    public BookingService() {
        this(10);
    }

    public BookingService(int initialCapacity) {
        this.bookings = new ArrayList<>(initialCapacity);
        this.bookingMap = new HashMap<>(initialCapacity);
        this.bookingHistory = new LinkedHashMap<>(initialCapacity);

        // Standard FIFO Queue
        this.pendingBookingQueue = new LinkedList<>();

        // Dual-ended Deque using ArrayDeque
        this.bookingRequestDeque = new ArrayDeque<>();

        // PriorityQueue: Ordered by customer membership tier priority score, then sequence number (FIFO tie-breaker)
        Comparator<BookingRequest> priorityComparator = (r1, r2) -> {
            int scoreDiff = Integer.compare(r2.getPriorityScore(), r1.getPriorityScore()); // Higher score first
            if (scoreDiff != 0) {
                return scoreDiff;
            }
            return Long.compare(r1.getSequenceNumber(), r2.getSequenceNumber()); // FIFO for equal priority
        };
        this.priorityBookingQueue = new PriorityQueue<>(priorityComparator);

        this.bookingAuditLog = new StringBuffer("=== Booking Service Audit Log ===\n");
    }

    // 1. Check vehicle availability
    public boolean checkVehicleAvailability(Vehicle vehicle) {
        if (vehicle == null) {
            return false;
        }
        return vehicle.isAvailable();
    }

    /**
     * Calculates rental amount demonstrating wrapper classes, autoboxing, and unboxing.
     */
    public double calculateRentalAmount(Vehicle vehicle, int rentalDays) throws InvalidBookingException {
        if (vehicle == null) {
            throw new InvalidBookingException("Calculation Error: Vehicle cannot be null.");
        }

        Integer daysObj = Integer.valueOf(rentalDays);
        if (daysObj.intValue() <= 0) {
            throw new InvalidBookingException("Calculation Error: Invalid rental duration (" + daysObj + " days). Must be at least 1 day.");
        }

        Double rateObj = Double.valueOf(vehicle.getPricePerDay());
        double total = rateObj.doubleValue() * daysObj.intValue();
        return total;
    }

    /**
     * Creates and confirms a new vehicle booking directly.
     * Prevents duplicate booking IDs and synchronizes List, HashMap, and LinkedHashMap.
     */
    public Booking createBooking(Customer customer, Vehicle vehicle, int rentalDays)
            throws InvalidBookingException, VehicleNotAvailableException {

        if (customer == null) {
            throw new InvalidBookingException("Booking Creation Failed: Customer reference cannot be null.");
        }
        if (vehicle == null) {
            throw new InvalidBookingException("Booking Creation Failed: Vehicle reference cannot be null.");
        }

        Integer durationWrapper = Integer.valueOf(rentalDays);
        int unboxedDuration = durationWrapper;
        if (unboxedDuration <= 0) {
            throw new InvalidBookingException("Booking Creation Failed: Rental duration (" + unboxedDuration + " days) must be at least 1 day.");
        }

        if (!checkVehicleAvailability(vehicle)) {
            throw new VehicleNotAvailableException(
                "Booking Creation Failed: Vehicle " + vehicle.getBrand() + " " + vehicle.getModel() +
                " [" + vehicle.getVehicleId() + "] is currently UNAVAILABLE.",
                vehicle.getVehicleId()
            );
        }

        Booking booking = null;
        try {
            booking = new Booking(customer, vehicle, unboxedDuration);

            String key = booking.getBookingId().toUpperCase();
            if (bookingMap.containsKey(key)) {
                throw new InvalidBookingException("Booking Creation Failed: Duplicate booking ID '" + booking.getBookingId() + "'.");
            }

            boolean confirmed = confirmBooking(booking);
            if (!confirmed) {
                throw new InvalidBookingException("Booking Confirmation Failed: Unable to lock vehicle availability.");
            }

            // Thread-safe update of service collections
            synchronized (this) {
                bookings.add(booking);
                bookingMap.put(key, booking);
                bookingHistory.put(booking.getBookingId(), booking);
            }

            bookingAuditLog.append(System.currentTimeMillis())
                           .append(" - Confirmed Booking [")
                           .append(booking.getBookingId())
                           .append("] for Vehicle [")
                           .append(vehicle.getVehicleId())
                           .append("] by User [")
                           .append(customer.getUserId())
                           .append("] Total: $")
                           .append(String.format("%.2f", booking.getTotalAmount()))
                           .append("\n");

            System.out.println("[BookingService] Booking successfully created! Booking ID: " + booking.getBookingId());
        } finally {
            System.out.println("[Audit] createBooking transaction completed for customer: " + customer.getName());
        }

        return booking;
    }

    /**
     * Thread-safe synchronized booking method protecting the critical section:
     * 1. Checking vehicle availability.
     * 2. Updating vehicle availability.
     * 3. Creating and registering the confirmed booking in List, HashMap, and LinkedHashMap.
     *
     * Locks on the vehicle shared-resource monitor, ensuring that two concurrent threads
     * targeting the same vehicle cannot cause a race condition or double booking.
     */
    public Booking bookVehicleSynchronized(Customer customer, Vehicle vehicle, int rentalDays)
            throws InvalidBookingException, VehicleNotAvailableException {
        if (vehicle == null) {
            throw new InvalidBookingException("Booking Creation Failed: Vehicle reference cannot be null.");
        }

        // Synchronize on the target vehicle instance (resource-level lock)
        synchronized (vehicle) {
            if (!checkVehicleAvailability(vehicle)) {
                throw new VehicleNotAvailableException(
                    "Booking Conflict: Vehicle " + vehicle.getBrand() + " " + vehicle.getModel() +
                    " [" + vehicle.getVehicleId() + "] is UNAVAILABLE (already booked by another customer).",
                    vehicle.getVehicleId()
                );
            }
            // Proceed to book within the protected critical section
            return createBooking(customer, vehicle, rentalDays);
        }
    }

    // Confirm booking
    public boolean confirmBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        Vehicle vehicle = booking.getVehicle();
        if (vehicle != null && vehicle.isAvailable()) {
            vehicle.bookVehicle();
            booking.setBookingStatus("CONFIRMED");
            return true;
        }
        return false;
    }

    /**
     * Cancels an existing booking and restores vehicle availability.
     */
    public boolean cancelBooking(String bookingId) throws InvalidBookingException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new InvalidBookingException("Cancellation Failed: Booking ID cannot be empty.");
        }

        Booking booking = searchBookingById(bookingId.trim());
        if (booking == null) {
            throw new InvalidBookingException("Cancellation Failed: Booking with ID '" + bookingId + "' not found.");
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new InvalidBookingException("Cancellation Failed: Booking '" + bookingId + "' is already cancelled.");
        }

        try {
            booking.setBookingStatus("CANCELLED");
            Vehicle vehicle = booking.getVehicle();
            if (vehicle != null) {
                vehicle.cancelBooking();
                System.out.println("[BookingService] Booking " + bookingId + " cancelled. Availability restored for " +
                                   vehicle.getBrand() + " " + vehicle.getModel() + " [" + vehicle.getVehicleId() + "].");
            }
            bookingAuditLog.append(System.currentTimeMillis())
                           .append(" - Cancelled Booking [")
                           .append(bookingId)
                           .append("]\n");
            return true;
        } finally {
            System.out.println("[Audit] cancelBooking process finished for Booking ID: " + bookingId);
        }
    }

    // Display booking details
    public void displayBookingDetails(String bookingId) {
        Booking booking = searchBookingById(bookingId);
        if (booking != null) {
            booking.displayBookingDetails();
        } else {
            System.out.println("[BookingService] Booking ID '" + bookingId + "' not found.");
        }
    }

    // Display all bookings
    public void displayAllBookings() {
        System.out.println("\n============== System Bookings Registry (" + bookings.size() + ") ==============");
        if (bookings.isEmpty()) {
            System.out.println("No bookings recorded in the system.");
            System.out.println("============================================================\n");
            return;
        }
        for (Booking booking : bookings) {
            booking.displayBookingDetails();
        }
        System.out.println("============================================================\n");
    }

    // Search booking by ID in O(1) time using HashMap
    public Booking searchBookingById(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return null;
        }
        return bookingMap.get(bookingId.trim().toUpperCase());
    }

    // ==========================================================
    // Map Implementations: LinkedHashMap and TreeMap
    // ==========================================================

    public LinkedHashMap<String, Booking> getBookingHistory() {
        return new LinkedHashMap<>(this.bookingHistory);
    }

    public void displayBookingHistory() {
        System.out.println("\n===== Booking History (LinkedHashMap - Insertion Order) =====");
        if (bookingHistory.isEmpty()) {
            System.out.println("No bookings in history.");
            System.out.println("=============================================================\n");
            return;
        }
        int rank = 1;
        for (Map.Entry<String, Booking> entry : bookingHistory.entrySet()) {
            Booking b = entry.getValue();
            System.out.printf("%d. [%s] Customer: %s | Vehicle: %s | Status: %s | Amount: $%.2f\n",
                rank++, entry.getKey(),
                b.getCustomer() != null ? b.getCustomer().getName() : "N/A",
                b.getVehicle() != null ? b.getVehicle().getVehicleId() : "N/A",
                b.getBookingStatus(), b.getTotalAmount());
        }
        System.out.println("=============================================================\n");
    }

    public TreeMap<String, Booking> getBookingsSortedById() {
        TreeMap<String, Booking> sorted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sorted.putAll(this.bookingMap);
        return sorted;
    }

    public void displayBookingsSortedById() {
        System.out.println("\n===== Bookings Registry (TreeMap - Sorted Alphabetically by ID) =====");
        TreeMap<String, Booking> sorted = getBookingsSortedById();
        if (sorted.isEmpty()) {
            System.out.println("No bookings recorded.");
            System.out.println("======================================================================\n");
            return;
        }
        for (Map.Entry<String, Booking> entry : sorted.entrySet()) {
            Booking b = entry.getValue();
            System.out.printf("Key: %-8s -> [%s] %s booked %s ($%.2f, %s)\n",
                entry.getKey(), b.getBookingId(),
                b.getCustomer() != null ? b.getCustomer().getName() : "N/A",
                b.getVehicle() != null ? b.getVehicle().getVehicleId() : "N/A",
                b.getTotalAmount(), b.getBookingStatus());
        }
        System.out.println("======================================================================\n");
    }

    // ==========================================================
    // Queue Implementation: FIFO Pending Booking Requests
    // ==========================================================

    /**
     * Submits a pending booking request into the FIFO queue.
     */
    public BookingRequest submitBookingRequest(Customer customer, Vehicle vehicle, int rentalDays) {
        String reqId = BookingRequest.generateNextRequestId();
        BookingRequest request = new BookingRequest(reqId, customer, vehicle, rentalDays);
        pendingBookingQueue.offer(request);
        System.out.println("[Queue] Submitted FIFO Booking Request: " + request);
        return request;
    }

    /**
     * Displays all pending requests in the FIFO queue without modifying the queue.
     */
    public void displayPendingRequests() {
        System.out.println("\n========== Pending Booking Requests (FIFO Queue) ==========");
        if (pendingBookingQueue.isEmpty()) {
            System.out.println("No pending booking requests in queue.");
            System.out.println("===========================================================\n");
            return;
        }
        int order = 1;
        for (BookingRequest req : pendingBookingQueue) {
            System.out.print("Position #" + (order++) + " -> ");
            req.displayRequestDetails();
        }
        System.out.println("===========================================================\n");
    }

    /**
     * Processes the next booking request in FIFO order.
     * Safely handles an empty queue.
     */
    public Booking processNextBookingRequest() throws InvalidBookingException, VehicleNotAvailableException {
        System.out.println("\n[Queue] Attempting to process next FIFO booking request...");
        if (pendingBookingQueue.isEmpty()) {
            System.out.println("[Queue] Safe Handling: Pending booking request queue is empty. No request to process.\n");
            return null;
        }

        BookingRequest request = pendingBookingQueue.poll();
        System.out.println("[Queue] Polled request from queue: " + request.getRequestId() + " (" + request.getCustomer().getName() + ")");
        Booking booking = createBooking(request.getCustomer(), request.getVehicle(), request.getRentalDays());
        System.out.println("[Queue] FIFO request successfully converted to confirmed booking: " + booking.getBookingId() + "\n");
        return booking;
    }

    public int getPendingQueueSize() {
        return pendingBookingQueue.size();
    }

    // ==========================================================
    // Deque Implementation: ArrayDeque Front & Rear Operations
    // ==========================================================

    public void addRequestToFront(BookingRequest request) {
        if (request != null) {
            bookingRequestDeque.addFirst(request);
            System.out.println("[Deque] Added request to FRONT (Express Priority): " + request.getRequestId());
        }
    }

    public void addRequestToRear(BookingRequest request) {
        if (request != null) {
            bookingRequestDeque.addLast(request);
            System.out.println("[Deque] Added request to REAR (Standard): " + request.getRequestId());
        }
    }

    public BookingRequest removeRequestFromFront() {
        if (bookingRequestDeque.isEmpty()) {
            System.out.println("[Deque] Deque is empty. Cannot poll from front.");
            return null;
        }
        BookingRequest req = bookingRequestDeque.pollFirst();
        System.out.println("[Deque] Polled from FRONT: " + req.getRequestId());
        return req;
    }

    public BookingRequest removeRequestFromRear() {
        if (bookingRequestDeque.isEmpty()) {
            System.out.println("[Deque] Deque is empty. Cannot poll from rear.");
            return null;
        }
        BookingRequest req = bookingRequestDeque.pollLast();
        System.out.println("[Deque] Polled from REAR: " + req.getRequestId());
        return req;
    }

    public void displayDequeRequests() {
        System.out.println("\n========== Deque Requests (ArrayDeque: Front to Rear) ==========");
        if (bookingRequestDeque.isEmpty()) {
            System.out.println("Booking request deque is empty.");
            System.out.println("=================================================================\n");
            return;
        }
        int index = 1;
        for (BookingRequest req : bookingRequestDeque) {
            System.out.print("Slot #" + (index++) + " -> ");
            req.displayRequestDetails();
        }
        System.out.println("=================================================================\n");
    }

    // ==========================================================
    // PriorityQueue Implementation: Membership Tier & FIFO Tie-Breaking
    // ==========================================================

    public void submitPriorityBookingRequest(BookingRequest request) {
        if (request != null) {
            priorityBookingQueue.offer(request);
            System.out.println("[PriorityQueue] Enqueued request: " + request);
        }
    }

    /**
     * Processes the highest priority booking request.
     * VIP customers are scheduled ahead of regular customers.
     * Equal-priority requests are resolved by sequence number (FIFO tie-breaker).
     */
    public Booking processNextPriorityRequest() throws InvalidBookingException, VehicleNotAvailableException {
        System.out.println("\n[PriorityQueue] Processing next highest-priority request...");
        if (priorityBookingQueue.isEmpty()) {
            System.out.println("[PriorityQueue] Priority queue is empty. No request to process.\n");
            return null;
        }

        BookingRequest request = priorityBookingQueue.poll();
        System.out.println("[PriorityQueue] Polled highest-priority request: " + request.getRequestId() +
                           " [Priority: " + request.getPriorityLabel() + ", Customer: " + request.getCustomer().getName() +
                           ", Seq: #" + request.getSequenceNumber() + "]");
        Booking booking = createBooking(request.getCustomer(), request.getVehicle(), request.getRentalDays());
        return booking;
    }

    public void displayPriorityQueue() {
        System.out.println("\n========== PriorityQueue Booking Requests ==========");
        if (priorityBookingQueue.isEmpty()) {
            System.out.println("Priority queue is empty.");
            System.out.println("====================================================\n");
            return;
        }
        System.out.println("[Note] PriorityQueue internally maintains a min/max heap; polling resolves exact priority:");
        // Copy to drain without destroying original queue
        PriorityQueue<BookingRequest> copy = new PriorityQueue<>(priorityBookingQueue);
        int rank = 1;
        while (!copy.isEmpty()) {
            BookingRequest req = copy.poll();
            System.out.printf("Priority #%d -> [%s] Tier: %-18s | Customer: %-15s | Seq: #%d\n",
                rank++, req.getRequestId(), req.getPriorityLabel(), req.getCustomer().getName(), req.getSequenceNumber());
        }
        System.out.println("====================================================\n");
    }

    public int getBookingCount() {
        return this.bookings.size();
    }

    public List<Booking> getAllBookings() {
        return Collections.unmodifiableList(this.bookings);
    }

    public Map<String, Booking> getBookingMap() {
        return Collections.unmodifiableMap(this.bookingMap);
    }

    public String getAuditLog() {
        return this.bookingAuditLog.toString();
    }
}
