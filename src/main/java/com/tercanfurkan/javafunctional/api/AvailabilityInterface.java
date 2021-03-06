package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface AvailabilityInterface {

    /**
     * Overloads the getIngredients API to support date-specific queries for ingredient as follows:
     *
     * For a given product code and date, anywhere in the tree, it returns every ingredient (children) for all levels
     * including or excluding ingredients that are active and inactive for the given date. As before, the returned
     * value is an ordered list of maps.
     */
    List<Map<String, Object>> getIngredients(String productCode, LocalDate date);
}
