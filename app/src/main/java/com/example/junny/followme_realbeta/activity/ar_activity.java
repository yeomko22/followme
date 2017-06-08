package com.example.junny.followme_realbeta.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.fragment.fragment_ar;
import com.example.junny.followme_realbeta.fragment.fragment_map;
import com.example.junny.followme_realbeta.staticValues;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapTapi;

import java.util.ArrayList;

import static com.example.junny.followme_realbeta.staticValues.mLastLatLong;
import static com.example.junny.followme_realbeta.staticValues.mLastLocation;

public class ar_activity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,SensorEventListener{

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private ViewPager vp;
    private LatLng cur_location;
    private LatLng nextPosition;
    private Handler mHandler;
    private TextView guide_text;
    private TextView guide_num;
    private TextView ar_destination;
    private TextView ar_distance;
    private fragment_ar fa;
    private fragment_map fm;
    private int cur_point=0;
    private float past_distance;
    private ImageView arrow;
    private float bearing=0;
    private GeomagneticField geomagneticField;
    private LocationListener locationListener;
    private SensorManager mSensorManager;
    private Sensor mAccelSensor;
    private Sensor mMagneticSensor;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private ArrayList<Float> azimuth=new ArrayList<Float>();
    private float azi_sum=0;
    private float added_degree=0;
    private int count=0;
    private float last_angle;

    private float cur_accuracy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity);
        mHandler=new Handler();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    Thread.sleep(2000);
//                    updateOrientationAngles();
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });

        vp=(ViewPager)findViewById(R.id.view_pager);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        guide_text=(TextView)findViewById(R.id.guide_text);
        guide_num=(TextView)findViewById(R.id.guide_num);
        ar_destination=(TextView)findViewById(R.id.ar_destination);
        ar_destination.setText(staticValues.to_title);
        ar_distance=(TextView)findViewById(R.id.ar_distance);

        if(staticValues.distance>1000){
            ar_distance.setText(String.format("%.2f",staticValues.distance/1000.f)+"km");
        }
        else{
            ar_distance.setText(String.format("%.2f",staticValues.distance)+"m");
        }
        guide_num.setText("1");
        guide_text.setText(staticValues.walk_guide_text.get(0));
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
            Log.e("버츄얼","11");
        }
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        mSensorManager.registerListener(this, mAccelSensor,SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagneticSensor,SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    protected void onPause(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
        mSensorManager.unregisterListener(this);
        Log.e("센서 해지","11");
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelSensor) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor == mMagneticSensor) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
        updateOrientationAngles();
    }
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        if(azimuth==null){
            azimuth=new ArrayList<Float>();
        }

        if(azimuth.size()<10){
            float added_degree=(float)Math.toDegrees(mOrientationAngles[0]);
            azimuth.add(added_degree);
            azi_sum+=added_degree;
        }
        else{
            //화살표가 사라지는 이유는 필터에서 반환값이 NAN이 나와서 없어지는 것, 그래서 우선 필터를 제외시킴
//            Log.e("필터링 전",Float.toString(azi_sum/10.0f));
//            Log.e("필터링 후", Float.toString(filter()));
//            fa.arrow.setRotation(filter());
            float rotation_angle=(azi_sum/10.0f);
            if(rotation_angle<0){rotation_angle=rotation_angle+360;}
//            Log.e("돌아가는 각도",Float.toString(rotation_angle));

            if(last_angle==0){
                last_angle=rotation_angle;
            }
            if(Math.abs(rotation_angle-last_angle)<90){
                last_angle=rotation_angle;
                fa.arrow.setRotation(staticValues.last_bearing-rotation_angle);
            }
            else{
//                Log.e("필터 작동",Float.toString(rotation_angle));
            }
            azimuth.clear();
            azi_sum=0;
            count+=1;
        }
        // "mOrientationAngles" now has up-to-date information.
    }
//    public float filter(){
//        float cur_avg=azi_sum/10.0f;
//        float new_sum=0;
//        float count=0;
//        for(int i=0;i<azimuth.size();i++){
//            if(Math.abs(azimuth.get(i)-cur_avg)>40){
//                Log.e("필터 작동",Float.toString(azimuth.get(i)));
//            }
//            else{
//                new_sum+=azimuth.get(i);
//                count+=1;
//            }
//        }
//        return new_sum/count;
//    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        Log.e("정확도 변함", Integer.toString(accuracy));
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
                    if(fa==null){
                        fa=new fragment_ar();
                    }
                    return fa;
                case 1:
                    if(fm==null){
                        fm=new fragment_map();
                    }
                    return fm;
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

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    class mLoactionListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            cur_accuracy=location.getAccuracy();
            Log.e("정확도",Float.toString(cur_accuracy));
            if(cur_accuracy>0&&cur_accuracy<20){
                Log.e("유효","11");
                staticValues.mLastLocation=location;
                mLastLatLong=new LatLng(location.getLatitude(),location.getLongitude());
                staticValues.mLastLat=location.getLatitude();
                staticValues.mLastLong=location.getLongitude();

                if(check_position(mLastLatLong)){
                    guide_num.setText(Integer.toString(cur_point));
                    guide_text.setText(staticValues.walk_guide_text.get(cur_point-1));

                }
                if(staticValues.distance>1000){
                    ar_distance.setText(String.format("%.2f",staticValues.distance/1000.f)+"km");
                }
                else{
                    ar_distance.setText(String.format("%.2f",staticValues.distance)+"m");
                }
                fm.myPosition.remove();
                fm.myPosition=fm.mMap.addMarker(new MarkerOptions().position(mLastLatLong));
                return;
            }
            else{
                Log.e("무효",Float.toString(cur_accuracy));
                return;
            }
        }
    }

    public boolean check_position(LatLng cur_position){
        Location from=new Location("from");
        Location to=new Location("to");
        Location next=new Location("next");
        from.setLatitude(cur_position.latitude);
        from.setLongitude(cur_position.longitude);
        to.setLatitude(staticValues.walk_guide_latlng.get(cur_point).latitude);
        to.setLongitude(staticValues.walk_guide_latlng.get(cur_point).longitude);
        //이전 거리와 지금 거리의 차이 - 이동 거리 - 이걸 토탈에서 빼주자
        float distance=from.distanceTo(to);
        if(distance<15){
            Toast.makeText(ar_activity.this, "주요 포인트 도착",Toast.LENGTH_LONG).show();
            cur_point+=1;
            if(staticValues.walk_guide_latlng.size()>cur_point){
                //포인트 지점에 도착하면, 해당 지점과 시작점 사이의 거리만큼을 빼주면 된다 현재 거리와 별개 전체 거리에서
                //이전 키포인트에서 지금 키포인트사이의 거리만큼을 빼준다? 노노 직선거리잖아 직선과 곡선 차이때문에 나는 차이일 듯
                next.setLatitude(staticValues.walk_guide_latlng.get(cur_point).latitude);
                next.setLongitude(staticValues.walk_guide_latlng.get(cur_point).longitude);
                past_distance=from.distanceTo(next);
                staticValues.last_bearing=from.bearingTo(next);
            }
            else{
                Toast.makeText(ar_activity.this,"목적지에 도착했습니다",Toast.LENGTH_LONG).show();
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,locationListener);
            }
            return true;
        }
        else{
            staticValues.distance-=(past_distance-distance);
            past_distance=distance;
            return false;
        }
    }

    public void start_virtual(View v){
        virtual_tracking();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("위도,경도",Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude()));
        Toast.makeText(ar_activity.this,Float.toString(location.getAccuracy()),Toast.LENGTH_LONG).show();

        staticValues.mLastLocation=location;
        mLastLatLong=new LatLng(location.getLatitude(),location.getLongitude());
        staticValues.mLastLat=location.getLatitude();
        staticValues.mLastLong=location.getLongitude();

        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));

        if(check_position(nextPosition)){
            guide_num.setText(Integer.toString(cur_point));
            guide_text.setText(staticValues.walk_guide_text.get(cur_point-1));

        }
        if(staticValues.distance>1000){
            ar_distance.setText(String.format("%.2f",staticValues.distance/1000.f)+"km");
        }
        else{
            ar_distance.setText(String.format("%.2f",staticValues.distance)+"m");
        }
        fm.myPosition.remove();
        fm.myPosition=fm.mMap.addMarker(new MarkerOptions().position(nextPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_arrow)));
        return;
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
        createLocationRequest();
        locationListener=new mLoactionListener();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
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

    public void virtual_tracking(){
        Log.e("트랙킹","11");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.e("런","11");
                try{
                    for(int i=0;i<staticValues.walk_all_latlng.size();i++){
                        Log.e("돌긴하니","11");
                        Thread.sleep(2000);
                        nextPosition=staticValues.walk_all_latlng.get(i);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(cur_point==staticValues.walk_guide_latlng.size()){return;}
                                if(check_position(nextPosition)){
                                    guide_num.setText(Integer.toString(cur_point));
                                    guide_text.setText(staticValues.walk_guide_text.get(cur_point-1));
                                }
                                if(staticValues.distance>1000){
                                    ar_distance.setText(String.format("%.2f",staticValues.distance/1000.f)+"km");
                                }
                                else{
                                    ar_distance.setText(String.format("%.2f",staticValues.distance)+"m");
                                }
                                fm.myPosition.remove();
                                fm.myPosition=fm.mMap.addMarker(new MarkerOptions().position(nextPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_arrow)));
                                fm.myPosition.setRotation(bearing);
                                return;
                            }
                        });
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}
