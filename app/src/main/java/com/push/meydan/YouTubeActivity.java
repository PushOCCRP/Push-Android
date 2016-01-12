package com.push.meydan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.youtube.player.YouTubePlayerFragment;
import com.push.meydan.fragment.YouTubeFragmentInterface;

/**
 * Created by christopher on 1/7/16.
 */
public class YouTubeActivity extends AppCompatActivity implements YouTubeFragmentInterface {

    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoId = getIntent().getStringExtra("videoId");
        setContentView(R.layout.activity_youtube);
    }

    //YouTubeFragmentInterface
    public String getYouTubeVideoId(){
        return videoId;
    }
}


