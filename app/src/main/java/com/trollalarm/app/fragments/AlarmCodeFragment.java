package com.trollalarm.app.fragments;

/**
 * Created by Aaron on 07-06-14.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.trollalarm.app.LockscreenAlarmActivity;
import com.trollalarm.app.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class AlarmCodeFragment extends Fragment {
    ArrayList<Button> buttons;
    public static final String ARG_PARAM1 = "code";
    private int[] code;
    private int counter;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AlarmCodeFragment.
     */

    public static AlarmCodeFragment newInstance(int[] code) {
        AlarmCodeFragment fragment = new AlarmCodeFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_PARAM1, code);
        fragment.setArguments(args);
        return fragment;
    }

    public AlarmCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        counter = 0;

        if (getArguments() != null) {
            code = getArguments().getIntArray(ARG_PARAM1);
        }
        buttons = new ArrayList<Button>(9);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_code, container, false);

        buttons.add((Button) root.findViewById(R.id.button_1));
        buttons.add((Button) root.findViewById(R.id.button_2));
        buttons.add((Button) root.findViewById(R.id.button_3));
        buttons.add((Button) root.findViewById(R.id.button_4));
        buttons.add((Button) root.findViewById(R.id.button_5));
        buttons.add((Button) root.findViewById(R.id.button_6));
        buttons.add((Button) root.findViewById(R.id.button_7));
        buttons.add((Button) root.findViewById(R.id.button_8));
        buttons.add((Button) root.findViewById(R.id.button_9));

        Iterator<Button> iter = buttons.iterator();
        Button tempButton;
        while (iter.hasNext())
        {
            tempButton = iter.next();
            tempButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(code[counter] == Integer.parseInt(((Button)v).getText().toString()))
                    {
                        counter++;
                        if (counter >= 6)
                            ((LockscreenAlarmActivity)getActivity()).stopAlarm();
                    }else {
                        counter = 0;
                        Toast.makeText(getActivity(), "Wrong code. Start over...", Toast.LENGTH_LONG).show();
                    }
                    randomize();
                }
            });
        }

        randomize();

        return root;
    }

    public void randomize(){
        Random rand = new Random();

        ArrayList<Integer> controlSet = new ArrayList<Integer>(9);
        for (int i = 1; i <= 9 ; i++) {
            controlSet.add(i);
        }
        for (int i = 0; i < buttons.size(); i++) {
            int randomNumber = rand.nextInt(controlSet.size());
            buttons.get(i).setText(controlSet.get(randomNumber)+"");

            controlSet.remove(randomNumber);

        }
    }
}