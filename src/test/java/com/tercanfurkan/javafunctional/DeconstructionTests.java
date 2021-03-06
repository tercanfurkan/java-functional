package com.tercanfurkan.javafunctional;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Sets;
import com.tercanfurkan.javafunctional.api.IngredientsProcessor;
import com.tercanfurkan.javafunctional.api.DeconstructionInterface;
import com.tercanfurkan.javafunctional.provider.DataProvider;
import org.junit.Test;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DeconstructionTests {
    @Test
    public void testGetIngredients() {
        assertTrue("getIngredients returns a hash of ingredients for the given product code", true);
    }

    @Test
    public void testGetNamesForIngredients() {
        assertTrue("getNamesForIngredients returns the codes of each ingredient of the given product", true);
        DeconstructionInterface processor = new IngredientsProcessor(DataProvider.getEntriesForDeconstruction());
        assertEquals(
            Sets.newHashSet("BREAD", "MAYO", "BACON", "FLOUR", "YEAST", "EGG", "EGG_WHITE", "EGG_YOLK", "OIL", "MUSTARD", "GARLIC_POWDER", "GARLIC"),
            processor.getNamesForIngredients("CLUB_SANDWICH")
        );

        assertEquals(
            Sets.newHashSet("GARLIC_POWDER", "GARLIC"),
            processor.getNamesForIngredients("MUSTARD")
        );
    }

    @Test
    public void testGetNamesForProducts() {
        assertTrue("getNamesForProducts returns the codes of each product that contains the given ingredient", true);
        DeconstructionInterface processor = new IngredientsProcessor(DataProvider.getEntriesForDeconstruction());

        assertEquals(
            Sets.newHashSet("GARLIC_POWDER", "MUSTARD", "MAYO", "CLUB_SANDWICH"),
            processor.getNamesForProducts("GARLIC")
        );

        assertEquals(
            Sets.newHashSet("MAYO", "CLUB_SANDWICH"),
            processor.getNamesForProducts("MUSTARD")
        );
    }

    @Test @SuppressWarnings("unchecked")
    public void testGetTreeForEggs() {
        assertTrue("getNamesForProducts returns the codes of each product that contains the given ingredient", true);
        DeconstructionInterface processor = new IngredientsProcessor(DataProvider.getEntriesForDeconstruction());
        List<Map<String, Object>> ingredients = processor.getIngredients("FRIED_EGGS");
        assertThat(ingredients, hasItems(
            of("depth", 1, "name", "EGG", "multiplier", 1),
            of("depth", 1, "name", "OIL", "multiplier", 1),
            of("depth", 2, "name", "EGG_WHITE", "multiplier", 1),
            of("depth", 2, "name", "EGG_YOLK", "multiplier", 1)
        ));
    }

    @Test @SuppressWarnings("unchecked")
    public void testGetTreeForSandwich() {
        assertTrue("getNamesForProducts returns the codes of each product that contains the given ingredient", true);
        DeconstructionInterface processor = new IngredientsProcessor(DataProvider.getEntriesForDeconstruction());
        List<Map<String, Object>> ingredients = processor.getIngredients("CLUB_SANDWICH");
        assertThat(ingredients, hasItems(
            of("depth", 1, "name", "BREAD", "multiplier", 1),
            of("depth", 1, "name", "MAYO", "multiplier", 1),
            of("depth", 1, "name", "BACON", "multiplier", 1),
            of("depth", 2, "name", "FLOUR", "multiplier", 1),
            of("depth", 2, "name", "YEAST", "multiplier", 1),
            of("depth", 2, "name", "EGG", "multiplier", 1),
            of("depth", 2, "name", "OIL", "multiplier", 1),
            of("depth", 2, "name", "MUSTARD", "multiplier", 1),
            of("depth", 3, "name", "EGG_WHITE", "multiplier", 1),
            of("depth", 3, "name", "EGG_YOLK", "multiplier", 1),
            of("depth", 3, "name", "GARLIC_POWDER", "multiplier", 1),
            of("depth", 4, "name", "GARLIC", "multiplier", 1)
        ));
    }
}
