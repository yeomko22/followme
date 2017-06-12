package com.example.junny.followme_realbeta.interfaces;

import com.example.junny.followme_realbeta.response.ShowRouteRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by junny on 2017. 6. 10..
 */

public interface ShowRoute {
    //파라미터로 집어넣고 싶은 값들은 {}로 묶어준
    @GET("maps/api/directions/json")

    Call<ShowRouteRes> showRoute(
            // param 값으로 들어가는 것들이다
            @Query("key") String key,
            @Query("language") String language,
            @Query("mode") String mode,
            @Query("origin") String origin,
            @Query("destination") String destination
            );
}
