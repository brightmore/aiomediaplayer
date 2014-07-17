package com.abhishekgarg.aiomediaplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;


public class NowPlaying extends Activity implements MediaPlayer.OnCompletionListener {

    public static String videoPath;
    private VideoView vw;
    PlayerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        vw= (VideoView) findViewById(R.id.videoView);
        controller=new PlayerController(this);
        if(SongsView.musicServ.isPng()){
            SongsView.musicServ.pausePlayer();
        }
        vw.setMediaController(controller);
        vw.setOnCompletionListener(this);
        vw.setVideoPath(videoPath);
        vw.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.now_playing, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
