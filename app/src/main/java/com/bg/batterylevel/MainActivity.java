package com.bg.batterylevel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int TTS_STREAM = AudioManager.STREAM_NOTIFICATION;

    private LevelFragment mFragment;
    private ImageButton mButton;
    private IntentFilter mIntentFilter;

    private TextToSpeech mTTS;
    private HashMap<String, String> mTTSParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // fragment
        mFragment = (LevelFragment) getFragmentManager().findFragmentById(R.id.activity_main_level);

        // button
        mButton = (ImageButton) findViewById(R.id.activity_main_voice);
        mButton.setEnabled(false);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTTS != null)
                {
                    final String speech = String.format("Battery level is %d percent", mFragment.getBatteryLevel());
                    mTTS.speak(speech,
                            TextToSpeech.QUEUE_FLUSH,
                            mTTSParams);
                }
            }
        });

        // speech
        mTTSParams = new HashMap<String, String>();
        mTTSParams.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(TTS_STREAM));
        this.setVolumeControlStream(TTS_STREAM);

        mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    setTextToSpeechSettings(Locale.getDefault());
                } else {
                    Log.e(TAG, "error creating text to speech");
                }
            }
        });

        //
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(mPowerConnectedReceiver, mIntentFilter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {

            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.action_about)
                    .setMessage(R.string.action_text)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton){

                        }
                    }).create();

            dialog.show();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(mPowerConnectedReceiver);
        if (mTTS != null)
        {
            mTTS.shutdown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mPowerConnectedReceiver, mIntentFilter);
    }


    private void setTextToSpeechSettings(final Locale locale)
    {
        Locale defaultOrPassedIn = locale;
        switch (mTTS.isLanguageAvailable(defaultOrPassedIn))
        {
            case TextToSpeech.LANG_AVAILABLE:
            case TextToSpeech.LANG_COUNTRY_AVAILABLE:
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                Log.d(TAG, "SUPPORTED");
                mTTS.setLanguage(locale);
                mButton.setEnabled(true);
                break;

            case TextToSpeech.LANG_MISSING_DATA:
                Log.d(TAG, "MISSING_DATA");
                // check if waiting, by checking
                // a shared preference
                break;

            case TextToSpeech.LANG_NOT_SUPPORTED:
                Log.d(TAG, "NOT SUPPORTED");
                break;
        }
    }


    private final BroadcastReceiver mPowerConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
                mFragment.showIsCharging(true);

            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {

                mFragment.showIsCharging(false);
            }
        }
    };



}
