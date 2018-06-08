package com.pushapp.press.model;

import java.util.HashMap;

import io.realm.RealmObject;

public class PushVideo extends RealmObject {
    public String youtubeId;

    public static PushVideo video(HashMap<String, String> hashMap) {
        PushVideo video = new PushVideo();
        video.youtubeId = hashMap.get("youtube_id");
        return video;
    }
}
