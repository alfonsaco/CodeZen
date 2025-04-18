package edu.alfonsaco.codezen.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import edu.alfonsaco.codezen.R;

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
        GradientDrawable drawable = new GradientDrawable();
        // Lo hacemos así para que sea circular el fondo
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        drawable.setSize(50, 50);

        int alpha=(int) (0.5 * 255);
        drawable.setAlpha(alpha);
        view.setBackgroundDrawable(drawable);
    }
}
