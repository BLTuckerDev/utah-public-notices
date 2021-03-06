package com.bltucker.utahpublicnotices;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bltucker.utahpublicnotices.data.NoticeCursor;
import com.bltucker.utahpublicnotices.data.PublicNoticeContract;
import com.bltucker.utahpublicnotices.utils.PreferenceFetcher;

import java.util.Date;

public final class NoticeListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int NOTICE_LOADER = 1;
    private static final String SELECTED_POSITION = "selectedPosition";

    public interface NoticeListFragmentCallBackListener{
        void onItemSelected(long noticeId);
    }

    private String currentCity;
    private NoticeListFragmentCallBackListener listener;
    private NoticeAdapter adapter;
    private ListView listView;
    private int selectedPosition = 0;


    public NoticeListFragment() {
        this.setRetainInstance(true);
        this.setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_fragment_notice_list, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings:
                startSettingsActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startSettingsActivity() {
        Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(settingsIntent);
    }


    @Override
    public void onResume() {
        super.onResume();

        if(currentCity != null && !currentCity.equals(new PreferenceFetcher().getCityPreference(getActivity()))){
            getLoaderManager().restartLoader(NOTICE_LOADER, null, this);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(NOTICE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onAttach(Activity activity) {
        this.listener = (NoticeListFragmentCallBackListener) activity;
        super.onAttach(activity);
    }


    @Override
    public void onDetach() {
        this.listener = null;
        super.onDetach();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (selectedPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_POSITION, selectedPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.fragment_notice_list_list_view);
        View emptyListView = inflater.inflate(R.layout.notice_list_empty_item, null, false);
        container.addView(emptyListView);
        listView.setEmptyView(emptyListView);
        attachAdapter();
        setupOnClickListener();

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_POSITION)){
            selectedPosition = savedInstanceState.getInt(SELECTED_POSITION);
        }


        return rootView;
    }

    private void setupOnClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                if(NoticeListFragment.this.listener != null){
                    Cursor cursor = adapter.getCursor();

                    if(cursor != null && cursor.moveToPosition(position)){
                        NoticeCursor nc = new NoticeCursor(cursor);
                        long noticeId = nc.getId();
                        NoticeListFragment.this.listener.onItemSelected(noticeId);
                    }
                }
            }
        });
    }

    private void attachAdapter(){
        adapter = new NoticeAdapter(getActivity(), null, 0);
        listView.setAdapter(adapter);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String today = PublicNoticeContract.getDbDateString(new Date());
        String sortOrder = PublicNoticeContract.NoticeEntry.COLUMN_DATE + " ASC";
        currentCity = new PreferenceFetcher().getCityPreference(getActivity());
        Uri noticeEntryUri = PublicNoticeContract.NoticeEntry.buildNoticeCityWithStartDate(currentCity, today);

        return new CursorLoader(getActivity(), noticeEntryUri, null, null,null, sortOrder);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        listView.smoothScrollToPosition(selectedPosition);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
