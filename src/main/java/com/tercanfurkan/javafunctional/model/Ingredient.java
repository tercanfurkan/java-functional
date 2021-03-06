package com.tercanfurkan.javafunctional.model;

import java.time.LocalDate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

public class Ingredient {
    @Nonnull public String code;
    @Nonnull public String ingredientCode;
    public double multiplier;
    @Nullable public LocalDate startDate;
    @Nullable public LocalDate endDate;

    public Ingredient(@Nonnull String code, @Nonnull String ingredientCode, double multiplier, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        this.code = code;
        this.multiplier = multiplier;
        this.ingredientCode = ingredientCode;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Ingredient NOOP() {
    	return new Ingredient("", "", 1, null, null);
	}

    @Nonnull
    public String getIngredientCode() {
        return ingredientCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("code", code)
            .add("ingredientCode", ingredientCode)
            .add("multiplier", multiplier)
            .add("startDate", startDate)
            .add("endDate", endDate)
            .toString();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code.hashCode();
		result = prime * result + ingredientCode.hashCode();
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		long temp;
		temp = Double.doubleToLongBits(multiplier);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ingredient other = (Ingredient) obj;
        if (!code.equals(other.code))
            return false;
        if (!ingredientCode.equals(other.ingredientCode))
            return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (Double.doubleToLongBits(multiplier) != Double.doubleToLongBits(other.multiplier))
			return false;
		if (startDate == null) {
            return other.startDate == null;
		} else return startDate.equals(other.startDate);
    }
}
