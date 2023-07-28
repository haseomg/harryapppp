package com.example.goldentoads;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class CustomValueFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value){
        return String.format(("%.1f%%"),value);
    }
}
