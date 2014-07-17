package com.abhishekgarg.aiomediaplayer;

/**
 * Created by Abhishek on 6/9/2014.
 */
public class Song {

    private long id;
    private  String title;
    private String artist;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
