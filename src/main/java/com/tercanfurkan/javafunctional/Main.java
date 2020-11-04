package com.tercanfurkan.javafunctional;

import java.time.LocalDate;

import com.tercanfurkan.javafunctional.provider.DataProvider;
import com.tercanfurkan.javafunctional.api.BomProcessor;

public class Main {
    public static void main(String[] args) {
        System.out.println("You can run your implementation here");
        BomProcessor p = new BomProcessor(DataProvider.getEntriesForAvailabilityChecks());
        
        p.getComponents("BREAD", LocalDate.parse("2000-01-01")).forEach(System.out::println);
    }
}
