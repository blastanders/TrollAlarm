package com.trollalarm.app;

import android.os.Parcel;
import android.os.Parcelable;

import com.trollalarm.app.methods.AlarmMethod_Toss;

/**
 * Created by Aaron on 06/06/2014.
 */
public class AlarmItem implements Parcelable {
    public int id;
    public int hour;
    public int minute;
    public AlarmMethod method;
    public int isOn;
    public int scheduleFlags;


    public AlarmItem() {
        id = -1;
        hour = 0;
        minute = 0;
        method = AlarmMethod_Toss.newInstance();
        isOn = 1;
        scheduleFlags = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeInt(method.getID());
        dest.writeInt(isOn);
        dest.writeInt(scheduleFlags);
    }

    public static final Parcelable.Creator<AlarmItem> CREATOR = new Parcelable.Creator<AlarmItem>() {
        public AlarmItem createFromParcel(Parcel in) {
            return new AlarmItem(in);
        }

        public AlarmItem[] newArray(int size) {
            return new AlarmItem[size];
        }
    };

    private AlarmItem(Parcel in) {
        id = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        method = AlarmMethod.getMethodFromID(in.readInt());
        isOn = in.readInt();
        scheduleFlags = in.readInt();
    }

    public String getTimeAsString() {
        return String.format("%02d", hour) + ":" + String.format("%02d", minute);
    }
}
