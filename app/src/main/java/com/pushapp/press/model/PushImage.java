package com.pushapp.press.model;

import java.util.HashMap;

import io.realm.RealmObject;

public class PushImage extends RealmObject {
    public String byline;
    public String caption;
    public String url;
    //public int length;
    //public int height;
    //public int width;
    //public int start;

    public static PushImage image(HashMap<String, String> hashMap) {
        PushImage image = new PushImage();
        image.byline = hashMap.get("byline");
        image.caption = hashMap.get("caption");
        image.url = hashMap.get("url");
        //image.width = Integer.getInteger(hashMap.get("width"));
        //image.height = Integer.getInteger(hashMap.get("height"));
        //image.length = Integer.getInteger(hashMap.get("length"));
        //image.start = Integer.getInteger(hashMap.get("start"));
        return image;
    }
}
