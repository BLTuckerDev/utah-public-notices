package com.bltucker.utahpublicnotices.notifications;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.bltucker.utahpublicnotices.data.PublicNoticeContract;

final class NotificationSenderAsyncTask extends AsyncTask<Void,Void, Void> {

    private final Context context;
    private final long noticeId;


    public NotificationSenderAsyncTask(Context context, long noticeId) {
        this.context = context;
        this.noticeId = noticeId;
    }


    @Override
    protected Void doInBackground(Void... params) {

        Cursor cursor = context.getContentResolver().query(PublicNoticeContract.NoticeEntry.CONTENT_URI,
                null,
                PublicNoticeContract.NoticeEntry._ID + " = '" + noticeId + "'",
                null,
                null);

        NotificationSender notificationSender = new NotificationSender(context);
        if(cursor.moveToFirst()){
            notificationSender.sendNotification(cursor);
        }

        return null;
    }
}
