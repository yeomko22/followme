package com.example.junny.followme_realbeta;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.SystemClock;
import android.support.annotation.Nullable;

/**
 * Created by junny on 2017. 6. 2..
 */

public class tracker extends IntentService{
    private String name;
    private Context mContext;
    private LocationManager lm;

    public tracker(String name, Context mContext) {
        super(name);
        this.name=name;
        this.mContext=mContext;
        lm=(LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        lm.addTestProvider(name, true,true,true,true,true,true,true, Criteria.NO_REQUIREMENT,Criteria.ACCURACY_FINE);
        lm.setTestProviderEnabled(name,true);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
    //기본 로직 - 새로운 지점을 하나 만들어서 로케이션 매니저에 추가를 해준다

    public void pushLocation(double lat, double lon) {
        try {
            LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            Location mockLocation = new Location(name);
            long currentTime = System.currentTimeMillis();
            mockLocation.setLatitude(lat);
            mockLocation.setLongitude(lon);
            mockLocation.setTime(currentTime);
            mockLocation.setAccuracy(1.0f);
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

            lm.setTestProviderStatus(name, LocationProvider.AVAILABLE, mockLocation.getExtras(), currentTime);
            lm.setTestProviderLocation(name, mockLocation);
        } catch(RuntimeException e){
            e.printStackTrace();
        }
    }
}
