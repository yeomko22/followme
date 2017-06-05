package com.example.junny.followme_realbeta.activity;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapTapi;

import static com.example.junny.followme_realbeta.staticValues.mLastLocation;

public class ar_activity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

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
    private float cur_zio=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity);
        mHandler=new Handler();
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

        SensorManager mSensorManager=(SensorManager)getSystemService(Application.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        SensorEventListener mSensorListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.values[3]>0.8){
                    Log.e("회전 감지 : ",Float.toString(event.values[1]*100));
                    Log.e("정확도 : ",Float.toString(event.values[3]*100));
                    cur_zio=event.values[3]*100;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.e("정확도 변화","11");
            }
        };
        mSensorManager.registerListener(mSensorListener,sensor,SensorManager.SENSOR_DELAY_UI);
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
//            virtual_tracking();
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
    public void start_virtual(View v){
        virtual_tracking();
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
                                if(check_postion(nextPosition)){
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
                                fm.myPosition=fm.mMap.addMarker(new MarkerOptions().position(nextPosition));
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
    public boolean check_postion(LatLng cur_position){
        Location from=new Location("from");
        Location to=new Location("to");
        Location next=new Location("next");
        from.setLatitude(cur_position.latitude);
        from.setLongitude(cur_position.longitude);
        to.setLatitude(staticValues.walk_guide_latlng.get(cur_point).latitude);
        to.setLongitude(staticValues.walk_guide_latlng.get(cur_point).longitude);
        //이전 거리와 지금 거리의 차이 - 이동 거리 - 이걸 토탈에서 빼주자
        float distance=from.distanceTo(to);
        bearing=from.bearingTo(to);
        Log.e("베어링", Float.toString(bearing));
        fa.arrow.setRotation(bearing-cur_zio);
        if(distance<5){
            cur_point+=1;
            if(staticValues.walk_guide_latlng.size()>cur_point){
                //포인트 지점에 도착하면, 해당 지점과 시작점 사이의 거리만큼을 빼주면 된다 현재 거리와 별개 전체 거리에서
                //이전 키포인트에서 지금 키포인트사이의 거리만큼을 빼준다? 노노 직선거리잖아 직선과 곡선 차이때문에 나는 차이일 듯
                next.setLatitude(staticValues.walk_guide_latlng.get(cur_point).latitude);
                next.setLongitude(staticValues.walk_guide_latlng.get(cur_point).longitude);
                past_distance=from.distanceTo(next);
            }
            return true;
        }
        else{
            Log.e("이동한 거리",Float.toString(past_distance-distance));
            staticValues.distance-=(past_distance-distance);
            past_distance=distance;
            return false;
        }
    }

}
