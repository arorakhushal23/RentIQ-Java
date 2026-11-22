# RentIQ-Java
## Java-Based Enterprise Vehicle Rental Management System

Welcome to **RentIQ-Java**, an enterprise-grade vehicle rental management system built with **Core Java 26 (JDK 26.0.1)** and **Apache Maven**.

RentIQ-Java follows a clean layered architecture, demonstrating core object-oriented principles, the Java Collections Framework, Map/Queue data structures, sorting algorithms, multithreading fundamentals, and advanced **Thread Coordination (wait, notify, notifyAll, guarded blocks, and the Producer-Consumer pattern)**.

---

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Architecture & Directory Structure](#project-architecture--directory-structure)
3. [Phase 2, Milestone 4: Thread Coordination Deep Dive](#phase-2-milestone-4-thread-coordination-deep-dive)
   - [3.1 The Need for Thread Coordination](#31-the-need-for-thread-coordination)
   - [3.2 wait(), notify(), and notifyAll() Mechanics](#32-wait-notify-and-notifyall-mechanics)
   - [3.3 Guarded Blocks & Condition Testing in while Loops](#33-guarded-blocks--condition-testing-in-while-loops)
   - [3.4 Producer-Consumer Bounded Buffer Architecture](#34-producer-consumer-bounded-buffer-architecture)
   - [3.5 Integrated RentIQ Booking Request Workflow](#35-integrated-rentiq-booking-request-workflow)
   - [3.6 Graceful Thread Shutdown & Zero-Leak Termination](#36-graceful-thread-shutdown--zero-leak-termination)
4. [Educational Demonstrations Overview](#educational-demonstrations-overview)
5. [Console Application Menu Structure](#console-application-menu-structure)
6. [How to Compile, Test, and Run](#how-to-compile-test-and-run)
7. [Viva & Technical Interview Q&A](#viva--technical-interview-qa)

---

## Prerequisites

Before building or running the project, verify your environment:

1. **Java Development Kit (JDK):**
   - **Version:** Java 26 (JDK 26.0.1)
   - Verify:
     ```bash
     java -version
     javac -version
     ```
   - Ensure `JAVA_HOME` points to your JDK 26 installation (e.g. `C:\Program Files\Java\jdk-26.0.1`).

2. **Apache Maven / Maven Wrapper:**
   - **Version:** Maven 3.9+ (A pre-configured Maven Wrapper `mvnw` / `mvnw.cmd` is included in `backend-java/`).
   - Verify:
     ```bash
     mvn -version
     ```

---

## Project Architecture & Directory Structure

```
RentIQ-Java/
├── .gitignore
├── README.md
├── backend-java/
│   ├── .mvn/wrapper/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/rentiq/
│       │   │   ├── Main.java                               # Application entry point & automated runner
│       │   │   ├── concurrency/                            # Multithreading & Thread Coordination
│       │   │   │   ├── AtomicityVisibilityDemo.java        # Unsafe vs Synchronized vs AtomicInteger & Volatile
│       │   │   │   ├── BookingConsumer.java                # Consumer worker processing booking requests
│       │   │   │   ├── BookingNotificationTask.java        # Runnable async notification worker
│       │   │   │   ├── BookingProducer.java                # Producer worker submitting booking requests
│       │   │   │   ├── BookingRequestBuffer.java           # Thread-safe bounded buffer with wait/notifyAll
│       │   │   │   ├── FleetMonitorThread.java             # Background audit thread extending Thread
│       │   │   │   ├── GuardedBlockDemo.java               # Guarded block condition evaluation in while loops
│       │   │   │   ├── NotifyAllDemo.java                  # Multiple waiting consumers & notifyAll demo
│       │   │   │   ├── ProducerConsumerDemo.java           # Complete Producer-Consumer integrated simulation
│       │   │   │   ├── RaceConditionDemo.java              # Double-booking race condition vs synchronized fix
│       │   │   │   ├── StartVsRunDemo.java                 # start() new thread vs run() calling thread
│       │   │   │   ├── ThreadLifecycleDemo.java            # NEW, RUNNABLE, TIMED_WAITING, WAITING, TERMINATED
│       │   │   │   ├── ThreadPriorityDemo.java             # MIN_PRIORITY, NORM_PRIORITY, MAX_PRIORITY
│       │   │   │   └── WaitNotifyDemo.java                 # wait() atomic lock release and notify() wakeup
│       │   │   ├── exception/                              # Custom checked domain exceptions
│       │   │   │   ├── InvalidBookingException.java
│       │   │   │   ├── InvalidUserException.java
│       │   │   │   └── VehicleNotAvailableException.java
│       │   │   ├── model/                                  # Domain entities
│       │   │   │   ├── Admin.java
│       │   │   │   ├── Bike.java
│       │   │   │   ├── Booking.java
│       │   │   │   ├── BookingRequest.java
│       │   │   │   ├── Car.java
│       │   │   │   ├── Customer.java
│       │   │   │   ├── User.java
│       │   │   │   └── Vehicle.java
│       │   │   ├── service/                                # Layered business logic services
│       │   │   │   ├── BookingService.java                 # Synchronized booking & queue processing
│       │   │   │   ├── UserService.java                    # Customer/Admin registration & HashMaps
│       │   │   │   └── VehicleService.java                 # Fleet inventory, Sets, sorting & search
│       │   │   ├── ui/console/
│       │   │   │   └── ConsoleApp.java                     # Interactive 15-option console menu
│       │   │   └── util/
│       │   │       ├── CollectionDemo.java                 # Collections framework educational showcase
│       │   │       ├── DynamicArray.java                   # Custom resizable array implementation
│       │   │       ├── GenericLinkedList.java              # Custom doubly-linked list implementation
│       │   │       └── VehicleComparators.java             # Multi-attribute vehicle comparators
│       │   └── resources/
│       └── test/
│           ├── java/
│           └── resources/
└── database/
    ├── README.md
    ├── sample-data.sql
    └── schema.sql
```

---

## Phase 2, Milestone 4: Thread Coordination Deep Dive

### 3.1 The Need for Thread Coordination

In concurrent systems, **synchronization (`synchronized`) provides mutual exclusion**—guaranteeing that only one thread executes a critical section at a time. However, mutual exclusion alone is insufficient when threads must **cooperate**:

- A **consumer thread** cannot proceed if the booking queue is empty. Polling repeatedly in a tight spin loop consumes 100% CPU time (busy waiting).
- A **producer thread** cannot insert new booking requests if the buffer has reached maximum capacity.
- **Thread coordination** enables threads to suspend execution efficiently and release CPU resources until another thread alters the application state and signals that conditions have changed.

### 3.2 wait(), notify(), and notifyAll() Mechanics

The Java `Object` class provides built-in coordination primitives:

| Method | Behavior | Monitor Requirement |
| :--- | :--- | :--- |
| `wait()` | Causes the current thread to **atomically release the monitor lock** and enter the `WAITING` state until another thread invokes `notify()` or `notifyAll()`. | Must hold the object's monitor lock (`synchronized (lock)`); otherwise throws `IllegalMonitorStateException`. |
| `notify()` | Wakes up a single arbitrary thread that is waiting on this object's monitor. | Must hold the monitor lock. The awakened thread moves to `BLOCKED` and cannot proceed until the signaling thread exits its synchronized block. |
| `notifyAll()` | Wakes up **all threads** waiting on this object's monitor. All awakened threads contend for the monitor lock. | Must hold the monitor lock. Safest choice when multiple threads wait on different conditions. |

```
Thread State Transition during wait() and notify():

[ RUNNABLE ] ── calls lock.wait() ──> [ WAITING ] (Monitor lock atomically released)
                                             │
                                             │ Another thread calls lock.notify() / notifyAll()
                                             ▼
[ RUNNABLE ] <── re-acquires lock ─── [ BLOCKED ] (Waiting to re-acquire monitor lock)
```

> **Key Rule:** An awakened thread does **NOT** resume execution immediately when `notify()` is called. It only transitions from `WAITING` to `BLOCKED`. It can only re-enter `RUNNABLE` after the signaling thread exits its `synchronized` block and releases the monitor lock.

### 3.3 Guarded Blocks & Condition Testing in while Loops

A **guarded block** is a pattern that repeatedly checks a condition before proceeding with an action:

```java
synchronized (lock) {
    // ALWAYS check the condition inside a while loop, NEVER an if statement!
    while (!condition) {
        lock.wait();
    }
    // Perform critical action now that condition is guaranteed TRUE
}
```

#### Why `while` is Strictly Required (Never `if`):
1. **Spurious Wakeups:** The underlying OS thread scheduler and JVM can wake up a waiting thread even if no explicit `notify()` or `notifyAll()` was invoked. A `while` loop re-checks the condition, sending the thread back to `wait()` if the wakeup was spurious.
2. **Stolen Signals / Race Conditions:** When `notifyAll()` wakes multiple threads (e.g. 3 consumers waiting on 1 produced item), the first thread to acquire the lock consumes the item. For the other 2 threads, the buffer is once again empty. If an `if` statement were used, the second and third threads would fall through and attempt to consume from an empty buffer, causing errors or corrupting state.
3. **Safety Invariant:** A thread must never execute past the guard unless the condition is genuinely satisfied at the exact moment it holds the monitor lock.

### 3.4 Producer-Consumer Bounded Buffer Architecture

`BookingRequestBuffer.java` implements a thread-safe bounded buffer coordinating producers and consumers:

```java
public class BookingRequestBuffer {
    private final Queue<BookingRequest> buffer = new LinkedList<>();
    private final int capacity;
    private final Object lock = new Object();
    private volatile boolean shutdown = false;

    public boolean produce(BookingRequest request) throws InterruptedException {
        synchronized (lock) {
            while (buffer.size() >= capacity && !shutdown) {
                lock.wait(); // Wait for consumer to free up a slot
            }
            if (shutdown) return false;
            buffer.offer(request);
            lock.notifyAll(); // Signal waiting consumers
            return true;
        }
    }

    public BookingRequest consume() throws InterruptedException {
        synchronized (lock) {
            while (buffer.isEmpty() && !shutdown) {
                lock.wait(); // Wait for producer to deposit a request
            }
            if (buffer.isEmpty() && shutdown) return null; // Exit condition
            BookingRequest req = buffer.poll();
            lock.notifyAll(); // Signal waiting producers
            return req;
        }
    }
}
```

### 3.5 Integrated RentIQ Booking Request Workflow

```
Customer Booking Requests
        │
        ▼
[ BookingProducer ] ── produce(req) ──> [ BookingRequestBuffer (capacity = N) ]
                                                        │
                                                        │ consume()
                                                        ▼
                                              [ BookingConsumer ]
                                                        │
                                                        ▼
                                       BookingService.bookVehicleSynchronized()
                                                        │
                                            ┌───────────┴───────────┐
                                            ▼                       ▼
                                   [ Vehicle Available ]   [ Vehicle Booked ]
                                            │                       │
                                   Booking Confirmed       VehicleNotAvailableException
```

1. **Producer Worker (`BookingProducer`):** Generates `BookingRequest` objects and places them into `BookingRequestBuffer`. If the buffer reaches `capacity`, the producer thread enters `wait()`.
2. **Consumer Workers (`BookingConsumer`):** Continuously pull requests from the buffer. If the buffer is empty, consumers enter `wait()`.
3. **Double-Booking Prevention:** Once a consumer retrieves a request, it invokes `BookingService.bookVehicleSynchronized`. This method synchronizes on the target `Vehicle` object, ensuring thread safety even if multiple consumers process requests for overlapping vehicles.
4. **Lock Independence:** The buffer lock (`lock` inside `BookingRequestBuffer`) is completely decoupled from the domain vehicle lock (`synchronized(vehicle)`). This strict lock hierarchy prevents deadlock.

### 3.6 Graceful Thread Shutdown & Zero-Leak Termination

Abrupt thread termination (e.g. `Thread.stop()`) is deprecated because it corrupts shared state and leaves locks in inconsistent conditions. RentIQ employs a cooperative, graceful shutdown protocol:

1. **Shutdown Signal:** `buffer.shutdown()` sets `shutdown = true` and calls `lock.notifyAll()`.
2. **Unblocking Waiting Workers:** Any consumers waiting on an empty buffer or producers waiting on a full buffer are immediately awakened.
3. **Drain Verification:** Consumers finish processing any requests already residing in the buffer. Once the buffer is empty and `shutdown == true`, `consume()` returns `null`.
4. **Clean Exit:** Workers break their operational loop, execute `finally` blocks, and terminate cleanly.
5. **Thread Joining:** Calling `thread.join()` in the orchestrator verifies that all background threads have completed (`TERMINATED` state) before application shutdown.

---

## Educational Demonstrations Overview

RentIQ includes dedicated demonstration classes for each coordination concept:

| Demo Class | Core Concept Demonstrated |
| :--- | :--- |
| `WaitNotifyDemo.java` | Single consumer waiting on empty buffer, atomic lock release, state inspection (`WAITING`), producer submission, and `notify()` wakeup. |
| `NotifyAllDemo.java` | 3 concurrent consumers waiting on empty buffer, 2 items produced, `notifyAll()` broadcast wakeup, condition recheck in while loop, and zero duplicate processing. |
| `GuardedBlockDemo.java` | Guard condition evaluation in a `while` loop, simulation of condition changes, and why `while` prevents spurious wakeups. |
| `ProducerConsumerDemo.java` | End-to-end multi-threaded workflow: 5 requests, 2 consumer threads, 1 producer thread, bounded buffer (capacity 3), and clean shutdown. |

---

## Console Application Menu Structure

The console interface (`ConsoleApp.java`) features an interactive 15-option menu:

```
----------------- RentIQ Main Menu -----------------
1.  Register User
2.  View Users
3.  View Vehicles
4.  Search Vehicles
5.  Book Vehicle
6.  View Bookings
7.  Cancel Booking
8.  View Recently Viewed Vehicles (LinkedList)
9.  View Unique Vehicle Categories (HashSet & LinkedHashSet)
10. View Sorted Vehicle Categories (TreeSet)
11. Run Complete Collections Framework Demonstration
12. Maps, Queues, Iterators & Sorting Submenu (Milestone 2)
13. Multithreading Demonstrations Submenu (Milestone 3)
14. Thread Coordination Demonstrations Submenu (Milestone 4)
15. Exit
----------------------------------------------------
```

### Option 14: Thread Coordination Submenu

```
======= Thread Coordination Demonstrations (Phase 2 - Milestone 4) =======
1. Demonstrate wait() (Consumer Waiting on Empty Buffer)
2. Demonstrate notify() (Producer Signaling Waiting Consumer)
3. Demonstrate notifyAll() (Multiple Waiting Consumers & Condition Recheck)
4. Demonstrate guarded blocks (while loop condition testing & spurious wakeups)
5. Run Producer-Consumer simulation (Multi-threaded buffer processing)
6. Run integrated RentIQ booking request processing
7. Demonstrate graceful thread shutdown (Buffer shutdown & consumer termination)
8. Return to Main Menu
==========================================================================
```

---

## How to Compile, Test, and Run

Navigate to the `backend-java/` directory:

```bash
cd backend-java
```

### 1. Compile the Project
Using Maven Wrapper (Windows):
```cmd
.\mvnw.cmd clean compile
```
Using standard Maven:
```bash
mvn clean compile
```

### 2. Run Automated Tests
```cmd
.\mvnw.cmd test
```

### 3. Execute the Application
```cmd
.\mvnw.cmd exec:java
```

The application will run:
- Phase 1 String handling, wrappers, and exception handling demonstrations.
- Collections framework showcase (ArrayList, LinkedList, HashSet, LinkedHashSet, TreeSet).
- Maps, Queues, Iterators, and Sorting demonstrations.
- Milestone 3 Multithreading Fundamentals (Thread, Runnable, start vs run, sleep, join, interrupt, lifecycle states, daemon threads, priorities, race conditions, atomic & volatile).
- **Milestone 4 Thread Coordination Live Demonstrations** (wait, notify, notifyAll, guarded blocks, bounded buffer producer-consumer, graceful shutdown).
- Automated interactive console session exercising all menus and submenus.

---

## Viva & Technical Interview Q&A

**Q1: What is the difference between `wait()` and `sleep()`?**
> `Thread.sleep(ms)` causes the current thread to pause for a specified duration **without releasing any monitor locks** it holds. In contrast, `Object.wait()` can only be called from a synchronized context and **atomically releases the monitor lock**, allowing other threads to acquire the lock while it waits.

**Q2: Why must `wait()`, `notify()`, and `notifyAll()` be called from within a `synchronized` block?**
> These methods operate directly on the monitor lock of the object. Calling them without holding the monitor lock results in an `IllegalMonitorStateException`. Synchronization ensures that state inspections and condition signaling are thread-safe and free from race conditions.

**Q3: Why should `wait()` always be enclosed in a `while` loop rather than an `if` statement?**
> Due to **spurious wakeups** (threads waking without an explicit notification) and **stolen signals** (another thread waking first and consuming the resource). A `while` loop forces the thread to re-evaluate the guard condition upon re-acquiring the lock before proceeding.

**Q4: When should you use `notifyAll()` instead of `notify()`?**
> `notify()` wakes up only one arbitrary thread. If threads are waiting for different conditions on the same object (e.g. producers waiting for space and consumers waiting for items), `notify()` might wake up a thread that cannot proceed, leading to lost signals or deadlock. `notifyAll()` wakes all waiting threads, allowing each to recheck its specific condition safely.

**Q5: How does RentIQ guarantee zero double-bookings in a multithreaded environment?**
> When a consumer processes a booking request, it enters `BookingService.bookVehicleSynchronized`, which obtains an intrinsic lock on the specific `Vehicle` object (`synchronized (vehicle)`). Any other thread attempting to book the same vehicle must wait until the first booking completes and sets `vehicle.setAvailable(false)`, preventing race conditions.

---

## License

This project is part of the RentIQ vehicle rental management platform. All rights reserved.
