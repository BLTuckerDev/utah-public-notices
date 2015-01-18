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

import java.util.Calendar;

public final class NoticeDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = NoticeDetailFragment.class.getSimpleName();

    private static final int NOTICE_DETAIL_LOADER = 0;


    private long currentNoticeId;

    private NoticeCursor currentNoticeCursor;

    private WebView webView;

    private ShareActionProvider shareActionProvider;

    private MenuItem shareMenuItem;
    private MenuItem mapMenuItem;
    private MenuItem calendarMenuItem;
    private MenuItem notificationMenuItem;

    private boolean menuCreated = false;

    public NoticeDetailFragment() {
        this.setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_details, menu);

        shareMenuItem = menu.findItem(R.id.action_share);
        mapMenuItem = menu.findItem(R.id.action_map);
        calendarMenuItem = menu.findItem(R.id.action_calendar);
        notificationMenuItem = menu.findItem(R.id.action_notify);

        shareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();
        setupShareProvider();
        menuCreated = true;

        if(null == currentNoticeCursor){
            disableMenuItems();
        }
    }


    private void disableMenuItems(){

        if(!menuCreated){
            return;
        }

        shareMenuItem.setVisible(false);
        mapMenuItem.setVisible(false);
        calendarMenuItem.setVisible(false);
        notificationMenuItem.setVisible(false);
    }


    private void enableMenuItems(){

        if(!menuCreated){
            return;
        }

        shareMenuItem.setVisible(true);
        mapMenuItem.setVisible(true);
        calendarMenuItem.setVisible(true);
        notificationMenuItem.setVisible(true);
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

        Intent alarmIntent = new Intent(getActivity(), NotificationAlarmReceiver.class);
        alarmIntent.putExtra(NotificationAlarmReceiver.NOTIFICATION_NOTICE_ID_EXTRA, currentNoticeCursor.getId());

        try{
            Calendar noticeCalendar = currentNoticeCursor.getCalendarDateTime();
            noticeCalendar.add(Calendar.HOUR, -1);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);

            alarmManager.set(AlarmManager.RTC, noticeCalendar.getTimeInMillis(), pendingAlarmIntent);
            Toast.makeText(getActivity(), getActivity().getString(R.string.notification_set), Toast.LENGTH_SHORT).show();

        } catch(Exception ex){
            Log.d(LOG_TAG, ex.toString());
        }
    }


    private void addCalendarAppointment(){
        CalendarEventCreator eventCreator = new CalendarEventCreator();
        eventCreator.createCalendarEvent(getActivity(), currentNoticeCursor);
    }


    private void showMeetingOnMap() {

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
            enableMenuItems();
            Log.d(LOG_TAG, "Detail fragment noticeId: " + currentNoticeCursor.getId());
            webView.loadUrl(currentNoticeCursor.getFullNotice());
            setupShareProvider();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
