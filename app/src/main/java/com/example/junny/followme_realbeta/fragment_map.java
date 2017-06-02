package com.example.junny.followme_realbeta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
}
