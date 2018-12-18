package com.pushapp.press.model;

import java.util.HashMap;

import io.realm.RealmObject;

public class PushVideo extends RealmObject {


    public String youtube_id;

    public static PushVideo video(HashMap<String, String> hashMap) {
        PushVideo video = new PushVideo();
        video.youtube_id = hashMap.get("youtube_id");
        return video;
    }

    public String getYoutubeId() {
        return youtube_id;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtube_id = youtubeId;
    }
}
