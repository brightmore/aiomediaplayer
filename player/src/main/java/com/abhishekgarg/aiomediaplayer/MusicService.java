package com.abhishekgarg.aiomediaplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Abhishek on 6/9/2014.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener {

    public static MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPos;
    private final IBinder musicBind=new MusicBinder();
    private String songTitle="";
    private static final int NOTIFY_ID=1;

    public class MusicBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void playSong(){

        player.reset();
        int id=player.getAudioSessionId();
        Song playSong =songs.get(songPos);
        songTitle=playSong.getTitle();
        long currSong =playSong.getId();
        Uri trackUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            player.setDataSource(getApplicationContext(),trackUri);
        }
        catch(Exception e){
            Log.e("Music Player", "Error setting data source", e);

        }
        player.prepareAsync();

    }


    @Override
    public void onCreate(){
       super.onCreate();
        player=new MediaPlayer();
        songPos=0;
        initMusicPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){

        player.stop();
        player.release();
        return false;

    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        playNext();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {


        mp.start();
        Intent notIntent = new Intent(this, NowPlayingSong.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt).setSmallIcon(R.drawable.play).setTicker(songTitle).setOngoing(true).setContentTitle("Playing").setContentText(songTitle);
        Notification not=builder.getNotification();
        startForeground(NOTIFY_ID,not);
   }

    public void setSong(int songIndex){
        songPos=songIndex;
    }


    public int getPosn(){
        Log.e("Get","POSN");
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        songPos--;
        if(songPos<0) songPos=songs.size()-1;
        playSong();
    }

    public void playNext(){
        songPos++;
        if(songPos>=songs.size()) songPos=0;
        playSong();
    }
}
