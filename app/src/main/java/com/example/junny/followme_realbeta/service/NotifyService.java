package com.example.junny.followme_realbeta.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.activity.MapsActivity;

public class NotifyService extends Service {
    NotificationManager mNotiManger;
    ServiceThread mThread;
    Notification mNoti;


    public NotifyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    //백그라운드에서 실행되는 동작들이 들어
    public void onCreate() {
        super.onCreate();
    }
    //검사는 쓰레드에서 한다 텍스트 설정은 검사가 맞을 때 스테틱에서 꺼내와서 쓰레드에서 해준다
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotiManger=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        MyServiceHandler myServiceHandler=new MyServiceHandler();
        mThread=new ServiceThread(myServiceHandler);
        mThread.start();
        return START_STICKY;
    }

    @Override
    //서비스 종료시 실행되는 함수
    public void onDestroy() {
        mThread.stopForever();
        super.onDestroy();
    }

    class MyServiceHandler extends Handler{
        Intent intent;
        PendingIntent pendingIntent;
        public MyServiceHandler(){
            intent = new Intent(NotifyService.this, MapsActivity.class);
            pendingIntent = PendingIntent.getActivity(NotifyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }

        public void set_noti(String text){
            mNoti=new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("팔로미 알림")
                    .setContentText(text)
                    .setSmallIcon(R.drawable.arrow)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();
        }

        public void handleMessage(android.os.Message msg) {
            if(mNoti==null){
                mNoti=new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("팔로미 알림")
                        .setContentText("초기화")
                        .setSmallIcon(R.drawable.arrow)
                        .setTicker("알림!!!")
                        .setContentIntent(pendingIntent)
                        .build();
            }

            mNoti.flags=Notification.DEFAULT_VIBRATE;

            //확인하면 자동으로 알림이 제거 되도록
            mNoti.flags = Notification.FLAG_AUTO_CANCEL;
            mNotiManger.notify(777, mNoti);
        }
    };
}












