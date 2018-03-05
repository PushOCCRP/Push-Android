package com.pushapp.press.util;

/**
 * @author Bryan Lamtoo.
 */

import android.content.Context;

import com.pushapp.press.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * This utility class contains all Date operations
 *
 * @author |Bryan Lamtoo|
 *
 */
public class DateUtil {

    /**
     * Date format for the posts in the main page.
     */
    private static final String POST_DATE_MAIN_LIST_ITEM = "LLLL dd, yyyy";


    /**
     * Date format for the post in the single post page.
     */
    private static final String POST_DATE_TITLE_FORMAT = "dd MMMMM yyyy";

    /**
     * Date format for the published posts.
     */
    private static final String POSTS_DATE_PUBLISHED_FORMAT = "yyyyMMdd";

    /**
     *<code>SimpleDateFormat</code> for the posts in the main page.
     */
    private static SimpleDateFormat postDateMainListItemFormat(Context context) {
        return new SimpleDateFormat(
                POST_DATE_MAIN_LIST_ITEM, Language.getLanguage(context));
    }

    /**
     * <code>SimpleDateFormat</code> for the post in the single post page.
     */
    public static final SimpleDateFormat postDateTitleFormat = new SimpleDateFormat(
            POST_DATE_TITLE_FORMAT);

    /**
     * <code>SimpleDateFormat</code> for the published posts.
     */
    public static final SimpleDateFormat postsDatePublishedFormatter = new SimpleDateFormat(
            POSTS_DATE_PUBLISHED_FORMAT);

    //Yes, you have to pass in context here, so we can get the strings file. Android is dumb
   public static String setTime(Context context, long time, boolean colloquial){
        String status = "";

        if(time != 0){
            //set the text to show date and time //find out time since last load in milliseconds
            long difference = System.currentTimeMillis() - (time); //the time since the last load
            if(colloquial && Language.dateShouldBeColloquial(context)) {
                if (difference < (1000 * 60 * 60) && difference > 0) {
                    //if within 1 hour, display minutes
                    int minutesAgo = (int) Math.floor((difference / 1000) / 60);
                    status += (minutesAgo == 0) ? context.getResources().getString(R.string.just_now) : ((minutesAgo == 1) ? minutesAgo + " " +  context.getResources().getString(R.string.minutes_ago) : minutesAgo + " " + context.getResources().getString(R.string.minutes_ago));
                } else {
                    //if we are within 24 hours, display hours
                    if (difference < (1000 * 60 * 60 * 24) && difference > 0) {
                        int hoursAgo = (int) Math.floor(((difference / 1000) / 60) / 60);
                        status += (hoursAgo == 1) ? hoursAgo + " " + context.getResources().getString(R.string.hours_ago) : hoursAgo + " " + context.getResources().getString(R.string.hours_ago);
                    } else if (difference > 0) {//if we are within 2 days, display yesterday
                        int hold = 1000 * 60 * 60;
                        status += difference < (hold * 48) ? context.getResources().getString(R.string.yesterday) : difference < (hold * 72) ? context.getResources().getString(R.string.three_day_ago) :
                                DateUtil.postDateMainListItemFormat(context).format(time);
                    }
                }
            } else {
                status = DateUtil.postDateMainListItemFormat(context).format(time);
            }

            status = Language.replaceForLanguages(context, status);
        }else return "";
        return status;
    }
}
