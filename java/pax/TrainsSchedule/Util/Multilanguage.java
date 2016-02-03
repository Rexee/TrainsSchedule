package pax.TrainsSchedule.Util;

import android.app.Activity;
import android.content.res.Resources;

import java.util.Locale;

import pax.TrainsSchedule.R;

public class Multilanguage {

    public static String app_name;
    public static String favorites;
    public static String in;
    public static String min;
    public static String h;
    public static String express;
    public static String onTomorrow;
    public static String onToday;
    public static String enterStation;
    public static String stationNotFound;
    public static String on;

    public static String addedToFav;
    public static String nonstop;
    public static String lang;

    public static void Init(Activity activity) {
        Resources res = activity.getResources();

        lang = Locale.getDefault().getLanguage();

        app_name = res.getString(R.string.app_name);
        favorites = res.getString(R.string.favorites);
        in = res.getString(R.string.in);
        min = res.getString(R.string.min);
        h = res.getString(R.string.hr);
        express = res.getString(R.string.express);
        onToday = res.getString(R.string.onToday);
        onTomorrow = res.getString(R.string.onTomorrow);
        enterStation = res.getString(R.string.enterStation);
        stationNotFound = res.getString(R.string.stationNotFound);
        addedToFav = res.getString(R.string.addedToFav);
        nonstop = res.getString(R.string.nonStop);
        on = res.getString(R.string.on);
    }
}
