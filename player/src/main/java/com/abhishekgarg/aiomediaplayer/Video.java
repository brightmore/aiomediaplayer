package com.abhishekgarg.aiomediaplayer;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by Abhishek on 6/5/2014.
 */
public class Video {

    private String fileType;
    private String path;
    private String thumbPath;
    private long id;
    private String title;
   // private Bitmap bmp;

 /*   public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    } */

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    public String getThumbPath() {
        return thumbPath;
    }

    public String getFileType() {
        return fileType;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
