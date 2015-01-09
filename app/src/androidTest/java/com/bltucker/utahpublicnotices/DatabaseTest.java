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
        MockValuesProvider valuesProvider = new MockValuesProvider();
        CursorValidator cursorValidator = new CursorValidator();

        ContentValues cityValues = valuesProvider.createFakeCity();

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
        assertTrue(cursorValidator.validateCursor(cityCursor, cityValues));
        cityCursor.close();


        ContentValues noticeValues = valuesProvider.createFakeNotice(cityId);

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
        assertTrue(cursorValidator.validateCursor(noticeCursor, noticeValues));

        dbHelper.close();

    }


}

