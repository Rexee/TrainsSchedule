package pax.TrainsSchedule.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pax.TrainsSchedule.DTO.Station;
import pax.TrainsSchedule.Util.Multilanguage;

public class Stop {

    public int     dayOfWeek;
    public String  fromStation;
    public String  toStation;
    public int     arrival;      //in minutes
    public int     departure;    //in minutes
    public String  duration;
    public String  route;
    public String  exclude;
    public boolean express;

    public void fill(Station from, Station to, int dayOfWeek, String departure, String arrival, int duration, String route, String exclude, String express) {
        this.fromStation = from.code;
        this.toStation = to.code;
        this.dayOfWeek = dayOfWeek;
        this.departure = getTimeInt(departure);
        this.arrival = getTimeInt(arrival);

        if (exclude == null || exclude.isEmpty() || exclude.equalsIgnoreCase("везде")) {
            this.exclude = "";
        }
        else if (exclude.equalsIgnoreCase("без остановок")){
            this.exclude = Multilanguage.nonstop;
        }
        else
            this.exclude = exclude.substring(0, 1).toUpperCase() + exclude.substring(1);

        if (express == null) {
            this.express = false;
            this.route = route;
        } else {
            this.express = true;
            this.route = route + " (" + Multilanguage.express + ")";
        }

        this.duration = durationStr(duration);
    }

    private int getTimeInt(String inStr) {
        //2016-01-25 01:07:00 -> 1*60+7 = 67

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(inStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
    }

    private static String durationStr(int seconds) {
        long hr = TimeUnit.SECONDS.toHours(seconds);
        long sec = seconds - hr * 3600;
        long min = TimeUnit.SECONDS.toMinutes(sec);

        if (hr == 0) return "" + min + " " + Multilanguage.min;
        return "" + hr + " " + Multilanguage.h + " " + min + " " + Multilanguage.min;
    }

    public static class List {

        private ArrayList<Stop> stops;

        public List(ArrayList<Stop> stops) {
            if (stops == null) {
                stops = new ArrayList<>();
            }

            this.stops = stops;
        }

        public ArrayList<Stop> getStops() {
            return stops;
        }

        public int size() {
            return stops.size();
        }

        public Stop get(int position) {
            return stops.get(position);
        }

        public void newStops(List stopsList) {
            stops.clear();
            for (Stop stop : stopsList.stops) {
                stops.add(stop);
            }
        }

        public void addStops(List stopsList) {
            for (Stop stop : stopsList.stops) {
                stops.add(stop);
            }
        }
    }
}
