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
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener{

    //google api 변수들 모음
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    //위치 변수들 모음
    private LatLng nextPosition;
    private Handler mHandler;
    private fragment_ar fa;
    private fragment_map fm;
    private int cur_point=0;
    private float past_distance;
    private float bearing=0;
    private GeomagneticField geomagneticField;
    private LocationListener locationListener;
    private LatLng cur_location;
    private float cur_accuracy;

    //센서 변수들
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
    private SensorManager mSensorManager;

    //현재 액티비티 뷰 요소들 모음
    private ViewPager vp;
    private TextView guide_text;
    private TextView guide_num;
    private TextView ar_destination;
    private TextView ar_distance;

    //초기화 감지 변수
    private boolean is_initi=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity);
        mHandler=new Handler();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        vp=(ViewPager)findViewById(R.id.view_pager);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

    }
    protected void initialize(){
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        Log.e("정확도 변함", Integer.toString(accuracy));
    }

    //로케이션리퀘스트 생성 함수, 10초에 한번씩 감지하도록 설정
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //위치 변화 이벤트 리스너
    class mLoactionListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            cur_accuracy=location.getAccuracy();
            Log.e("정확도",Float.toString(cur_accuracy));
            if(cur_accuracy>0&&cur_accuracy<=20){
                Log.e("유효","11");
                if(!is_initi){
                    is_initi=true;
                    initialize();
                }

                //인식한 내 위치로 메모리상의 변수들 변경
                staticValues.mLastLocation=location;
                mLastLatLong=new LatLng(location.getLatitude(),location.getLongitude());
                staticValues.mLastLat=location.getLatitude();
                staticValues.mLastLong=location.getLongitude();

                //다음 체크 포인트에 도착했는지를 확인, 도착했을 경우 변수들 변경
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

                //내 위치로 지도 상의 마커를 찍어준다
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
//
//    public boolean check_position(Location cur_location){
//        Location from=cur_location;
//        Location to=new Location("to");
//
//    }

//    다음 체크 포인트에 도착했는지 판별 함수
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
        if(distance<20){
            cur_point+=1;
            if(staticValues.walk_guide_latlng.size()>cur_point){
                //포인트 지점에 도착하면, 해당 지점과 시작점 사이의 거리만큼을 빼주면 된다 현재 거리와 별개 전체 거리에서
                //이전 키포인트에서 지금 키포인트사이의 거리만큼을 빼준다? 노노 직선거리잖아 직선과 곡선 차이때문에 나는 차이일 듯
                next.setLatitude(staticValues.walk_guide_latlng.get(cur_point).latitude);
                next.setLongitude(staticValues.walk_guide_latlng.get(cur_point).longitude);
                past_distance=from.distanceTo(next);
                staticValues.last_bearing=to.bearingTo(next);
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
    public void next_checkpoint(View v){
        LatLng virtual_point=staticValues.walk_guide_latlng.get(cur_point);
        Location virtual_location=new Location("virtual");
        virtual_location.setLatitude(virtual_point.latitude);
        virtual_location.setLongitude(virtual_point.longitude);

        staticValues.mLastLocation=virtual_location;
        mLastLatLong=virtual_point;
        staticValues.mLastLat=virtual_point.latitude;
        staticValues.mLastLong=virtual_point.longitude;

        check_position(virtual_point);
        guide_num.setText(Integer.toString(cur_point));
        guide_text.setText(staticValues.walk_guide_text.get(cur_point-1));

        if(staticValues.distance>1000){
            ar_distance.setText(String.format("%.2f",staticValues.distance/1000.f)+"km");
        }
        else{
            ar_distance.setText(String.format("%.2f",staticValues.distance)+"m");
        }

        //내 위치로 지도 상의 마커를 찍어준다
        fm.myPosition.remove();
        fm.myPosition=fm.mMap.addMarker(new MarkerOptions().position(mLastLatLong));
    }



//    @Override
//    public void onLocationChanged(Location location) {
//        Log.e("위도,경도",Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude()));
//        Toast.makeText(ar_activity.this,Float.toString(location.getAccuracy()),Toast.LENGTH_LONG).show();
//
//        staticValues.mLastLocation=location;
//        mLastLatLong=new LatLng(location.getLatitude(),location.getLongitude());
//        staticValues.mLastLat=location.getLatitude();
//        staticValues.mLastLong=location.getLongitude();
//
//        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
//
//        if(check_position(nextPosition)){
//            guide_num.setText(Integer.toString(cur_point));
//            guide_text.setText(staticValues.walk_guide_text.get(cur_point-1));
//
//        }
//        if(staticValues.distance>1000){
//            ar_distance.setText(String.format("%.2f",staticValues.distance/1000.f)+"km");
//        }
//        else{
//            ar_distance.setText(String.format("%.2f",staticValues.distance)+"m");
//        }
//        fm.myPosition.remove();
//        fm.myPosition=fm.mMap.addMarker(new MarkerOptions().position(nextPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_arrow)));
//        return;
//    }

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
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    //뷰페이져 어댑터 이너 클래스
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

    //뷰 페이져 전환 버튼 리스너
    public void change_fragment(View v){
        if(vp.getCurrentItem()==0){
            vp.setCurrentItem(1);
        }
        else{
            vp.setCurrentItem(0);
        }
    }

    //센서 인식, 방향 감지 부분 완성
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
            float rotation_angle=(azi_sum/10.0f);
            if(rotation_angle<0){rotation_angle=rotation_angle+360;}

            if(last_angle==0){
                last_angle=rotation_angle;
            }
            if(Math.abs(rotation_angle-last_angle)<90){
                last_angle=rotation_angle;
                fa.arrow.setRotation(staticValues.last_bearing-rotation_angle);
            }
            azimuth.clear();
            azi_sum=0;
            count+=1;
        }
    }

    //시뮬레이션 버튼 리스너
    public void start_virtual(View v){
        virtual_tracking();
    }

    //가상 GPS, 지금은 별로 쓸모 없음
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
