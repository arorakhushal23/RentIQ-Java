package com.rentiq.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Educational utility demonstrating core Java Collections Framework interfaces and classes:
 * 1. ArrayList  - Dynamic resizing, indexed access, list operations
 * 2. LinkedList - Fast node insertions/deletions at ends, queue/deque operations
 * 3. HashSet    - Hash table storage, O(1) operations, unique elements, non-deterministic order
 * 4. LinkedHashSet - Hash table + linked list, unique elements, preserves insertion order
 * 5. TreeSet    - Red-Black tree storage, unique elements, natural or custom sorted order
 */
public class CollectionDemo {

    public static void runAllDemos() {
        System.out.println("\n============================================================");
        System.out.println("   RentIQ Phase 2: Java Collections Framework Showcase      ");
        System.out.println("============================================================");

        demoArrayList();
        demoLinkedList();
        demoHashSet();
        demoLinkedHashSet();
        demoTreeSet();

        System.out.println("============================================================\n");
    }

    /**
     * Demonstrates ArrayList capabilities: dynamic growth, indexed access, removals.
     */
    public static void demoArrayList() {
        System.out.println("\n--- 1. ArrayList Demonstration (Dynamic Resizing & Indexing) ---");
        List<String> list = new ArrayList<>();
        list.add("Sedan");
        list.add("SUV");
        list.add("Truck");
        list.add("Convertible");

        System.out.println("Initial ArrayList: " + list);
        System.out.println("Element at index 1: " + list.get(1));

        // Insert at specific index
        list.add(1, "Hatchback");
        System.out.println("After inserting 'Hatchback' at index 1: " + list);

        // Remove element
        list.remove("Truck");
        System.out.println("After removing 'Truck': " + list);
        System.out.println("Current list size (dynamic): " + list.size());
    }

    /**
     * Demonstrates LinkedList capabilities: addFirst, addLast, removeFirst, removeLast.
     */
    public static void demoLinkedList() {
        System.out.println("\n--- 2. LinkedList Demonstration (Recently Viewed Vehicles Deque) ---");
        LinkedList<String> recentVehicles = new LinkedList<>();

        // Customer views vehicles
        recentVehicles.addFirst("V101 (Tesla Model 3)");
        recentVehicles.addFirst("V102 (Toyota RAV4)");
        recentVehicles.addFirst("B201 (Yamaha YZF-R3)");

        System.out.println("Recently viewed (newest first): " + recentVehicles);
        System.out.println("Most recently viewed (head): " + recentVehicles.peekFirst());
        System.out.println("Oldest viewed (tail): " + recentVehicles.peekLast());

        // Evict oldest when list exceeds limit
        String evicted = recentVehicles.removeLast();
        System.out.println("Evicted oldest viewed vehicle: " + evicted);
        System.out.println("Remaining recently viewed: " + recentVehicles);
    }

    /**
     * Demonstrates HashSet: unique elements, O(1) performance, no order guarantee.
     */
    public static void demoHashSet() {
        System.out.println("\n--- 3. HashSet Demonstration (Unique Categories - Unordered) ---");
        Set<String> categories = new HashSet<>();

        // Add elements with duplicates
        categories.add("Sedan");
        categories.add("SUV");
        categories.add("Electric");
        categories.add("Sedan");      // Duplicate, rejected
        categories.add("SUV");        // Duplicate, rejected

        System.out.println("HashSet elements (duplicates rejected): " + categories);
        System.out.println("Contains 'SUV': " + categories.contains("SUV"));
        System.out.println("Total unique categories: " + categories.size());
    }

    /**
     * Demonstrates LinkedHashSet: unique elements with insertion order preserved.
     */
    public static void demoLinkedHashSet() {
        System.out.println("\n--- 4. LinkedHashSet Demonstration (Unique Categories - Insertion Order) ---");
        Set<String> categories = new LinkedHashSet<>();

        // Add elements in specific order with duplicates
        categories.add("Luxury");
        categories.add("Economy");
        categories.add("Electric");
        categories.add("Motorcycle");
        categories.add("Economy");    // Duplicate, rejected

        System.out.println("LinkedHashSet maintains insertion sequence: " + categories);
    }

    /**
     * Demonstrates TreeSet: unique elements sorted according to natural order.
     */
    public static void demoTreeSet() {
        System.out.println("\n--- 5. TreeSet Demonstration (Unique Categories - Alphabetically Sorted) ---");
        Set<String> categories = new TreeSet<>();

        categories.add("Sedan");
        categories.add("Convertible");
        categories.add("SUV");
        categories.add("Bike");
        categories.add("Truck");
        categories.add("Sedan");      // Duplicate, rejected

        System.out.println("TreeSet automatically sorted (alphabetical): " + categories);
        System.out.println("First category (alphabetically first): " + ((TreeSet<String>) categories).first());
        System.out.println("Last category (alphabetically last): " + ((TreeSet<String>) categories).last());
    }
}
