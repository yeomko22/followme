package com.example.junny.followme_realbeta;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class ar_activity extends AppCompatActivity implements SurfaceHolder.Callback{

    private CameraDevice camera;
    private SurfaceView mCameraView;
    private SurfaceHolder mCameraHolder;
    private android.hardware.Camera mCamera;
    private Button mStart;
    private ImageView arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity);

        mCameraView=(SurfaceView)findViewById(R.id.cameraView);
        arrow=(ImageView)findViewById(R.id.arrow);
        init();

        SensorManager mSensorManager=(SensorManager)getSystemService(Application.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        SensorEventListener mSensorListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.e("회전 감지 : ",Float.toString(event.values[1]*100));
                Log.e("정확도 : ",Float.toString(event.values[3]*100));
                arrow.setRotation(event.values[1]*200);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.e("정확도 변화","11");
            }
        };
        mSensorManager.registerListener(mSensorListener,sensor,SensorManager.SENSOR_DELAY_UI);
    }

    private void init(){
        final int CAMERA_PERMISSION_REQUEST_CODE = 1;
        int permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(ar_activity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        mCamera= android.hardware.Camera.open();
        mCamera.setDisplayOrientation(90);

        mCameraHolder=mCameraView.getHolder();
        mCameraHolder.addCallback(this);
        mCameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            if(mCamera == null){
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        }catch(IOException e){
            StringWriter sw=new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString=sw.toString();
            Log.e("IO 예외 발생",exceptionAsString);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCameraHolder.getSurface()==null){
            return;
        }
        try{
            mCamera.stopPreview();
        }catch (Exception e){
            StringWriter sw=new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString=sw.toString();
            Log.e("IO 예외 발생",exceptionAsString);
        }
        Camera.Parameters parameters=mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
            parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        mCamera.setParameters(parameters);

        try{
            mCamera.setPreviewDisplay(mCameraHolder);
            mCamera.startPreview();
        }
        catch(Exception e){
            StringWriter sw=new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString=sw.toString();
            Log.e("IO 예외 발생",exceptionAsString);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }
}
