package pax.TrainsSchedule.API;

import pax.TrainsSchedule.DTO.StopsList;
import retrofit.http.GET;
import retrofit.http.Query;

public interface YandexRaspAPI {
//    https://api.rasp.yandex.net/v1.0/search/?apikey=cb12e7a7-4f68-437c-9733-c8e39bc98046&from=s9601627&to=s9602231&date=2015-10-18

    @GET("/search/")
    StopsList search(@Query("from") String from, @Query("to") String to, @Query("date") String date, @Query("page") int page);

    @GET("/search/")
    StopsList search(@Query("from") String from, @Query("to") String to, @Query("date") String date);
}