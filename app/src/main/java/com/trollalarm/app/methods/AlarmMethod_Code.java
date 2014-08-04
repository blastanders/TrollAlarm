package com.trollalarm.app.methods;

import com.trollalarm.app.AlarmMethod;

import java.util.Random;

/**
 * Created by Aaron on 07-06-14.
 */
public class AlarmMethod_Code extends AlarmMethod {

    public int[] code;
    private Random rand;

    public static AlarmMethod newInstance() {
        return new AlarmMethod_Code();
    }

    public AlarmMethod_Code() {
        this.code = new int[]{0,0,0,0,0,0};
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            this.code[i] = rand.nextInt(10);
        }
    }

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public String getName() {
        return "Code";
    }

    @Override
    public String getDesc() {
        return "Enter the code on the screen when the alarm set off. You have 10 seconds before you are given another code";
    }


    public void generateCode(){
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            this.code[i] = rand.nextInt(9)%9+1;
        }
    }
    public int[] getCode(){
        return this.code;
    }

    @Override
    public String getInstruction() {
        String codeForPrint = "";

        for (int i = 0; i < 6; i++) {
            codeForPrint += code[i];
        }
        return "Type the code " + codeForPrint;
    }
}