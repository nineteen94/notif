package com.rn_episode1.Util;

import android.content.Context;

public class Constants {
    public static final long MILLI_IN_SECOND = 1000;
    public static final long MILLI_IN_MINUTE = MILLI_IN_SECOND * 60;
    public static final long MILLI_IN_HOUR = MILLI_IN_MINUTE * 60;
    public static final long MILLI_IN_DAY = MILLI_IN_HOUR * 24;
    public static final int HISTORICAL_DATA_DAYS = 30;
    public static final int DEFAULT_AVERAGE_USAGE = 10;

    public static final long ONE_MEGABYTE = 1024 * 1024;
    public static final int MIN_GAP_BW_LIVE_NOTIFICATIONS = 5;

    public static final String NOTIFICATION_SEGMENT_LIVE_FIRST = "L1";
    public static final String NOTIFICATION_SEGMENT_LIVE_SECOND = "L2";

    public static final String NOTIFICATION_SEGMENT_DAILY_GOOD = "DG";
    public static final String NOTIFICATION_SEGMENT_DAILY_BAD = "DB";

    public static final String NOTIFICATION_SEGMENT_WEEKLY_REPORT = "WR";
    public static final String NOTIFICATION_SEGMENT_WEEKLY_CUMULATIVE = "WC";

    public static final String TIME_MAP_START = "TIME_MAP_START";
    public static final String TIME_MAP_START_OF_THE_DAY = "TIME_MAP_START_OF_THE_DAY";
    public static final String TIME_MAP_START_OF_THE_WEEK = "TIME_MAP_START_OF_THE_WEEK";
    public static final String TIME_MAP_DAY_OF_WEEK = "TIME_MAP_DAY_OF_WEEK";

    public static final String IS_ONE_TIME_WORK = "IS_ONE_TIME_WORK";

    public static final String NOTIFICATION_MODEL_TITLE = "Title";
    public static final String NOTIFICATION_MODEL_MESSAGE = "Message";
    public static final String NOTIFICATION_MODEL_SEGMENT = "Segment";
    public static final String NOTIFICATION_MODEL_IMAGE_PATH = "ImagePath";

    public static final String NOTIFICATION_CHANNEL_NAME = "Continuous_Notifications_Channel";
    public static final String NOTIFICATION_CHANNEL_ID = "Channel_ID_Continuous";

    public static final String[] NOTIFICATION_MODEL_TITLE_DEFAULT_VALUE = {
            "Dont be a prisoner",
            "Breathe In Breathe Out",
            "Are you like POOR?",
            "Congrats!!! Wohoo!!",
            "Nice One #Respect",
            "Disrespect",
            "Report card out!"
    };
    public static final String[] NOTIFICATION_MODEL_MESSAGE_DEFAULT_VALUE = {
            "Someone said - Funny how we volunteer for some prisons blindly. {applicationUsage} minutes {applicationName} so far",
            "Come on Buddy. Self Control. {applicationUsage} minutes {applicationName}",
            "Seems like your whole family is using this phone. {applicationUsage} minutes already!",
            "I hope you were made the CEO of {applicationName}. Right? {applicationUsage} minutes used today. Wow!!",
            "Nigga you were good yesterday. {applicationUsage} minutes {applicationName}",
            "Nigga you fucked up yesterday. {applicationUsage} minutes {applicationName}",
            "A total {applicationUsage} minutes spent this week. {report}"
    };
    public static final String[] NOTIFICATION_MODEL_SEGMENT_DEFAULT_VALUE = {"L1", "L1", "L2", "L2", "DG", "DB", "WR"};

    public static final String[] reportHead = {"\uD83D\uDD18 \uD83D\uDD18 \uD83D\uDD18", "⬛ ⬛ ⬛"};
    public static final String[] reportTailGood = {"\uD83D\uDFE2"};
    public static final String[] reportTailBad = {"\uD83D\uDD3A"};


    public static final String APPMODEL_PACKAGENAME = "PACKAGENAME";
    public static final String APPMODEL_APPNAME = "APPNAME";
    public static final String APPMODEL_URI = "URI";
    public static final String APPMODEL_HISTORICALUSAGE = "HISTORICALUSAGE";
    public static final String APPMODEL_AVERAGEUSAGE = "AVERAGEUSAGE";
    public static final String APPMODEL_WEEKDAYSUSAGE = "WEEKDAYSUSAGE";
    public static final String APPMODEL_LASTWEEKUSAGE = "LASTWEEKUSAGE";
    public static final String APPMODEL_ISMONITORED = "ISMONITORED";
    public static final String APPMODEL_DAYOFTHEWEEK = "DAYOFTHEWEEK";
    public static final String APPMODEL_THISWEEKUSAGE = "THISWEEKUSAGE";


}