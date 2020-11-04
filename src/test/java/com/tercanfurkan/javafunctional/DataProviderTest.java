package com.tercanfurkan.javafunctional;

import java.util.List;

import com.tercanfurkan.javafunctional.provider.DataProvider;
import com.tercanfurkan.javafunctional.model.Entry;
import com.tercanfurkan.javafunctional.model.Forecast;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class DataProviderTest {
    @Test public void testReturnsPart1Data() {
        List<Entry> entries = DataProvider.getEntriesForDeconstruction();
        assertThat(entries, isA(List.class));
        assertThat(entries, hasSize(14));
    }

    @Test public void testReturnsPart2Data() {
        List<Entry> entries = DataProvider.getEntriesForAvailabilityChecks();
        assertThat(entries, isA(List.class));
        assertThat(entries, hasSize(21));
    }

    @Test public void testReturnsPart3Data() {
        List<Entry> entries = DataProvider.getEntriesForForecasts();
        assertThat(entries, isA(List.class));
        assertThat(entries, hasSize(19));
    }

    @Test public void testReturnsForecasts() {
        List<Forecast> entries = DataProvider.getForecasts();
        assertThat(entries, isA(List.class));
        assertThat(entries, hasSize(3));
    }
}
