package com.abhishekgarg.aiomediaplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideosView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideosView#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class VideosView extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static ArrayList<Video> videoList=new ArrayList<Video>();
    private ListView listView;
    private videoAdapter adp;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideosView.
     */
    // TODO: Rename and change types and number of parameters
    public static VideosView newInstance(String param1, String param2) {
        VideosView fragment = new VideosView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public VideosView() {
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



    public static void add(Video v){
        videoList.add(v);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_videos_view, container, false);
        listView= (ListView) view.findViewById(R.id.videoList);
        adp=new videoAdapter(getActivity().getBaseContext(),videoList);
        listView.setAdapter(adp);
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

 class videoAdapter extends BaseAdapter{

     private ArrayList<Video> videos;
     private LayoutInflater videoInf;

    @Override
    public int getCount() {
        return videos.size();
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
        RelativeLayout layout= (RelativeLayout) videoInf.inflate(R.layout.video,parent,false);
        ImageView iv= (ImageView) layout.findViewById(R.id.videoThumb);
        TextView title=(TextView) layout.findViewById(R.id.videoTitle);
        TextView path= (TextView) layout.findViewById(R.id.videoPath);
        Video currVideo=videos.get(position);
        path.setText(currVideo.getPath());
        path.setVisibility(View.INVISIBLE);
      /* VideoView vw= (VideoView) layout.findViewById(R.id.videow);
        vw.setVideoPath(currVideo.getPath());
        vw.start();
     //   vw.setVisibility(View.INVISIBLE);
       // iv.setImageBitmap(currVideo.getBmp()); */
        iv.setImageURI(Uri.parse(currVideo.getThumbPath()));
        title.setText(currVideo.getTitle());
        return layout;
    }

     public videoAdapter(Context e,ArrayList<Video> theVideos){
         videos=theVideos;
         videoInf=LayoutInflater.from(e);
     }
}
