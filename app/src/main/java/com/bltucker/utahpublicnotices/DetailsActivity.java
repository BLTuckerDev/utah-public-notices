package com.bltucker.utahpublicnotices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public final class DetailsActivity extends Activity {

    public static final String NOTICE_ID_EXTRA = "notice_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {

            NoticeDetailFragment detailFragment = new NoticeDetailFragment();
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putLong(DetailsActivity.NOTICE_ID_EXTRA, getIntent().getExtras().getLong(DetailsActivity.NOTICE_ID_EXTRA));
            detailFragment.setArguments(fragmentArgs);

            getFragmentManager().beginTransaction()
                    .add(R.id.container, detailFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }


    private void startSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
