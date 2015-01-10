package com.bltucker.utahpublicnotices.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.bltucker.utahpublicnotices.R;
import com.bltucker.utahpublicnotices.data.PublicNoticeContract;
import com.bltucker.utahpublicnotices.data.PublicNoticeProvider;

public final class PublicNoticeSyncAdapter extends AbstractThreadedSyncAdapter{

    public static final int SYNC_INTERVAL_SECONDS = 4 * 60 * 60;
    public static final int SYNC_FLEX_TIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;


    //TODO: Move this to a notification class responsible for notifications
    private static final int MEETING_NOTIFICATION_ID = 101;


    public PublicNoticeSyncAdapter(Context context, boolean autoInitialize){
        super(context,autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        //get the user's city preference

        //create an object that will make the request and get the data
        // create an object that will map it to a list of ContentValues

        //add the content values that are returned into our content resolver.

    }


    public static Account getSyncAccount(Context context){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if(null == accountManager.getPassword(newAccount)){

            if(!accountManager.addAccountExplicitly(newAccount, "", null)){
                return null;
            }

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context){
        configurePeriodicSync(context);
        ContentResolver.setSyncAutomatically(newAccount, PublicNoticeContract.CONTENT_AUTHORITY, true);
        syncImmediately(context);
    }


    public static void configurePeriodicSync(Context context){
        Account account = getSyncAccount(context);
        String authority = PublicNoticeContract.CONTENT_AUTHORITY;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            SyncRequest request = new SyncRequest.Builder()
                    .setExtras(new Bundle())
                    .syncPeriodic(SYNC_INTERVAL_SECONDS, SYNC_FLEX_TIME_SECONDS)
                    .setSyncAdapter(account, authority).build();

            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), SYNC_INTERVAL_SECONDS);
        }
    }


    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }

    public static void syncImmediately(Context context){
        Bundle syncRequest = new Bundle();
        syncRequest.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        syncRequest.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), PublicNoticeContract.CONTENT_AUTHORITY, syncRequest);
    }
}
