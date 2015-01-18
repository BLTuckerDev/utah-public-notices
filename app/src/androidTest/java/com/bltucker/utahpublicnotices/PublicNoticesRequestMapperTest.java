package com.bltucker.utahpublicnotices;

import android.test.AndroidTestCase;

import com.bltucker.utahpublicnotices.sync.PublicNoticesRequestMapper;

public final class PublicNoticesRequestMapperTest extends AndroidTestCase{

    //<editor-fold desc="SampleResponse">
    private static final String sampleResponse = "<li class=\"meetingRow\">\n" +
            "    <a href=\"/pmn/sitemap/notice/248217.html\" target=\"_blank\">\n" +
            "        <span class=\"meetingTitle\">Legislative Breakfast Meeting</span>\n" +
            "        <span class=\"meetingDate\">\n" +
            "            <span class=\"month\">01</span>\n" +
            "            <span class=\"day\">12</span>\n" +
            "            <span class=\"meetingTime\">07:30 AM</span>\n" +
            "            <span class=\"meetingLocation\">239 South Main Street, Salt Lake City,  84111</span>\n" +
            "        </a>\n" +
            "    </li>\n" +
            "    <li class=\"meetingRow\">\n" +
            "        <a href=\"/pmn/sitemap/notice/248833.html\" target=\"_blank\">\n" +
            "            <span class=\"meetingTitle\">Meeting Agenda</span>\n" +
            "            <span class=\"meetingDate\">\n" +
            "                <span class=\"month\">01</span>\n" +
            "                <span class=\"day\">12</span>\n" +
            "                <span class=\"meetingTime\">16:00 PM</span>\n" +
            "                <span class=\"meetingLocation\">451 South State Street, Room 542, Salt Lake City,  84101</span>\n" +
            "            </a>\n" +
            "        </li>\n" +
            "        <li class=\"meetingRow\">\n" +
            "            <a href=\"/pmn/sitemap/notice/248651.html\" target=\"_blank\">\n" +
            "                <span class=\"meetingTitle\">Bicycle Advisory Committee Meeting</span>\n" +
            "                <span class=\"meetingDate\">\n" +
            "                    <span class=\"month\">01</span>\n" +
            "                    <span class=\"day\">12</span>\n" +
            "                    <span class=\"meetingTime\">17:00 PM</span>\n" +
            "                    <span class=\"meetingLocation\">349 South 200 East, Ste 150, Salt Lake City,  84111</span>\n" +
            "                </a>\n" +
            "            </li>\n" +
            "            <li class=\"meetingRow\">\n" +
            "                <a href=\"/pmn/sitemap/notice/248803.html\" target=\"_blank\">\n" +
            "                    <span class=\"meetingTitle\">Meeting of the Redevelopment Agency Board of Directors</span>\n" +
            "                    <span class=\"meetingDate\">\n" +
            "                        <span class=\"month\">01</span>\n" +
            "                        <span class=\"day\">13</span>\n" +
            "                        <span class=\"meetingTime\">14:00 PM</span>\n" +
            "                        <span class=\"meetingLocation\">451 S State Street, Salt Lake City,  84111</span>\n" +
            "                    </a>\n" +
            "                </li>\n" +
            "                <li class=\"meetingRow\">\n" +
            "                    <a href=\"/pmn/sitemap/notice/248489.html\" target=\"_blank\">\n" +
            "                        <span class=\"meetingTitle\">Council Meeting</span>\n" +
            "                        <span class=\"meetingDate\">\n" +
            "                            <span class=\"month\">01</span>\n" +
            "                            <span class=\"day\">13</span>\n" +
            "                            <span class=\"meetingTime\">15:30 PM</span>\n" +
            "                            <span class=\"meetingLocation\">451 South State Street, Salt Lake City,  84111</span>\n" +
            "                        </a>\n" +
            "                    </li>\n" +
            "                    <li class=\"meetingRow\">\n" +
            "                        <a href=\"/pmn/sitemap/notice/248801.html\" target=\"_blank\">\n" +
            "                            <span class=\"meetingTitle\">Study Session</span>\n" +
            "                            <span class=\"meetingDate\">\n" +
            "                                <span class=\"month\">01</span>\n" +
            "                                <span class=\"day\">13</span>\n" +
            "                                <span class=\"meetingTime\">16:30 PM</span>\n" +
            "                                <span class=\"meetingLocation\">7905 S. Redwood Road, West Jordan,  84084</span>\n" +
            "                            </a>\n" +
            "                        </li>\n" +
            "                        <li class=\"meetingRow\">\n" +
            "                            <a href=\"/pmn/sitemap/notice/248467.html\" target=\"_blank\">\n" +
            "                                <span class=\"meetingTitle\">BAB Agenda Jan 2015</span>\n" +
            "                                <span class=\"meetingDate\">\n" +
            "                                    <span class=\"month\">01</span>\n" +
            "                                    <span class=\"day\">14</span>\n" +
            "                                    <span class=\"meetingTime\">08:00 AM</span>\n" +
            "                                    <span class=\"meetingLocation\">451 S. State Street, Salt Lake City,  84111</span>\n" +
            "                                </a>\n" +
            "                            </li>\n" +
            "                            <li class=\"meetingRow\">\n" +
            "                                <a href=\"/pmn/sitemap/notice/246631.html\" target=\"_blank\">\n" +
            "                                    <span class=\"meetingTitle\">Agenda</span>\n" +
            "                                    <span class=\"meetingDate\">\n" +
            "                                        <span class=\"month\">01</span>\n" +
            "                                        <span class=\"day\">14</span>\n" +
            "                                        <span class=\"meetingTime\">13:00 PM</span>\n" +
            "                                        <span class=\"meetingLocation\">451 South State Street Room 126, Salt Lake CIty ,  84111</span>\n" +
            "                                    </a>\n" +
            "                                </li>\n" +
            "                                <li class=\"meetingRow\">\n" +
            "                                    <a href=\"/pmn/sitemap/notice/247039.html\" target=\"_blank\">\n" +
            "                                        <span class=\"meetingTitle\">January 14, 2015, Salt Lake City Planning Commission Meeting</span>\n" +
            "                                        <span class=\"meetingDate\">\n" +
            "                                            <span class=\"month\">01</span>\n" +
            "                                            <span class=\"day\">14</span>\n" +
            "                                            <span class=\"meetingTime\">17:30 PM</span>\n" +
            "                                            <span class=\"meetingLocation\">451 South State Street, Salt Lake City, UT,  84114</span>\n" +
            "                                        </a>\n" +
            "                                    </li>\n" +
            "                                    <li class=\"meetingRow\">\n" +
            "                                        <a href=\"/pmn/sitemap/notice/247111.html\" target=\"_blank\">\n" +
            "                                            <span class=\"meetingTitle\">AMENDED Historic Landmark Commission Agenda for January 15, 2015</span>\n" +
            "                                            <span class=\"meetingDate\">\n" +
            "                                                <span class=\"month\">01</span>\n" +
            "                                                <span class=\"day\">15</span>\n" +
            "                                                <span class=\"meetingTime\">17:30 PM</span>\n" +
            "                                                <span class=\"meetingLocation\">451 S State Street, Room 326, Salt Lake City,</span>\n" +
            "                                            </a>\n" +
            "                                        </li>";
    //</editor-fold>


    public void testMapper(){
        PublicNoticesRequestMapper mapper = new PublicNoticesRequestMapper();
        mapper.mapHtmlToContentValues(sampleResponse);
    }
}
