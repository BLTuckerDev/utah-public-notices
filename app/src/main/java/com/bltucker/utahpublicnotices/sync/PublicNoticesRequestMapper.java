package com.bltucker.utahpublicnotices.sync;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.bltucker.utahpublicnotices.data.PublicNoticeContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class PublicNoticesRequestMapper {

    private final String LOG_TAG = PublicNoticesRequestMapper.class.getSimpleName();


    public List<ContentValues> mapHtmlToContentValues(String apiResponse) {
        List<ContentValues> mappedValues = new ArrayList<>();
        Document document = Jsoup.parse(apiResponse);
        Elements listItems = document.select("li");

        for (Element element : listItems) {
            try {
                mappedValues.add(parseListElement(element));
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error parsing api response: " + ex);
            }
        }

        return mappedValues;
    }


    private ContentValues parseListElement(Element listElement) {
        Uri.Builder uriBuilder = new Uri.Builder();
        Uri noticesUri = uriBuilder.scheme("http").authority(UtahOpenGovApiProvider.API_AUTHORITY).build();

        String linkToFullNotice = listElement.select("a").first().attr("href");
        String noticeTitle = listElement.select("span.meetingTitle").first().text();

        Element dateSpan = listElement.select("span.meetingDate").first();

        String month = dateSpan.select("span.month").first().text();
        String day = dateSpan.select("span.day").first().text();
        String noticeTime = dateSpan.select("span.meetingLocation").first().text();

        String formattedDate = getFormattedDate(month, day);

        ContentValues values = new ContentValues();

        values.put(PublicNoticeContract.NoticeEntry.COLUMN_FULL_NOTICE, noticesUri.toString() + linkToFullNotice);
        values.put(PublicNoticeContract.NoticeEntry.COLUMN_TITLE, noticeTitle);
        values.put(PublicNoticeContract.NoticeEntry.COLUMN_DATE, formattedDate);
        values.put(PublicNoticeContract.NoticeEntry.COLUMN_TIME, noticeTime);

        return values;
    }


    public String getFormattedDate(String month, String day) {
        Calendar calendar = Calendar.getInstance();
        int calendarMonth = calendar.get(Calendar.MONTH) + 1;
        int calendarYear = calendar.get(Calendar.YEAR);
        if (calendarMonth < Integer.parseInt(month)) {
            //the meeting date is a smaller month than the current month
            //so it must be in the new year
            calendarYear++;
        }

        return String.format("%d%s%s", calendarYear, month, day);
    }
}
