package com.push.occrp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;

public class PushImage extends RealmObject  {
    public String byline;
    public String caption;
    public String url;

    public HashMap<String, String> getHashMap() {

        HashMap<String,String> tempHash = new HashMap<String, String>();
        tempHash.put("byline",byline);
        tempHash.put("caption",caption);
        tempHash.put("url",url);

        return tempHash;
    }


    public static PushImage image(HashMap<String, String> hashMap) {
        PushImage image = new PushImage();
        image.byline = hashMap.get("byline");
        image.caption = hashMap.get("caption");
        image.url = hashMap.get("url");


       // image.width = Integer.getInteger(hashMap.get("width"));
       // image.height = Integer.getInteger(hashMap.get("height"));
        //image.length = Integer.getInteger(hashMap.get("length"));
       // image.start = Integer.getInteger(hashMap.get("start"));
        return image;
    }



    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

   /* public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
  */
}
