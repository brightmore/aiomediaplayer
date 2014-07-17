package com.abhishekgarg.aiomediaplayer;

import java.io.IOException;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener,ViewPager.OnPageChangeListener,SongsView.OnFragmentInteractionListener,VideosView.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    android.app.ActionBar actionBar;
    private SongsView songsView=new SongsView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        actionBar=getActionBar();
       // actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText("Songs").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Videos").setTabListener(this));
        actionBar.setSelectedNavigationItem(0);

        // Set up the ViewPager with the sections adapter.
        initViews();
    }


    public void initViews(){

        Log.e("INIT","VIews");
        if(VideosView.videoList.isEmpty()) {
            ContentResolver resolver = getContentResolver();
            Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            Uri thumbs = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
            Cursor cursor = resolver.query(videoUri, null, null, null, null);
            if (cursor == null) {
                Log.e("Cursor", "Null");
            }
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Video v = new Video();
                    int idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                    v.setId(cursor.getLong(idColumn));
                    Log.e("ID", String.valueOf(cursor.getLong(idColumn)));
                    int titleColumn = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    v.setPath(path);
                    Log.e("Path", path);
                    v.setTitle(cursor.getString(titleColumn));
                    Log.e("Video", cursor.getString(titleColumn));
                    String fileType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                    v.setFileType(fileType);
                    String[] arr = new String[]{String.valueOf(cursor.getLong(idColumn))};
                /*Bitmap bmp=MediaStore.Video.Thumbnails.getThumbnail(resolver,cursor.getLong(idColumn), MediaStore.Video.Thumbnails.MINI_KIND,null);
                v.setBmp(bmp);
                Log.e("BMP","Set");*/
                    try {
                        Log.e("Thumb", "Before");
                        Cursor thumb = resolver.query(thumbs, null, "VIDEO_ID =" + String.valueOf(cursor.getLong(idColumn)), null, null);
                        //Cursor thumb=managedQuery(thumbs,null,MediaStore.Video.Thumbnails.VIDEO_ID+"=?",arr,null);
                        if (thumb == null) {
                            Log.e("Thumb", "Null");
                        }
                        if (thumb != null && thumb.moveToFirst()) {
                            Log.e("Thumb", "Cursor");
                            String thumbpath = thumb.getString(thumb.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                            Log.e("Thumb", thumbpath);
                            v.setThumbPath(thumbpath);
                        }
                    } catch (Exception e) {
                        Log.e("Exc", e.getMessage());
                    }

                    VideosView.add(v);
                }
                while (cursor.moveToNext());
            }
        }
        if(SongsView.songs.isEmpty()) {
            ContentResolver resolver=getContentResolver();
            Uri songsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor songsCursor = resolver.query(songsUri, null, null, null, null);
            if (songsCursor != null && songsCursor.moveToFirst()) {
                do {
                    int titleColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int artistColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int idColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    String title = songsCursor.getString(titleColumn);
                    String artist = songsCursor.getString(artistColumn);
                    long id = songsCursor.getLong(idColumn);
                    Song s = new Song();
                    s.setArtist(artist);
                    s.setId(id);
                    s.setTitle(title);
                    SongsView.add(s);
                }
                while (songsCursor.moveToNext());
            }
        }
    }

    public void playVideo(View v){

        TextView data= (TextView) v.findViewById(R.id.videoPath);
        //VideoView vw= (VideoView) v.findViewById(R.id.videow);
        String path= (String) data.getText();
        //vw.setVisibility(View.VISIBLE);
        //vw.setVideoPath(path);
        //vw.start();
        NowPlaying.videoPath=path;
        NowPlaying n=new NowPlaying();
        Intent intent=new Intent(this,NowPlaying.class);

        startActivity(intent);
    }

    public void playSong(View view){


        songsView.playSong(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.Exit){
            SongsView.musicServ.stopForeground(true);
            SongsView.musicServ.stopSelf();
            System.exit(0);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        actionBar.setSelectedNavigationItem(position);


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
          mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return songsView;
                case 1:
                    return new VideosView();

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);

            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(SongsView.musicServ==null){
            System.exit(0);
        }
        else if(!SongsView.musicServ.isPng()){
            SongsView.musicServ.stopForeground(true);
            SongsView.musicServ.stopSelf();
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        SongsView.musicServ.stopForeground(true);
        SongsView.musicServ.stopSelf();
        super.onDestroy();
    }

    /**
     * A placeholder fragment containing a simple view.
     */

}
