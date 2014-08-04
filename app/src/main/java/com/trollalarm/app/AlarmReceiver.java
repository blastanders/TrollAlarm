package com.trollalarm.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Aaron on 07-06-14.
 */
public class AlarmReceiver extends BroadcastReceiver
{


    @Override
    public void onReceive(Context context, Intent intent)
    {

        // TODO Auto-generated method stub

        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone

        AlarmItem alarm = intent.getParcelableExtra(EditAlarmActivity.EXTRA_ALARMITEM);
        // Show the toast  like in above screen shot
        //Toast.makeText(context, "Alarm Alarm Alarm Alarm Alarm Alarm Alarm", Toast.LENGTH_LONG).show();

        Intent intent1 = new Intent(context, LockscreenAlarmActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent1.putExtra(EditAlarmActivity.EXTRA_ALARMITEM, alarm);
        context.startActivity(intent1);


    }

}