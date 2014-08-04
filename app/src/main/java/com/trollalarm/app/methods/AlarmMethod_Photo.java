package com.trollalarm.app.methods;

import com.trollalarm.app.AlarmMethod;

/**
 * Created by Aaron on 07-06-14.
 */
public class AlarmMethod_Photo extends AlarmMethod {
    public static AlarmMethod newInstance() {
        return new AlarmMethod_Photo();
    }

    @Override
    public int getID() {
        return 4;
    }

    @Override
    public String getName() {
        return "Photo";
    }

    @Override
    public String getDesc() {
        return "Take a picture when you set the alarm. To dismiss it you have to take the same photo again (pick a place where the light does not change much i.e. your bathroom)";
    }

    @Override
    public String getInstruction() {
        return "Take the same photo";
    }
}
