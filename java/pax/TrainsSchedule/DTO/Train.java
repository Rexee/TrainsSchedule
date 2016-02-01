package pax.TrainsSchedule.DTO;

import java.lang.*;

public class Train {
    //    Station from;
//    Station to;
    String departure;
    String arrival;
    int    duration;
    String departure_terminal;
    String departure_platform;
    String arrival_terminal;
    String arrival_platform;
    Thread thread;
    String stops;

    public Train(String arrival, String arrival_platform) {
        this.arrival = arrival;
        this.arrival_platform = arrival_platform;
    }

    public String getTime() {
        return arrival;
    }

    public String getName() {
        return arrival_platform;
    }
}
