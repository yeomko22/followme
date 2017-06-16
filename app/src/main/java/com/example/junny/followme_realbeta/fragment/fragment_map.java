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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.junny.followme_realbeta.staticValues.mLastLatLong;
import static com.example.junny.followme_realbeta.staticValues.middle_point;
import static com.example.junny.followme_realbeta.staticValues.to_latlng;
import static com.example.junny.followme_realbeta.staticValues.tourAttractions;

/**
 * Created by junny on 2017. 6. 2..
 */

public class fragment_map extends android.support.v4.app.Fragment implements OnMapReadyCallback{
    public GoogleMap mMap;
    public Marker myPosition;
    public ar_activity mActivity;
    public fragment_map() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mActivity=(ar_activity) getActivity();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.addMarker(new MarkerOptions().position(to_latlng).title("도착점"));
        myPosition=mMap.addMarker(new MarkerOptions().position(mLastLatLong).title("내 위치"));

        for(int i=0;i<mActivity.cur_guide_latlng.size();i++){
            mMap.addMarker(new MarkerOptions().position(mActivity.cur_guide_latlng.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinholder)));
        }

        for(int i=0;i<tourAttractions.size();i++){
            mMap.addMarker(new MarkerOptions().position(tourAttractions.get(i).getPoint()).title(tourAttractions.get(i).getTitle()).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)));
        }

        set_zoom();
        mMap.addPolyline(staticValues.cur_poly);
    }
    public void set_zoom(){
        Log.e("현재의 직선 거리",Float.toString(staticValues.distance));
        if(staticValues.distance<100){
            Log.e("직선 거리","100 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,19));
        }
        else if(staticValues.distance<300){
            Log.e("직선 거리","300 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,17));
        }

        else if(staticValues.distance<500){
            Log.e("직선 거리","500 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,16));
        }

        else if(staticValues.distance<1000){
            Log.e("직선 거리","1000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,15));
        }

        else if(staticValues.distance<2000){
            Log.e("직선 거리","2000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,14));
        }
        else if(staticValues.distance<3500){
            Log.e("직선 거리","3500 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,13));
        }
        else if(staticValues.distance<7500){
            Log.e("직선 거리","7500 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,12));
        }
        else if(staticValues.distance<15000){
            Log.e("직선 거리","15000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,11));
        }
        else if(staticValues.distance<30000){
            Log.e("직선 거리","30000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,10));
        }
        else if(staticValues.distance<40000){
            Log.e("직선 거리","40000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,9));
        }
        else if(staticValues.distance<80000){
            Log.e("직선 거리","80000 미만");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,8));
        }
        else{
            Log.e("직선 거리","그 외");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,8));
        }
    }
}
