package com.trollalarm.app;

import com.trollalarm.app.fragments.AlarmCodeFragment;
import com.trollalarm.app.fragments.AlarmPhotoFragment;
import com.trollalarm.app.fragments.AlarmShakeFragment;
import com.trollalarm.app.fragments.AlarmTossFragment;
import com.trollalarm.app.methods.AlarmMethod_Code;
import com.trollalarm.app.util.SystemUiHider;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LockscreenAlarmActivity extends Activity {
    private AlarmItem alarm;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen_alarm);

        Intent intent = getIntent();
        alarm = intent.getParcelableExtra(EditAlarmActivity.EXTRA_ALARMITEM);

        //alarm = new AlarmItem();
        //alarm.method = AlarmMethod.getMethodFromID(4);

        assert(alarm != null);
        scheduleAlarm();

        ((TextView)findViewById(R.id.textView_lockscreen_instruction)).setText(alarm.method.getInstruction());

        Fragment fragment = null;

        switch (alarm.method.getID())
        {
            case 1:
                fragment = AlarmTossFragment.newInstance();
                break;
            case 2:
                fragment = AlarmCodeFragment.newInstance(((AlarmMethod_Code)alarm.method).getCode());
                break;
            case 3:
                fragment = AlarmShakeFragment.newInstance();
                break;
            case 4:
                fragment = AlarmPhotoFragment.newInstance("","");
                break;
        }



        getFragmentManager().beginTransaction().add(R.id.fragment_lockscreen, fragment).commit();
        playAlarm();
    }

    public void scheduleAlarm()
    {
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(EditAlarmActivity.EXTRA_ALARMITEM, alarm);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, alarm.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the alarm to start at 8:30 a.m.
        long minInterval = 999999999999999999L;
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
        calendar.set(Calendar.MINUTE, alarm.minute);
        calendar.set(Calendar.SECOND,0);

        if((alarm.scheduleFlags & (1<<6)) == (1<<6)) {
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
            minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
        calendar.set(Calendar.MINUTE, alarm.minute);
        calendar.set(Calendar.SECOND,0);
        if((alarm.scheduleFlags & (1<<5)) == (1<<5)) {
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
            minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
        calendar.set(Calendar.MINUTE, alarm.minute);
        calendar.set(Calendar.SECOND,0);
        if((alarm.scheduleFlags & (1<<4)) == (1<<4)) {
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
            if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
            minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
        calendar.set(Calendar.MINUTE, alarm.minute);
        calendar.set(Calendar.SECOND,0);
        if((alarm.scheduleFlags & (1<<3)) == (1<<3)) {
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
            if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
            minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
        calendar.set(Calendar.MINUTE, alarm.minute);
        calendar.set(Calendar.SECOND,0);
        if((alarm.scheduleFlags & (1<<2)) == (1<<2)) {
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
            if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
            minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
        calendar.set(Calendar.MINUTE, alarm.minute);
        calendar.set(Calendar.SECOND,0);
        if((alarm.scheduleFlags & (1<<1)) == (1<<1)) {
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
            if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
            minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
        calendar.set(Calendar.MINUTE, alarm.minute);
        calendar.set(Calendar.SECOND,0);
        if((alarm.scheduleFlags & (1<<0)) == (1<<0)) {
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
            if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
            minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
        }
        if(minInterval >= 999999999999999999L) {
            return;
        }

        alarmMgr.set(AlarmManager.RTC_WAKEUP, minInterval, alarmIntent);
        long intervalInSec = (minInterval-System.currentTimeMillis())/1000;
        Toast.makeText(this, "Alarm Scheduled in " + (int) intervalInSec / 60 / 60 + " hours " + (int) (intervalInSec / 60) % 60 + " minutes " + (int) intervalInSec % 60, Toast.LENGTH_LONG).show();

    }

    public void playAlarm(){
        mp = MediaPlayer.create(this, R.raw.beedoo_minions);

        AudioManager mAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) , AudioManager.FLAG_ALLOW_RINGER_MODES);
        //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1 , AudioManager.FLAG_ALLOW_RINGER_MODES);
        mp.setLooping(true);
        mp.setVolume(1.0f,1.0f);
        mp.start();
    }
    public void stopAlarm() {
        mp.stop();
        finish();
    }
}
