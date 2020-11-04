package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AvailabilityInterface {
    public List<Map<String, Object>> getComponents(String productCode, LocalDate date);
}
