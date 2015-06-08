package com.push.app.util;

/**
 * @author Bryan Lamtoo.
 */
public class Contants {

    /**
     * URL of your WordPress server
     */
    public static final String WORDPRESS_SERVER_URL = "http://dmb-team.com/wp/";
    /**
     * Maximum number of posts for the slider in the MainActivity
     */
    public static final int WORDPRES_SLIDER_MAX_POSTS = 3;

    /**
     * URL to fetch Wordpress recent posts by given category
     */
    private String WORDPRESS_FETCH_POSTS_BY_CAT_URL = "%s?json=get_category_posts&category_id=%d&count=%s&page=%d";

    /**
     * URL to fetch Wordpress recent posts by given category
     */
    private String WORDPRESS_FETCH_RECENT_POSTS_URL = "%s?json=get_recent_posts&count=%s&page=%d";


    public static class URLS{
        public static String FEED_URL = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";
    }
}
