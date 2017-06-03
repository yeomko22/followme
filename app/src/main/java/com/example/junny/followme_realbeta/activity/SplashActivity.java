package com.example.junny.followme_realbeta.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.junny.followme_realbeta.R;

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
