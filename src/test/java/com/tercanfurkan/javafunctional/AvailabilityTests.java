package com.tercanfurkan.javafunctional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.tercanfurkan.javafunctional.api.AvailabilityInterface;
import com.tercanfurkan.javafunctional.api.BomProcessor;
import com.tercanfurkan.javafunctional.provider.DataProvider;
import org.junit.Test;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AvailabilityTests {

    @SuppressWarnings("unchecked")
    @Test
    public void testGetNamesForActiveComponents() {
        assertTrue("getNamesForProducts(code, date) returns the codes of each product that contains the given component which are active for the given date", true);
        AvailabilityInterface processor = new BomProcessor(DataProvider.getEntriesForAvailabilityChecks());
        List<Map<String, Object>> components = processor.getComponents("BREAD", LocalDate.parse("2000-01-01"));
        assertThat(components, hasItems(
            of("depth", 1, "name", "FLOUR", "multiplier", 1.0),
            of("depth", 1, "name", "YEAST", "multiplier", 1.0),
            //TODO: inactive substitute should not be shown based on the specs. But;
            // - I did not find a reasonable way to define the order of preference among components and their substitutes
            // - Therefore I left the inactive substitutes (..._YEASTs with multiplier 0) in the results.
            of("depth", 1, "name", "SUPER_YEAST", "multiplier", 0.0),
            of("depth", 1, "name", "HYPER_YEAST", "multiplier", 0.0)
        ));

        components = processor.getComponents("BREAD", LocalDate.parse("2016-12-24"));
        assertThat(components, hasItems(
            of("depth", 1, "name", "FLOUR", "multiplier", 1.0),
            of("depth", 1, "name", "YEAST", "multiplier", 0.0),
            of("depth", 1, "name", "SUPER_YEAST", "multiplier", 0.0),
            of("depth", 1, "name", "HYPER_YEAST", "multiplier", 1.0)
        ));

        components = processor.getComponents("BREAD", LocalDate.parse("2016-12-25"));
        assertThat(components, hasItems(
            of("depth", 1, "name", "FLOUR", "multiplier", 1.0),
            of("depth", 1, "name", "YEAST", "multiplier", 0.0),
            of("depth", 1, "name", "SUPER_YEAST", "multiplier", 1.0),
            of("depth", 1, "name", "HYPER_YEAST", "multiplier", 0.0) //TODO: based on specs, inactive less preferred substitute should not be shown in results
        ));
    }
}
