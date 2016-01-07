package com.push.meydan.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Bryan Lamtoo.
 */
public class Article implements Serializable{

    @Expose
    private String headline;
    @Expose
    private String description;
    @Expose
    private String body;
    @SerializedName("publish_date")
    @Expose
    private Integer publishDate;
    @Expose
    private String author;
    @Expose
    private String organization;
    @Expose
    private String language;
    @Expose
    @SerializedName("images")
    private List<HashMap<String, String>> images = new ArrayList<HashMap<String, String>>();
    @Expose
    @SerializedName("videos")
    private List<HashMap<String, String>> videos = new ArrayList<HashMap<String, String>>();
    @Expose
    private Integer id;
    @SerializedName("image_urls")
    @Expose
    private List<String> imageUrls = new ArrayList<String>();
    @SerializedName("captions")
    @Expose
    private List<String> captions = new ArrayList<String>();

    /**
     *
     * @return
     * The headline
     */
    public String getHeadline() {
        return headline;
    }

    /**
     *
     * @param headline
     * The headline
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The body
     */
    public String getBody() {
        return body;
    }

    /**
     *
     * @param body
     * The body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     *
     * @return
     * The publishDate
     */
    public Integer getPublishDate() {
        return publishDate;
    }

    /**
     *
     * @param publishDate
     * The publish_date
     */
    public void setPublishDate(Integer publishDate) {
        this.publishDate = publishDate;
    }

    /**
     *
     * @return
     * The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     * The organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     *
     * @param organization
     * The organization
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     *
     * @return
     * The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     * The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }


    /**
     *
     * @param images
     * The images
     */
    public void setImages(List<HashMap<String, String>> images) {
        this.images = images;
    }

    /**
     *
     * @return
     * The images
     */
    public List<HashMap<String, String>> getImages() {
        return images;
    }

    /**
     *
     * @param videos
     * The videos
     */
    public void setVideos(List<HashMap<String, String>> videos) {
        this.videos = videos;
    }

    /**
     *
     * @return
     * The videos
     */
    public List<HashMap<String, String>> getVideos() {
        return videos;
    }


    /**
     *
     * @return
     * The imageUrls
     */
    public List<String> getImageUrls() {
        return imageUrls;
    }

    /**
     *
     * @param imageUrls
     * The image_urls
     */
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }


    /**
     *
     * @return
     * The image captions
     */
    public List<String> getCaptions() {
        return captions;
    }

    /**
     *
     * @param captions
     * The image captions
     */
    public void setCaptions(List<String> captions) {
        this.captions = captions;
    }

}