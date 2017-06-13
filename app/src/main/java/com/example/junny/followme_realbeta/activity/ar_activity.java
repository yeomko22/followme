package com.example.junny.followme_realbeta.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.fragment.fragment_ar;
import com.example.junny.followme_realbeta.fragment.fragment_map;
import com.example.junny.followme_realbeta.sensors.Orientation;
import com.example.junny.followme_realbeta.staticValues;
import com.example.junny.followme_realbeta.utils.OrientationSensorInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapTapi;

import static com.example.junny.followme_realbeta.staticValues.mLastLatLong;
import static com.example.junny.followme_realbeta.staticValues.mLastLocation;

public class ar_activity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OrientationSensorInterface{

    //google api 변수들 모음
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    //위치 변수들 모음
    private fragment_ar fa;
    private fragment_map fm;
    private int cur_point=0;
    private float past_distance;
    private LocationListener locationListener;
    private float cur_accuracy;

    //라이브러리 센서
    Orientation orientationSensor;

    //현재 액티비티 뷰 요소들 모음
    private ViewPager vp;
    private TextView guide_text;
    private TextView guide_num;
    private TextView ar_destination;
    private TextView ar_distance;
    private DrawerLayout dLayout;
    private ImageView setting_btn;
    private boolean setting_open=false;
    private Spinner spinner;
    private ArrayAdapter arrayAdapter;

    private Switch switch_vibe1;
    private Switch switch_vibe2;
    private Switch switch_auto;
    private Switch switch_tour;
    private Switch switch_tourAR;

    //진동 변수
    Vibrator mVibe;

    //셰어드, 설정 초기화 감지
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private boolean is_initi=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity);

        //센서 활성화
        orientationSensor=new Orientation(ar_activity.this, this);
        orientationSensor.init(1.0,1.0,1.0);
        orientationSensor.on(0);

        //뷰페이져 설정, 초기화
        vp=(ViewPager)findViewById(R.id.view_pager);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        spinner=(Spinner)findViewById(R.id.language_spinner);
        arrayAdapter=ArrayAdapter.createFromResource(this,R.array.language,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //셰어드로 설정 확인, 진동 설정
        pref=getSharedPreferences("pref", MODE_PRIVATE);
        editor=pref.edit();
        if(!pref.getString("personal_setting","").equals("setting")){
            editor.putString("setting_fore_vibe","on");
            editor.putString("setting_back_vibe","on");
            editor.putString("setting_auto","on");
            editor.putString("setting_tour","on");
            editor.putString("setting_tourAR","on");
            editor.putString("personal_setting","setting");
            Log.e("개인 설정 초기화","11");
            editor.commit();
        }
        mVibe= (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        switch_vibe1=(Switch)findViewById(R.id.switch_vibe1);
        switch_vibe2=(Switch)findViewById(R.id.switch_vibe2);
        switch_auto=(Switch)findViewById(R.id.switch_auto);
        switch_tour=(Switch)findViewById(R.id.switch_tourpush);
        switch_tourAR=(Switch)findViewById(R.id.switch_tourAR);

        switch_vibe1.setChecked(pref.getString("setting_fore_vibe","").equals("on"));
        switch_vibe2.setChecked(pref.getString("setting_back_vibe","").equals("on"));
        switch_auto.setChecked(pref.getString("setting_auto_vibe","").equals("on"));
        switch_tour.setChecked(pref.getString("setting_tour","").equals("on"));
        switch_tourAR.setChecked(pref.getString("setting_tourAR","").equals("on"));

        switch_vibe1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString("setting_fore_vibe","on");
                }
                else{
                    editor.putString("setting_fore_vibe","off");
                }
                editor.commit();
            }
        });

        switch_vibe2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString("setting_back_vibe","on");
                }
                else{
                    editor.putString("setting_back_vibe","off");
                }
                editor.commit();
            }
        });
        switch_tour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString("setting_tour","on");
                }
                else{
                    editor.putString("setting_tour","off");
                }
                editor.commit();
            }
        });
        switch_tourAR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString("setting_tourAR","on");
                }
                else{
                    Toast.makeText(ar_activity.this, "투어ar 꺼짐", Toast.LENGTH_LONG).show();
                    editor.putString("setting_tourAR","off");
                }
                editor.commit();
            }
        });

        //네비게이션 설정
        dLayout=(DrawerLayout)findViewById(R.id.drawable_layout);
        setting_btn=(ImageView)findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!setting_open){
                    dLayout.openDrawer(Gravity.RIGHT);
                    setting_open=true;
                }
                else{
                    dLayout.closeDrawer(Gravity.RIGHT);
                    setting_open=false;
                }
            }
        });
    }
    public void close_navigation(View v){
        if(!setting_open){
            dLayout.openDrawer(Gravity.RIGHT);
            setting_open=true;
        }
        else{
            dLayout.closeDrawer(Gravity.RIGHT);
            setting_open=false;
        }
    }

    protected void onResume() {
        //위치 요청을 위한 구글 API 요청
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        //경로 검색을 위한 티맵 API 설정
        TMapTapi tMapTapi = new TMapTapi(this);
        tMapTapi.setSKPMapAuthentication(staticValues.tMapKey);

        super.onResume();
    }
    @Override
    protected void onStop(){
        //액티비티 멈춤시 센서 해지 및 API 요청 중지
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
        orientationSensor.off();
        super.onStop();
    }

    public void onBackPressed(){
        //백 버튼 눌렀을 경우 바로 길 안내가 종료되지 않도록 다이얼로그 설정
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ar_activity.this);
        alertDialog.setMessage("길 안내를 종료합니다 \n 길 안내 푸쉬 알림을 받으시겠습니까?");
        alertDialog.setNegativeButton("알림 받기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ar_activity.super.onBackPressed();
            }
        });
        alertDialog.setNeutralButton("완전 종료", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ar_activity.super.onBackPressed();
            }
        });
        alertDialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    //나머지 화면 구성요소들의 초기화 설정,
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
            if(cur_accuracy>0&&cur_accuracy<=25){
                Log.e("유효","11");
                //초기화 함수를 여기서 호출하는 이유는 유효한 정보를 제공하기 위해서는 처음에 GPS를 잡아내야만 하기 때문
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
                    if(pref.getString("setting_fore_vibe","").equals("on")){
                        mVibe.vibrate(500);
                    }
                    else{
                        Toast.makeText(ar_activity.this, "진동 꺼짐",Toast.LENGTH_LONG).show();
                    }
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
        if(distance<25){
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
            staticValues.mLastLatLong = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        createLocationRequest();
        locationListener=new mLoactionListener();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

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

    @Override
    public void orientation(Double AZIMUTH, Double PITCH, Double ROLL) {
        fa.arrow.setRotation((float)(staticValues.last_bearing-(double)AZIMUTH));
    }
}
