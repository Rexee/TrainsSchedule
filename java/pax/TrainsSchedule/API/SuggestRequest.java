package pax.TrainsSchedule.API;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import pax.TrainsSchedule.Model.Station.List;

public class SuggestRequest extends RetrofitSpiceRequest<List, YandexSuggestAPI> {

    private String searchStr;

    public SuggestRequest(String searchStr) {
        super(List.class, YandexSuggestAPI.class);
        this.searchStr = searchStr;
    }

    @Override
    public List loadDataFromNetwork() throws Exception {
        return getService().search(searchStr);
    }

    public String createCacheKey() {
        return "suggest." + searchStr;
    }
}


