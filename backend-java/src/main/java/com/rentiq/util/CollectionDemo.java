package com.rentiq.util;

import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Educational utility demonstrating core Java Collections Framework interfaces and classes
 * covering Lectures 16–28:
 * - Lists: ArrayList, LinkedList
 * - Sets: HashSet, LinkedHashSet, TreeSet
 * - Maps: HashMap, LinkedHashMap, TreeMap
 * - Queues: Queue (FIFO), Deque (ArrayDeque), PriorityQueue (Priority-based)
 * - Iterators: Iterator (safe remove), ListIterator (bidirectional)
 * - Sorting: Comparable (natural), Comparator (multi-criteria)
 */
public class CollectionDemo {

    public static void runAllDemos() {
        System.out.println("\n============================================================");
        System.out.println("   RentIQ Phase 2: Complete Collections Showcase (16–28)    ");
        System.out.println("============================================================");

        // Milestone 1 Demos
        demoArrayList();
        demoLinkedList();
        demoHashSet();
        demoLinkedHashSet();
        demoTreeSet();

        // Milestone 2 Demos
        demoHashMap();
        demoLinkedHashMap();
        demoTreeMap();
        demoQueue();
        demoDeque();
        demoPriorityQueue();
        demoIteratorAndListIterator();
        demoComparableAndComparator();

        System.out.println("============================================================\n");
    }

    // ==========================================
    // Milestone 1 Demonstrations
    // ==========================================

    public static void demoArrayList() {
        System.out.println("\n--- 1. ArrayList Demonstration (Dynamic Resizing & Indexing) ---");
        List<String> list = new ArrayList<>();
        list.add("Sedan");
        list.add("SUV");
        list.add("Truck");
        list.add("Convertible");

        System.out.println("Initial ArrayList: " + list);
        System.out.println("Element at index 1: " + list.get(1));
        list.add(1, "Hatchback");
        System.out.println("After inserting 'Hatchback' at index 1: " + list);
        list.remove("Truck");
        System.out.println("After removing 'Truck': " + list);
        System.out.println("Current list size (dynamic): " + list.size());
    }

    public static void demoLinkedList() {
        System.out.println("\n--- 2. LinkedList Demonstration (Recently Viewed Vehicles Deque) ---");
        LinkedList<String> recentVehicles = new LinkedList<>();
        recentVehicles.addFirst("V101 (Tesla Model 3)");
        recentVehicles.addFirst("V102 (Toyota RAV4)");
        recentVehicles.addFirst("B201 (Yamaha YZF-R3)");

        System.out.println("Recently viewed (newest first): " + recentVehicles);
        System.out.println("Most recently viewed (head): " + recentVehicles.peekFirst());
        System.out.println("Oldest viewed (tail): " + recentVehicles.peekLast());

        String evicted = recentVehicles.removeLast();
        System.out.println("Evicted oldest viewed vehicle: " + evicted);
        System.out.println("Remaining recently viewed: " + recentVehicles);
    }

    public static void demoHashSet() {
        System.out.println("\n--- 3. HashSet Demonstration (Unique Categories - Unordered) ---");
        Set<String> categories = new HashSet<>();
        categories.add("Sedan");
        categories.add("SUV");
        categories.add("Electric");
        categories.add("Sedan"); // Duplicate rejected
        categories.add("SUV");   // Duplicate rejected

        System.out.println("HashSet elements (duplicates rejected): " + categories);
        System.out.println("Contains 'SUV': " + categories.contains("SUV"));
        System.out.println("Total unique categories: " + categories.size());
    }

    public static void demoLinkedHashSet() {
        System.out.println("\n--- 4. LinkedHashSet Demonstration (Unique Categories - Insertion Order) ---");
        Set<String> categories = new LinkedHashSet<>();
        categories.add("Luxury");
        categories.add("Economy");
        categories.add("Electric");
        categories.add("Motorcycle");
        categories.add("Economy"); // Duplicate rejected

        System.out.println("LinkedHashSet maintains insertion sequence: " + categories);
    }

    public static void demoTreeSet() {
        System.out.println("\n--- 5. TreeSet Demonstration (Unique Categories - Alphabetically Sorted) ---");
        Set<String> categories = new TreeSet<>();
        categories.add("Sedan");
        categories.add("Convertible");
        categories.add("SUV");
        categories.add("Bike");
        categories.add("Truck");
        categories.add("Sedan"); // Duplicate rejected

        System.out.println("TreeSet automatically sorted (alphabetical): " + categories);
        System.out.println("First category: " + ((TreeSet<String>) categories).first());
        System.out.println("Last category: " + ((TreeSet<String>) categories).last());
    }

    // ==========================================
    // Milestone 2 Demonstrations
    // ==========================================

    public static void demoHashMap() {
        System.out.println("\n--- 6. HashMap Demonstration (O(1) Key-Value Lookup) ---");
        Map<String, String> fleetIndex = new HashMap<>();
        fleetIndex.put("V101", "Tesla Model 3");
        fleetIndex.put("V102", "Toyota RAV4");
        fleetIndex.put("B201", "Yamaha YZF-R3");

        System.out.println("HashMap Entries: " + fleetIndex);
        System.out.println("O(1) Lookup for 'V101': " + fleetIndex.get("V101"));
        System.out.println("Duplicate ID check ('V102' exists): " + fleetIndex.containsKey("V102"));
    }

    public static void demoLinkedHashMap() {
        System.out.println("\n--- 7. LinkedHashMap Demonstration (Insertion Order Preservation) ---");
        Map<String, String> recentActivity = new LinkedHashMap<>();
        recentActivity.put("10:01 AM", "User Alice registered");
        recentActivity.put("10:05 AM", "Vehicle V101 added");
        recentActivity.put("10:12 AM", "Booking BK-1001 created");
        recentActivity.put("10:20 AM", "Booking BK-1001 cancelled");

        System.out.println("LinkedHashMap guarantees exact chronological order:");
        for (Map.Entry<String, String> entry : recentActivity.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static void demoTreeMap() {
        System.out.println("\n--- 8. TreeMap Demonstration (Sorted Key-Value Pairs) ---");
        Map<String, String> sortedFleet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedFleet.put("V105", "Ford Mustang");
        sortedFleet.put("B201", "Yamaha R3");
        sortedFleet.put("V101", "Tesla Model 3");
        sortedFleet.put("A001", "Admin Car");

        System.out.println("TreeMap automatically sorted by Key (Vehicle ID): " + sortedFleet);
    }

    public static void demoQueue() {
        System.out.println("\n--- 9. Queue Demonstration (FIFO Pending Booking Requests) ---");
        Queue<String> fifoQueue = new LinkedList<>();
        fifoQueue.offer("Request #1: Alice");
        fifoQueue.offer("Request #2: Bob");
        fifoQueue.offer("Request #3: Charlie");

        System.out.println("Pending Queue state: " + fifoQueue);
        System.out.println("Processing next in FIFO order: " + fifoQueue.poll());
        System.out.println("Next up to process (peek): " + fifoQueue.peek());
        System.out.println("Remaining queue: " + fifoQueue);
    }

    public static void demoDeque() {
        System.out.println("\n--- 10. Deque Demonstration (ArrayDeque Dual-Ended Operations) ---");
        Deque<String> deque = new ArrayDeque<>();
        deque.addLast("Standard Request 1");
        deque.addLast("Standard Request 2");
        System.out.println("Standard queue: " + deque);

        deque.addFirst("VIP Priority Request (Front Insert)");
        System.out.println("After front insertion: " + deque);

        System.out.println("Removed from Front: " + deque.pollFirst());
        System.out.println("Removed from Rear: " + deque.pollLast());
        System.out.println("Remaining in Deque: " + deque);
    }

    public static void demoPriorityQueue() {
        System.out.println("\n--- 11. PriorityQueue Demonstration (VIP > Regular with Sequence FIFO) ---");
        // Pair: Priority (3=VIP, 1=Regular) and Name
        class DummyRequest {
            int priority;
            long seq;
            String name;
            DummyRequest(int p, long s, String n) { this.priority = p; this.seq = s; this.name = n; }
        }

        PriorityQueue<DummyRequest> pq = new PriorityQueue<>((a, b) -> {
            if (a.priority != b.priority) return Integer.compare(b.priority, a.priority); // Higher first
            return Long.compare(a.seq, b.seq); // FIFO for equal priority
        });

        // Submit Regular first, then VIP
        pq.offer(new DummyRequest(1, 1, "Regular Customer (Seq #1)"));
        pq.offer(new DummyRequest(1, 2, "Regular Customer (Seq #2)"));
        pq.offer(new DummyRequest(3, 3, "VIP Customer (Seq #3)"));
        pq.offer(new DummyRequest(3, 4, "VIP Customer (Seq #4)"));

        System.out.println("Drain order from PriorityQueue (VIP customers processed first, then FIFO):");
        while (!pq.isEmpty()) {
            DummyRequest r = pq.poll();
            System.out.printf("  Processed: %-28s [Priority: %d, Seq: #%d]\n", r.name, r.priority, r.seq);
        }
    }

    public static void demoIteratorAndListIterator() {
        System.out.println("\n--- 12. Iterator and ListIterator Demonstration ---");
        List<String> items = new ArrayList<>();
        items.add("Active Vehicle 1");
        items.add("Discontinued Vehicle 2");
        items.add("Active Vehicle 3");

        System.out.println("Original List: " + items);

        // Safe removal with Iterator
        Iterator<String> it = items.iterator();
        while (it.hasNext()) {
            String item = it.next();
            if (item.contains("Discontinued")) {
                it.remove(); // Safe removal without ConcurrentModificationException
                System.out.println("Iterator safely removed: " + item);
            }
        }
        System.out.println("List after Iterator.remove(): " + items);

        // Bidirectional traversal with ListIterator
        ListIterator<String> lit = items.listIterator();
        System.out.println("ListIterator Forward:");
        while (lit.hasNext()) {
            System.out.println("  Index " + lit.nextIndex() + " -> " + lit.next());
        }
        System.out.println("ListIterator Backward:");
        while (lit.hasPrevious()) {
            System.out.println("  Index " + lit.previousIndex() + " <- " + lit.previous());
        }
    }

    public static void demoComparableAndComparator() {
        System.out.println("\n--- 13. Comparable and Comparator Sorting Demonstration ---");
        class DemoCar implements Comparable<DemoCar> {
            String id;
            String model;
            double price;
            double rating;
            DemoCar(String id, String model, double price, double rating) {
                this.id = id; this.model = model; this.price = price; this.rating = rating;
            }
            @Override
            public int compareTo(DemoCar o) {
                return this.id.compareToIgnoreCase(o.id);
            }
            @Override
            public String toString() {
                return String.format("[%s: %s, $%.2f, Rating: %.1f]", id, model, price, rating);
            }
        }

        List<DemoCar> cars = new ArrayList<>();
        cars.add(new DemoCar("V104", "BMW 3 Series", 95.0, 4.8));
        cars.add(new DemoCar("V101", "Tesla Model 3", 85.0, 4.9));
        cars.add(new DemoCar("V102", "Toyota RAV4", 65.0, 4.6));

        System.out.println("Unsorted List: " + cars);

        // Comparable (Natural sorting by ID)
        Collections.sort(cars);
        System.out.println("Natural Sort (Comparable by ID): " + cars);

        // Comparator by Price Ascending
        cars.sort(Comparator.comparingDouble(c -> c.price));
        System.out.println("Comparator (Price: Low to High): " + cars);

        // Comparator by Rating Descending
        cars.sort(Comparator.comparingDouble((DemoCar c) -> c.rating).reversed());
        System.out.println("Comparator (Rating: High to Low): " + cars);
    }
}
