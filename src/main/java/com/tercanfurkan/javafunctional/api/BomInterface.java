package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author massive
 *         Date: 15/11/2016
 *         Time: 12.11
 */
public interface BomInterface {
    public Set<String> getNamesForComponents(String productCode);
    public Set<String> getNamesForProducts(String productCode);
    public List<Map<String, Object>> getComponents(String productCode);
    public List<Map<String, Object>> getComponents(String productCode, LocalDate date);
    public List<Map<String, Object>> getForecastsFor(LocalDate date);
}
