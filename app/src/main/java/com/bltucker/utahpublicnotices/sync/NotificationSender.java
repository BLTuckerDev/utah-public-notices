package com.bltucker.utahpublicnotices.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.bltucker.utahpublicnotices.DetailsActivity;
import com.bltucker.utahpublicnotices.MainActivity;
import com.bltucker.utahpublicnotices.R;
import com.bltucker.utahpublicnotices.data.NoticeCursor;

public final class NotificationSender {

    private static final int MEETING_NOTIFICATION_ID = 101;

    private final Context context;

    public NotificationSender(Context context){
        this.context = context;
    }


    public void sendNotification(Cursor cursor){

        NoticeCursor noticeCursor = new NoticeCursor(cursor);
        noticeCursor.moveToFirst();

        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(R.drawable.ic_launcher);
        String formattedNotificationTitle = String.format("%s - %s", noticeCursor.getTime(), noticeCursor.getTitle());
        builder.setContentTitle(formattedNotificationTitle);
        builder.setContentText(noticeCursor.getAddress());

        Intent resultIntent = new Intent(context, DetailsActivity.class);
        resultIntent.putExtra(DetailsActivity.NOTICE_ID_EXTRA, noticeCursor.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(MEETING_NOTIFICATION_ID, builder.build());
    }
}