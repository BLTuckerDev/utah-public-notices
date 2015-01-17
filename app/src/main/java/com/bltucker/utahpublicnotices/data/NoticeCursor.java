package com.bltucker.utahpublicnotices.data;

import android.database.Cursor;

public final class NoticeCursor {

    private final Cursor cursor;

    public NoticeCursor(Cursor cursor){
        this.cursor = cursor;
    }


    public boolean moveToFirst(){
        return cursor.moveToFirst();
    }


    public boolean moveToNext(){
        return cursor.moveToNext();
    }


    public void close(){
        cursor.close();
    }


    public String getFullNotice(){
        return cursor.getString(cursor.getColumnIndex(PublicNoticeContract.NoticeEntry.COLUMN_FULL_NOTICE));
    }


    public String getTitle(){
        return cursor.getString(cursor.getColumnIndex(PublicNoticeContract.NoticeEntry.COLUMN_TITLE));
    }


    public String getDate(){
        return cursor.getString(cursor.getColumnIndex(PublicNoticeContract.NoticeEntry.COLUMN_DATE));
    }


    public String getTime(){
        return cursor.getString(cursor.getColumnIndex(PublicNoticeContract.NoticeEntry.COLUMN_TIME));
    }


    public String getAddress(){
        return cursor.getString(cursor.getColumnIndex(PublicNoticeContract.NoticeEntry.COLUMN_LOCATION));
    }


    public long getId() {
        return cursor.getLong(0);
    }
}
