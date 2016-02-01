package pax.TrainsSchedule.Model;

public class Favorite {
    public String fromCode;
    public String fromName;

    public String toCode;
    public String toName;

    public Favorite(String fromCode, String fromName, String toCode, String toName) {
        this.fromCode = fromCode;
        this.fromName = fromName;
        this.toCode = toCode;
        this.toName = toName;
    }
}
