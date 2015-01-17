package com.bltucker.utahpublicnotices;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.bltucker.utahpublicnotices.data.NoticeCursor;

import java.util.Calendar;

public final class CalendarEventCreator {

    private final String LOG_TAG = CalendarEventCreator.class.getSimpleName();

    public void createCalendarEvent(Context context, NoticeCursor noticeCursor){
        try{
            Calendar noticeCalendar = noticeCursor.getCalendarDateTime();

            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, noticeCalendar.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, noticeCursor.getTitle())
                    .putExtra(CalendarContract.Events.DESCRIPTION,  noticeCursor.getFullNotice())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, noticeCursor.getAddress())
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

            context.startActivity(intent);

        } catch(Exception ex){
            Toast.makeText(context, context.getString(R.string.unable_add_to_calendar), Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, ex.toString());
        }
    }
}
