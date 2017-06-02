package com.example.junny.followme_realbeta;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapTapi;

import static com.example.junny.followme_realbeta.staticValues.mLastLocation;

public class test_activity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private CameraDevice camera;
    private SurfaceView mCameraView;
    private SurfaceHolder mCameraHolder;
    private Camera mCamera;
    private Button mStart;
    private ImageView arrow;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Marker mMarker;
    private Handler mHandler;
    private LatLng nextPosition;
    private int cur_point=0;
    private Location from;
    private Location to;
    private TextView guide_text;
    private TextView guide_num;
    private TextView distance_view;
    private float distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        guide_text=(TextView)findViewById(R.id.guide_text);
        guide_num=(TextView)findViewById(R.id.guide_num);
        distance_view=(TextView)findViewById(R.id.distance);
        guide_text.setText(staticValues.walk_guide.get(cur_point));
        from=new Location("From");
        to=new Location("To");
        mHandler=new Handler();
    }
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

        staticValues.mini_mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.minimap);
        staticValues.mini_mapFragment.getMapAsync(this);
//
//        if (mGoogleApiClient.isConnected()) {
//            start_tracking();
//        }

        super.onResume();
    }
    public void start_tracking(){
        createLocationRequest();
        if (ContextCompat.checkSelfPermission(test_activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("위도 변화",Double.toString(location.getLatitude()));
                Log.e("경도 변화",Double.toString(location.getLongitude()));
//                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(test_activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.e("위치 잘 받아옴", "온커넥트");
            Log.e("받아온 경도", Double.toString(mLastLocation.getLatitude()));
            Log.e("받아온 위도", Double.toString(mLastLocation.getLongitude()));

            //일단 스태틱에 위치 저장, 이게 불필요한지 확인해서 제거할 것
            staticValues.mLastLat = mLastLocation.getLatitude();
            staticValues.mLastLong = mLastLocation.getLongitude();

            LatLng cur_location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.e("mMap검사", mMap.toString());
            mMarker=mMap.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
            for(int i=0;i<staticValues.walk_google_poly.size();i++){
                mMap.addMarker(new MarkerOptions().position(staticValues.walk_google_poly.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
            }
            for(int i=0;i<staticValues.walk_guide_poly.size();i++){
                mMap.addMarker(new MarkerOptions().position(staticValues.walk_guide_poly.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluepin)));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_location, 15));
            mMap.addPolyline(staticValues.cur_poly);
            virtual_tracking();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void virtual_tracking(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i=0;i<staticValues.walk_google_poly.size();i++){
                        Log.e("돌긴하니","11");
                        Thread.sleep(1000);
                        nextPosition=staticValues.walk_google_poly.get(i);
                        if(check_postion(nextPosition)){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    distance_view.setText(distance+"m");
                                    guide_num.setText(Integer.toString(cur_point));
                                    mMarker.remove();
                                    mMarker=mMap.addMarker(new MarkerOptions().position(nextPosition));
                                    guide_text.setText(staticValues.walk_guide.get(cur_point-1));
                                    return;
                                }
                            });
                        }
                        else{
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    distance_view.setText(distance+"m");
                                    mMarker.remove();
                                    mMarker=mMap.addMarker(new MarkerOptions().position(nextPosition));
                                    return;
                                }
                            });
                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public boolean check_postion(LatLng cur_position){
        from.setLatitude(cur_position.latitude);
        from.setLongitude(cur_position.longitude);
        to.setLatitude(staticValues.walk_guide_poly.get(cur_point).latitude);
        to.setLongitude(staticValues.walk_guide_poly.get(cur_point).longitude);
        distance=from.distanceTo(to);
        if(distance<5){
            cur_point+=1;
            return true;
        }
        else{
            return false;
        }
    }
}
