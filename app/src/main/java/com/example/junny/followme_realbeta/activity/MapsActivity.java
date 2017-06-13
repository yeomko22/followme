package com.example.junny.followme_realbeta.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.interfaces.ReverseGeo;
import com.example.junny.followme_realbeta.response.ReverseGeoRes;
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

import java.io.PrintWriter;
import java.io.StringWriter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.junny.followme_realbeta.staticValues.gMapKey;
import static com.example.junny.followme_realbeta.staticValues.mLastLatLong;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //구글 맵 변수들
    private GoogleMap mMap;
    private Geocoder geocoder;
    private Marker mMarker;

    //구글 장소 리퀘스트 변수들
    private MyLoactionListener myLocationListener;
    private LocationRequest myLocationRequest;


    //통신 요소들
    private GoogleApiClient mGoogleApiClient;
    private Retrofit retrofit;

    //액티비티 뷰 요소들
    private TextView start_point;

    //네트워크, GPS 사용 가능 여부 확인 변수들
    private boolean isGPSEnable=false;
    private boolean isNetworkEnable=false;
    private LocationManager locationManager;
    private ConnectivityManager conn_manager;
    private boolean initi=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent=new Intent(MapsActivity.this, SplashActivity.class);
        startActivity(intent);

        conn_manager=(ConnectivityManager)MapsActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        locationManager=(LocationManager)MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        start_point=(TextView)findViewById(R.id.start_point);

        staticValues.mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        staticValues.mapFragment.getMapAsync(this);

        //레트로핏 객체를 만들 때에 기본적인 유알엘을 정의
        retrofit=new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        if(!check_env()){
            Log.e("환경 설정이 안되어있음","11");
            initi=true;
            return;
        }
        else{
            initi=true;
            Log.e("환경 설정 잘됨","11");
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            mGoogleApiClient.connect();
            //지오코더 셋팅
            if(geocoder==null){
                geocoder=new Geocoder(MapsActivity.this);
            }

            //T맵 api 연결
            TMapTapi tMapTapi = new TMapTapi(this);
            tMapTapi.setSKPMapAuthentication("4004a4c7-8e67-3c17-88d9-9799c613ecc7");
        }
    }
    protected boolean check_env(){
        NetworkInfo activeNetwork=conn_manager.getActiveNetworkInfo();
        if(activeNetwork!=null){
            if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI&&activeNetwork.isConnectedOrConnecting()){
                Log.e("와이파이","연결됨");
                isNetworkEnable=true;
            }
            else if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE&&activeNetwork.isConnectedOrConnecting()){
                Log.e("데이터","연결됨");
                isNetworkEnable=true;
            }
            else{
                Toast.makeText(MapsActivity.this,"네트워크 연결을 확인하세요",Toast.LENGTH_LONG).show();
                isNetworkEnable=false;
            }
        }
        else{
            Log.e("네트워크가 없음","!!");
            isNetworkEnable=false;
        }

        isGPSEnable=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnable){
            showGPSAlert();
        }
        Log.e("네트워크 결과",Boolean.toString(isNetworkEnable));
        Log.e("GPS 결과",Boolean.toString(isGPSEnable));

        if(isNetworkEnable&&isGPSEnable){
            return true;
        }
        else{
            if((!isNetworkEnable)&&(initi)){
                Toast.makeText(MapsActivity.this, "네트워크 연결을 확인하세요", Toast.LENGTH_LONG).show();
            }
            else{
                Log.e("네트워크",Boolean.toString(!isNetworkEnable));
                Log.e("이니시",Boolean.toString(initi));
            }
            return false;
        }
    }

    protected void showGPSAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setTitle("GPS 셋팅");
        alertDialog.setMessage("GPS를 설정해야만 서비스를 이용할 수 있습니다.\n설정창으로 이동하시겠습니까?");
        alertDialog.setPositiveButton("설정하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MapsActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    protected void onStart(){
        super.onStart();
    }
    protected void onStop(){
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //구글맵 설정 콜백
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    //구글 api 연결 콜백
    @Override
    public void onConnected(@Nullable Bundle connnectionHint) {
        Log.e("구글 API 연결됨","11");
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
        createLocationRequest();
        myLocationListener=new MyLoactionListener();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, myLocationRequest, myLocationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //현재 위치 못잡고 바로 도착지 검색 시도 시에 작동
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
        //환경 설정 안되있으면 멈춤
        if(!check_env()){
            return;
        }

        //환경설정 성공시 작동
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        v.startAnimation(anim);

        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }

        //구글 api 연결, onConnect 콜백 호출
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        //나머지 지오코더, 티맵 연결
        if(geocoder==null){
            geocoder=new Geocoder(MapsActivity.this);
        }

        //T맵 api 연결
        TMapTapi tMapTapi = new TMapTapi(this);
        tMapTapi.setSKPMapAuthentication("4004a4c7-8e67-3c17-88d9-9799c613ecc7");
    }

    //로케이션리퀘스트 생성 함수, 10초에 한번씩 감지하도록 설정
    protected void createLocationRequest() {
        myLocationRequest = new LocationRequest();
        myLocationRequest.setInterval(5000);
        myLocationRequest.setFastestInterval(2500);
        myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    class MyLoactionListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            float cur_accuracy=location.getAccuracy();
            Log.e("정확도",Float.toString(cur_accuracy));
            if(cur_accuracy>0&&cur_accuracy<=25){
                Log.e("유효","11");

                //잡아낸 변수 스태틱에 전달
                staticValues.mLastLocation=location;
                staticValues.mLastLatLong=new LatLng(location.getLatitude(),location.getLongitude());
                staticValues.mLastLat=location.getLatitude();
                staticValues.mLastLong=location.getLongitude();

                //지도 상에 현 위치 표시
                mMap.addMarker(new MarkerOptions().position(mLastLatLong));
                mMap.addMarker(new MarkerOptions().position(mLastLatLong).title("내 위치"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLong,18));

                //잡아낸 좌표로 통신해서 주소 값 받아오기 레트로핏
                ReverseGeo retro_geo=retrofit.create(ReverseGeo.class);
                Call<ReverseGeoRes> call = retro_geo.reverseGeo(gMapKey,"ko",Double.toString(staticValues.mLastLat)+","+Double.toString(staticValues.mLastLong));
                call.enqueue(new Callback<ReverseGeoRes>() {
                    @Override
                    public void onResponse(Call<ReverseGeoRes> call, Response<ReverseGeoRes> response) {
                        if(response.isSuccessful()){
                            ReverseGeoRes res = response.body();
                            Log.e("요청 보기",response.toString());
                            try{
                                String cur_address=res.getAddress();
                                cur_address=cur_address.replace("대한민국", "");
                                if(cur_address.contains("서울특별시")){
                                    cur_address=cur_address.replace("서울특별시","");
                                }
                                cur_address=cur_address.trim();
                                start_point.setText(cur_address);
                                staticValues.cur_address=cur_address;
                            }
                            catch(Exception e){
                                StringWriter sw = new StringWriter();
                                e.printStackTrace(new PrintWriter(sw));
                                String exceptionAsStrting = sw.toString();
                                Log.e("예외발생", exceptionAsStrting);
                            }
                        }
                        else{
                            Log.e("에러 메세지", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReverseGeoRes> call, Throwable t) {
                        Log.e("실패원인",t.toString());
                    }
                });

                //첫 번째 값 받아오고 센서 해지하기
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, myLocationListener);
                ((TextView)findViewById(R.id.loading_text)).setVisibility(View.INVISIBLE);
            }
            else{
                Log.e("무효",Float.toString(cur_accuracy));
            }
        }
    }
}
