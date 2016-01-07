package com.push.meydan.util;

/**
 * @author Bryan Lamtoo.
 */

import java.text.SimpleDateFormat;

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
    public static final String POST_DATE_MAIN_LIST_ITEM = "LLLL dd, yyyy";


    /**
     * Date format for the post in the single post page.
     */
    public static final String POST_DATE_TITLE_FORMAT = "dd MMMMM yyyy";

    /**
     * Date format for the published posts.
     */
    public static final String POSTS_DATE_PUBLISHED_FORMAT = "yyyyMMdd";

    /**
     *<code>SimpleDateFormat</code> for the posts in the main page.
     */
    public static final SimpleDateFormat postDateMainListItemFormat = new SimpleDateFormat(
            POST_DATE_MAIN_LIST_ITEM);

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

   public static String setTime(long time, boolean colloquial){
        String status = "";

        if(time != 0){
            //set the text to show date and time //find out time since last load in milliseconds
            long difference = System.currentTimeMillis() - (time); //the time since the last load

            if(colloquial) {
                if (difference < (1000 * 60 * 60) && difference > 0) {
                    //if within 1 hour, display minutes
                    int minutesAgo = (int) Math.floor((difference / 1000) / 60);
                    status += (minutesAgo == 0) ? "just now" : ((minutesAgo == 1) ? minutesAgo + " minutes ago" : minutesAgo + " minutes ago");
                } else {
                    //if we are within 24 hours, display hours
                    if (difference < (1000 * 60 * 60 * 24) && difference > 0) {
                        int hoursAgo = (int) Math.floor(((difference / 1000) / 60) / 60);
                        status += (hoursAgo == 1) ? hoursAgo + " hours ago" : hoursAgo + " hours ago";
                    } else if (difference > 0) {//if we are within 2 days, display yesterday
                        int hold = 1000 * 60 * 60;
                        status += difference < (hold * 48) ? "Yesterday" : difference < (hold * 72) ? "3 days ago" :
                                DateUtil.postDateMainListItemFormat.format(time);
                    }
                }
            } else {
                status = DateUtil.postDateMainListItemFormat.format(time);
            }
        }else return "";
        return status;
    }
}
