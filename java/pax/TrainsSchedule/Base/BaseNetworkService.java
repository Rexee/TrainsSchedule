package pax.TrainsSchedule.Base;

import android.app.Application;

import com.google.gson.Gson;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit.GsonRetrofitObjectPersisterFactory;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pax.TrainsSchedule.API.YandexRaspAPI;
import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

public abstract class BaseNetworkService extends SpiceService {

    private Map<Class<?>, Object> retrofitInterfaceToServiceMap = new HashMap<Class<?>, Object>();
    private RestAdapter         restAdapter;
    protected List<Class<?>> retrofitInterfaceList = new ArrayList<Class<?>>();
    private Converter mConverter;

    @Override
    public void onCreate() {
        super.onCreate();

//        Ln.getConfig().setLoggingLevel(Log.ERROR);

        RestAdapter.Builder builder = createRetrofitBuilder();
        restAdapter = builder.build();
        addRetrofitInterface(YandexRaspAPI.class);
    }


    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new GsonRetrofitObjectPersisterFactory(application, getConverter(), getCacheFolder()));
        return cacheManager;
    }

    public File getCacheFolder() {
        return null;
    }

    protected abstract RestAdapter.Builder createRetrofitBuilder();

    protected Converter createConverter() {
        return new GsonConverter(new Gson());
    }

    protected final Converter getConverter() {
        if (mConverter == null) {
            mConverter = createConverter();
        }

        return mConverter;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getRetrofitService(Class<T> serviceClass) {
        T service = (T) retrofitInterfaceToServiceMap.get(serviceClass);
        if (service == null) {
            service = restAdapter.create(serviceClass);
            retrofitInterfaceToServiceMap.put(serviceClass, service);
        }
        return service;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void addRequest(CachedSpiceRequest<?> request, Set<RequestListener<?>> listRequestListener) {
        if (request.getSpiceRequest() instanceof RetrofitSpiceRequest) {
            RetrofitSpiceRequest retrofitSpiceRequest = (RetrofitSpiceRequest) request.getSpiceRequest();
            retrofitSpiceRequest.setService(getRetrofitService(retrofitSpiceRequest.getRetrofitedInterfaceClass()));
        }
        super.addRequest(request, listRequestListener);
    }

    public final List<Class<?>> getRetrofitInterfaceList() {
        return retrofitInterfaceList;
    }

    protected void addRetrofitInterface(Class<?> serviceClass) {
        retrofitInterfaceList.add(serviceClass);
    }

}
