package com.trollalarm.app.methods;

import com.trollalarm.app.AlarmMethod;

/**
 * Created by Aaron on 06/06/2014.
 */
public class AlarmMethod_Toss extends AlarmMethod {
    public static AlarmMethod newInstance() {
        return new AlarmMethod_Toss();
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public String getName() {
        return "Toss";
    }

    @Override
    public String getDesc() {
        return "Throw your phone high in the air to stop the alarm!";
    }

    @Override
    public String getInstruction() { return "Throw up 0.5 meter or higher"; }
}
