package com.push.occrp.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Christopher Guess on 1/4/16.
 */
public class Language {
    public static Set<String> getAppLanguages(Context ctx, int id ) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        Configuration conf = ctx.getResources().getConfiguration();
        Locale originalLocale = conf.locale;
        conf.locale = Locale.ENGLISH;
        final String reference = new Resources( ctx.getAssets(), dm, conf ).getString( id );

        Set<String> result = new HashSet<>();
        result.add( Locale.ENGLISH.getLanguage() );

        for( String loc : ctx.getAssets().getLocales() ){
            if( loc.isEmpty() ) continue;
            Locale l = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH ? new Locale( loc.substring( 0, 2 ) ) : Locale.forLanguageTag( loc );
            conf.locale = l;
            if( !reference.equals( new Resources( ctx.getAssets(), dm, conf ).getString( id ) ) ) result.add( l.getLanguage() );
        }
        conf.locale = originalLocale;
        return result;
    }
}
