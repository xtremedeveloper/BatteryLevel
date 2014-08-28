package com.bg.batterylevel;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class LevelFragment extends Fragment {

    private LevelView mLevelView;
    private float mBatteryLevel;

    public LevelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_level, container, false);

        mLevelView = (LevelView)root.findViewById(R.id.fragment_level_view);

        updateStatus();

        return root;
    }

    private void updateStatus() {

        Intent batteryStatus = getActivity().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        showValue(level, scale, status);

    }

    public void showIsCharging(boolean val) {
        mLevelView.setIsCharging(val);
    }

    public void showValue(int level, int scale, int status) {

        if (level == -1 || scale == -1) {
            mBatteryLevel =  50.0f;
        } else {
            mBatteryLevel = ((float) level / (float) scale) * 100.0f;
        }

        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        mLevelView.setValue((int)mBatteryLevel);
        mLevelView.setIsCharging(isCharging);
    }

    public int getBatteryLevel() {
        return (int)mBatteryLevel;
    }



}
