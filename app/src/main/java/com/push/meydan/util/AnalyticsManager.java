package com.push.meydan.util;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.ShareEvent;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

/**
 * Created by christopher on 2/5/16.
 */



public class AnalyticsManager {

    public enum AnalyticType {
        FABRIC, GOOGLE_ANALYTICS
    }

    private AnalyticType analyticType;
    private static HashMap<String, TimerEvent> timeTrackers;

    public static AnalyticsManager analyticsManager;

    private AnalyticsManager() {
        // Init
    }

    public static AnalyticsManager getAnalyticsManager() {
        if (analyticsManager == null) {
            analyticsManager = new AnalyticsManager();
        }
        return analyticsManager;
    }

    public void setAnalyticsManager(AnalyticType analyticType, Context context){
        this.analyticType = analyticType;
        switch (analyticType){
            case FABRIC:
                Fabric.with(context, new Crashlytics());
                break;
            case GOOGLE_ANALYTICS:
                break;
        }
    }

    public AnalyticType getAnalyticType(){
        return this.analyticType;
    }

    public void setLanguage(String language){
        switch (getAnalyticsManager().analyticType) {
            case FABRIC:
                Crashlytics.setString("language", language);
                break;
            case GOOGLE_ANALYTICS:
                break;
        }
    }

    public static void logContentView(String contentName){
        logContentView(contentName, null, null);
    }

    public static void logContentView(String contentName, String contentType){
        logContentView(contentName, contentType);
    }

    public static void logContentView(String contentName, String contentType, String contentId) {
        if(!analyticsCorrectlySetUp()){
            return;
        }
        switch (getAnalyticsManager().analyticType) {
            case FABRIC:
                ContentViewEvent viewEvent = new ContentViewEvent();
                if(contentName != null){
                    viewEvent.putContentName(contentName);
                }

                if(contentType != null){
                    viewEvent.putContentType(contentType);
                }

                if(contentId != null){
                    viewEvent.putContentId(contentId);
                }

                Answers.getInstance().logContentView(viewEvent);
                break;
            case GOOGLE_ANALYTICS:
                //Not implemented
                break;
            default:
                //Do nothing
        }

    }

    public static void logSearch(String query) {
        if(!analyticsCorrectlySetUp()){
            return;
        }
        switch (getAnalyticsManager().analyticType) {
            case FABRIC:
                Answers.getInstance().logSearch(new SearchEvent().putQuery(query));
                break;
            case GOOGLE_ANALYTICS:
                //Not implemented
                break;
            default:
                //Do nothing
        }

    }

    public static void logShare(String method, String contentName, String contentType, String contentId) {
        if(!analyticsCorrectlySetUp()){
            return;
        }
        switch (getAnalyticsManager().analyticType) {
            case FABRIC:
                Answers.getInstance().logShare(new ShareEvent()
                        .putMethod(method)
                        .putContentName(contentName)
                        .putContentType(contentType)
                        .putContentId(contentId));
                break;
            case GOOGLE_ANALYTICS:
                //Not implemented
                break;
            default:
                //Do nothing
        }

    }

    public static void startTimerForContentView(Object object, String name){
        if(!analyticsCorrectlySetUp()){
            return;
        }
        TimerEvent timerEvent = new TimerEvent(name);
        String identifier = identitfier(object, name);

        if(timeTrackers == null) {
            timeTrackers = new HashMap<>();
        }

        if(timeTrackers.get(identifier) != null){
            return;
        }

        timeTrackers.put(identifier, timerEvent);
    }

    public static void endTimerForContentView(Object object, String name){
        if(!analyticsCorrectlySetUp() || timeTrackers == null){
            return;
        }
        String identifier = identitfier(object,name);
        TimerEvent timerEvent = timeTrackers.get(identifier);
        if(timerEvent != null) {
            timerEvent.endTimer();

            switch (getAnalyticsManager().analyticType) {
                case FABRIC:
                    Answers.getInstance().logCustom(timerEvent);
                    break;
                case GOOGLE_ANALYTICS:
                    //Not implemented
                    break;
                default:
                    //Do nothing
            }
            timeTrackers.remove(identifier);
        }
    }

    public static void logError(String errorMessage) {
        if(!analyticsCorrectlySetUp()){
            return;
        }
        switch (getAnalyticsManager().analyticType) {
            case FABRIC:
                Answers.getInstance().logCustom(new CustomEvent(errorMessage));
                break;
            case GOOGLE_ANALYTICS:
                //Not implemented
                break;
            default:
                //Do nothing
        }
    }

    private static String identitfier(Object object, String name) {
        String identifier = Integer.toString(object.hashCode()) + ":" + name;
        return identifier;
    }

    private static Boolean analyticsCorrectlySetUp() {

        Boolean valid = true;

        if(getAnalyticsManager().analyticType == null){
            valid = false;
        }

        return valid;
    }
}
