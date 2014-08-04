package com.trollalarm.app.methods;

import com.trollalarm.app.AlarmMethod;

/**
 * Created by Aaron on 06/06/2014.
 */
public class AlarmMethod_Shake extends AlarmMethod {
    public static AlarmMethod newInstance() {
        return new AlarmMethod_Shake();
    }

    @Override
    public int getID() {
        return 3;
    }

    @Override
    public String getName() {
        return "Shake";
    }

    @Override
    public String getDesc() {
        return "Shake your device 30 times to dismiss your alarm";
    }

    @Override
    public String getInstruction() { return "Shake your phone 30 times"; }

}
