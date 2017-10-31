package com.example.ht_laptop.my_imu_test_1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MySensorEventListener mySensorEventListener;
    private SensorManager mSensorManager;
    private Sensor mAcc;
    private Sensor mGyro;
    private Sensor mComp;

    private Float[] offset_acc;
    private Float[] value_acc;
    private Float[] old_value_acc;
    private Float[] max_value_calibration_acc;
    private Float[] min_value_calibration_acc;
    private boolean flag_busy_calibration_acc;
    private int limit_sample_calibration_acc;
    private int number_sample_calibration_acc;
    private float time_calibration_acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        offset_acc = new Float[3];
        offset_acc[0] = 0.0f;
        offset_acc[1] = 0.0f;
        offset_acc[2] = 0.0f;

        value_acc = new Float[3];
        value_acc[0] = 0.0f;
        value_acc[1] = 0.0f;
        value_acc[2] = 0.0f;

        old_value_acc = new Float[3];
        old_value_acc[0] = 0.0f;
        old_value_acc[1] = 0.0f;
        old_value_acc[2] = 0.0f;

        max_value_calibration_acc = new Float[3];
        max_value_calibration_acc[0] = 0.0f;
        max_value_calibration_acc[1] = 0.0f;
        max_value_calibration_acc[2] = 0.0f;

        min_value_calibration_acc = new Float[3];
        min_value_calibration_acc[0] = 0.0f;
        min_value_calibration_acc[1] = 0.0f;
        min_value_calibration_acc[2] = 0.0f;

        flag_busy_calibration_acc = false;
        number_sample_calibration_acc = 0;
        limit_sample_calibration_acc = 500;
        time_calibration_acc = 0.0f;

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Float[] data = (Float[]) msg.obj;
                if(data[0] == 1.0f) {
                    if(flag_busy_calibration_acc == true) {
                        value_acc[0] = data[1];
                        value_acc[1] = data[2];
                        value_acc[2] = data[3];
                        float time = data[4];
                        if(number_sample_calibration_acc == limit_sample_calibration_acc) {
                            offset_acc[0] += time*(value_acc[0] + old_value_acc[0])/2.0f;
                            offset_acc[1] += time*(value_acc[1] + old_value_acc[1])/2.0f;
                            if(value_acc[0] > max_value_calibration_acc[0]) {
                                max_value_calibration_acc[0] = value_acc[0];
                            }
                            if(value_acc[1] > max_value_calibration_acc[1]) {
                                max_value_calibration_acc[1] = value_acc[1];
                            }
                            if(value_acc[0] < min_value_calibration_acc[0]) {
                                min_value_calibration_acc[0] = value_acc[0];
                            }
                            if(value_acc[1] < min_value_calibration_acc[1]) {
                                min_value_calibration_acc[1] = value_acc[1];
                            }
                            time_calibration_acc += time;
                            //Log.i("Debug", "Temp Offset acc : X - " + Float.toString(offset_acc[0]) + " | Y - " + Float.toString(offset_acc[1]));
                            offset_acc[0] = offset_acc[0]/time_calibration_acc;
                            offset_acc[1] = offset_acc[1]/time_calibration_acc;
                            max_value_calibration_acc[0] = offset_acc[0] + 2.0f*(max_value_calibration_acc[0] - offset_acc[0]);
                            max_value_calibration_acc[1] = offset_acc[1] + 2.0f*(max_value_calibration_acc[1] - offset_acc[1]);
                            min_value_calibration_acc[0] = offset_acc[0] + 2.0f*(min_value_calibration_acc[0] - offset_acc[0]);
                            min_value_calibration_acc[1] = offset_acc[1] + 2.0f*(min_value_calibration_acc[1] - offset_acc[1]);
                            Log.i("Debug", "Offset acc : X - " + Float.toString(offset_acc[0]) + " | Y - " + Float.toString(offset_acc[1]));
                            old_value_acc[0] = 0.0f;
                            old_value_acc[1] = 0.0f;
                            old_value_acc[2] = 0.0f;
                            number_sample_calibration_acc = 0;
                            flag_busy_calibration_acc = false;
                        } else if(number_sample_calibration_acc == 0) {
                            old_value_acc[0] = value_acc[0];
                            old_value_acc[1] = value_acc[1];
                            old_value_acc[2] = value_acc[2];
                            max_value_calibration_acc[0] = value_acc[0];
                            max_value_calibration_acc[1] = value_acc[1];
                            max_value_calibration_acc[2] = value_acc[2];
                            min_value_calibration_acc[0] = value_acc[0];
                            min_value_calibration_acc[1] = value_acc[1];
                            min_value_calibration_acc[2] = value_acc[2];
                            number_sample_calibration_acc++;
                        } else {
                            offset_acc[0] += time*(value_acc[0] + old_value_acc[0])/2.0f;
                            offset_acc[1] += time*(value_acc[1] + old_value_acc[1])/2.0f;
                            if(value_acc[0] > max_value_calibration_acc[0]) {
                                max_value_calibration_acc[0] = value_acc[0];
                            }
                            if(value_acc[1] > max_value_calibration_acc[1]) {
                                max_value_calibration_acc[1] = value_acc[1];
                            }
                            if(value_acc[0] < min_value_calibration_acc[0]) {
                                min_value_calibration_acc[0] = value_acc[0];
                            }
                            if(value_acc[1] < min_value_calibration_acc[1]) {
                                min_value_calibration_acc[1] = value_acc[1];
                            }
                            time_calibration_acc += time;
                            //Log.i("Debug", "Temp Offset acc : X - " + Float.toString(offset_acc[0]) + " | Y - " + Float.toString(offset_acc[1]));
                            old_value_acc[0] = value_acc[0];
                            old_value_acc[1] = value_acc[1];
                            old_value_acc[2] = value_acc[2];
                            number_sample_calibration_acc++;
                        }
                    } else {
                        if((data[1] > max_value_calibration_acc[0]) || (data[1] < min_value_calibration_acc[0])) {
                            value_acc[0] = data[1] - offset_acc[0];
                        } else {
                            value_acc[0] = 0.0f;
                        }
                        if((data[2] > max_value_calibration_acc[1]) || (data[2] < min_value_calibration_acc[1])) {
                            value_acc[1] = data[2] - offset_acc[1];
                        } else {
                            value_acc[1] = 0.0f;
                        }
                        //value_acc[1] = data[2] - offset_acc[1];
                        value_acc[2] = data[3] - offset_acc[2];
                        float time = data[4];
                        TextView textView = (TextView)findViewById(R.id.textViewAccX);
                        textView.setText(Float.toString(value_acc[0]));
                        textView = (TextView)findViewById(R.id.textViewAccY);
                        textView.setText(Float.toString(value_acc[1]));
                        textView = (TextView)findViewById(R.id.textViewAccZ);
                        textView.setText(Float.toString(value_acc[2]));
                        textView = (TextView)findViewById(R.id.textViewTimingAcc);
                        textView.setText(Float.toString(time));
                    }
                } else if(data[0] == 2.0f) {
                    TextView textView = (TextView)findViewById(R.id.textViewCompassX);
                    textView.setText(Float.toString(data[1]));
                    textView = (TextView)findViewById(R.id.textViewCompassY);
                    textView.setText(Float.toString(data[2]));
                    textView = (TextView)findViewById(R.id.textViewCompassZ);
                    textView.setText(Float.toString(data[3]));
                    textView = (TextView)findViewById(R.id.textViewTimingCompass);
                    textView.setText(Float.toString(data[4]));
                }
            }
        };

        mySensorEventListener = new MySensorEventListener(mHandler);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mComp = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Button mButtonCalibrationAcc = (Button)findViewById(R.id.button_calibration_acc);
        mButtonCalibrationAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_busy_calibration_acc == false) {
                    flag_busy_calibration_acc = true;
                    number_sample_calibration_acc = 0;
                    limit_sample_calibration_acc = 500;
                    time_calibration_acc = 0.0f;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mySensorEventListener, mAcc, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mySensorEventListener, mComp, SensorManager.SENSOR_DELAY_FASTEST);
        //mSensorManager.registerListener(mySensorEventListener, mGyro, SensorManager.SENSOR_DELAY_FASTEST);

        //mSensorManager.registerListener(mySensorEventListener, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(mySensorEventListener, mComp, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mySensorEventListener);
    }
}

class MySensorEventListener implements SensorEventListener {

    private Handler mHandler;
    private long timing_acc, timing_gyro, timing_compass;

    public MySensorEventListener(Handler handler) {
        mHandler = handler;
        timing_acc = android.os.SystemClock.elapsedRealtimeNanos();
        timing_gyro = timing_acc;
        timing_compass = timing_acc;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Message msg = mHandler.obtainMessage();
            Float[] data = new Float[5];
            data[0] = 1.0f;
            data[1] = event.values[0];
            data[2] = event.values[1];
            data[3] = event.values[2];
            long temp = android.os.SystemClock.elapsedRealtimeNanos();
            data[4] = ((float)(temp - timing_acc))/1000000000.0f;
            timing_acc = temp;
            msg.obj = data;
            mHandler.sendMessage(msg);
        } else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Message msg = mHandler.obtainMessage();
            Float[] data = new Float[5];
            data[0] = 2.0f;
            data[1] = event.values[0];
            data[2] = event.values[1];
            data[3] = event.values[2];
            long temp = android.os.SystemClock.elapsedRealtimeNanos();
            data[4] = ((float)(temp - timing_compass))/1000000000.0f;
            timing_compass = temp;
            msg.obj = data;
            mHandler.sendMessage(msg);
        } else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Message msg = mHandler.obtainMessage();
            Float[] data = new Float[5];
            data[0] = 3.0f;
            data[1] = event.values[0];
            data[2] = event.values[1];
            data[3] = event.values[2];
            long temp = System.nanoTime();
            data[4] = ((float)(temp - timing_gyro))/1000000000.0f;
            timing_gyro = temp;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
