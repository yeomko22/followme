package com.example.junny.followme_realbeta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapTapi;

import java.io.IOException;
import java.util.List;

import static com.example.junny.followme_realbeta.staticValues.mLastLocation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private TextView start_point;
    private TextView end_point;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //TMap 앱 연동 인증

    }
    protected void onStart(){
        super.onStart();
    }
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        TMapTapi tMapTapi = new TMapTapi(this);
        tMapTapi.setSKPMapAuthentication("4004a4c7-8e67-3c17-88d9-9799c613ecc7");

        start_point=(TextView)findViewById(R.id.start_point);
        end_point=(TextView)findViewById(R.id.end_point);

        staticValues.mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        if(geocoder==null){
            geocoder=new Geocoder(MapsActivity.this);
        }

        staticValues.mapFragment.getMapAsync(this);
        super.onResume();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("지도 준비됬다","11");
        mMap = googleMap;
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.e("카메라 이동을 감지","뿜뿜");
            }
        });
        // Add a marker in Sydney and move the camera
    }

    @Override
    public void onConnected(@Nullable Bundle connnectionHint) {
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            Log.e("현재 위치 호출","온커넥트에서");
            set_currentlocation();
        }
       else{
            Toast.makeText(MapsActivity.this,"현재 위치를 잡지 못했습니다\n잠시 뒤에 새로고침을 눌러주세요",Toast.LENGTH_LONG).show();
            LatLng seoul=new LatLng(37.5665, 126.9780);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,10));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void search_endpoint(View v){
        Intent go_search = new Intent(getApplicationContext(), search_endpoint_activity.class);
        go_search.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(go_search);
    }
    public void set_currentlocation(){
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);//이걸 못 받아온다
        //안될리가 없다 내가 못할 뿐

        if(mLastLocation!=null){
            Log.e("위치 잘 받아옴","11");
            Log.e("받아온 경도", Double.toString(mLastLocation.getLatitude()));
            Log.e("받아온 위도", Double.toString(mLastLocation.getLongitude()));
            staticValues.mLastLat=mLastLocation.getLatitude();
            staticValues.mLastLong=mLastLocation.getLongitude();
            LatLng cur_location=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.e("mMap검사", mMap.toString());
            mMap.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_location,18));

            try{
                List<Address> addresses= geocoder.getFromLocation(staticValues.mLastLat, staticValues.mLastLong,4);
                String str_address=addresses.get(0).getAddressLine(0);
                str_address=str_address.replaceFirst("대한민국","");
                str_address=str_address.replaceFirst("특별시","");
                staticValues.cur_address=str_address;
                start_point.setText(str_address);
            }
            catch(IOException e){
                Log.e("IO 예외 뜨심","!!");
            }
        }
        else{
            Toast.makeText(MapsActivity.this, "잠시 뒤에 새로고침을 눌러주세요", Toast.LENGTH_LONG).show();
            Log.e("되야만해 이거뜨면 안대","11");
        }
    }

    public void reset_curlocation(View v){
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            staticValues.mLastLat=mLastLocation.getLatitude();
            staticValues.mLastLong=mLastLocation.getLongitude();
            LatLng cur_location=new LatLng(staticValues.mLastLat, staticValues.mLastLong);
            mMap.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_location,18));
            try{
                List<Address> addresses= geocoder.getFromLocation(staticValues.mLastLat, staticValues.mLastLong,4);
                String str_address=addresses.get(0).getAddressLine(0);
                str_address=str_address.replaceFirst("대한민국","");
                str_address=str_address.replaceFirst("특별시","");
                staticValues.cur_address=str_address;
                start_point.setText(str_address);
            }
            catch(IOException e){
                Log.e("IO 예외 뜨심","!!");
            }
        }
        else{
            Log.e("되야만해 이거뜨면 안대","11");
        }
    }
}
