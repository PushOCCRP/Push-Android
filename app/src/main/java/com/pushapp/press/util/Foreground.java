package com.pushapp.press.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by christopher on 7/14/16.
 * adopted from https://steveliles.github.io/is_my_android_app_currently_foreground_or_background.html
 */
public class Foreground implements Application.ActivityLifecycleCallbacks {

    private static Foreground instance;

    public static void init(Application app){
        if (instance == null){
            instance = new Foreground();
            app.registerActivityLifecycleCallbacks(instance);
        }
    }

    public static Foreground get(){
        return instance;
    }

    private Foreground(){}

    private boolean foreground;

    public boolean isForeground(){
        return foreground;
    }

    public boolean isBackground(){
        return !foreground;
    }

    public void onActivityPaused(Activity activity){
        foreground = false;
    }

    public void onActivityResumed(Activity activity){
        foreground = true;
    }

    public void onActivityDestroyed(Activity activity){}
    public void onActivitySaveInstanceState(Activity activity, Bundle outState){}
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    public void onActivityStarted(Activity activity) {}
    public void onActivityStopped(Activity activity) {}

}
