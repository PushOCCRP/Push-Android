package com.push.occrpnews.util;

import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by christopher on 2/5/16.
 */
public class TimerEvent extends CustomEvent {

    private Long startTime;
    private Long endTime;
    private Long duration;

    public TimerEvent(String eventName){
        super(eventName);

        startTime = System.currentTimeMillis();
    }

    public void endTimer(){
        endTime = System.currentTimeMillis();
        duration = (endTime - startTime) / 1000;
        this.putCustomAttribute("Duration", duration);
    }

}
