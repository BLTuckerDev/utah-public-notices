package com.bltucker.utahpublicnotices.sync;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class UtahOpenGovApiProvider {

    private final String LOG_TAG = UtahOpenGovApiProvider.class.getSimpleName();

    public static final String API_AUTHORITY = "utah.gov";
    public static final String LOCATION_AWARE_PATH = "locationaware";
    public static final String GET_MEETINGS_PATH = "getMeetings.html";
    public static final String CITY_NAME_QUERY_PARAMETER = "cityName";

    private final String city;

    public UtahOpenGovApiProvider(String city) {
        this.city = city;
    }


    public List<ContentValues> getPublicNotices(){
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        Uri.Builder uriBuilder = new Uri.Builder();
        Uri noticesUri = uriBuilder.scheme("http")
                .authority(API_AUTHORITY)
                .appendPath(LOCATION_AWARE_PATH)
                .appendPath(GET_MEETINGS_PATH)
                .appendQueryParameter(CITY_NAME_QUERY_PARAMETER, city).build();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{

            URL url = new URL(noticesUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if(null == inStream){
                return contentValues;
            }

            reader = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line);
            }

            if(buffer.length() == 0){
                return contentValues;
            }

            PublicNoticesRequestMapper mapper = new PublicNoticesRequestMapper();
            contentValues.addAll(mapper.mapHtmlToContentValues(buffer.toString()));
        } catch(Exception ex){
            Log.e(LOG_TAG, "Error getting response from the api: " + ex.toString());
        } finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

        // create an object that will map it to a list of ContentValues
        return contentValues;
    }
}
