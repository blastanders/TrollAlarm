package com.trollalarm.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;


public class EditAlarmActivity extends ActionBarActivity {

    public static final String EXTRA_ALARMITEM = "com.trollalarm.app.EXTRA_ALARMITEM";
    private static final int SELECT_PICTURE = 1;

    private AlarmItem alarm = null;

    private ToggleButton mSun = null;
    private ToggleButton mMon = null;
    private ToggleButton mTue = null;
    private ToggleButton mWed = null;
    private ToggleButton mThu = null;
    private ToggleButton mFri = null;
    private ToggleButton mSat = null;
    private TimePicker mTimePicker = null;
    private TextView mMethodName = null;
    private TextView mMethodDesc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        Intent intent = getIntent();
        alarm = intent.getParcelableExtra(EXTRA_ALARMITEM);

        mSun = (ToggleButton)findViewById(R.id.toggle_main_sun);
        mMon = (ToggleButton)findViewById(R.id.toggle_main_mon);
        mTue = (ToggleButton)findViewById(R.id.toggle_main_tue);
        mWed = (ToggleButton)findViewById(R.id.toggle_main_wed);
        mThu = (ToggleButton)findViewById(R.id.toggle_main_thu);
        mFri = (ToggleButton)findViewById(R.id.toggle_main_fri);
        mSat = (ToggleButton)findViewById(R.id.toggle_main_sat);
        mTimePicker = (TimePicker)findViewById(R.id.timepicker_main);
        mMethodName = (TextView)findViewById(R.id.text_main_methodname);
        mMethodDesc = (TextView)findViewById(R.id.text_main_methoddesc);

        findViewById(R.id.checkedTextView_method1).setOnClickListener(new MethodClickListener(1));
        findViewById(R.id.checkedTextView_method2).setOnClickListener(new MethodClickListener(2));
        findViewById(R.id.checkedTextView_method3).setOnClickListener(new MethodClickListener(3));
        findViewById(R.id.checkedTextView_method4).setOnClickListener(new MethodClickListener(4));

        if(alarm == null) {
            alarm = new AlarmItem();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            alarm.hour = calendar.get(Calendar.HOUR);
            alarm.minute = calendar.get(Calendar.MINUTE);
        }

        mSun.setChecked((alarm.scheduleFlags & (1 << 6)) == (1 << 6));
        mMon.setChecked((alarm.scheduleFlags & (1 << 5)) == (1 << 5));
        mTue.setChecked((alarm.scheduleFlags & (1 << 4)) == (1 << 4));
        mWed.setChecked((alarm.scheduleFlags & (1 << 3)) == (1 << 3));
        mThu.setChecked((alarm.scheduleFlags & (1 << 2)) == (1 << 2));
        mFri.setChecked((alarm.scheduleFlags & (1 << 1)) == (1 << 1));
        mSat.setChecked((alarm.scheduleFlags & (1 << 0)) == (1 << 0));

        mTimePicker.setCurrentHour(alarm.hour);
        mTimePicker.setCurrentMinute(alarm.minute);

        mMethodName.setText(alarm.method.getName());
        mMethodDesc.setText(alarm.method.getDesc());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_changePhoto) {
            String photoPath = "NotSet";

            if(photoPath.equals("NotSet"))
            {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra
                        (
                                Intent.EXTRA_INITIAL_INTENTS,
                                new Intent[] { takePhotoIntent }
                        );

                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
            return true;
        } else if (id == R.id.action_save) {
            saveAlarm();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveAlarm() {
        alarm.hour = mTimePicker.getCurrentHour();
        alarm.minute = mTimePicker.getCurrentMinute();
        alarm.scheduleFlags =   ((mSun.isChecked() ? 1 : 0) << 6) |
                                ((mMon.isChecked() ? 1 : 0) << 5) |
                                ((mTue.isChecked() ? 1 : 0) << 4) |
                                ((mWed.isChecked() ? 1 : 0) << 3) |
                                ((mThu.isChecked() ? 1 : 0) << 2) |
                                ((mFri.isChecked() ? 1 : 0) << 1) |
                                ((mSat.isChecked() ? 1 : 0) << 0);
        alarm.isOn = 1;

        scheduleAlarm(alarm);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_ALARMITEM, alarm);
        startActivity(intent);
        finish();
    }

    public void scheduleAlarm(AlarmItem alarm)
    {
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARMITEM, alarm);
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
        Toast.makeText(this, "Alarm Scheduled in " + (int)intervalInSec/60/60 + " hours " +(int)(intervalInSec/60)%60 + " minutes " + (int)intervalInSec%60 , Toast.LENGTH_LONG).show();

    }

    private class MethodClickListener implements View.OnClickListener
    {

        private AlarmMethod method;

        private MethodClickListener(int methodNumber) {
            method = AlarmMethod.getMethodFromID(methodNumber);
        }

        @Override
        public void onClick(View v) {
            ((TextView)findViewById(R.id.text_main_methodname)).setText(method.getName());
            ((TextView)findViewById(R.id.text_main_methoddesc)).setText(method.getDesc());
            findViewById(R.id.layout_chosemethod).setVisibility(View.GONE);

            String photoPath = "NotSet";

            if(this.method.getID() == 4)
            {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                String defaultValue = "NotSet";
                photoPath = sharedPref.getString(getString(R.string.preference_file_key), defaultValue);

                if(photoPath.equals("NotSet"))
                {
                    Intent pickIntent = new Intent();
                    pickIntent.setType("image/*");
                    pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
                    Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                    chooserIntent.putExtra
                            (
                                    Intent.EXTRA_INITIAL_INTENTS,
                                    new Intent[] { takePhotoIntent }
                            );

                    startActivityForResult(chooserIntent, SELECT_PICTURE);
                }else {
                    alarm.method = this.method;
                }

            }else{
                alarm.method = this.method;
            }
        }
    }

    public void chooseMethod(View view)
    {
        findViewById(R.id.layout_chosemethod).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            Uri _uri = data.getData();

            //User had pick an image.
            Cursor cursor = getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();

            //Link to the image
            final String imageFilePath = cursor.getString(0);
            cursor.close();
            editor.putString(getString(R.string.preference_file_key), imageFilePath);
            editor.commit();

            Toast.makeText(this, imageFilePath, Toast.LENGTH_LONG);
            alarm.method = AlarmMethod.getMethodFromID(4);
            Log.i("MainActivity:", imageFilePath);
        }else {
            Log.i("MainActivity:", "FAILED to save photo path");

        }
    }

}
