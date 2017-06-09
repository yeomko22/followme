package com.example.junny.followme_realbeta.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.staticValues;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapTapi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.junny.followme_realbeta.staticValues.cur_address;
import static com.example.junny.followme_realbeta.staticValues.mLastLocation;
import static com.example.junny.followme_realbeta.staticValues.mLocationRequest;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private TextView start_point;
    private TextView end_point;
    private GoogleApiClient mGoogleApiClient;
    private HttpURLConnection conn;
    private android.os.Handler mHandler;
    private JSONArray features;
    private Marker mMarker;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent=new Intent(MapsActivity.this, SplashActivity.class);
        startActivity(intent);

        start_point=(TextView)findViewById(R.id.start_point);
        mHandler=new android.os.Handler();

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
        super.onResume();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
    }


    @Override
    public void onConnected(@Nullable Bundle connnectionHint) {
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
        createLocationRequest();
        staticValues.mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            Log.e("위치 잘 받아옴","온커넥트");
            Log.e("받아온 경도", Double.toString(mLastLocation.getLatitude()));
            Log.e("받아온 위도", Double.toString(mLastLocation.getLongitude()));

            //일단 스태틱에 위치 저장, 이게 불필요한지 확인해서 제거할 것
            staticValues.mLastLat=mLastLocation.getLatitude();
            staticValues.mLastLong=mLastLocation.getLongitude();
            staticValues.mLastLatLong=new LatLng(staticValues.mLastLat, staticValues.mLastLong);

            LatLng cur_location=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.e("mMap검사", mMap.toString());
            mMap.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_location,18));


            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=YOUR_API_KEY
                        String tes_url="https://maps.googleapis.com/maps/api/geocode/json?latlng="+staticValues.mLastLat+","
                                +staticValues.mLastLong+"&key=AIzaSyC2KPG-dhy-IqT1iBhb6W4N3WC1od4qAN0&language=ko";
                        URL url = new URL(tes_url);
                        Log.e("url",tes_url);

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(2000);
                        conn.connect();

                        if(conn.getResponseCode()==200){
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            StringBuilder sb = new StringBuilder();
                            String line = null;

                            while ((line=br.readLine()) != null) {
                                sb.append(line);
                            }
                            JSONObject org_obj=new JSONObject(sb.toString());
                            JSONArray result_array=new JSONArray(org_obj.getString("results"));
                            JSONObject target_obj=result_array.getJSONObject(0);
                            features=new JSONArray(target_obj.getString("address_components"));

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        staticValues.cur_address=features.getJSONObject(2).getString("short_name")+" "+features.getJSONObject(1).getString("short_name")+" "+features.getJSONObject(0).getString("short_name");
                                        if(cur_address.length()>15){
                                            start_point.setText(staticValues.cur_address.substring(0,10)+"...");
                                        }
                                        else{
                                            start_point.setText(cur_address);
                                        }
                                    } catch (Exception e) {
                                        StringWriter sw = new StringWriter();
                                        e.printStackTrace(new PrintWriter(sw));
                                        String exceptionAsStrting = sw.toString();
                                        Log.e("예외발생", exceptionAsStrting);
                                    }
                                    return;
                                }
                            });
                        }
                        else{
                            Log.e("유알엘",tes_url);
                            Log.e("실패코드", Integer.toString(conn.getResponseCode()));
                            Log.e("실패코드", conn.getResponseMessage());
                        }
                    } catch (Exception e) {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String exceptionAsStrting = sw.toString();
                        Log.e("예외발생", exceptionAsStrting);
                    }
                }
            });
        }
       else{
            Log.e("위치 못잡음","얘 정신 못차린다");
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
        if(staticValues.mLastLocation==null){
            Toast.makeText(MapsActivity.this, "현재 위치를 설정해야 합니다.\n 새로고침을 눌러주세요",Toast.LENGTH_LONG).show();
        }
        else{
            Intent go_search = new Intent(getApplicationContext(), search_endpoint_activity.class);
            go_search.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(go_search);
        }
    }

    public void reset_curlocation(View v){
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        v.startAnimation(anim);

        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
        if(mLastLocation==null){
            mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation!=null){
                Log.e("위치 잘 받아옴","온커넥트");
                Log.e("받아온 경도", Double.toString(mLastLocation.getLatitude()));
                Log.e("받아온 위도", Double.toString(mLastLocation.getLongitude()));

                //일단 스태틱에 위치 저장, 이게 불필요한지 확인해서 제거할 것
                staticValues.mLastLat=mLastLocation.getLatitude();
                staticValues.mLastLong=mLastLocation.getLongitude();
                staticValues.mLastLatLong=new LatLng(staticValues.mLastLat,staticValues.mLastLong);


                final LatLng cur_location=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                Log.e("mMap검사", mMap.toString());
                mMap.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_location,18));

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=YOUR_API_KEY
                            String tes_url="https://maps.googleapis.com/maps/api/geocode/json?latlng="+staticValues.mLastLat+","
                                    +staticValues.mLastLong+"&key=AIzaSyC2KPG-dhy-IqT1iBhb6W4N3WC1od4qAN0&language=ko";
                            URL url = new URL(tes_url);
                            Log.e("url",tes_url);

                            conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            conn.setConnectTimeout(2000);
                            conn.connect();

                            if(conn.getResponseCode()==200){
                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                                StringBuilder sb = new StringBuilder();
                                String line = null;

                                while ((line=br.readLine()) != null) {
                                    sb.append(line);
                                }
                                JSONObject org_obj=new JSONObject(sb.toString());
                                JSONArray result_array=new JSONArray(org_obj.getString("results"));
                                JSONObject target_obj=result_array.getJSONObject(0);
                                features=new JSONArray(target_obj.getString("address_components"));

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            staticValues.cur_address=features.getJSONObject(2).getString("short_name")+" "+features.getJSONObject(1).getString("short_name")+" "+features.getJSONObject(0).getString("short_name");
                                            if(cur_address.length()>15){
                                                start_point.setText(staticValues.cur_address.substring(0,10)+"...");
                                            }
                                            else{
                                                start_point.setText(cur_address);
                                            }
                                        } catch (Exception e) {
                                            StringWriter sw = new StringWriter();
                                            e.printStackTrace(new PrintWriter(sw));
                                            String exceptionAsStrting = sw.toString();
                                            Log.e("예외발생", exceptionAsStrting);
                                        }
                                        return;
                                    }
                                });
                            }
                            else{
                                Log.e("유알엘",tes_url);
                                Log.e("실패코드", Integer.toString(conn.getResponseCode()));
                                Log.e("실패코드", conn.getResponseMessage());
                            }
                        } catch (Exception e) {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            String exceptionAsStrting = sw.toString();
                            Log.e("예외발생", exceptionAsStrting);
                        }
                    }
                });
            }
            else{
                Log.e("위치 못잡음","얘 정신 못차린다");

                LatLng seoul=new LatLng(37.5665, 126.9780);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,10));
            }
        }
        else{
            Log.e("위치 잘 받아옴","온커넥트");
            Log.e("받아온 경도", Double.toString(mLastLocation.getLatitude()));
            Log.e("받아온 위도", Double.toString(mLastLocation.getLongitude()));

            //일단 스태틱에 위치 저장, 이게 불필요한지 확인해서 제거할 것
            staticValues.mLastLat=mLastLocation.getLatitude();
            staticValues.mLastLong=mLastLocation.getLongitude();
            staticValues.mLastLatLong=new LatLng(staticValues.mLastLat,staticValues.mLastLong);


            final LatLng cur_location=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.e("mMap검사", mMap.toString());
            mMap.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_location,18));
        }
    }
}
