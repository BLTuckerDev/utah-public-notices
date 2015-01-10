package com.bltucker.utahpublicnotices.sync;

import android.content.ContentValues;

import java.util.List;

public final class UtahOpenGovApiProvider {

    private final String city;

    public UtahOpenGovApiProvider(String city) {
        this.city = city;
    }


    public List<ContentValues> getPublicNotices(){
        // create an object that will map it to a list of ContentValues
        return null;
    }
}
