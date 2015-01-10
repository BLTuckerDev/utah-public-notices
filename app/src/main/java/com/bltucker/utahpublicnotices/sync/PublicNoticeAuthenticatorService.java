package com.bltucker.utahpublicnotices.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public final class PublicNoticeAuthenticatorService extends Service {

    private PublicNoticeAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new PublicNoticeAuthenticator(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
