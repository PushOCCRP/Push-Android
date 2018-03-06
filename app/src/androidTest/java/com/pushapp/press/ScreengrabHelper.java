//courtesy: https://gist.github.com/Peddro/1d6cafc72a9d77c274b7

package com.pushapp.press;

import android.os.Looper;

import android.os.Handler;

import tools.fastlane.screengrab.Screengrab;

public class ScreengrabHelper {

    private static ScreengrabHelper instance;
    private static final long INIT_DELAY = 2000;

    private long delay;
    private boolean screenShotTaken;

    public ScreengrabHelper() {
        this.delay = INIT_DELAY;
    }

    public static void delayScreenshot(String name) {
        ensureInstance();
        instance.takeDelayScreenshot(name);
    }

    private static void ensureInstance() {
        if (instance == null) {
            instance = new ScreengrabHelper();
        }
    }

    private void takeDelayScreenshot(String name) {
        screenShotTaken = false;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                screenShotTaken = true;
            }
        }, delay);

        new Wait(new Wait.Condition() {
            @Override
            public boolean check() {
                return screenShotTaken;
            }
        }).waitForIt();

        Screengrab.screenshot(name);
    }

    public static void setDelay(long delay) {
        ensureInstance();
        instance.changeDelay(delay);
    }
    
    public void changeDelay(long delay) {
        this.delay = delay;
    }
}