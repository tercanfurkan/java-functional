package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface AvailabilityInterface {

    /**
     * Overloads the getComponents API to support date-specific queries for components as follows:
     *
     * For a given product code and date, anywhere in the tree, it returns every component (children) for all levels
     * including or excluding ingredients that are active and inactive for the given date. As before, the returned
     * value is an ordered list of maps.
     */
    List<Map<String, Object>> getComponents(String productCode, LocalDate date);
}
