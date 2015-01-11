package com.bltucker.utahpublicnotices.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bltucker.utahpublicnotices.R;
import com.bltucker.utahpublicnotices.data.PublicNoticeContract;
import com.bltucker.utahpublicnotices.utils.PreferenceFetcher;

import java.util.List;

public final class PublicNoticeSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL_SECONDS = 4 * 60 * 60;
    public static final int SYNC_FLEX_TIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;


    public PublicNoticeSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        PreferenceFetcher prefFetcher = new PreferenceFetcher();
        String city = prefFetcher.getCityPreference(getContext());
        long cityId = getCityId(city);

        UtahOpenGovApiProvider apiProvider = new UtahOpenGovApiProvider(city);
        List<ContentValues> notices = apiProvider.getPublicNotices();

        for(ContentValues values : notices){
            values.put(PublicNoticeContract.NoticeEntry.COLUMN_CITY_KEY, cityId);
        }

        //TODO: delete meeting notices that are in the past!

        ContentValues[] values = notices.toArray(new ContentValues[notices.size()]);
        getContext().getContentResolver().bulkInsert(PublicNoticeContract.NoticeEntry.CONTENT_URI, values);
    }


    private long getCityId(String city){

        Cursor cityCursor = getContext().getContentResolver().query(PublicNoticeContract.CityEntry.CONTENT_URI,
                null,
                PublicNoticeContract.CityEntry.COLUMN_NAME + " = ?",
                new String[]{city},
                null);

        if(cityCursor.moveToFirst()){
            //city doesn't exist need to add it.
            return addCity(city);
        } else {
            return cityCursor.getLong(cityCursor.getColumnIndex(PublicNoticeContract.CityEntry._ID));
        }
    }


    private long addCity(String city) {
        ContentValues cityValues = new ContentValues();
        cityValues.put(PublicNoticeContract.CityEntry.COLUMN_NAME, city);
        Uri insertUri = getContext().getContentResolver().insert(PublicNoticeContract.CityEntry.CONTENT_URI, cityValues);
        return ContentUris.parseId(insertUri);
    }


    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        configurePeriodicSync(context);
        ContentResolver.setSyncAutomatically(newAccount, PublicNoticeContract.CONTENT_AUTHORITY, true);
        syncImmediately(context);
    }


    public static void configurePeriodicSync(Context context) {
        Account account = getSyncAccount(context);
        String authority = PublicNoticeContract.CONTENT_AUTHORITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .setExtras(new Bundle())
                    .syncPeriodic(SYNC_INTERVAL_SECONDS, SYNC_FLEX_TIME_SECONDS)
                    .setSyncAdapter(account, authority).build();

            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), SYNC_INTERVAL_SECONDS);
        }
    }


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


    public static void syncImmediately(Context context) {
        Bundle syncRequest = new Bundle();
        syncRequest.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        syncRequest.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), PublicNoticeContract.CONTENT_AUTHORITY, syncRequest);
    }
}
