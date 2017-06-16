package com.example.junny.followme_realbeta;

import android.location.Location;

import com.example.junny.followme_realbeta.item.DBHelper;
import com.example.junny.followme_realbeta.item.TourAttraction;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by junny on 2017. 5. 23..
 */

public class staticValues {
    //구글 지도 변수들
    public static SupportMapFragment mapFragment;
    public static SupportMapFragment mini_mapFragment;
    public static GoogleApiClient mGoogleApiClient;

    //디비 헬퍼 객체
    public static DBHelper dbHelper;
    public static PolylineOptions cur_poly;
    public static LocationRequest mLocationRequest;

    //도보 경로 안내 자료 구조
    public static ArrayList<LatLng> walk_all_latlng;
    public static ArrayList<LatLng> walk_guide_latlng;
    public static ArrayList<String> walk_guide_text;

    //대중교통 경로 안내 자료 구조
    public static ArrayList<LatLng> transit_all_latlng;
    public static ArrayList<LatLng> transit_guide_latlng;
    public static ArrayList<String> transit_guide_text;

    //도착지점 변수들
    public static LatLng to_latlng;
    public static Double to_lat;
    public static Double to_long;
    public static String to_title;
    public static Location to_location;

    //현재 내 위치 변수들
    public static Location mLastLocation;
    public static Double mLastLong;
    public static Double mLastLat;
    public static LatLng mLastLatLong;
    public static String cur_address;

    //중간 지점, 거리, 각도
    public static LatLng middle_point;
    public static float distance;
    public static float last_bearing;

    //현재 길찾기 모드
    public static String static_cur_mode;
    public static int static_cur_point=0;

    //반경 10km 이내의 관광 명소들
    public static ArrayList<TourAttraction> tourAttractions;

    //api 키들
    public static final String gMapKey="AIzaSyC2KPG-dhy-IqT1iBhb6W4N3WC1od4qAN0";
    public static final String gRouteKey="AIzaSyBAy4VmTHyOCI1e_XvXwRlNn7pqwKU0t7o";
    public static final String daumMapKey="00b029ef729c6020abe2c0fe859eb77f";
    public static final String tMapKey="4004a4c7-8e67-3c17-88d9-9799c613ecc7";
}
