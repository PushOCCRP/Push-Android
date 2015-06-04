package com.push.app.util;

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
    public static final String POST_DATE_MAIN_LIST_ITEM = "dd.MM.yyyy";

    /**
     * Date format for the post in the single post page.
     */
    public static final String POST_DATE_TITLE_FORMAT = "dd MMMMM yyyy";

    /**
     * Date format for the published posts.
     */
    public static final String POSTS_DATE_PUBLISHED_FORMAT = "yyyy-MM-dd hh:mm:ss";

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
}
