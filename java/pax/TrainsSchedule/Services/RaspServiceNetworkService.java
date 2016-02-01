package pax.TrainsSchedule.Services;

import pax.TrainsSchedule.API.API;
import pax.TrainsSchedule.API.Key;
import pax.TrainsSchedule.Base.BaseNetworkService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.RestAdapter.LogLevel;

public class RaspServiceNetworkService extends BaseNetworkService {

    @Override
    protected Builder createRetrofitBuilder() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("apikey", Key.KEY);
                request.addQueryParam("format", "json");
                request.addQueryParam("transport_types", "suburban");
                request.addQueryParam("lang", "ru");
            }
        };

        return new RestAdapter.Builder()
                .setLogLevel(LogLevel.NONE)
                .setEndpoint(API.RASP_BASE)
                .setConverter(getConverter())
                .setRequestInterceptor(requestInterceptor);
    }

}
