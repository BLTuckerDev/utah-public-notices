package com.bltucker.utahpublicnotices;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import com.bltucker.utahpublicnotices.sync.UtahOpenGovApiProvider;

import java.util.List;

public final class UtahOpenGovApiProviderTest extends AndroidTestCase {

    public void testApiCall(){

        UtahOpenGovApiProvider apiProvider = new UtahOpenGovApiProvider("Salt Lake City");
        List<ContentValues> notices = apiProvider.getPublicNotices();

        assertFalse(notices.isEmpty());
    }
}
