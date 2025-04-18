package edu.alfonsaco.codezen.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class MyDayDecorator implements DayViewDecorator {

    private final CalendarDay day;
    private final int color;

    public MyDayDecorator(CalendarDay day, int color) {
        this.day = day;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(new ColorDrawable(color));
    }
}
