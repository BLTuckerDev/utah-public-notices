package com.bltucker.utahpublicnotices.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PublicNoticeContract {

    public static final String CONTENT_AUTHORITY = "com.bltucker.utahpublicnotices";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_TO_CITY = "city";
    public static final String PATH_TO_NOTICE = "notice";

    public static final String DATE_FORMAT = "yyyyMMdd";


    public static final class CityEntry implements BaseColumns {

        public static final String TABLE_NAME = "city";

        public static final String COLUMN_NAME = "name";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TO_CITY).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TO_CITY;

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TO_CITY;


        public static Uri buildCityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    public static final class NoticeEntry implements BaseColumns {

        public static final String TABLE_NAME = "notice";

        public static final String COLUMN_CITY_KEY = "city_id";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_TIME = "time";

        public static final String COLUMN_LOCATION = "location";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_FULL_NOTICE = "full_notice";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TO_NOTICE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TO_NOTICE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TO_NOTICE;


        public static Uri buildNoticeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildNoticeCity(String city) {
            return CONTENT_URI.buildUpon().appendPath(city).build();
        }


        public static Uri buildNoticeCityWithStartDate(String city, String startDate) {
            return buildNoticeCity(city).buildUpon().appendQueryParameter(COLUMN_DATE, startDate).build();
        }


        public static String getCityFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }


        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATE);
        }
    }
}
