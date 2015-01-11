package com.bltucker.utahpublicnotices;

import android.view.View;
import android.widget.TextView;

public final class NoticeListItemViewHolder {

    public final TextView dayTextView;
    public final TextView timeTextView;
    public final TextView dateTextView;
    public final TextView titleTextView;

    public NoticeListItemViewHolder(View parentView){
        dayTextView = (TextView) parentView.findViewById(R.id.notice_list_item_day_text_view);
        timeTextView = (TextView) parentView.findViewById(R.id.notice_list_item_time_text_view);
        dateTextView = (TextView) parentView.findViewById(R.id.notice_list_item_date_text_view);
        titleTextView = (TextView) parentView.findViewById(R.id.notice_list_item_title_text_view);
    }
}
