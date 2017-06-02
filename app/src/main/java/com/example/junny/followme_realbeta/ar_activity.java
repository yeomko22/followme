package com.example.junny.followme_realbeta;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapTapi;

import static com.example.junny.followme_realbeta.staticValues.mLastLocation;

public class ar_activity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private CameraDevice camera;
    private SurfaceView mCameraView;
    private SurfaceHolder mCameraHolder;
    private android.hardware.Camera mCamera;
    private Button mStart;
    private ImageView arrow;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private ViewPager vp;
    private LatLng cur_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity);
        vp=(ViewPager)findViewById(R.id.view_pager);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        SensorManager mSensorManager=(SensorManager)getSystemService(Application.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//        SensorEventListener mSensorListener= new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent event) {
//                Log.e("회전 감지 : ",Float.toString(event.values[1]*100));
//                Log.e("정확도 : ",Float.toString(event.values[3]*100));
//                arrow.setRotation(event.values[1]*200);
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//                Log.e("정확도 변화","11");
//            }
//        };
//        mSensorManager.registerListener(mSensorListener,sensor,SensorManager.SENSOR_DELAY_UI);
    }
    class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new fragment_ar();
                case 1:
                    return new fragment_map();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 2;
        }
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

        if (mGoogleApiClient.isConnected()) {
            start_tracking();
        }

        super.onResume();
    }
    public void start_tracking(){
        createLocationRequest();
        if (ContextCompat.checkSelfPermission(ar_activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("위도 변화",Double.toString(location.getLatitude()));
                Log.e("경도 변화",Double.toString(location.getLongitude()));
                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
                

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
        if (ContextCompat.checkSelfPermission(ar_activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            cur_location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

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

    public void change_fragment(View v){
        if(vp.getCurrentItem()==0){
            vp.setCurrentItem(1);
        }
        else{
            vp.setCurrentItem(0);
        }
    }
    public LatLng get_curlocation(){
        return cur_location;
    }

//    private void init(){
//        final int CAMERA_PERMISSION_REQUEST_CODE = 1;
//        int permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
//        if(permissionCheck== PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(ar_activity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
//        }
//
//        mCamera= android.hardware.Camera.open();
//        mCamera.setDisplayOrientation(90);
//
//        mCameraHolder=mCameraView.getHolder();
//        mCameraHolder.addCallback(this);
//        mCameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        try{
//            if(mCamera == null){
//                mCamera.setPreviewDisplay(holder);
//                mCamera.startPreview();
//            }
//        }catch(IOException e){
//            StringWriter sw=new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            String exceptionAsString=sw.toString();
//            Log.e("IO 예외 발생",exceptionAsString);
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        if(mCameraHolder.getSurface()==null){
//            return;
//        }
//        try{
//            mCamera.stopPreview();
//        }catch (Exception e){
//            StringWriter sw=new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            String exceptionAsString=sw.toString();
//            Log.e("IO 예외 발생",exceptionAsString);
//        }
//        Camera.Parameters parameters=mCamera.getParameters();
//        List<String> focusModes = parameters.getSupportedFocusModes();
//        if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
//            parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
//        }
//        mCamera.setParameters(parameters);
//
//        try{
//            mCamera.setPreviewDisplay(mCameraHolder);
//            mCamera.startPreview();
//        }
//        catch(Exception e){
//            StringWriter sw=new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            String exceptionAsString=sw.toString();
//            Log.e("IO 예외 발생",exceptionAsString);
//        }
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        if(mCamera != null){
//            mCamera.stopPreview();
//            mCamera.release();
//            mCamera=null;
//        }
//    }
}
