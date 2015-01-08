package com.bltucker.utahpublicnotices.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public final class NoticeProvider extends ContentProvider{

    private static final UriMatcher uriMatcher = buildUriMatcher();


    private static final int CITY_URI_CODE = 100;
    private static final int CITY_WITH_ID_URI_CODE = 101;

    private static final int NOTICE_URI_CODE = 200;

    private static final int NOTICE_WITH_CITY_URI_CODE = 202;
    private static final int NOTICE_WITH_CITY_AND_DATE_URI_CODE = 203;



    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PublicNoticeContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PublicNoticeContract.PATH_TO_CITY, CITY_URI_CODE);
        matcher.addURI(authority, PublicNoticeContract.PATH_TO_CITY + "/#", CITY_WITH_ID_URI_CODE);

        matcher.addURI(authority, PublicNoticeContract.PATH_TO_NOTICE, NOTICE_URI_CODE);
        matcher.addURI(authority, PublicNoticeContract.PATH_TO_NOTICE + "/*", NOTICE_WITH_CITY_URI_CODE);
        matcher.addURI(authority, PublicNoticeContract.PATH_TO_NOTICE + "/*/*", NOTICE_WITH_CITY_AND_DATE_URI_CODE;

        return matcher;
    }

    private PublicNoticeDbHelper dbHelper;


    @Override
    public boolean onCreate() {
        return false;
    }


    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }


    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
