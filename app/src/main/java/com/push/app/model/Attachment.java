package com.push.app.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * This class represents an Attachment from Wordpress
 * The attachment is an image and it is always
 *
 * @author Bryan Lamtoo.
 */
public class Attachment implements Serializable {

    /**
     * UID version for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Full size of the Attachment
     */
    private AttachmentType mFullSize;

    /**
     * Large size of the Attachment
     */
    private AttachmentType mLargeSize;

    /**
     * Medium size of the Attachment
     */
    private AttachmentType mMediumSize;

    private AttachmentType mThumbnailSize;

    public AttachmentType getThumbnailSize() {
        return mThumbnailSize;
    }


    /**
     * Class constructor
     *
     * @param jsonObject
     *            JSON object
     * @throws JSONException
     *             exception to be thrown
     */
    public Attachment(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("sizes")) {

            JSONObject images = jsonObject.optJSONObject("sizes");

            if (images != null) {
                setThumbnailSize(images);
                setLargeSize(images);
                setMediumSize(images);
            }

        } else {
            setThumbnailSize(jsonObject);
            setLargeSize(jsonObject);
            setMediumSize(jsonObject);

        }

    }



//    private void setFullSize(JSONObject jsonObject) throws JSONException {
//        if (jsonObject.has("full")) {
//            String fullUrl = jsonObject.getJSONObject("full").getString("url");
//            int fullWidth = jsonObject.getJSONObject("full").getInt("width");
//            int fullHeight = jsonObject.getJSONObject("full").getInt("height");
//
//            this.mFullSize = new AttachmentType(fullUrl, fullWidth, fullHeight);
//        }
//    }

    private void setLargeSize(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("app_thumbnail")) {
            String largeUrl = jsonObject.getJSONObject("app_thumbnail").getString("url");
            int largeWidth = jsonObject.getJSONObject("app_thumbnail").getInt("width");
            int largeHeight = jsonObject.getJSONObject("app_thumbnail").getInt("height");

            this.mLargeSize = new AttachmentType(largeUrl, largeWidth, largeHeight);
        }

    }

    private void setMediumSize(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("medium")) {
            String mediumUrl = jsonObject.getJSONObject("medium").getString("url");
            int mediumWidth = jsonObject.getJSONObject("medium").getInt("width");
            int mediumHeight = jsonObject.getJSONObject("medium").getInt("height");

            this.mMediumSize = new AttachmentType(mediumUrl, mediumWidth,
                    mediumHeight);
        }
    }

    private void setThumbnailSize(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("thumbnail")) {

            String mediumUrl = jsonObject.getJSONObject("thumbnail").getString("url");
            int mediumWidth = jsonObject.getJSONObject("thumbnail").getInt("width");
            int mediumHeight = jsonObject.getJSONObject("thumbnail").getInt("height");

            this.mThumbnailSize = new AttachmentType(mediumUrl, mediumWidth,
                    mediumHeight);
        }
    }

    /**
     * Getter for the Full size of the Attachment
     *
     * @return Full size of the Attachment
     */
    public AttachmentType getFullSize() {
        return mFullSize;
    }

    /**
     * Getter for the Large size of the Attachment
     *
     * @return Large size of the Attachment
     */
    public AttachmentType getLargeSize() {
        return mLargeSize;
    }

    /**
     * Getter for the Medium size of the Attachment
     *
     * @return Medium size of the Attachment
     */
    public AttachmentType getMediumSize() {
        return mMediumSize;
    }






    /**
     * Returns the best attachment by given width
     *
     * @param width
     *            to dermine the best attachment
     * @return the best attachment by the given width
     */
    public AttachmentType giveMeBestAttachmentForWidth(int width) {

        if (getLargeSize() != null && getFullSize() != null && width > getLargeSize().getWidth()) {
            return getFullSize();
        } else if (getLargeSize() != null && getMediumSize() != null && width > getMediumSize().getWidth()) {
            return getLargeSize();
        } else if (getMediumSize() != null){
            return mMediumSize;
        }

        return null;
    }
}
