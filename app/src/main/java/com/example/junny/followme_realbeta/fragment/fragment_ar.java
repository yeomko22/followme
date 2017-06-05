package com.example.junny.followme_realbeta.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.junny.followme_realbeta.R;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by junny on 2017. 6. 2..
 */

public class fragment_ar extends android.support.v4.app.Fragment implements SurfaceHolder.Callback{
    private CameraDevice camera;
    private SurfaceView mCameraView;
    public ImageView arrow;
    private SurfaceHolder mCameraHolder;
    private Camera mCamera;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ar, container, false);

        mCameraView=(SurfaceView)view.findViewById(R.id.cameraView);
        arrow=(ImageView)view.findViewById(R.id.arrow);
        init();
        return view;
    }
    private void init(){
        final int CAMERA_PERMISSION_REQUEST_CODE = 1;
        int permissionCheck= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
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
