package com.bltucker.utahpublicnotices;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.bltucker.utahpublicnotices.data.NoticeCursor;
import com.bltucker.utahpublicnotices.data.PublicNoticeContract;
import com.bltucker.utahpublicnotices.utils.NoticeDateFormatHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class NoticeAdapter extends CursorAdapter {

    private static final NoticeDateFormatHelper dateFormatHelper = new NoticeDateFormatHelper();


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

            Date meetingDate = dateFormatHelper.getDbDateFormatter().parse(noticeCursor.getDate());

            NoticeListItemViewHolder viewHolder = (NoticeListItemViewHolder) view.getTag();

            viewHolder.dayTextView.setText(dateFormatHelper.getDayOfWeekFormatter().format(meetingDate));
            Date date = dateFormatHelper.get24HourDateFormatter().parse(noticeCursor.getTime());
            viewHolder.timeTextView.setText(dateFormatHelper.get12HourDateFormatter().format(date));
            viewHolder.dateTextView.setText(dateFormatHelper.getFullDisplayDateFormatter().format(meetingDate));
            viewHolder.titleTextView.setText(noticeCursor.getTitle());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
