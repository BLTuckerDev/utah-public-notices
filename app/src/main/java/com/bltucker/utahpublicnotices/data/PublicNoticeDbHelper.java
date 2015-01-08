package com.bltucker.utahpublicnotices.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class PublicNoticeDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "publicnotice.db";

    public PublicNoticeDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CITY_TABLE = "CREATE TABLE " + PublicNoticeContract.CityEntry.TABLE_NAME + " ("
                + PublicNoticeContract.CityEntry._ID + " INTEGER PRIMARY KEY,"
                + PublicNoticeContract.CityEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + "UNIQUE (" + PublicNoticeContract.CityEntry.COLUMN_NAME + ") ON CONFLICT IGNORE );";

        final String SQL_CREATE_NOTICE_TABLE = "CREATE TABLE " + PublicNoticeContract.NoticeEntry.TABLE_NAME + "("
                + PublicNoticeContract.NoticeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PublicNoticeContract.NoticeEntry.COLUMN_CITY_KEY + " INTEGER NOT NULL, "
                + PublicNoticeContract.NoticeEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + PublicNoticeContract.NoticeEntry.COLUMN_TIME + " TEXT NOT NULL, "
                + PublicNoticeContract.NoticeEntry.COLUMN_LOCATION + " TEXT NOT NULL, "
                + PublicNoticeContract.NoticeEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + PublicNoticeContract.NoticeEntry.COLUMN_FULL_NOTICE + " TEXT NOT NULL,"
                + " FOREIGN KEY ( " + PublicNoticeContract.NoticeEntry.COLUMN_CITY_KEY + ") REFERENCES "
                + PublicNoticeContract.CityEntry.TABLE_NAME + " (" + PublicNoticeContract.CityEntry._ID + ");";


        sqLiteDatabase.execSQL(SQL_CREATE_CITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_NOTICE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PublicNoticeContract.CityEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PublicNoticeContract.NoticeEntry.TABLE_NAME);
    }
}
