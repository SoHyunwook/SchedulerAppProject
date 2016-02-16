package com.example.schedulerproject;

/**
 * Created by 현욱 on 2016-02-16.
 */

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;

//볼륨처리->p.516
public class AlarmEnd extends AppCompatActivity {
    private static final String TAG = "AlarmActivity";

    View.OnClickListener handler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dofinish:
                    doFinish();
                    break;
            }
        }
    };
    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
        }
    };
    MediaPlayer mediaPlayer2;

    //    긴 음원 재생
    void doAction(String fName) {
        if (mediaPlayer2 == null) {
            mediaPlayer2 = new MediaPlayer();
        }
        if (mediaPlayer2.isPlaying()) {
//            기존에 재생되던 음원 멈춤
            mediaPlayer2.stop();
            mediaPlayer2.reset();
        }
        mediaPlayer2.start();
    }

    int position = 0;

    //    음원멈춤
    void doFinish() {
        if (mediaPlayer2.isPlaying()) {
            position = mediaPlayer2.getCurrentPosition();
            mediaPlayer2.pause();
        }
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    MediaPlayer.OnSeekCompleteListener seekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            mp.start();
        }
    };

    //    음원종료
    void killMediaPlaying() {
        if (mediaPlayer2 != null) {
            if (mediaPlayer2.isPlaying()) {
                mediaPlayer2.stop();
            }
            mediaPlayer2.release();
            mediaPlayer2 = null;
            System.gc();
        }
    }

    @Override
    protected void onDestroy() {
        killMediaPlaying();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_alarm);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.oohahh);
        findViewById(R.id.dofinish).setOnClickListener(handler);
        doAction("oohahh.mp3");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}