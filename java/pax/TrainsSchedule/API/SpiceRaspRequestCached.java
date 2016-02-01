package pax.TrainsSchedule.API;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import pax.TrainsSchedule.DTO.StopsList;

public class SpiceRaspRequestCached extends RetrofitSpiceRequest<RaspRequest, YandexRaspAPI> {

    private String from;
    private String to;
    private int day;
    public int    page;

    public SpiceRaspRequestCached(String from, String to, int day, int page) {
        super(RaspRequest.class, YandexRaspAPI.class);
        this.from = from;
        this.to = to;
        this.day = day;
        this.page = page;
    }

    @Override
    public RaspRequest loadDataFromNetwork() throws Exception {
        return new RaspRequest(new StopsList(), page);
    }

  public Object createCacheKey() {
        return "searchDay." + from + " " + to + " " + day + " " + page;
    }
}


