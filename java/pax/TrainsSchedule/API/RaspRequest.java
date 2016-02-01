package pax.TrainsSchedule.API;

import pax.TrainsSchedule.DTO.StopsList;

public class RaspRequest {
    public StopsList stopsList;
    public int       page;

    public RaspRequest() {

    }

    public RaspRequest(StopsList stopsList, int page) {
        this.stopsList = stopsList;
        this.page = page;
    }


}
