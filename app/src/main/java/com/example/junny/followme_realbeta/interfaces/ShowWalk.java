package com.example.junny.followme_realbeta.interfaces;

import com.example.junny.followme_realbeta.response.ShowWalkRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by junny on 2017. 6. 10..
 */

public interface ShowWalk {
    //파라미터로 집어넣고 싶은 값들은 {}로 묶어준
    @GET("tmap/routes/pedestrian?version=1")

    Call<ShowWalkRes> showWalk(
            // param 값으로 들어가는 것들이다
            @Query("appKey") String appkey,
            @Query("startX") String startX,
            @Query("startY") String startY,
            @Query("endX") String endX,
            @Query("endY") String endY,
            @Query("startName") String startName,
            @Query("endName") String endName,
            @Query("reqCoordType") String reqCoordType,
            @Query("resCoordType") String resCoordType
    );
}
