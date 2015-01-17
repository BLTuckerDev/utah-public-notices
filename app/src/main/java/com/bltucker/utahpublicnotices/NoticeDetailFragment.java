package com.bltucker.utahpublicnotices;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.bltucker.utahpublicnotices.data.NoticeCursor;
import com.bltucker.utahpublicnotices.data.PublicNoticeContract;
import com.bltucker.utahpublicnotices.utils.NoticeDateFormatHelper;

import java.util.Calendar;
import java.util.Date;

public final class NoticeDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = NoticeDetailFragment.class.getSimpleName();

    private static final int NOTICE_DETAIL_LOADER = 0;


    private long currentNoticeId;

    private NoticeCursor currentNoticeCursor;

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
            case R.id.action_calendar:
                addCalendarAppointment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void addCalendarAppointment(){

        if(null == currentNoticeCursor){
            return;
        }

        try{
            NoticeDateFormatHelper dateFormatHelper = new NoticeDateFormatHelper();

            Date noticeDate = dateFormatHelper.getDbDateFormatter().parse(currentNoticeCursor.getDate());
            Date noticeTime = dateFormatHelper.get24HourDateFormatter().parse(currentNoticeCursor.getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(noticeDate);
            calendar.set(Calendar.HOUR, noticeTime.getHours());
            calendar.set(Calendar.MINUTE, noticeTime.getMinutes());

            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, currentNoticeCursor.getTitle())
                    .putExtra(CalendarContract.Events.DESCRIPTION,  currentNoticeCursor.getFullNotice())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, currentNoticeCursor.getAddress())
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

            startActivity(intent);


        } catch(Exception ex){
            Toast.makeText(getActivity(), getActivity().getString(R.string.unable_add_to_calendar), Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, ex.toString());
        }
    }


    private void showMeetingOnMap() {

        if(null == currentNoticeCursor){
            return;
        }

        Intent mapIntent = new Intent();
        mapIntent.setAction(Intent.ACTION_VIEW);
        String meetingAddress = currentNoticeCursor.getAddress();
        mapIntent.setData(Uri.parse(String.format("geo:0,0?q=%s", meetingAddress)));
        startActivity(mapIntent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);
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
        } else {
            webView.loadUrl("http://utah.gov/pmn/index.html");
        }

    }


    private void setupShareProvider(){
        if(shareActionProvider != null && currentNoticeCursor != null){
            shareActionProvider.setShareIntent(getShareIntent());
        }
    }


    private Intent getShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentNoticeCursor.getTitle());
        String formattedMessage = String.format("%s %s \n %s", currentNoticeCursor.getTime(), currentNoticeCursor.getDate(), currentNoticeCursor.getAddress());
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

        if(data.moveToFirst()){
            currentNoticeCursor = new NoticeCursor(data);
            Log.d(LOG_TAG, "Detail fragment noticeId: " + currentNoticeCursor.getId());
            webView.loadUrl(currentNoticeCursor.getFullNotice());
            setupShareProvider();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
