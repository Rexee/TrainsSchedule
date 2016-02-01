package pax.TrainsSchedule.Services;

import com.google.gson.Gson;

import pax.TrainsSchedule.API.API;
import pax.TrainsSchedule.API.SuggestGsonConverter;
import pax.TrainsSchedule.Base.BaseNetworkService;
import pax.TrainsSchedule.Util.Multilanguage;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.RestAdapter.LogLevel;

public class SuggestsRaspService extends BaseNetworkService {

    private static final String MAX_SUGGETS = "10";

    @Override
    protected Builder createRetrofitBuilder() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("format", "old");
                request.addQueryParam("field", "from");
                request.addQueryParam("limit", MAX_SUGGETS);
                request.addQueryParam("client_city", "213");
                request.addQueryParam("lang", Multilanguage.lang);
            }
        };

        return new RestAdapter.Builder()
                .setLogLevel(LogLevel.NONE)
                .setEndpoint(API.SUGGEST_BASE)
                .setConverter(new SuggestGsonConverter(new Gson()))
                .setRequestInterceptor(requestInterceptor);
    }
}
