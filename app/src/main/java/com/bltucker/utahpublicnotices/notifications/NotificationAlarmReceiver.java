package com.bltucker.utahpublicnotices.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class NotificationAlarmReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_NOTICE_ID_EXTRA = "noticeId";


    public NotificationAlarmReceiver() {
    }


    @Override
    public void onReceive(final Context context, Intent intent) {

        if(!intent.hasExtra(NOTIFICATION_NOTICE_ID_EXTRA)){
            return;
        }

        long noticeId = intent.getLongExtra(NOTIFICATION_NOTICE_ID_EXTRA, -1);

        if(noticeId != -1){
            new NotificationSenderAsyncTask(context, noticeId).execute();
        }
    }
}
