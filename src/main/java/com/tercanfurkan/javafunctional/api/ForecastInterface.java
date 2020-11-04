package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ForecastInterface {
    public List<Map<String, Object>> getForecastsFor(LocalDate date);
}
