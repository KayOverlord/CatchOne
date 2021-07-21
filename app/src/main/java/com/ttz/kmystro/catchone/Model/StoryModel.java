package com.ttz.kmystro.catchone.Model;

import android.net.Uri;

import java.util.Date;

public class StoryModel {

    public StoryModel(){

    }

    private String Article;
    private String ID;
    private String Title;
    private long com;
    private Date timestamp;
    private String pic;


    public StoryModel(String article, String ID, String title, long com, Date timestamp, String pic) {
        Article = article;
        this.ID = ID;
        Title = title;
        this.com = com;
        this.timestamp = timestamp;
        this.pic = pic;
    }


    public long getCom() {
        return com;
    }


    public String getArticle() {
        return Article;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getPic() {
        return pic;
    }





}
