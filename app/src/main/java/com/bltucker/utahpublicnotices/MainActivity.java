package com.bltucker.utahpublicnotices;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.bltucker.utahpublicnotices.sync.PublicNoticeSyncAdapter;
import com.bltucker.utahpublicnotices.utils.PreferenceFetcher;

public final class MainActivity extends Activity
        implements NoticeListFragment.NoticeListFragmentCallBackListener,InitialSetup.InitialSetupFragmentListener{

    private boolean isInTabletMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeMasterContainer(savedInstanceState);
        initializeDetailContainer(savedInstanceState);
    }


    private void initializeMasterContainer(Bundle savedInstanceState){

        if(savedInstanceState != null){
            return;
        }

        String currentCity = new PreferenceFetcher().getCityPreference(this);
        if(currentCity.equals(getString(R.string.city_setting_pref_default))){
            getFragmentManager().beginTransaction().replace(R.id.fragment_master_container, InitialSetup.newInstance()).commit();
        } else {
            getFragmentManager().beginTransaction().replace(R.id.fragment_master_container, new NoticeListFragment()).commit();
        }
    }


    private void initializeDetailContainer(Bundle savedInstanceState){
        if(findViewById(R.id.notice_detail_container) != null){
            isInTabletMode = true;
            if(savedInstanceState == null){
                getFragmentManager().beginTransaction().replace(R.id.notice_detail_container, new NoticeDetailFragment()).commit();
            }
        }
    }


    @Override
    public void onItemSelected(long noticeId) {

        if(isInTabletMode){
            Bundle args = new Bundle();
            args.putLong(DetailsActivity.NOTICE_ID_EXTRA, noticeId);
            NoticeDetailFragment df = new NoticeDetailFragment();
            df.setArguments(args);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.notice_detail_container, df);
            ft.commit();
        } else {
            Intent detailsIntent = new Intent(this, DetailsActivity.class);
            detailsIntent.putExtra(DetailsActivity.NOTICE_ID_EXTRA, noticeId);
            startActivity(detailsIntent);
        }
    }


    @Override
    public void onInitialSetupCompleted() {
        PublicNoticeSyncAdapter.syncImmediately(this);
        getFragmentManager().beginTransaction().replace(R.id.fragment_master_container, new NoticeListFragment()).commit();
    }
}
