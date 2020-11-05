package com.tercanfurkan.javafunctional.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeconstructionInterface {

    /**
     * For a given product code, anywhere in the tree, it returns the values of child products
     * in an unordered Set
     */
    Set<String> getNamesForComponents(String productCode);

    /**
     * For a given product code, anywhere in the tree, it returns the values of ancestor products
     * in an unordered Set.
     */
    Set<String> getNamesForProducts(String productCode);

    /**
     * For a given product code, anywhere in the tree, it returns every component (children) for all levels.
     * Returned value is an ordered list of key-value pairs.
     */
    List<Map<String, Object>> getComponents(String productCode);
}
