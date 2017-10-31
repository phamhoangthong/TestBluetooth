package com.example.phamh.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    String mprovider;
    MyGPSListen mGPS;
    MyStoreData mStoreData;
    //PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        //wakeLock.acquire();
        mStoreData = new MyStoreData(getApplicationContext());
        if(mStoreData.isExternalStorageWritable() == false) {
            Log.i("MDEBUG", "Storage unwritable");
        } else {
            Log.i("MDEBUG", "Storage writable");
        }
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                Double[] data = (Double[])msg.obj;
                TextView textView = (TextView) findViewById(R.id.textViewGPSLongitude);
                textView.setText("Longitude : " + Double.toString(data[0]));
                textView = (TextView) findViewById(R.id.textViewGPSLatitude);
                textView.setText("Latitude : " + Double.toString(data[1]));
                String temp = "";
                Calendar cal = Calendar.getInstance();
                temp += "|" + Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
                temp += ":" + Integer.toString(cal.get(Calendar.MINUTE));
                temp += ":" + Integer.toString(cal.get(Calendar.SECOND));
                temp += "-" + Double.toString(data[1]);
                temp += "," + Double.toString(data[0]) + "|" + '\n';
                mStoreData.writeFile(temp);
            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mGPS = new MyGPSListen(mHandler);

        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);
        mprovider = LocationManager.GPS_PROVIDER;
        Log.i("MDEBUG", mprovider);
        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 1000, 1, mGPS);
        }
    }
    @Override
    protected void onDestroy() {
        //wakeLock.release();
        super.onDestroy();
    }
}

class MyStoreData {
    public MyStoreData(Context context) {
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void writeFile(String str) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/DataGPS.txt"),true));
            out.write(str + '\r' + '\n');
            out.close();
        } catch (Exception ex) {
        }
    }
}

class MyGPSListen implements LocationListener {

    private Handler mHandler;

    public MyGPSListen(Handler handler) {
        mHandler = handler;
    }
    @Override
    public void onLocationChanged(Location location) {
        Double[] mData = new Double[2];
        mData[0] = location.getLongitude();
        mData[1] = location.getLatitude();
        Message msg = mHandler.obtainMessage();
        msg.obj = mData;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onProviderDisabled(String provider) {
        /*
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
                */
    }

    @Override
    public void onProviderEnabled(String provider) {
        /*
        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
                */
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}
