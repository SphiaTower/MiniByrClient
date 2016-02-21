package com.example.qingunext.app.util;

import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;

/**
 * Created by Rye on 5/16/2015.
 * 包裹时间值与单位的辅助类
 * getUnit改成switch(enum)，使enum与单位的文字解耦
 */
public class TimeWrapper {
    private int value;
    private TimeUnit unit;

    private TimeWrapper(int timeValue, TimeUnit timeUnit) {
        this.value = timeValue;
        this.unit = timeUnit;
    }

    public static TimeWrapper valueOf(long timeInSecond) {
        int deltaTimeInSecond = (int) (System.currentTimeMillis() / 1000 - timeInSecond);
        int minutes = deltaTimeInSecond / 60;
        if (minutes < 1) return new TimeWrapper(deltaTimeInSecond, TimeUnit.SECONDS);
        int hours = minutes / 60;
        if (hours < 1) return new TimeWrapper(minutes, TimeUnit.MINUTES);
        int days = hours / 24;
        if (days < 1) return new TimeWrapper(hours, TimeUnit.HOURS);
        int months = days / 30;
        if (months < 1) return new TimeWrapper(days, TimeUnit.DAYS);
        int years = months / 12;
        if (years < 1) return new TimeWrapper(months, TimeUnit.MONTHS);
        return new TimeWrapper(years, TimeUnit.YEARS);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + getUnit();
    }

    public String getUnit() {
        int resID;
        switch (unit){
            case SECONDS:
                resID = R.string.time_unit_seconds;
                break;
            case MINUTES:
                resID = R.string.time_unit_minutes;
                break;
            case HOURS:
                resID = R.string.time_unit_hours;
                break;
            case DAYS:
                resID = R.string.time_unit_days;
                break;
            case MONTHS:
                resID = R.string.time_unit_months;
                break;
            case YEARS:
                resID = R.string.time_unit_years;
                break;
            default:
                return null;
        }
        return QingUApp.getInstance().getString(resID);
    }


    public enum TimeUnit {
        SECONDS, MINUTES, HOURS, DAYS, MONTHS, YEARS
    }
}
