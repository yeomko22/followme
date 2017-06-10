package com.example.junny.followme_realbeta.interfaces;

import com.example.junny.followme_realbeta.response.SearchRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by junny on 2017. 6. 10..
 */

public interface Search {
    //파라미터로 집어넣고 싶은 값들은 {}로 묶어준
    @GET("local/v1/search/keyword.json")

    Call<SearchRes> search(
            // param 값으로 들어가는 것들이다
            @Query("apikey") String apikey,
            @Query("sort") String sort,
            @Query("query") String query);
}
