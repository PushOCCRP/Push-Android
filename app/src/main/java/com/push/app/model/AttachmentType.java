package com.push.app.model;

import java.io.Serializable;

/**
 * This class represents the type of an Attachment
 *
 * @author Bryan Lamtoo.
 */
public class AttachmentType implements Serializable {
    /**
     * UID version for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Height of the attachment
     */
    private int mHeight;

    /**
     * URL of the attachment
     */
    private String mUrl;

    /**
     * Width of the attachment
     */
    private int mWidth;

    /**
     * Class constructor
     *
     * @param url
     *            URL of the attachment
     * @param width
     *            Width of the attachment
     * @param height
     *            Height of the attachment
     */
    public AttachmentType(String url, int width, int height) {
        super();
        this.mUrl = url;
        this.mWidth = width;
        this.mHeight = height;
    }

    /**
     * Returns the height of the attachment
     *
     * @return height of the attachment
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Returns the URL of the attachment
     *
     * @return URL of the attachment
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns the width of the attachment
     *
     * @return width of the attachment
     */
    public int getWidth() {
        return mWidth;
    }
}
