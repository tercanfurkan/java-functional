package com.tercanfurkan.javafunctional;

import java.time.LocalDate;

import com.tercanfurkan.javafunctional.provider.DataProvider;
import com.tercanfurkan.javafunctional.api.IngredientsProcessor;

public class Main {
    public static void main(String[] args) {
        IngredientsProcessor p = new IngredientsProcessor(DataProvider.getEntriesForAvailabilityChecks());
        
        p.getIngredients("BREAD", LocalDate.parse("2000-01-01")).forEach(System.out::println);
    }
}
