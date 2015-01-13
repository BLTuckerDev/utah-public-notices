package com.bltucker.utahpublicnotices;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.bltucker.utahpublicnotices.data.NoticeCursor;
import com.bltucker.utahpublicnotices.data.PublicNoticeContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class NoticeAdapter extends CursorAdapter {

    private static final SimpleDateFormat dbDateFormatter = new SimpleDateFormat(PublicNoticeContract.DATE_FORMAT);
    private static final SimpleDateFormat dayOfWeekFormatter = new SimpleDateFormat("EEEE");
    private static final SimpleDateFormat fullDisplayDateFormatter = new SimpleDateFormat("MMMM dd yyyy");
    private static final SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");


    public NoticeAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.notice_list_item, parent, false);
        NoticeListItemViewHolder viewHolder = new NoticeListItemViewHolder(inflatedView);
        inflatedView.setTag(viewHolder);

        return inflatedView;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NoticeCursor noticeCursor = new NoticeCursor(cursor);

        try {
            Date meetingDate = dbDateFormatter.parse(noticeCursor.getDate());

            NoticeListItemViewHolder viewHolder = (NoticeListItemViewHolder) view.getTag();

            viewHolder.dayTextView.setText(dayOfWeekFormatter.format(meetingDate));
            Date date = _24HourSDF.parse(noticeCursor.getTime());
            viewHolder.timeTextView.setText(_12HourSDF.format(date));
            viewHolder.dateTextView.setText(fullDisplayDateFormatter.format(meetingDate));
            viewHolder.titleTextView.setText(noticeCursor.getTitle());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
