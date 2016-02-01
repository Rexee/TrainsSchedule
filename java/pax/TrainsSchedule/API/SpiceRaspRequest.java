package pax.TrainsSchedule.API;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import pax.TrainsSchedule.DTO.StopsList;

public class SpiceRaspRequest extends RetrofitSpiceRequest<RaspRequest, YandexRaspAPI> {

    private String from;
    private String to;
    private String date;
    public int    page;

    public SpiceRaspRequest(String from, String to, String date, int page) {
        super(RaspRequest.class, YandexRaspAPI.class);
        this.from = from;
        this.to = to;
        this.date = date;
        this.page = page;
    }

    @Override
    public RaspRequest loadDataFromNetwork() throws Exception {
        StopsList stopsList;
        if (page == 1) {
            stopsList = getService().search(from, to, date);
        } else {
            stopsList = getService().search(from, to, date, page);
        }
        return new RaspRequest(stopsList, page);
    }

    public String createCacheKey() {
        return "search." + from + " " + to + " " + date + " " + page;
    }


}


