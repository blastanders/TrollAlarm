package com.trollalarm.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.trollalarm.app.LockscreenAlarmActivity;
import com.trollalarm.app.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AlarmPhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final double IMAGE_KEYPOINT_TOLERANCE = 500.0f;

    public static final int TAKE_PICTURE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String photoPath;
    private String imageFilePath;
    private ImageView imageView;
    public final static String DEFAULT_IMAGE_PATH = "NotSet";


    private boolean CVIsLoaded = false;
    private boolean imageIsTaken = false;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(String param1, String param2) {
        AlarmPhotoFragment fragment = new AlarmPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public AlarmPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        this.photoPath = sharedPref.getString(getString(R.string.preference_file_key), DEFAULT_IMAGE_PATH);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_photo, container, false);
        this.imageView = ((ImageView)root.findViewById(R.id.imageview_fragmentphoto));

        if(this.photoPath.equals(DEFAULT_IMAGE_PATH))
        {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.missing_img));
            ((LockscreenAlarmActivity)getActivity()).stopAlarm();
        }else{
            Drawable d = Drawable.createFromPath(this.photoPath);
            imageView.setImageDrawable(d);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });

        return root;

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("MainActivity:", "OpenCV loaded successfully");
                    CVIsLoaded = true;
                    join();
                    //join_test();
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
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, getActivity(), mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri _uri = data.getData();
                        //User had pick an image.
                        Cursor cursor = getActivity().getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                        cursor.moveToFirst();

                        //Link to the image
                        this.imageFilePath = cursor.getString(0);
                        cursor.close();

                        imageIsTaken  =  true;
                        join();

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    /*public void join_test(){

        Mat baseImg = Highgui.imread("/storage/emulated/0/DCIM/Camera/0.jpg");
        Mat testImg = Highgui.imread("/storage/emulated/0/DCIM/Camera/" + 1  + ".jpg");

        for (int ij = 0; ij < 20; ij++) {
            testImg = Highgui.imread("/storage/emulated/0/DCIM/Camera/" + ij  + ".jpg");

            //http://stackoverflow.com/questions/10691521/surf-description-faster-with-fast-detection
            //FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
            FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);

            MatOfKeyPoint baseKeypoints = new MatOfKeyPoint();
            MatOfKeyPoint testKeypoints = new MatOfKeyPoint();

            ArrayList<KeyPoint> baseAboveAL = new ArrayList<KeyPoint>();
            ArrayList<KeyPoint> testAboveAL = new ArrayList<KeyPoint>();

            Imgproc.cvtColor(baseImg, baseImg, Imgproc.COLOR_RGBA2RGB);
            Imgproc.cvtColor(testImg, testImg, Imgproc.COLOR_RGBA2RGB);
            //extract keypoints

            detector.detect(baseImg, baseKeypoints);

            Log.e("LOG!", "number of query Keypoints= " + baseKeypoints.size());
            KeyPoint[] baseKeypoints_arr = baseKeypoints.toArray();

            float baseAvgResponse = 0.0f;

            for (int i = 0; i < baseKeypoints_arr.length; i++) {
                baseAvgResponse += baseKeypoints_arr[i].response;
            }
            baseAvgResponse = baseAvgResponse / baseKeypoints_arr.length;

            for (int i = 0; i < baseKeypoints_arr.length; i++) {
                if (baseKeypoints_arr[i].response > baseAvgResponse)
                    baseAboveAL.add(baseKeypoints_arr[i]);
            }

            Collections.sort(baseAboveAL, new Comparator<KeyPoint>() {
                @Override
                public int compare(final KeyPoint entry1, final KeyPoint entry2) {
                    return (int) (entry2.response - entry1.response);
                }
            });


            detector.detect(testImg, testKeypoints);

            Log.e("LOG!", "number of query Keypoints= " + testKeypoints.size());
            KeyPoint[] testKeypoints_arr = testKeypoints.toArray();

            float testAvgResponse = 0.0f;

            for (int i = 0; i < testKeypoints_arr.length; i++) {
                testAvgResponse += testKeypoints_arr[i].response;
            }
            testAvgResponse = testAvgResponse / testKeypoints_arr.length;

            for (int i = 0; i < testKeypoints_arr.length; i++) {
                if (testKeypoints_arr[i].response > testAvgResponse)
                    testAboveAL.add(testKeypoints_arr[i]);
            }

            Collections.sort(testAboveAL, new Comparator<KeyPoint>() {
                @Override
                public int compare(final KeyPoint entry1, final KeyPoint entry2) {
                    return (int) (entry2.response - entry1.response);
                }
            });


            double baseAvgX = 0.0f;
            double baseAvgY = 0.0f;

            for (int i = 0; i < baseAboveAL.size(); i++) {
                baseAvgX += baseAboveAL.get(i).pt.x;
                baseAvgY += baseAboveAL.get(i).pt.y;
            }

            baseAvgX = baseAvgX/baseAboveAL.size();
            baseAvgY = baseAvgY/baseAboveAL.size();

            for (int i = 0; i < baseAboveAL.size(); i++) {
                baseAboveAL.get(i).pt.x -= baseAvgX;
                baseAboveAL.get(i).pt.y -= baseAvgY;
            }

            double testAvgX = 0.0f;
            double testAvgY = 0.0f;

            for (int i = 0; i < testAboveAL.size(); i++) {
                testAvgX += testAboveAL.get(i).pt.x;
                testAvgY += testAboveAL.get(i).pt.y;
            }

            testAvgX = testAvgX/testAboveAL.size();
            testAvgY = testAvgY/testAboveAL.size();

            for (int i = 0; i < testAboveAL.size(); i++) {
                testAboveAL.get(i).pt.x -= testAvgX;
                testAboveAL.get(i).pt.y -= testAvgY;
            }

            int halfSmallerGroupSize = Math.min(testAboveAL.size(), baseAboveAL.size()) / 2;
            int hitTimes = 0;

            for (int i = 0; i < halfSmallerGroupSize; i++) {
                if (Math.abs(baseAboveAL.get(i).pt.x - testAboveAL.get(i).pt.x) < IMAGE_KEYPOINT_TOLERANCE && Math.abs(baseAboveAL.get(i).pt.y - testAboveAL.get(i).pt.y) < IMAGE_KEYPOINT_TOLERANCE)
                    hitTimes++;
            }

            if((float)hitTimes/(float)halfSmallerGroupSize > 0.4f)

            //Log.e("Final filter: " + ij, "");
            Log.e("Final filter: " + ij, (float)hitTimes / (float) halfSmallerGroupSize + "");

            //Log.e("Avarage Base Response:", baseAvgResponse+"");
            //Log.e("Avarage Test Response:", testAvgResponse+"");
            //Log.e("LOG!", "number of logo Keypoints= " + testKeypoints.size());

        }
    }*/

    public void join(){
        if(imageIsTaken && CVIsLoaded)
        {
            boolean matches = true;
            Mat baseImg = Highgui.imread(this.photoPath);
            Mat testImg = Highgui.imread(imageFilePath);

            Mat hist_base = new Mat();
            Mat hist_test = new Mat();

            MatOfFloat ranges = new MatOfFloat(0f, 256f);
            MatOfInt histSize = new MatOfInt(25);

            Imgproc.calcHist(Arrays.asList(baseImg), new MatOfInt(0), new Mat(), hist_base, histSize, ranges);
            Imgproc.calcHist(Arrays.asList(testImg), new MatOfInt(0), new Mat(), hist_test, histSize, ranges);

            double res = Imgproc.compareHist(hist_base, hist_test, Imgproc.CV_COMP_CORREL);

            if(res < 0.4)
                matches = false;

            if(matches)
            {
                //http://stackoverflow.com/questions/10691521/surf-description-faster-with-fast-detection
                //FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
                FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);

                MatOfKeyPoint baseKeypoints = new MatOfKeyPoint();
                MatOfKeyPoint testKeypoints = new MatOfKeyPoint();

                ArrayList<KeyPoint> baseAboveAL = new ArrayList<KeyPoint>();
                ArrayList<KeyPoint> testAboveAL = new ArrayList<KeyPoint>();

                Imgproc.cvtColor(baseImg, baseImg, Imgproc.COLOR_RGBA2RGB);
                Imgproc.cvtColor(testImg, testImg, Imgproc.COLOR_RGBA2RGB);
                //extract keypoints

                detector.detect(baseImg, baseKeypoints);

                Log.e("LOG!", "number of query Keypoints= " + baseKeypoints.size());
                KeyPoint[] baseKeypoints_arr = baseKeypoints.toArray();

                float baseAvgResponse = 0.0f;

                for (int i = 0; i < baseKeypoints_arr.length; i++) {
                    baseAvgResponse += baseKeypoints_arr[i].response;
                }
                baseAvgResponse = baseAvgResponse / baseKeypoints_arr.length;

                for (int i = 0; i < baseKeypoints_arr.length; i++) {
                    if (baseKeypoints_arr[i].response > baseAvgResponse)
                        baseAboveAL.add(baseKeypoints_arr[i]);
                }

                Collections.sort(baseAboveAL, new Comparator<KeyPoint>() {
                    @Override
                    public int compare(final KeyPoint entry1, final KeyPoint entry2) {
                        return (int) (entry2.response - entry1.response);
                    }
                });


                detector.detect(testImg, testKeypoints);

                Log.e("LOG!", "number of query Keypoints= " + testKeypoints.size());
                KeyPoint[] testKeypoints_arr = testKeypoints.toArray();

                float testAvgResponse = 0.0f;

                for (int i = 0; i < testKeypoints_arr.length; i++) {
                    testAvgResponse += testKeypoints_arr[i].response;
                }
                testAvgResponse = testAvgResponse / testKeypoints_arr.length;

                for (int i = 0; i < testKeypoints_arr.length; i++) {
                    if (testKeypoints_arr[i].response > testAvgResponse)
                        testAboveAL.add(testKeypoints_arr[i]);
                }

                Collections.sort(testAboveAL, new Comparator<KeyPoint>() {
                    @Override
                    public int compare(final KeyPoint entry1, final KeyPoint entry2) {
                        return (int) (entry2.response - entry1.response);
                    }
                });


                double baseAvgX = 0.0f;
                double baseAvgY = 0.0f;

                for (int i = 0; i < baseAboveAL.size(); i++) {
                    baseAvgX += baseAboveAL.get(i).pt.x;
                    baseAvgY += baseAboveAL.get(i).pt.y;
                }

                baseAvgX = baseAvgX/baseAboveAL.size();
                baseAvgY = baseAvgY/baseAboveAL.size();

                for (int i = 0; i < baseAboveAL.size(); i++) {
                    baseAboveAL.get(i).pt.x -= baseAvgX;
                    baseAboveAL.get(i).pt.y -= baseAvgY;
                }

                double testAvgX = 0.0f;
                double testAvgY = 0.0f;

                for (int i = 0; i < testAboveAL.size(); i++) {
                    testAvgX += testAboveAL.get(i).pt.x;
                    testAvgY += testAboveAL.get(i).pt.y;
                }

                testAvgX = testAvgX/testAboveAL.size();
                testAvgY = testAvgY/testAboveAL.size();

                for (int i = 0; i < testAboveAL.size(); i++) {
                    testAboveAL.get(i).pt.x -= testAvgX;
                    testAboveAL.get(i).pt.y -= testAvgY;
                }

                int halfSmallerGroupSize = Math.min(testAboveAL.size(), baseAboveAL.size()) / 2;
                int hitTimes = 0;

                for (int i = 0; i < halfSmallerGroupSize; i++) {
                    if (Math.abs(baseAboveAL.get(i).pt.x - testAboveAL.get(i).pt.x) < IMAGE_KEYPOINT_TOLERANCE && Math.abs(baseAboveAL.get(i).pt.y - testAboveAL.get(i).pt.y) < IMAGE_KEYPOINT_TOLERANCE)
                        hitTimes++;
                }

                Log.e("Final filter: ", (float)hitTimes / (float) halfSmallerGroupSize + "");

                if((float)hitTimes/(float)halfSmallerGroupSize < 0.2f)
                    matches = false;
            }
            if(matches)
            {
                ((LockscreenAlarmActivity)getActivity()).stopAlarm();

            }else {
                Toast.makeText(getActivity(), "Not match la, try again la", Toast.LENGTH_LONG).show();
                this.imageIsTaken = false;
            }
        }
    }
}
