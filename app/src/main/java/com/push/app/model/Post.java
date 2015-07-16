package com.push.app.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.push.app.util.DateUtil;

/**
 * This class represents a Post from Wordpress
 *
 * @author |Bryan Lamtoo|
 *
 */
public class Post implements Serializable {

    /**
     * UID version for serialization
     */
    private static final long serialVersionUID = 1L;
    private String[] imageUrls;
    private String organisation;
    private String language;
    private String author;

    /**
     * Content of the Post
     */
    private String mContent;

    /**
     * Identifier of the Post
     */
    private String mid;

    /**
     * The description for the story
     */
    private String except;

    public String getExcept() {
        return except;
    }

    public void setExcept(String except) {
        this.except = except;
    }

    /**
     * The date that the Post was published
     */
    private Date mPublishedDate;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Title of the Post
     */
    private String mTitle;

    public Post(){}

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Date getmPublishedDate() {
        return mPublishedDate;
    }

    public void setmPublishedDate(Date mPublishedDate) {
        this.mPublishedDate = mPublishedDate;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Post( String author,String organisation,String language, List<Category> mCategories, String mContent, String mid, String except, Date mPublishedDate, String mTitle) {
        this.mContent = mContent;
        this.mid = mid;
        this.except = except;
        this.mPublishedDate = mPublishedDate;
        this.mTitle = mTitle;
        this.author = author;
        this.organisation = organisation;
        this.language = language;

    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Class constructor
     *
     * @param jsonObject
     *            JSON Object
     * @throws Exception
     */
    public Post(JSONObject jsonObject) throws Exception {
        this.mid = jsonObject.getString("ID");
        this.mTitle = Html.fromHtml(jsonObject.getString("headline"))
                .toString();
        this.except = Html.fromHtml(jsonObject.getString("description"))
                .toString();
        this.mContent = jsonObject.getString("body");
//
//        removeAdsense();
//        this.mStatus = jsonObject.getString("status");
        this.mPublishedDate = DateUtil.postsDatePublishedFormatter
                .parse(jsonObject.getString("publish_date"));
        this.author = jsonObject.getString("author");
//        this.imageUrls = jsonObject.get;

    }

    /**
     * Getter of the Post content
     *
     * @return Post content
     */
    public Spanned getContent() {
        return Html.fromHtml(mContent.replace((char) 160, (char) 32).replace((char) 65532, (char) 32).trim());
    }

    public String getContentString() {
        return mContent;
    }

    /**
     * Getter of the Post identifier
     *
     * @return Post identifier
     */
    public String getId() {
        return mid;
    }

    /**
     * Getter of date when the Post was published
     *
     * @return date when the Post was published
     */
    public Date getPublishedDate() {
        return mPublishedDate;
    }


    /**
     * Getter of the Post Title
     *
     * @return Post Title
     */
    public String getTitle() {
        return mTitle;
    }
}
