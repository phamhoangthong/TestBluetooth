package com.example.ht_laptop.imu;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private LocationManager locationManager;
    private LocationListener locationListener;

    private SensorManager mSensorManager;
    private Sensor mAcc;
    private Sensor mGyro;
    private Sensor mComp;

    float angle_x = 0;
    float angle_y = 0;
    float angle_z = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mComp = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mComp, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.equals(mAcc)) {
            float[] mData;
            mData = event.values;
            TextView textView = (TextView)findViewById(R.id.textView1);
            textView.setText("Acc x : " + Float.toString(mData[0]));
            textView = (TextView)findViewById(R.id.textView2);
            textView.setText("Acc y : " + Float.toString(mData[1]));
            textView = (TextView)findViewById(R.id.textView3);
            textView.setText("Acc z : " + Float.toString(mData[2]));
        } else if(event.sensor.equals(mGyro)) {
            float[] mData;
            mData = event.values;
            TextView textView = (TextView)findViewById(R.id.textView4);
            angle_x += mData[0];
            textView.setText("Gyro x : " + Float.toString(angle_x));
            textView = (TextView)findViewById(R.id.textView5);
            angle_y += mData[1];
            textView.setText("Gyro y : " + Float.toString(angle_y));
            textView = (TextView)findViewById(R.id.textView6);
            angle_z += mData[2];
            textView.setText("Gyro z : " + Float.toString(angle_z));
        } else if(event.sensor.equals(mComp)) {
            float[] mData;
            mData = event.values;
            TextView textView = (TextView)findViewById(R.id.textView7);
            textView.setText("Compass x : " + Float.toString(mData[0]));
            textView = (TextView)findViewById(R.id.textView8);
            textView.setText("Compass y : " + Float.toString(mData[1]));
            textView = (TextView)findViewById(R.id.textView9);
            textView.setText("Compass z : " + Float.toString(mData[2]));
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}