package com.example.a303com_laukuansin.utilities;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateXAxisValueFormatter extends IndexAxisValueFormatter {
    private List<String> dateList;
    public DateXAxisValueFormatter(List<String> dateList)
    {
        this.dateList=dateList;
    }
    @Override
    public String getFormattedValue(float value) {
        if(value>=0&&value<dateList.size())
        {
            return String.valueOf(dateList.get((int)value));
        }
        else{
             return "";
        }

    }
}
