package com.trollalarm.app;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class MainActivity extends ListActivity {

    private class AlarmArrayAdapter extends BaseAdapter {
        private final LayoutInflater inflater;
        public ArrayList<AlarmItem> alarms;


        public AlarmArrayAdapter(Context context) {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            alarms = new ArrayList<AlarmItem>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View entry = null;

            if(position < alarms.size()) {
                entry = inflater.inflate(R.layout.item_alarms, parent, false);

                AlarmItem item = this.getItem(position);
                ((TextView) entry.findViewById(R.id.text_itemalarms_time)).setText(item.getTimeAsString());
                ((TextView) entry.findViewById(R.id.text_itemalarms_method)).setText(item.method.getName());
                ((Switch) entry.findViewById(R.id.switch_itemalarms)).setChecked(item.isOn == 1);

                ((Switch) entry.findViewById(R.id.switch_itemalarms)).setOnCheckedChangeListener(new SwitchClickListener(item));

                entry.findViewById(R.id.text_itemalarms_time).setOnClickListener(new AlarmClickListener(item));
                entry.findViewById(R.id.itemalarms_white_arrow).setOnClickListener(new AlarmClickListener(item));
                entry.findViewById(R.id.text_itemalarms_time).setOnLongClickListener(new AlarmLongClickListener(item));
            } else {
                entry = inflater.inflate(R.layout.item_newalarm, parent, false);

                entry.findViewById(R.id.text_itemnewalarm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newAlarm();
                    }
                });
            }

            return entry;
        }

        public int getCount() {
            return alarms.size() + 1;
        }

        public void clear() {
            alarms.clear();
        }

        public void addAll(ArrayList<AlarmItem> photos) {
            this.alarms.addAll(photos);
        }

        public void add(AlarmItem photo) {this.alarms.add(photo);}

        @Override
        public AlarmItem getItem(int index) {
            return alarms.get(index);
        }

        @Override
        public long getItemId(int index) {
            return 0;
        }
    }

    private AlarmArrayAdapter adapter;
    protected Context mContext;
    private AlarmDBHelper mDBHelper;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("MainActivity:", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new AlarmArrayAdapter(this);
        setListAdapter(adapter);

        mContext = this;
        mDBHelper = new AlarmDBHelper(this);

        Intent intent = getIntent();
        AlarmItem alarm = null;
        alarm = intent.getParcelableExtra(EditAlarmActivity.EXTRA_ALARMITEM);

        if(alarm != null) {
            if(alarm.id == -1) {
                SQLiteDatabase db = mDBHelper.getWritableDatabase();

                final String SQL_INSERT_ENTRY = "INSERT INTO alarms values(" +
                        "null," +
                        alarm.hour + "," +
                        alarm.minute + "," +
                        alarm.method.getID() + "," +
                        alarm.scheduleFlags + "," +
                        alarm.isOn +
                        ")";



                db.execSQL(SQL_INSERT_ENTRY);
            } else {
                SQLiteDatabase db = mDBHelper.getReadableDatabase();

                final String SQL_UPDATE_ENTRY = "UPDATE alarms " +
                        "SET " +
                        AlarmDBHelper.SQL_ALARM_HOUR + " = " + alarm.hour + "," +
                        AlarmDBHelper.SQL_ALARM_MINUTE + " = " + alarm.minute + "," +
                        AlarmDBHelper.SQL_ALARM_METHOD + " = " + alarm.method.getID() + "," +
                        AlarmDBHelper.SQL_ALARM_SCHEDULEFLAGS + " = " + alarm.scheduleFlags + "," +
                        AlarmDBHelper.SQL_ALARM_ISON + " = " + alarm.isOn + " " +
                        "WHERE " + AlarmDBHelper.SQL_ALARM_ID + " = " + alarm.id;
                db.execSQL(SQL_UPDATE_ENTRY);
            }
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        final String SQL_SELECT_ENTRIES = "SELECT * FROM alarms";
        Cursor c = db.rawQuery(SQL_SELECT_ENTRIES, null);

        if(c.moveToFirst()) {
            do {
                alarm = new AlarmItem();
                alarm.id = c.getInt(c.getColumnIndex(AlarmDBHelper.SQL_ALARM_ID));
                alarm.hour = c.getInt(c.getColumnIndex(AlarmDBHelper.SQL_ALARM_HOUR));
                alarm.minute = c.getInt(c.getColumnIndex(AlarmDBHelper.SQL_ALARM_MINUTE));
                alarm.scheduleFlags = c.getInt(c.getColumnIndex(AlarmDBHelper.SQL_ALARM_SCHEDULEFLAGS));
                alarm.method = AlarmMethod.getMethodFromID(c.getInt(c.getColumnIndex(AlarmDBHelper.SQL_ALARM_METHOD)));
                alarm.isOn = c.getInt(c.getColumnIndex(AlarmDBHelper.SQL_ALARM_ISON));
                adapter.add(alarm);
            } while(c.moveToNext());

            adapter.notifyDataSetChanged();
        }

        //upload ringtone to sd card first
        //http://stackoverflow.com/questions/1986756/setting-ringtone-in-android
        File newSoundFile = new File("/sdcard/media/ringtone", "beedoo_minions.mp3");
        Uri mUri = Uri.parse("android.resource://com.trollalarm.app/" + R.raw.beedoo_minions);
        ContentResolver mCr = this.getContentResolver();
        AssetFileDescriptor soundFile;

        try {
            soundFile= mCr.openAssetFileDescriptor(mUri, "r");
        } catch (FileNotFoundException e) {
            soundFile=null;
            Log.i("MainActivity:", "Sound file not found!");
            Toast.makeText(this, "Sound file failed to load, default alarm sound is used", Toast.LENGTH_LONG);
        }
        if(soundFile != null)
        {
            try {

                byte[] readData = new byte[102400];
                FileInputStream fis = soundFile.createInputStream();
                FileOutputStream fos = new FileOutputStream(newSoundFile);
                int i = fis.read(readData);

                while (i != -1) {
                    fos.write(readData, 0, i);
                    i = fis.read(readData);
                }
                fos.close();
            } catch (IOException io) {
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_new) {
            newAlarm();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStop() {
        super.onStop();
        AlarmDBHelper mDBHelper = new AlarmDBHelper(this);
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Iterator<AlarmItem> iter = adapter.alarms.iterator();
        AlarmItem alarm;

        while(iter.hasNext())
        {
            alarm = iter.next();
            final String SQL_UPDATE_ENTRY = "UPDATE alarms " +
                    "SET " +
                    AlarmDBHelper.SQL_ALARM_HOUR + " = " + alarm.hour + "," +
                    AlarmDBHelper.SQL_ALARM_MINUTE + " = " + alarm.minute + "," +
                    AlarmDBHelper.SQL_ALARM_METHOD + " = " + alarm.method.getID() + "," +
                    AlarmDBHelper.SQL_ALARM_SCHEDULEFLAGS + " = " + alarm.scheduleFlags + "," +
                    AlarmDBHelper.SQL_ALARM_ISON + " = " + alarm.isOn + " " +
                    "WHERE " + AlarmDBHelper.SQL_ALARM_ID + " = " + alarm.id;
            db.execSQL(SQL_UPDATE_ENTRY);
        }
    }

    public void newAlarm() {
        Intent intent = new Intent(mContext, EditAlarmActivity.class);
        startActivity(intent);
    }

    private class SwitchClickListener implements CompoundButton.OnCheckedChangeListener{

        private AlarmItem item;

        private SwitchClickListener(AlarmItem item) {
            this.item = item;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            item.isOn = isChecked ? 1:0;

            if(isChecked)
            {
                buttonView.setBackgroundResource(R.drawable.blueish_kind_of_green_switch_on);
                AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, item.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                // Set the alarm to start at 8:30 a.m.
                long minInterval = 999999999999999999L;
                long currentTime = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.HOUR_OF_DAY, item.hour);
                calendar.set(Calendar.MINUTE, item.minute);
                calendar.set(Calendar.SECOND,0);



                if((item.scheduleFlags & (1<<6)) == (1<<6)) {
                    calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                    if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
                    minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
                }
                if((item.scheduleFlags & (1<<5)) == (1<<5)) {
                    calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                    if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
                    minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
                }
                if((item.scheduleFlags & (1<<4)) == (1<<4)) {
                    calendar.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
                    if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
                    minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
                }
                if((item.scheduleFlags & (1<<3)) == (1<<3)) {
                    calendar.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
                    if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
                    minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
                }
                if((item.scheduleFlags & (1<<2)) == (1<<2)) {
                    calendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
                    if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
                    minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
                }
                if((item.scheduleFlags & (1<<1)) == (1<<1)) {
                    calendar.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
                    if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
                    minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
                }
                if((item.scheduleFlags & (1<<0)) == (1<<0)) {
                    calendar.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
                    if(calendar.getTimeInMillis() <= currentTime) calendar.add(Calendar.DATE,7);
                    minInterval = Math.min(calendar.getTimeInMillis(), minInterval);
                }
                if(minInterval >= 999999999999999999L) {

                    return;
                }

                alarmMgr.set(AlarmManager.RTC_WAKEUP, minInterval, alarmIntent);
                Toast.makeText(mContext, "Alarm Scheduled", Toast.LENGTH_LONG).show();
            }else
            {
                buttonView.setBackgroundResource(R.drawable.blueish_kind_of_green_switch_off);
                AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, item.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                alarmMgr.cancel(alarmIntent);
                Toast.makeText(mContext, "Alarm Unscheduled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AlarmClickListener implements View.OnClickListener{

        private AlarmItem item;

        private AlarmClickListener(AlarmItem item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, EditAlarmActivity.class);
            intent.putExtra(EditAlarmActivity.EXTRA_ALARMITEM, item);
            startActivity(intent);
        }
    }

    private class AlarmLongClickListener implements View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
        private AlarmItem item;
        private PopupMenu popupMenu;

        private AlarmLongClickListener(AlarmItem item) {
            this.item = item;
        }

        @Override
        public boolean onLongClick(View v) {

            popupMenu = new PopupMenu(mContext, v);
            popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Edit");
            popupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Delete");
            popupMenu.setOnMenuItemClickListener(this);

            popupMenu.show();

            return false;
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case 1:
                    intent = new Intent(mContext, EditAlarmActivity.class);
                    intent.putExtra(EditAlarmActivity.EXTRA_ALARMITEM, this.item);
                    startActivity(intent);
                    break;
                case 2:
                    SQLiteDatabase db = mDBHelper.getWritableDatabase();
                    final String SQL_DELETE_ENTRY = "DELETE FROM alarms where id = " + this.item.id;
                    db.execSQL(SQL_DELETE_ENTRY);

                    adapter.alarms.remove(this.item);
                    adapter.notifyDataSetChanged();

                    AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    intent = new Intent(mContext, AlarmReceiver.class);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, this.item.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    alarmMgr.cancel(alarmIntent);

                    break;
            }
            return false;
        }
    }
}
