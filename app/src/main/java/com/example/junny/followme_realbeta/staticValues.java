package com.example.junny.followme_realbeta;

import android.location.Location;

import com.example.junny.followme_realbeta.item.DBHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skp.Tmap.TMapData;

import java.util.ArrayList;

/**
 * Created by junny on 2017. 5. 23..
 */

public class staticValues {
    public static SupportMapFragment mapFragment;
    public static SupportMapFragment mini_mapFragment;
    public static GoogleApiClient mGoogleApiClient;

    public static TMapData tMapData;
    public static String cur_address;
    public static DBHelper dbHelper;
    public static String total_distance;
    public static String total_time;
    public static PolylineOptions cur_poly;
    public static LocationRequest mLocationRequest;
    public static ArrayList<LatLng> walk_all_latlng;
    public static ArrayList<LatLng> walk_guide_latlng;
    public static ArrayList<String> walk_guide_text;

    public static Double to_lat;
    public static Double to_long;
    public static String to_title;
    public static Location to_location;
    public static LatLng to_latlng;

    public static Location mLastLocation;
    public static Double mLastLong;
    public static Double mLastLat;
    public static LatLng mLastLatLong;

    public static LatLng middle_point;
    public static float distance;
}
