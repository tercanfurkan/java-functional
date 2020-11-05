package com.tercanfurkan.javafunctional.model;

import java.time.LocalDate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

public class Entry {
    @Nonnull public String code;
    @Nonnull public String componentCode;
    public double multiplier;
    @Nullable public LocalDate startDate;
    @Nullable public LocalDate endDate;

    public Entry(@Nonnull String code, @Nonnull String componentCode, double multiplier, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        this.code = code;
        this.multiplier = multiplier;
        this.componentCode = componentCode;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Entry NOOP() {
    	return new Entry("", "", 1, null, null);
	}

    @Nonnull
    public String getComponentCode() {
        return componentCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("code", code)
            .add("componentCode", componentCode)
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
		result = prime * result + componentCode.hashCode();
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
		Entry other = (Entry) obj;
        if (!code.equals(other.code))
            return false;
        if (!componentCode.equals(other.componentCode))
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
