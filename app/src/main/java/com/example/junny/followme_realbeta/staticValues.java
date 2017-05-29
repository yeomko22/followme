package com.example.junny.followme_realbeta;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.skp.Tmap.TMapData;

/**
 * Created by junny on 2017. 5. 23..
 */

public class staticValues {
    public static SupportMapFragment mapFragment;
    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;
    public static Double mLastLong;
    public static Double mLastLat;
    public static TMapData tMapData;
    public static String cur_address;
    public static DBHelper dbHelper;
}
