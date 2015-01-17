package com.bltucker.utahpublicnotices;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.bltucker.utahpublicnotices.data.NoticeCursor;
import com.bltucker.utahpublicnotices.data.PublicNoticeContract;
import com.bltucker.utahpublicnotices.sync.UtahOpenGovApiProvider;

public final class NoticeDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = NoticeDetailFragment.class.getSimpleName();

    private static final int NOTICE_DETAIL_LOADER = 0;

    private boolean cursorLoaded = false;

    private long currentNoticeId;

    private NoticeCursor currentNoticeCusor;

    private WebView webView;

    private ShareActionProvider shareActionProvider;

    public NoticeDetailFragment() {
        this.setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_details, menu);
        shareActionProvider = (ShareActionProvider) menu.findItem(R.id.action_share).getActionProvider();
        setupShareProvider();
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

        if(!cursorLoaded){
            return;
        }

        Intent mapIntent = new Intent();
        mapIntent.setAction(Intent.ACTION_VIEW);
        String meetingAddress = currentNoticeCusor.getAddress();
        mapIntent.setData(Uri.parse(String.format("geo:0,0?q=%s", meetingAddress)));
        startActivity(mapIntent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        webView = (WebView) rootView.findViewById(R.id.fragment_detail_web_view);
        getArgumentsFromBundle();

        return rootView;
    }


    @Override
    public void onDestroy() {
        if(currentNoticeCusor != null){
            currentNoticeCusor.close();
        }
        super.onDestroy();
    }


    private void getArgumentsFromBundle(){

        Bundle args = getArguments();

        if(args != null && args.containsKey(DetailsActivity.NOTICE_ID_EXTRA)){
            currentNoticeId = args.getLong(DetailsActivity.NOTICE_ID_EXTRA);
            getLoaderManager().initLoader(NOTICE_DETAIL_LOADER, null, this);
        }
    }


    private void setupShareProvider(){
        if(shareActionProvider != null && cursorLoaded){
            shareActionProvider.setShareIntent(getShareIntent());
        }
    }


    private Intent getShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentNoticeCusor.getTitle());
        String formattedMessage = String.format("%s %s \n %s", currentNoticeCusor.getTime(), currentNoticeCusor.getDate(), currentNoticeCusor.getAddress());
        shareIntent.putExtra(Intent.EXTRA_TEXT, formattedMessage);

        return shareIntent;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri noticeUriWithId = PublicNoticeContract.NoticeEntry.buildNoticeUri(currentNoticeId);
        return new CursorLoader(getActivity(),noticeUriWithId, null, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorLoaded = true;
        if(data.moveToFirst()){
            currentNoticeCusor = new NoticeCursor(data);
            webView.loadUrl(currentNoticeCusor.getFullNotice());
            setupShareProvider();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorLoaded = false;
        currentNoticeCusor = null;
    }
}
