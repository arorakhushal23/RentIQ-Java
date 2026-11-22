package com.rentiq.util;

import com.rentiq.model.Vehicle;
import java.util.Comparator;

/**
 * Custom Comparators for sorting Vehicle records in RentIQ:
 * 1. Price: Lowest to Highest (Ascending)
 * 2. Price: Highest to Lowest (Descending)
 * 3. Name: Alphabetical Order (Ascending)
 * 4. Rating: Highest to Lowest (Descending)
 */
public class VehicleComparators {

    /**
     * Sorts vehicles by rental price per day in ascending order (Lowest to Highest).
     */
    public static final Comparator<Vehicle> BY_PRICE_ASC =
        Comparator.comparingDouble(Vehicle::getPricePerDay);

    /**
     * Sorts vehicles by rental price per day in descending order (Highest to Lowest).
     */
    public static final Comparator<Vehicle> BY_PRICE_DESC =
        Comparator.comparingDouble(Vehicle::getPricePerDay).reversed();

    /**
     * Sorts vehicles alphabetically by name (Brand + Model) in case-insensitive order.
     */
    public static final Comparator<Vehicle> BY_NAME_ASC =
        Comparator.comparing(Vehicle::getName, String.CASE_INSENSITIVE_ORDER);

    /**
     * Sorts vehicles by customer review rating in descending order (Highest to Lowest).
     */
    public static final Comparator<Vehicle> BY_RATING_DESC =
        Comparator.comparingDouble(Vehicle::getRating).reversed();
}
