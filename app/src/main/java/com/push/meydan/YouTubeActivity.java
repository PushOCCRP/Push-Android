package com.push.meydan;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.push.meydan.interfaces.YouTubeFragmentInterface;

/**
 * Created by christopher on 1/7/16.
 */
public class YouTubeActivity extends AppCompatActivity implements YouTubeFragmentInterface {

    private String videoId;

    @Override

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoId = getIntent().getStringExtra("videoId");
        setContentView(R.layout.activity_youtube);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int color = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = this.getColor(R.color.primary_dark_material_dark);
        } else {
            //noinspection deprecation
            color = this.getResources().getColor(R.color.primary_dark_material_dark);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    //YouTubeFragmentInterface
    public String getYouTubeVideoId(){
        return videoId;
    }
}


