package com.tercanfurkan.javafunctional.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeconstructionInterface {
    public Set<String> getNamesForComponents(String productCode);
    public Set<String> getNamesForProducts(String productCode);
    public List<Map<String, Object>> getComponents(String productCode);
}
