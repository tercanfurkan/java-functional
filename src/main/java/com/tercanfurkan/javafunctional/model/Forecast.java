package com.tercanfurkan.javafunctional.model;

import java.time.LocalDate;
import javax.annotation.Nonnull;

public class Forecast {
    @Nonnull public String productCode;
    @Nonnull public LocalDate date;
    public double quantity;

    public Forecast(@Nonnull String productCode, @Nonnull LocalDate date, double quantity) {
        this.productCode = productCode;
        this.date = date;
        this.quantity = quantity;
    }
}
