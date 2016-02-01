package pax.TrainsSchedule.API;

import pax.TrainsSchedule.Model.Station.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface YandexSuggestAPI {
//    https://suggests.rasp.yandex.ru/suburban?format=old&field=from&query=%D0%BC%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&other_query=&other_point=&limit=11&lang=ru&client_city=213&national_version=ru&_=1446057073684
//    https://suggests.rasp.yandex.ru/suburban?format=old&field=from&query=%D0%BC%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&limit=3&lang=ru

    @GET("/suburban")
    List search(@Query("query") String query);

}
