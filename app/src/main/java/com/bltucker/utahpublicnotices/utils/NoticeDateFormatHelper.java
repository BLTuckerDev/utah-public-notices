package com.bltucker.utahpublicnotices.utils;

import com.bltucker.utahpublicnotices.data.PublicNoticeContract;

import java.text.SimpleDateFormat;

public final class NoticeDateFormatHelper {

    private static final SimpleDateFormat dbDateFormatter = new SimpleDateFormat(PublicNoticeContract.DATE_FORMAT);
    private static final SimpleDateFormat dayOfWeekFormatter = new SimpleDateFormat("EEEE");
    private static final SimpleDateFormat fullDisplayDateFormatter = new SimpleDateFormat("MMMM dd yyyy");
    private static final SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");


    public SimpleDateFormat getDbDateFormatter(){
        return dbDateFormatter;
    }

    public SimpleDateFormat getDayOfWeekFormatter(){
        return dayOfWeekFormatter;
    }

    public SimpleDateFormat getFullDisplayDateFormatter(){
        return fullDisplayDateFormatter;
    }

    public SimpleDateFormat get12HourDateFormatter(){
        return _12HourSDF;
    }

    public SimpleDateFormat get24HourDateFormatter(){
        return _24HourSDF;
    }
}
