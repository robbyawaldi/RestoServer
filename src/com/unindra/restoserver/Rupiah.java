package com.unindra.restoserver;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Rupiah {
    private static DecimalFormat decimalFormat;

    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp");
        decimalFormatSymbols.setMonetaryDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');

        decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
    }

    public static String rupiah(int number) {
        return decimalFormat.format(number);
    }
}
