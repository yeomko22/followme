package com.example.junny.followme_realbeta.interfaces;

import com.example.junny.followme_realbeta.response.ReverseGeoRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by junny on 2017. 6. 10..
 */

public interface ReverseGeo {
    //파라미터로 집어넣고 싶은 값들은 {}로 묶어준
    @GET("maps/api/geocode/json")
    // JSON Array를 리턴하므로 List<>가 되었다
    //제이썬 어레이가 되돌아온다, 하나의 제이썬은 객체로 정의, 필요한 정보들을 가져온다
    //인터페이스이므로 몸체가 없는 함수이다 패쓰에 정의된 것은 파라미터들
    //함수 호출시에 매개변수로 집어넣는 것들이 위에 정의한 URL 상의 빈 부분들에 들어간
    Call<ReverseGeoRes> reverseGeo(
            // param 값으로 들어가는 것들이다
            @Query("key") String key,
            @Query("language") String lang,
            @Query("latlng") String latlng);
}
