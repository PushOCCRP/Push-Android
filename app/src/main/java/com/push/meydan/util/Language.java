package com.push.meydan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.push.meydan.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Christopher Guess on 1/4/16.
 */
public class Language {

    private static String preferenceName = "LanguagePreference";
    private static String languageKey = "defaultLanguage";

    private static ArrayList<LanguageListener> listeners;

    public static void addListener(LanguageListener listener) {
        if(listeners == null) {
            listeners = new ArrayList<>();
        }

        if(listeners.contains(listener)){
            return;
        }

        listeners.add(listener);
    }

    // Adopted from http://stackoverflow.com/a/33145518
    public static Set<String> getAppLanguages(Context ctx) {
        int id = R.string.menu_search;
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        Configuration conf = ctx.getResources().getConfiguration();
        Locale originalLocale = conf.locale;
        conf.locale = Locale.ENGLISH;
        final String reference = new Resources( ctx.getAssets(), dm, conf ).getString( id );

        Set<String> result = new HashSet<>();
        result.add( Locale.ENGLISH.getLanguage() );

        for( String loc : ctx.getAssets().getLocales() ){
            if( loc.isEmpty() ) continue;
            Locale l = languageForTag(loc);
            conf.locale = l;
            Resources resources = new Resources(ctx.getAssets(), dm, conf);

            if( !reference.equals(resources.getString(id))) {
                result.add(l.getLanguage());
            }
        }

        conf.locale = originalLocale;
        return result;
    }

    public static Locale languageForTag(String tag) {
        Locale l;
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH){
            l = new Locale(tag.substring(0, 2));
        } else {
            l = Locale.forLanguageTag(tag);
        }

        return l;
    }

    public static Locale getLanguage(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);

        String language = preferences.getString(languageKey, null);

        Locale l;
        if(language == null){
            l = Locale.getDefault();
        } else {
            l = languageForTag(language);
        }

        return l;
    }

    public static void setLanguage(Context context, Locale locale){
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(languageKey, locale.getLanguage());

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

        editor.commit();

        callListeners();
    }

    public static void setDeviceToSavedLanguage(Context context) {
        Locale locale = getLanguage(context);
        setLanguage(context, locale);
    }

    public static String bylineSeperator(Context context) {
        Locale locale = getLanguage(context);
        String langauge = locale.getLanguage();

        String seperator = null;
        if(langauge.equals("en")){
            seperator = " by ";
        } else if(langauge.equals("az")) {
            seperator = ", ";
        } else if(langauge.equals("ru")) {
            seperator = ", ";
        } else {
            seperator = " ";
        }

        return seperator;
    }

    private static void callListeners() {
        if(listeners == null){
            return;
        }

        for(LanguageListener listener : listeners){
            listener.languageChanged();
        }
    }
}
