package com.bltucker.utahpublicnotices;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
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
import com.bltucker.utahpublicnotices.notifications.NotificationAlarmReceiver;
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
            case R.id.action_notify:
                createNoticeNotification();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createNoticeNotification(){
//TODO toasts !!!
        if(null == currentNoticeCursor){
            return;
        }


        Intent alarmIntent = new Intent(getActivity(), NotificationAlarmReceiver.class);
        alarmIntent.putExtra(NotificationAlarmReceiver.NOTIFICATION_NOTICE_ID_EXTRA, currentNoticeCursor.getId());

        try{
            Calendar noticeCalendar = currentNoticeCursor.getCalendarDateTime();
            noticeCalendar.add(Calendar.HOUR, -1);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);

            alarmManager.set(AlarmManager.RTC, noticeCalendar.getTimeInMillis(), pendingAlarmIntent);

        } catch(Exception ex){
            Log.d(LOG_TAG, ex.toString());
        }
    }


    private void addCalendarAppointment(){

        if(null == currentNoticeCursor){
            return;
        }
//TODO convert to object in charge of making appointments
        try{
            Calendar noticeCalendar = currentNoticeCursor.getCalendarDateTime();

            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, noticeCalendar.getTimeInMillis())
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
