package com.push.app.model;

import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * This class represents a Category from Wordpress
 *
 * @author Bryan Lamtoo.
 */
public class Category implements Serializable{
    /**
     * UID version for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identifier of the Category
     */
    private Integer mId;
    /**
     * Slug of the Category
     */
    private String mSlug;

    /**
     * Title of the Category
     */
    private String mTitle;

    /**
     * Class constructor
     *
     * @param id
     *            Identifier of the Category
     * @param title
     *            Title of the Category
     */
    public Category(Integer id, String title) {
        super();
        this.mId = id;
        this.mTitle = title;
    }

    /**
     * Class constructor
     *
     * @param jsonObject
     *            JSON Object
     * @throws JSONException
     *             Exception to be thrown
     */
    public Category(JSONObject jsonObject) throws JSONException {
        try {
            this.mId = Integer.valueOf(jsonObject.getString("id"));

            this.mTitle = Html.fromHtml(jsonObject.getString("title")).toString();
            this.mSlug = Html.fromHtml(jsonObject.getString("slug")).toString();
        } catch (NumberFormatException e) {
            //
        }
    }

    /**
     * Getter of the Category identifier
     *
     * @return the Category identifier
     */
    public Integer getId() {
        return mId;
    }


    /**
     * Getter of the Category Slug
     *
     * @return the Category Slug
     */
    public String getSlug() {

        return mSlug;
    }

    /**
     * Getter of the Category title
     *
     * @return the Category title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns a string representation of the object
     */
    @Override
    public String toString() {
        return getTitle();
    }
}
