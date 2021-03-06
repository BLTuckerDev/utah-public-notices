package com.bltucker.utahpublicnotices.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public final class PublicNoticeProvider extends ContentProvider{

    private static final UriMatcher uriMatcher = buildUriMatcher();


    private static final int CITY_URI_CODE = 100;
    private static final int CITY_WITH_ID_URI_CODE = 101;

    private static final int NOTICE_URI_CODE = 200;
    private static final int NOTICE_URI_WITH_ID_CODE = 201;

    private static final int NOTICE_WITH_CITY_URI_CODE = 202;
    private static final int NOTICE_WITH_CITY_AND_DATE_URI_CODE = 203;

    private static final SQLiteQueryBuilder noticeByCityQueryBuilder;

    static {
        noticeByCityQueryBuilder = new SQLiteQueryBuilder();
        noticeByCityQueryBuilder.setTables(PublicNoticeContract.NoticeEntry.TABLE_NAME
            + " INNER JOIN " + PublicNoticeContract.CityEntry.TABLE_NAME
            + " ON " + PublicNoticeContract.NoticeEntry.TABLE_NAME
            + "." + PublicNoticeContract.NoticeEntry.COLUMN_CITY_KEY
            + " = " + PublicNoticeContract.CityEntry.TABLE_NAME
            + "." + PublicNoticeContract.CityEntry._ID);
    }

    private static final String citySettingSelection = PublicNoticeContract.CityEntry.TABLE_NAME
            + "." + PublicNoticeContract.CityEntry.COLUMN_NAME + " = ?";

    private static final String citySettingWithStartDate = PublicNoticeContract.CityEntry.TABLE_NAME
            + "." + PublicNoticeContract.CityEntry.COLUMN_NAME + " = ? AND " + PublicNoticeContract.NoticeEntry.COLUMN_DATE + " >= ?";

    private static final String citySettingAndDate = PublicNoticeContract.CityEntry.TABLE_NAME
            + "." + PublicNoticeContract.CityEntry.COLUMN_NAME + " = ? AND "
            + PublicNoticeContract.NoticeEntry.COLUMN_DATE + "= ? ";


    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PublicNoticeContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PublicNoticeContract.PATH_TO_CITY, CITY_URI_CODE);
        matcher.addURI(authority, PublicNoticeContract.PATH_TO_CITY + "/#", CITY_WITH_ID_URI_CODE);

        matcher.addURI(authority, PublicNoticeContract.PATH_TO_NOTICE, NOTICE_URI_CODE);
        matcher.addURI(authority, PublicNoticeContract.PATH_TO_NOTICE + "/#", NOTICE_URI_WITH_ID_CODE);
        matcher.addURI(authority, PublicNoticeContract.PATH_TO_NOTICE + "/*", NOTICE_WITH_CITY_URI_CODE);
        matcher.addURI(authority, PublicNoticeContract.PATH_TO_NOTICE + "/*/*", NOTICE_WITH_CITY_AND_DATE_URI_CODE);

        return matcher;
    }

    private PublicNoticeDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new PublicNoticeDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch(uriMatcher.match(uri)){

            case NOTICE_URI_CODE:

                retCursor = dbHelper.getReadableDatabase().query(
                        PublicNoticeContract.NoticeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case NOTICE_URI_WITH_ID_CODE:

                retCursor = dbHelper.getReadableDatabase().query(
                        PublicNoticeContract.NoticeEntry.TABLE_NAME,
                        projection,
                        PublicNoticeContract.NoticeEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder);
                break;


            case NOTICE_WITH_CITY_AND_DATE_URI_CODE:
                retCursor = getNoticesByCityAndDate(uri, projection, sortOrder);
                break;


            case NOTICE_WITH_CITY_URI_CODE:
                retCursor = getNoticesByCity(uri, projection, sortOrder);
                break;

            case CITY_URI_CODE:
                retCursor = dbHelper.getReadableDatabase().query(
                    PublicNoticeContract.CityEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;

            case CITY_WITH_ID_URI_CODE:

                retCursor = dbHelper.getReadableDatabase().query(
                        PublicNoticeContract.CityEntry.TABLE_NAME,
                        projection,
                        PublicNoticeContract.CityEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder);


                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public String getType(Uri uri) {
        switch(uriMatcher.match(uri)){
            case NOTICE_WITH_CITY_AND_DATE_URI_CODE:
            case NOTICE_WITH_CITY_URI_CODE:
            case NOTICE_URI_CODE:
                return PublicNoticeContract.NoticeEntry.CONTENT_TYPE;

            case NOTICE_URI_WITH_ID_CODE:
                return PublicNoticeContract.NoticeEntry.CONTENT_ITEM_TYPE;

            case CITY_URI_CODE:
                return PublicNoticeContract.CityEntry.CONTENT_TYPE;

            case CITY_WITH_ID_URI_CODE:
                return PublicNoticeContract.CityEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int uriMatchCode = uriMatcher.match(uri);
        Uri returnUri = null;
        long id = 0;

        switch(uriMatchCode){
            case NOTICE_URI_CODE:

                id = db.insert(PublicNoticeContract.NoticeEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = PublicNoticeContract.NoticeEntry.buildNoticeUri(id);
                }

                break;

            case CITY_URI_CODE:

                id = db.insert(PublicNoticeContract.CityEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = PublicNoticeContract.CityEntry.buildCityUri(id);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }


        if(null == returnUri){
            throw new SQLException("Failed to insert row with uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch(match){
            case NOTICE_URI_CODE:
                rowsDeleted = db.delete(PublicNoticeContract.NoticeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case CITY_URI_CODE:
                rowsDeleted = db.delete(PublicNoticeContract.CityEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }

        if(null == selection || rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match){
            case NOTICE_URI_CODE:
                rowsUpdated = db.update(PublicNoticeContract.NoticeEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;


            case CITY_URI_CODE:

                rowsUpdated = db.update(PublicNoticeContract.CityEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = uriMatcher.match(uri);
        int insertCount = 0;
        switch(match){
            case NOTICE_URI_CODE:
                insertCount = this.bulkInsertNotices(values);
                break;

            default:
                insertCount = super.bulkInsert(uri, values);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return insertCount;
    }


    private int bulkInsertNotices(ContentValues[] notices){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;

        try{
            for(ContentValues noticeValues : notices){
                long _id = db.insert(PublicNoticeContract.NoticeEntry.TABLE_NAME, null, noticeValues);
                if(_id != -1){
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return returnCount;
    }


    private Cursor getNoticesByCity(Uri uri, String[] projections, String sortOrder){
        String citySetting = PublicNoticeContract.NoticeEntry.getCityFromUri(uri);
        String startDate = PublicNoticeContract.NoticeEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if(null == startDate){
            selection = citySettingSelection;
            selectionArgs = new String[]{citySetting};
        } else {
            selection = citySettingWithStartDate;
            selectionArgs = new String[]{citySetting, startDate};
        }

        return noticeByCityQueryBuilder.query(dbHelper.getReadableDatabase(),
                projections,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    private Cursor getNoticesByCityAndDate(Uri uri, String[] projection, String sortOrder){
        String citySetting = PublicNoticeContract.NoticeEntry.getCityFromUri(uri);
        String date = PublicNoticeContract.NoticeEntry.getDateFromUri(uri);

        return noticeByCityQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                citySettingAndDate,
                new String[] {citySetting, date},
                null,
                null,
                sortOrder);
    }
}
