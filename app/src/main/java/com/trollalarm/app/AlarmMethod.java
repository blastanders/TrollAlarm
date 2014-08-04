package com.trollalarm.app;

import com.trollalarm.app.methods.AlarmMethod_Code;
import com.trollalarm.app.methods.AlarmMethod_Photo;
import com.trollalarm.app.methods.AlarmMethod_Shake;
import com.trollalarm.app.methods.AlarmMethod_Toss;

/**
 * Created by Aaron on 06/06/2014.
 */
public abstract class AlarmMethod {
    public abstract int getID();
    public abstract String getName();
    public abstract String getDesc();
    public abstract String getInstruction();

    public static AlarmMethod getMethodFromID(int id) {
        switch(id) {
            case 1:default:
                return AlarmMethod_Toss.newInstance();
            case 2:
                return AlarmMethod_Code.newInstance();
            case 3:
                return AlarmMethod_Shake.newInstance();
            case 4:
                return AlarmMethod_Photo.newInstance();
        }
    }
}
