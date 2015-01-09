package com.bltucker.utahpublicnotices;

import android.content.ContentValues;

import com.bltucker.utahpublicnotices.data.PublicNoticeContract;

public final class MockValuesProvider {

    public ContentValues createFakeCity(){

        ContentValues mockValues = new ContentValues();
        mockValues.put(PublicNoticeContract.CityEntry.COLUMN_NAME, "Huntington");
        return mockValues;
    }


    public ContentValues createFakeNotice(long cityId){

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
