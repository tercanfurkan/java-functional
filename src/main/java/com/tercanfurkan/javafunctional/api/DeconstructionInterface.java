package com.tercanfurkan.javafunctional.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeconstructionInterface {
    Set<String> getNamesForComponents(String productCode);
    Set<String> getNamesForProducts(String productCode);
    List<Map<String, Object>> getComponents(String productCode);
}
