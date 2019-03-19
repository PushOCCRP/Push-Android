package com.push.occrpnews.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.push.occrpnews.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Christopher Guess on 1/4/16.
 */
public class Language {

    private static String preferenceName = "LanguagePreference";
    private static String languageKey = "defaultLanguage";

    // For Serbian, in API < 21 it's annoyingly complex to get the Latin variant of the Serbian Language
    // So, instead of running a very annoying FOR loop every time, we just run it once per run and cache it here.
    // I can't imagine a system provided static locale changing ever...
    private static Locale serbianLatinLocale = null;

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

        // Here we go through the languages that are set in the config
        String setLanguagesString = ctx.getResources().getString(R.string.available_languages);
        String[] setLanguages = setLanguagesString.replaceAll("\\s+","").split(",");
        Set<String> comparedResults = new HashSet<>();

        for(String language : result){
            // ew
            for(String compareLanguage : setLanguages){
                if(language.equals(compareLanguage)){
                    comparedResults.add(compareLanguage);
                }
            }
        }
        return comparedResults;
    }

    public static Locale languageForTag(String tag) {
        Locale l;
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH){
            l = new Locale(tag.substring(0, 2));
        } else {
            if(tag == null){
                //stuff
                String something = "";
            }
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
            if(language.equals("sr")){
                l = serbianLatinLocale();
            } else {
                l = languageForTag(language);
            }
        }

        return l;
    }

    public static void setLanguage(Context context, Locale locale){
        //Check if the language can be set to this

        if(!languageAvailable(context, locale)){
            return;
        }

        if(locale.getLanguage().equals("sr")) {
            locale = serbianLatinLocale();
        }

        SharedPreferences preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(languageKey, locale.getLanguage());

        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, locale);
        }

        updateResourcesLegacy(context, locale);


        editor.commit();

        //Sets the language to report if there's a crash
        AnalyticsManager.getAnalyticsManager().setLanguage(locale.getDisplayLanguage());

        callListeners();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale locale) {

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, Locale locale) {

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static Context setDeviceToSavedLanguage(Context context) {
        Locale locale = getLanguage(context);

        //Check if the saved language is available, if not, get the first one
        if(languageAvailable(context, locale)) {
            setLanguage(context, locale);
        } else {
            Set<String> languages = getAppLanguages(context);
            String language = null;
            for(String tempLanguage : languages){
                language = tempLanguage;
                break;
            }
            setLanguage(context, languageForTag(language));
        }

        return context;
    }

    public static String bylineSeperator(Context context) {
        String language = getLanguageString(context);

        String separator;
        if(language.equals("en")){
            separator = " by ";
        } else if(language.equals("az")) {
            separator = ", ";
        } else if(language.equals("ru")) {
            separator = ", ";
        } else if(language.equals("rom")) {
            separator = ", ";
        } else if(language.equals("sr")) {
            separator = ", ";
        } else {
            separator = " ";
        }

        return separator;
    }

    public static Boolean dateShouldBeColloquial(Context context) {
        Locale locale = getLanguage(context);
        String language = locale.getLanguage();

        if(language.equals("az") || language.equals("rom") || language.equals("sr")){
            return false;
        }

        return true;
    }

    // Mostly for Serbian, but this does any language/script specific replacements that Android messes up
    public static String replaceForLanguages(Context context, String string){

        // Serbian comes in both Latin and Cyrillic scrips, but we're going to prefer Latin
        if(Language.getLanguage(context).getLanguage().equals("sr")){
            HashMap<String, String> languageReplacements = serbianDateReplacements();
            for (String key: languageReplacements.keySet()) {
                string = string.replaceAll(key, languageReplacements.get(key));
            }
        }

        return string;
    }

    // Builds up a HashMap for string replacements used in replaceForLanguages
    private static HashMap<String, String> serbianDateReplacements() {
        HashMap<String, String> languageReplacements = new HashMap<>();
        languageReplacements.put("january_cyrillic",   "january_latin");
        languageReplacements.put("february_cyrillic",  "february_latin");
        languageReplacements.put("march_cyrillic",     "march_latin");
        languageReplacements.put("april_cyrillic",     "april_latin");
        languageReplacements.put("may_cyrillic",       "may_latin");
        languageReplacements.put("june_cyrillic",      "june_latin");
        languageReplacements.put("july_cyrillic",      "july_latin");
        languageReplacements.put("august_cyrillic",    "august_latin");
        languageReplacements.put("september_cyrillic", "september_latin");
        languageReplacements.put("october_cyrillic",   "october_latin");
        languageReplacements.put("november_cyrillic",  "november_latin");
        languageReplacements.put("december_cyrillic",  "december_latin");
        return languageReplacements;
    }

    private static String getLanguageString(Context context){
        Locale locale = getLanguage(context);
        String langauge = locale.getLanguage();
        return langauge;
    }
    private static void callListeners() {

        if(listeners == null){
            return;
        }

        for(LanguageListener listener : listeners){
            listener.languageChanged();
        }
    }

    private static Boolean languageAvailable(Context context, Locale locale){
        Set<String> languages = getAppLanguages(context);
        Boolean languageAvailable = false;
        for(String language : languages){
            if(language.equals(locale.getLanguage())){
                languageAvailable = true;
            }
        }

        return languageAvailable;
    }

    private static Locale serbianLatinLocale(){

        if(serbianLatinLocale != null){
            return serbianLatinLocale;
        }

        Locale locale = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            for (Locale checkLocale : Locale.getAvailableLocales()) {
                if (checkLocale.getISO3Language().equals("srp") && checkLocale.getCountry().equals("LATN") && checkLocale.getVariant().equals("")) {
                    locale = checkLocale;
                }
            }
        } else {
            locale = new Locale.Builder().setLanguage("sr").setRegion("RS").setScript("Latn").build();
        }

        serbianLatinLocale = locale;

        return locale;
    }
}
