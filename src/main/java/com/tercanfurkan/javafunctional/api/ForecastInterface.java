package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface ForecastInterface {

    /**
     * For the given date, it calculates the demand of basic ingredients (i.e. tree leaves) for all active products.
     * Returned value is a list of maps containing the ingredient code (String),
     * amount to order (double) and date (LocalDate).
     */
    List<Map<String, Object>> getForecastsFor(LocalDate date);
}
