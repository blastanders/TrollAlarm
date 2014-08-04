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

import com.trollalarm.app.LockscreenAlarmActivity;
import com.trollalarm.app.R;


public class AlarmShakeFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorMgr;
    private Sensor mAccelerometer;
    private long lastUpdate;

    private float last_x = 0, last_y = 0 ,last_z = 0;

    private TextView mView;
    private float shakeCountDown = 30.0f;

    public static AlarmShakeFragment newInstance() {
        AlarmShakeFragment fragment = new AlarmShakeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public AlarmShakeFragment() {
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
        View root = inflater.inflate(R.layout.fragment_shake, container, false);
        mView = (TextView)root.findViewById(R.id.textview_fragment_shake);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMgr.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {

            if(shakeCountDown <= 0) return;

            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float dot = x*last_x + y*last_y + z*last_z;

                if (dot < 0) {
                    shakeCountDown -= 0.5f;
                    mView.setText((int) shakeCountDown + "");

                    if(shakeCountDown <= 0)
                    {
                        ((LockscreenAlarmActivity)getActivity()).stopAlarm();
                        sensorMgr.unregisterListener(this);
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}