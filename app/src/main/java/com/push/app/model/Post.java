package com.push.app.model;

import java.io.Serializable;

/**
 * Created by Bryan on 03/06/2015.
 */
public class Post implements Serializable{
    String Title;
    String Description;
    String mDatePosted;
    String Url;

    /**
     * UID version for serialization
     */
    private static final long serialVersionUID = 1L;


    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getmDatePosted() {
        return mDatePosted;
    }

    public void setmDatePosted(String mDatePosted) {
        this.mDatePosted = mDatePosted;
    }

    public Post(String Title, String Description, String mDatePosted, String Url) {
        this.Title = Title;
        this.Description = Description;
        this.mDatePosted = mDatePosted;
        this.Url = Url;
    }
}
