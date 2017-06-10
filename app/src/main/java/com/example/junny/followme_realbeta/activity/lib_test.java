package com.example.junny.followme_realbeta.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.sensors.Orientation;
import com.example.junny.followme_realbeta.utils.OrientationSensorInterface;

import java.util.ArrayList;

public class lib_test extends AppCompatActivity implements SensorEventListener, OrientationSensorInterface {
    private ImageView lib_arrow;
    private ImageView sensor_arrow;
    private TextView lib_accuracy;
    private TextView sensor_accuracy;
    private TextView lib_value;
    private TextView sensor_value;

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

    private Orientation orientationSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_test);

        lib_arrow=(ImageView)findViewById(R.id.lib_arrow);
        sensor_arrow=(ImageView)findViewById(R.id.sensor_arrow);
        lib_accuracy=(TextView)findViewById(R.id.lib_accuracy);
        sensor_accuracy=(TextView)findViewById(R.id.sensor_accuracy);
        lib_value=(TextView)findViewById(R.id.lib_value);
        sensor_value=(TextView)findViewById(R.id.sensor_value);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelSensor,SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagneticSensor,SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

        orientationSensor = new Orientation(lib_test.this, this);

        //------Turn Orientation sensor ON-------
        // set tolerance for any directions
        orientationSensor.init(1.0, 1.0, 1.0);
        orientationSensor.on(0);
    }

    @Override
    public void orientation(Double AZIMUTH, Double PITCH, Double ROLL) {
        String azi=Double.toString(AZIMUTH);
        Log.e("라이브러리 값",azi);
        if(lib_value!=null){
            lib_value.setText(azi);
        }
        if(lib_arrow!=null){
            lib_arrow.setRotation(Float.parseFloat(azi));
        }
    }

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
                sensor_arrow.setRotation(rotation_angle);
                sensor_value.setText(Float.toString(rotation_angle));
            }
            azimuth.clear();
            azi_sum=0;
            count+=1;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onStop(){
        super.onStop();
        mSensorManager.unregisterListener(this);
        orientationSensor.off();
    }
}
