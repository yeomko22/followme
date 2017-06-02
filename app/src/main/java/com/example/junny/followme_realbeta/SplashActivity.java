package com.example.junny.followme_realbeta;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1500);
                    finish();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
