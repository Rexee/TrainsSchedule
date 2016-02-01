package pax.TrainsSchedule.Model;

import pax.TrainsSchedule.Util.Util;

public class SearchParams {
    public String fromCode;
    public String fromName;

    public String toCode;
    public String toName;

    public long date;
    public int dayWeek;

    public SearchParams(long date, String fromCode, String fromName, String toCode, String toName) {
        this.date = date;
        this.fromCode = fromCode;
        this.fromName = fromName;
        this.toCode = toCode;
        this.toName = toName;
        this.dayWeek = Util.getDayOfWeek(date);
    }
}
