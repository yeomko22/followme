package com.example.junny.followme_realbeta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
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
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.junny.followme_realbeta.staticValues.mLastLocation;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        start_point=(TextView)findViewById(R.id.start_point);
        mHandler=new android.os.Handler();
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
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
    }

    @Override
    public void onConnected(@Nullable Bundle connnectionHint) {
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            Log.e("위치 잘 받아옴","온커넥트");
            Log.e("받아온 경도", Double.toString(mLastLocation.getLatitude()));
            Log.e("받아온 위도", Double.toString(mLastLocation.getLongitude()));

            //일단 스태틱에 위치 저장, 이게 불필요한지 확인해서 제거할 것
            staticValues.mLastLat=mLastLocation.getLatitude();
            staticValues.mLastLong=mLastLocation.getLongitude();

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
                                        start_point.setText(features.getJSONObject(0).getString("short_name"));
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
        Intent go_search = new Intent(getApplicationContext(), search_endpoint_activity.class);
        go_search.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(go_search);
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
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsStrting = sw.toString();
                Log.e("예외발생", exceptionAsStrting);
            }
        }
        else{
            Log.e("되야만해 이거뜨면 안대","11");
        }
    }
}
