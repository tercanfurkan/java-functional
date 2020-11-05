package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface AvailabilityInterface {
    List<Map<String, Object>> getComponents(String productCode, LocalDate date);
}
