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


    private long currentNoticeId;

    private WebView webView;

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
        webView = (WebView) rootView.findViewById(R.id.fragment_detail_web_view);

        getArgumentsFromBundle();
        return rootView;
    }


    private void getArgumentsFromBundle(){

        Bundle args = getArguments();

        if(args != null && args.containsKey(DetailsActivity.NOTICE_ID_EXTRA)){
            currentNoticeId = args.getLong(DetailsActivity.NOTICE_ID_EXTRA);
            getLoaderManager().initLoader(NOTICE_DETAIL_LOADER, null, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri noticeUriWithId = PublicNoticeContract.NoticeEntry.buildNoticeUri(currentNoticeId);

        return new CursorLoader(
                getActivity(),
                noticeUriWithId,
                null,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.moveToFirst()){
            NoticeCursor currentNoticeCusor = new NoticeCursor(data);
            webView.loadUrl(currentNoticeCusor.getFullNotice());
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
