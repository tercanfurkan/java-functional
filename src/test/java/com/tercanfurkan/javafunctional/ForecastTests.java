package com.tercanfurkan.javafunctional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.tercanfurkan.javafunctional.api.BomInterface;
import com.tercanfurkan.javafunctional.api.BomProcessor;
import com.tercanfurkan.javafunctional.api.ForecastInterface;
import com.tercanfurkan.javafunctional.provider.DataProvider;
import org.junit.Test;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ForecastTests {
    @SuppressWarnings("unchecked")
    @Test
    public void testGetForecastsForBasicIngredients() {
        assertTrue("getForecastsFor(date) returns a list of maps containing the ingredient code (String), amount to order (double) and date (LocalDate).", true);
        ForecastInterface processor = new BomProcessor(DataProvider.getEntriesForForecasts());
        List<Map<String, Object>> components = processor.getForecastsFor(LocalDate.parse("2017-01-01"));
        assertThat(components, hasItems(
            of("date", LocalDate.parse("2017-01-01"), "name", "FLOUR", "multiplier", 4.0),
            of("date", LocalDate.parse("2017-01-01"), "name", "YEAST", "multiplier", 2.0),
            of("date", LocalDate.parse("2017-01-01"), "name", "EGG_WHITE", "multiplier", 17.0),
            of("date", LocalDate.parse("2017-01-01"), "name", "EGG_YOLK", "multiplier", 17.0),
            of("date", LocalDate.parse("2017-01-01"), "name", "OIL", "multiplier", 20.0),
            of("date", LocalDate.parse("2017-01-01"), "name", "MUSTARD_SEEDS", "multiplier", 30.0),
            of("date", LocalDate.parse("2017-01-01"), "name", "BACON", "multiplier", 8.0)
        ));

        components = processor.getForecastsFor(LocalDate.parse("2016-12-24"));
        assertThat(components, hasItems(
            of("date", LocalDate.parse("2016-12-24"), "name", "FLOUR", "multiplier", 8.0),
            of("date", LocalDate.parse("2016-12-24"), "name", "HYPER_YEAST", "multiplier", 0.4),
            of("date", LocalDate.parse("2016-12-24"), "name", "EGG_WHITE", "multiplier", 24.0),
            of("date", LocalDate.parse("2016-12-24"), "name", "EGG_YOLK", "multiplier", 24.0),
            of("date", LocalDate.parse("2016-12-24"), "name", "OIL", "multiplier", 30.0),
            of("date", LocalDate.parse("2016-12-24"), "name", "MUSTARD_SEEDS", "multiplier", 60.0),
            of("date", LocalDate.parse("2016-12-24"), "name", "BACON", "multiplier", 16.0)
        ));
    }
}
