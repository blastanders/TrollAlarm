package com.trollalarm.app.fragments;

/**
 * Created by Aaron on 07-06-14.
 */

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.trollalarm.app.LockscreenAlarmActivity;
import com.trollalarm.app.R;

import java.text.DecimalFormat;


public class AlarmTossFragment extends Fragment implements SensorEventListener {

    public static final float THRESHHOLD_LOW = 15.00f;


    private SensorManager sensorMgr;
    private Sensor mAccelerometer;
    private long lastUpdate;
    private long tossStart;
    private TextView mView;


    public static AlarmTossFragment newInstance() {
        AlarmTossFragment fragment = new AlarmTossFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public AlarmTossFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorMgr = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_toss, container, false);
        mView = (TextView)root.findViewById(R.id.textview_fragment_toss);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMgr.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            if(tossStart < 0) return;
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 0) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float acc = x*x + y*y + z*z;

               // Log.i("acc: ", acc+"");

                if (acc < THRESHHOLD_LOW) {
                   if(tossStart == 0)
                       tossStart = curTime;
                } else{
                    if(tossStart != 0)
                    {
                        float timeDifference = (curTime - tossStart)/1000.0f;
                        float height = ((timeDifference)*(timeDifference)*9.79f)/4.0f;
                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(2);
                        //mView.setText(df.format(height) + "m");
                        mView.setText(timeDifference+"");
                        if(height > 0.5f) {
                            ((LockscreenAlarmActivity) getActivity()).stopAlarm();
                            tossStart = -1;
                            sensorMgr.unregisterListener(this);
                            /*tossStart = 0;
                            Toast.makeText(getActivity(), "You did make it.Try again",Toast.LENGTH_LONG).show();*/
                        }else{
                            tossStart = 0;
                            Toast.makeText(getActivity(), "You didn't make it..",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}