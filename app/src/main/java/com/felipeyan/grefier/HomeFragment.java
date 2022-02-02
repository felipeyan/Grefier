package com.felipeyan.grefier;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    /* Java classes */
    Record record;

    /* Variables */
    private static final int MICROPHONE_PERMISSION = 200;
    boolean isRecording = false;

    /* View context */
    Context context;

    /* Widgets */
    AppCompatImageButton recordButton, stopButton;
    Chronometer recordTimer;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = rootView.getContext();

        if (microphonePresent()) {
            microphoneAccess();
        }
        
        recordButton = rootView.findViewById(R.id.recordButton);
        stopButton = rootView.findViewById(R.id.stopButton);
        recordTimer = rootView.findViewById(R.id.recordTimer);

        record = new Record(new MediaRecorder(), context);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    record.recordAudio(recordPath());
                    recordTimer.setBase(SystemClock.elapsedRealtime());
                    recordTimer.start();
                    isRecording = true;
                    recordingLayout();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    record.stopAudio();
                    recordTimer.stop();
                    isRecording = false;
                    recordingLayout();
                }
            }
        });

        return rootView;
    }

    private boolean microphonePresent() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void microphoneAccess() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {
                    Manifest.permission.RECORD_AUDIO
            }, MICROPHONE_PERMISSION);
        }
    }

    public void recordingLayout() {
        if (isRecording) {
            stopButton.setVisibility(View.VISIBLE);
            recordTimer.setVisibility(View.VISIBLE);
        } else {
            stopButton.setVisibility(View.INVISIBLE);
            recordTimer.setVisibility(View.INVISIBLE);
        }
    }

    private String recordPath() {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File musicDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDir, audioFile() + ".mp3");

        return file.getPath();
    }

    private String audioFile() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }
}