package com.gunz.carrental.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Gunz on 22/10/2016.
 */
public class CurrencyFormatter {
    private DecimalFormat IDRformat = new DecimalFormat("#,###,###");
    private DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("sv", "SE"));
    private double price;

    public CurrencyFormatter(double price) {
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        IDRformat.setDecimalFormatSymbols(symbols);
        this.price = price;
    }

    public String format() {
        return "Rp. " + IDRformat.format(price);
    }
}
