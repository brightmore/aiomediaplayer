package com.abhishekgarg.aiomediaplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SongsView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SongsView#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SongsView extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static ArrayList<Song> songs=new ArrayList<Song>();
    private ListView songsList;
    private SongAdapter songadp;
    public static MusicService musicServ;
    private View view;
    private boolean musicBound=false;
    private Intent playIntent;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongsView.
     */


    public static void resetSongs(){
        songs.clear();
    }

    public static void add(Song s){
        songs.add(s);
    }

    // TODO: Rename and change types and number of parameters
    public static SongsView newInstance(String param1, String param2) {
        SongsView fragment = new SongsView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SongsView() {
        // Required empty public constructor
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
    public void onStart(){
        super.onStart();
        if(playIntent==null){
            playIntent =new Intent(getActivity(),MusicService.class);
            Log.e("PLAY","INTENT");
            Log.e("MusicConnection",musicConnection.toString());
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

            Log.e("Bind", "Service");
            getActivity().startService(playIntent);

            Log.e("Play","Intent");
        }
    }

    private ServiceConnection musicConnection =new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("Binder","Entry");
            MusicService.MusicBinder binder= (MusicService.MusicBinder) service;
            Log.e("Binder","TRUE");
            musicServ=binder.getService();

            musicServ.setList(songs);
            musicBound=true;
            NowPlayingSong.musicBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound=false;
            NowPlayingSong.musicBound=false;
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_songs_view, container, false);
        songsList= (ListView) view.findViewById(R.id.songList);
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
        songadp =new SongAdapter(getActivity().getBaseContext(),songs);
        songsList.setAdapter(songadp);
        songsList.setFastScrollEnabled(true);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public void playSong(View view){
        TextView v= (TextView) view.findViewById(R.id.songPosition);
        int pos=Integer.parseInt(String.valueOf(v.getText()));
        NowPlayingSong.setPos(pos);
        if(musicServ.isPng()){
            musicServ.pausePlayer();


        }

            NowPlayingSong.getMusicServ(musicServ);
            NowPlayingSong.songs = songs;
            Intent intent = new Intent(getActivity().getBaseContext(), NowPlayingSong.class);
            startActivity(intent);

    }





    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

class SongAdapter extends BaseAdapter implements SectionIndexer{
    private ArrayList<Song> songs;
    private LayoutInflater songsInf;
    HashMap<String,Integer> alphaIndexer;
    String[] sections;
    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout= (LinearLayout) songsInf.inflate(R.layout.song,parent,false);
        Song currSong=songs.get(position);
        TextView title= (TextView) layout.findViewById(R.id.songTitle);
        TextView artist= (TextView) layout.findViewById(R.id.songArtist);
        title.setText(currSong.getTitle());
        artist.setText(currSong.getArtist());
        TextView pos= (TextView) layout.findViewById(R.id.songPosition);
        pos.setText(String.valueOf(position));
        pos.setVisibility(View.INVISIBLE);
        return layout;
    }

    public SongAdapter(Context c,ArrayList<Song> theSongs){
        songs=theSongs;
        songsInf=LayoutInflater.from(c);
        alphaIndexer = new HashMap<String,Integer>();
        int size=theSongs.size();
        for(int x=0;x<size;x++){
            String s=theSongs.get(x).getTitle();
            String ch=s.substring(0,1);
            ch=ch.toUpperCase();
            if(!alphaIndexer.containsKey(ch)){
                alphaIndexer.put(ch,x);
            }
        }
        Set<String> sectionLetters=alphaIndexer.keySet();
        ArrayList<String> sectionList=new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sections=new String[sectionList.size()];
        sectionList.toArray(sections);
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return alphaIndexer.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
