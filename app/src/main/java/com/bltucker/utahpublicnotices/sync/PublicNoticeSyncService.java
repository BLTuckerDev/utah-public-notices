package com.bltucker.utahpublicnotices.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public final class PublicNoticeSyncService extends Service {

    private static final Object syncAdapterLock = new Object();
    private static PublicNoticeSyncAdapter syncAdapter = null;


    @Override
    public void onCreate() {
        synchronized (syncAdapterLock){
            if(syncAdapter == null){
                syncAdapter = new PublicNoticeSyncAdapter(getApplicationContext(), true);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
