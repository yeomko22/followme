package com.example.junny.followme_realbeta.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.activity.ar_activity;
import com.example.junny.followme_realbeta.staticValues;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.junny.followme_realbeta.R.id.distance;
import static com.example.junny.followme_realbeta.staticValues.middle_point;

/**
 * Created by junny on 2017. 6. 2..
 */

public class fragment_map extends android.support.v4.app.Fragment implements OnMapReadyCallback{
    public GoogleMap mMap;
    private ar_activity mActivity;
    public fragment_map() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=(ar_activity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.addMarker(new MarkerOptions().position(new LatLng(staticValues.mLastLat,staticValues.mLastLong)).title("내 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(staticValues.mLastLat,staticValues.mLastLong), 13));
        mMap.addPolyline(staticValues.cur_poly);
    }
    public void set_zoom(){
        if(distance<100){
            Log.e("직선 거리","100 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,19));
        }
        else if(distance<300){
            Log.e("직선 거리","300 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,17));
        }

        else if(distance<500){
            Log.e("직선 거리","500 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,16));
        }

        else if(distance<1000){
            Log.e("직선 거리","1000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,15));
        }

        else if(distance<2000){
            Log.e("직선 거리","2000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,14));
        }
        else if(distance<3500){
            Log.e("직선 거리","3500 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,13));
        }
        else if(distance<7500){
            Log.e("직선 거리","7500 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,12));
        }
        else if(distance<15000){
            Log.e("직선 거리","10000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,11));
        }
        else if(distance<30000){
            Log.e("직선 거리","20000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,10));
        }
        else if(distance<40000){
            Log.e("직선 거리","20000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,9));
        }
        else if(distance<80000){
            Log.e("직선 거리","50000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,8));
        }
        else{
            Log.e("직선 거리","그 외");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,8));
        }
    }
}
