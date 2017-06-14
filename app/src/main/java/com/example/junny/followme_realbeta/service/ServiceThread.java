package com.example.junny.followme_realbeta.service;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.junny.followme_realbeta.activity.ar_activity;
import com.example.junny.followme_realbeta.staticValues;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import static com.example.junny.followme_realbeta.staticValues.mGoogleApiClient;
import static com.example.junny.followme_realbeta.staticValues.mLastLatLong;
import static com.example.junny.followme_realbeta.staticValues.mLastLocation;
import static com.example.junny.followme_realbeta.staticValues.mLocationRequest;
import static com.example.junny.followme_realbeta.staticValues.static_cur_point;
import static com.example.junny.followme_realbeta.staticValues.walk_guide_latlng;
import static com.example.junny.followme_realbeta.staticValues.walk_guide_text;

/**
 * Created by junny on 2017. 6. 13..
 */

public class ServiceThread extends Thread{
    private NotifyService.MyServiceHandler handler;
    boolean isRun=true;

    //위치 체크 변수들
    private float cur_accuracy;
    private ArrayList<LatLng> cur_guide_latlng;
    private ArrayList<String> cur_guide_text;
    private mLoactionListener locationListener;


    public ServiceThread(NotifyService.MyServiceHandler handler){
        this.handler=handler;
        if(staticValues.static_cur_mode.equals("walk")){
            cur_guide_latlng= walk_guide_latlng;
            cur_guide_text= walk_guide_text;
        }
        else{
            cur_guide_latlng=staticValues.transit_guide_latlng;
            cur_guide_text=staticValues.transit_guide_text;
        }
    }
    public void stopForever(){
        synchronized (this){
            this.isRun=false;
        }
    }

    public void onConnected(@Nullable Bundle bundle) {


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


    //내가 지금 원하는 것은 백그라운드 쓰레드에서 쥐피에스를 돌리는 것, 그리고 원하는 결과가 나왔을 때 해당 값을 노티해주는 것
    public void run(){
        while(isRun){
            try{
                createLocationRequest();
                locationListener=new mLoactionListener();


                Thread.sleep(10000);
                handler.set_noti("내가 설정한 밸류");
                handler.sendEmptyMessage(0);
            }
            catch(Exception e){
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsStrting = sw.toString();
                Log.e("예외발생", exceptionAsStrting);
            }
        }
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    class mLoactionListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            cur_accuracy=location.getAccuracy();
            Log.e("정확도",Float.toString(cur_accuracy));
            if(cur_accuracy>0&&cur_accuracy<=25){
                Log.e("유효","11");

                //인식한 내 위치로 메모리상의 변수들 변경
                mLastLocation=location;
                mLastLatLong=new LatLng(location.getLatitude(),location.getLongitude());
                staticValues.mLastLat=location.getLatitude();
                staticValues.mLastLong=location.getLongitude();

                //다음 체크 포인트에 도착했는지를 확인, 도착했을 경우 변수들 변경
                if(check_position(mLastLatLong)){
                    //여기서 넘겨줘야 한다
                    handler.set_noti(Integer.toString(staticValues.static_cur_point)+". "+cur_guide_text.get(staticValues.static_cur_point-1));
                    handler.sendEmptyMessage(0);
                }
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
        to.setLatitude(cur_guide_latlng.get(staticValues.static_cur_point).latitude);
        to.setLongitude(cur_guide_latlng.get(static_cur_point).longitude);
        //이전 거리와 지금 거리의 차이 - 이동 거리 - 이걸 토탈에서 빼주자
        float distance=from.distanceTo(to);
        if(distance<25){
            static_cur_point+=1;
            if(cur_guide_latlng.size()>static_cur_point){
                //포인트 지점에 도착하면, 해당 지점과 시작점 사이의 거리만큼을 빼주면 된다 현재 거리와 별개 전체 거리에서
                //이전 키포인트에서 지금 키포인트사이의 거리만큼을 빼준다? 노노 직선거리잖아 직선과 곡선 차이때문에 나는 차이일 듯
                next.setLatitude(cur_guide_latlng.get(staticValues.static_cur_point).latitude);
                next.setLongitude(cur_guide_latlng.get(staticValues.static_cur_point).longitude);
                staticValues.last_bearing=to.bearingTo(next);
            }
            else{
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,locationListener);
            }
            return true;
        }
        else{
            return false;
        }
    }
}
