package com.bltucker.utahpublicnotices;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.bltucker.utahpublicnotices.data.PublicNoticeContract;
import com.bltucker.utahpublicnotices.data.PublicNoticeDbHelper;

import java.util.Map;
import java.util.Set;

public class DatabaseTest extends AndroidTestCase {


    public void createDbTest() throws Throwable{

        mContext.deleteDatabase(PublicNoticeDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new PublicNoticeDbHelper(this.mContext).getWritableDatabase();

        assertTrue(db.isOpen());
        db.close();
    }


    public void insertAndReadTest(){

        PublicNoticeDbHelper dbHelper = new PublicNoticeDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cityValues = this.createFakeCity();

        long cityId = -1;
        cityId = db.insert(PublicNoticeContract.CityEntry.TABLE_NAME, null, cityValues);

        assertTrue(cityId != -1);


        Cursor cityCursor = db.query(PublicNoticeContract.CityEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);


        cityCursor.moveToFirst();
        assertTrue(validateCursor(cityCursor, cityValues));
        cityCursor.close();


        ContentValues noticeValues = this.createFakeNotice(cityId);

        long noticeId = -1;
        noticeId = db.insert(PublicNoticeContract.NoticeEntry.TABLE_NAME, null, noticeValues);

        assertTrue(noticeId != -1);

        Cursor noticeCursor = db.query(PublicNoticeContract.NoticeEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        noticeCursor.moveToFirst();
        assertTrue(validateCursor(noticeCursor, noticeValues));

        dbHelper.close();

    }


    private boolean validateCursor(Cursor cursor, ContentValues values){

        Set<Map.Entry<String, Object>> valueSet = values.valueSet();
        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int index = cursor.getColumnIndex(columnName);

            if(index == -1){
                return false;
            }

            String expectedValue = entry.getValue().toString();

            if(!expectedValue.equals(cursor.getString(index))){
                return false;
            }
        }

        return true;
    }


    private ContentValues createFakeCity(){

        ContentValues mockValues = new ContentValues();
        mockValues.put(PublicNoticeContract.CityEntry.COLUMN_NAME, "Huntington");
        return mockValues;
    }


    private ContentValues createFakeNotice(long cityId){

        ContentValues noticeValues = new ContentValues();


        noticeValues.put(PublicNoticeContract.NoticeEntry.COLUMN_CITY_KEY, cityId);
        noticeValues.put(PublicNoticeContract.NoticeEntry.COLUMN_DATE, "20150109");
        noticeValues.put(PublicNoticeContract.NoticeEntry.COLUMN_TIME, "5:00");
        noticeValues.put(PublicNoticeContract.NoticeEntry.COLUMN_LOCATION, "The Town Hall");
        noticeValues.put(PublicNoticeContract.NoticeEntry.COLUMN_TIME, "Town Meeting");
        noticeValues.put(PublicNoticeContract.NoticeEntry.COLUMN_FULL_NOTICE, "/some/index.html");

        return noticeValues;
    }



}

