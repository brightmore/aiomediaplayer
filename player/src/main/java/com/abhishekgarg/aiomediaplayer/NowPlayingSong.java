package com.abhishekgarg.aiomediaplayer;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import android.os.Handler;
import java.util.logging.LogRecord;


public class NowPlayingSong extends Activity implements MediaController.MediaPlayerControl {


    private Visualizer mVisualizer;
    private static final float VISUALIZER_HEIGHT_DIP=50f;
    private Equalizer mEqualizer;
    private VisualizerView mVisualizerView;
    private static int pos;
    private static MusicService musicServ;
    private Intent playIntent;
    public static boolean musicBound=false;
    LayoutInflater layinf;
    Handler handler= new Handler();
    RelativeLayout layout;

    private MediaController controller;
    public static ArrayList<Song> songs=new ArrayList<>();

    public static void setPos(int i){
        pos=i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing_song);
        initTitle();
        setController();
        setupEqualizer();
        setupVisualizer();
        mVisualizer.setEnabled(true);
    }

    public void initTitle(){
        Song s=songs.get(pos);
        TextView title= (TextView) findViewById(R.id.NowPlayingSongTitle);
        title.setText(s.getTitle());

    }

    public static void getMusicServ(MusicService srv){
        musicServ=srv;
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(!musicServ.isPng()){
        playSong();
        }
    }


    public void setController(){


        controller= (MediaController) findViewById(R.id.mediaController);
        //controller=new MediaController(this);
        Log.e("Controller","Set");
        controller.setPrevNextListeners(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                playNext();
                                            }
                                        },

                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        playPrev();

                    }
                });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.layout.activity_now_playing_song));
       /* handler.post(new Runnable() {
            @Override
            public void run() {
                controller.setEnabled(true);
                controller.show();
            }
        }); */
        controller.setEnabled(true);
        controller.show();

        //controller.setEnabled(true);

    }

    public void playNext(){
        pos++;
        if(pos>songs.size())
            pos=0;
        initTitle();
        musicServ.playNext();
        controller.show(0);

    }

    public void playPrev(){
        pos--;
        if(pos<0) pos=songs.size()-1;
        initTitle();
        musicServ.playPrev();
        controller.show(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.now_playing_song, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }*/
        int id=item.getItemId();
        Log.e("ID",String.valueOf(id));

        return super.onOptionsItemSelected(item);
    }




    public static void playSong(){

        if(musicServ==null){
            Log.e("Serv", "NULL");
        }

        musicServ.setSong(pos);

        musicServ.playSong();
        //setController();
        //controller.show(0);

    }

    @Override
    public void start() {
        musicServ.go();
    }

    @Override
    public void pause() {
        Log.e("PAUSE","WORKING");
        musicServ.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicServ!=null && musicBound)
        {
            return musicServ.getDur();
        }
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicServ!=null && musicBound){
            return musicServ.getPosn();
        }
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        Log.e("Seek","True");
        musicServ.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicServ!=null && musicBound)
        {
            return musicServ.isPng();
        }
        return false;

    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void setupVisualizer(){

       mVisualizerView= (VisualizerView) findViewById(R.id.visualizerview);
       mVisualizerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,(int)(VISUALIZER_HEIGHT_DIP*getResources().getDisplayMetrics().density)));

       mVisualizer=new Visualizer(MusicService.player.getAudioSessionId());
       mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
       mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                   public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                      int samplingRate) {
                       mVisualizerView.updateVisualizer(bytes);
                  }

                 public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}
               }, Visualizer.getMaxCaptureRate() / 2, true, false);

}


    public void setupEqualizer(){

    }


 }


class VisualizerView extends View {
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();

    private Paint mForePaint = new Paint();

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context,AttributeSet attrs){
        super(context,attrs);
        init();
    }

    private void init() {
        mBytes = null;

        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
        }

        canvas.drawLines(mPoints, mForePaint);
    }
}
