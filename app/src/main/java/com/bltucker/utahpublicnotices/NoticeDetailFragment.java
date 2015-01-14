package com.bltucker.utahpublicnotices;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;

public final class NoticeDetailFragment extends Fragment {

    public NoticeDetailFragment() {
        this.setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_fragment_details, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) item.getActionProvider();

        if(shareActionProvider != null){
            shareActionProvider.setShareIntent(getShareIntent());
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_map:
                showMeetingOnMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showMeetingOnMap() {
        Intent mapIntent = new Intent();
        mapIntent.setAction(Intent.ACTION_VIEW);
        //TODO replace with actual meeting location!
        String meetingAddress = "10 North Main, Cedar City,  84720";
        mapIntent.setData(Uri.parse(String.format("geo:0,0?q=%s", meetingAddress)));
        startActivity(mapIntent);
    }


    private Intent getShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //TODO: replace with actual meeting details for the share!
        shareIntent.putExtra(Intent.EXTRA_TEXT, "You Shared!");

        return shareIntent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        return rootView;
    }
}
