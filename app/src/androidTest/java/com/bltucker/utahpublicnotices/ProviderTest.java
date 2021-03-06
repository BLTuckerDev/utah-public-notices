package com.bltucker.utahpublicnotices;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.bltucker.utahpublicnotices.data.PublicNoticeContract;

public class ProviderTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        clearProvider();
    }


    public void testInsertAndRead(){

        CursorValidator cursorValidator = new CursorValidator();
        MockValuesProvider valuesProvider = new MockValuesProvider();

        ContentValues fakeCity = valuesProvider.createFakeCity();
        Uri cityUri = mContext.getContentResolver().insert(PublicNoticeContract.CityEntry.CONTENT_URI, fakeCity);
        long cityId = ContentUris.parseId(cityUri);

        assertTrue(cityId != -1);

        Cursor cityCursor = mContext.getContentResolver().query(PublicNoticeContract.CityEntry.CONTENT_URI, null, null, null, null);
        cityCursor.moveToFirst();
        cursorValidator.validateCursor(cityCursor, fakeCity);

        ContentValues fakeNotice = valuesProvider.createFakeNotice(cityId);
        Uri noticeUri = mContext.getContentResolver().insert(PublicNoticeContract.NoticeEntry.CONTENT_URI, fakeNotice);
        long noticeId = ContentUris.parseId(noticeUri);

        assertTrue(noticeId != -1);


        Cursor noticeCursor = mContext.getContentResolver().query(PublicNoticeContract.NoticeEntry.CONTENT_URI, null, null,null, null);
        noticeCursor.moveToFirst();
        cursorValidator.validateCursor(noticeCursor, fakeNotice);


        noticeCursor = mContext.getContentResolver().query(PublicNoticeContract.NoticeEntry.buildNoticeUri(noticeId), null, null, null, null);

        assertTrue(noticeCursor.getCount() == 1);

        //get joined data for city and date
        //probably wont need this

        //get joined data for specific date.
        //probably wont need this




    }








    private void clearProvider(){
        mContext.getContentResolver().delete(PublicNoticeContract.NoticeEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(PublicNoticeContract.CityEntry.CONTENT_URI, null, null);

        Cursor noticeCursor = mContext.getContentResolver().query(PublicNoticeContract.NoticeEntry.CONTENT_URI, null, null, null, null);
        assertTrue(noticeCursor.getCount() == 0);
        noticeCursor.close();

        Cursor cityCursor = mContext.getContentResolver().query(PublicNoticeContract.CityEntry.CONTENT_URI, null, null, null, null);
        assertTrue(cityCursor.getCount() == 0);
        cityCursor.close();
    }
}
