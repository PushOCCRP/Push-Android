package com.push.meydan.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.push.meydan.R;

import java.util.logging.Logger;

/**
 * Created by christopher on 1/8/16.
 */
public class YouTubeFragment extends Fragment {

    private static YouTubePlayer mYoutubePlayer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        YouTubeFragmentInterface parentActivity = (YouTubeFragmentInterface)getActivity();
        final String videoId = parentActivity.getYouTubeVideoId();

        View rootView = inflater.inflate(R.layout.fragment_youtube, container, false);

        YouTubePlayerFragment youTubePlayerFragment = new YouTubePlayerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(youTubePlayerFragment, "player").commit();

        youTubePlayerFragment.initialize(getActivity().getApplicationContext().getString(R.string.youtube_key), new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

                if (!wasRestored) {
                    mYoutubePlayer = player;
                    mYoutubePlayer.setFullscreen(true);
                    mYoutubePlayer.loadVideo(videoId);
                    mYoutubePlayer.play();
                }

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                Log.e("youtube", arg1.toString());
                // TODO Auto-generated method stub

            }
        });

        //return null;

        return rootView;
    }
}