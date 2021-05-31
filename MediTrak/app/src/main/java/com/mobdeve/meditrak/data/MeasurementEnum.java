package com.mobdeve.meditrak.data;

import androidx.annotation.NonNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public enum MeasurementEnum {
    KILOGRAM, GRAMS, MILLIGRAM, MICROGRAM,
    LITRE, MILLILITRE, CUBIC_CENTIMETRE,
    MOLE, MILLIMOLE, TABLET, CAPSULE;

    public static DecimalFormat DF = new DecimalFormat("0.00");

    public MeasurementEnum[] validConversions() {
        if (this == KILOGRAM || this == GRAMS || this == MILLIGRAM || this == MICROGRAM)
            return new MeasurementEnum[]{KILOGRAM, GRAMS, MILLIGRAM, MICROGRAM};
        else if (this == LITRE || this == MILLILITRE || this == CUBIC_CENTIMETRE)
            return new MeasurementEnum[]{LITRE, MILLILITRE, CUBIC_CENTIMETRE};
        else if (this == MOLE || this == MILLIMOLE)
            return new MeasurementEnum[]{MOLE, MILLIMOLE};
        else
            return new MeasurementEnum[]{ TABLET, CAPSULE };
    }

    public float convert(float q1, MeasurementEnum meas1) {
        DF.setRoundingMode(RoundingMode.HALF_UP);

        if (this == KILOGRAM) {
            switch (meas1) {
                case GRAMS: return q1 * 1000;
                case MILLIGRAM: return q1 * 1000 * 1000;
                case MICROGRAM: return q1 * 1000 * 1000 * 1000;
            }
        } else if (this == GRAMS) {
            switch (meas1) {
                case KILOGRAM: return q1 / 1000;
                case MILLIGRAM: return q1 * 1000;
                case MICROGRAM: return q1 * 1000 * 1000;
            }
        } else if (this == MILLIGRAM) {
            switch (meas1) {
                case GRAMS: return q1 / 1000;
                case KILOGRAM: return q1 / 1000 / 1000;
                case MICROGRAM: return q1 * 1000;
            }
        } else if (this == MICROGRAM) {
            switch (meas1) {
                case GRAMS: return q1 / 1000;
                case MILLIGRAM: return q1 / 1000  / 1000;
                case KILOGRAM: return q1 / 1000  / 1000 / 1000;
            }
        }

        if (this == LITRE) {
            switch (meas1) {
                case MILLILITRE: return q1 * 1000;
                case CUBIC_CENTIMETRE: return q1 * 1000;
            }
        } else if (this == MILLILITRE) {
            switch (meas1) {
                case LITRE: return q1 / 1000;
                case CUBIC_CENTIMETRE: return q1;
            }
        } else if (this == CUBIC_CENTIMETRE) {
            switch (meas1) {
                case LITRE: return q1 / 1000;
                case MILLILITRE: return q1;
            }
        }

        if (this == MOLE && meas1 == MILLIMOLE) {
            return q1 * 1000;
        } else if (this == MILLIMOLE && meas1 == MOLE) {
            return q1 / 1000;
        }

        return Float.parseFloat(DF.format(q1));
    }

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case KILOGRAM:
                return "kg";
            case GRAMS:
                return "g";
            case MILLIGRAM:
                return "mg";
            case MICROGRAM:
                return "mcg";
            case LITRE:
                return "L";
            case MILLILITRE:
                return "ml";
            case CUBIC_CENTIMETRE:
                return "cc";
            case MOLE:
                return "mol";
            case MILLIMOLE:
                return "mmol";
            case TABLET:
                return "tab";
            case CAPSULE:
                return "caps";
        }

        return "Invalid";
    }

    public static MeasurementEnum getEnum(String str) {
        if (str.compareToIgnoreCase("kg") == 0) return KILOGRAM;
        if (str.compareToIgnoreCase("g") == 0) return GRAMS;
        if (str.compareToIgnoreCase("mg") == 0) return MILLIGRAM;
        if (str.compareToIgnoreCase("mcg") == 0) return MICROGRAM;
        if (str.compareToIgnoreCase("L") == 0) return LITRE;
        if (str.compareToIgnoreCase("ml") == 0) return MILLILITRE;
        if (str.compareToIgnoreCase("cc") == 0) return CUBIC_CENTIMETRE;
        if (str.compareToIgnoreCase("mol") == 0) return MOLE;
        if (str.compareToIgnoreCase("mmol") == 0) return MILLIMOLE;
        if (str.compareToIgnoreCase("tab") == 0) return TABLET;
        if (str.compareToIgnoreCase("caps") == 0) return CAPSULE;

        return null;
    }
}
