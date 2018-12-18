package com.pushapp.press.model;

import android.media.Image;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * @author Bryan Lamtoo.
 */
public class Article extends RealmObject implements Serializable{

    @Expose
    private String headline;
    @Expose
    private String description;
    @Expose
    private String body;
    @SerializedName("publish_date")
    @Expose
    private String publishDate;
    @Expose
    private String author;
    @Expose
    private String organization;
    @Expose
    private String language;
    @Expose
    @SerializedName("header_image")
    private PushImage header_image = new PushImage();
    @Expose
    @SerializedName("images")
    private RealmList<PushImage> images = new RealmList<>();
    @Expose
    @SerializedName("videos")
    private RealmList<PushVideo> videos = new RealmList<>();
    @Expose
    @PrimaryKey
    private String id;
    @SerializedName("image_urls")
    @Expose
    private RealmList<String> imageUrls = new RealmList<String>();
    @SerializedName("captions")
    @Expose
    private RealmList<String> photoBylines = new RealmList<String>();
    @SerializedName("photoBylines")
    @Expose
    private RealmList<String> captions = new RealmList<String>();
    @SerializedName("url")
    @Expose
    private String url;

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
    public String getPublishDate() {
        return publishDate;
    }

    /**
     *
     * @param publishDate
     * The publish_date
     */
    public void setPublishDate(String publishDate) {
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
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @param headerImage
     * The header_image
     */
    public void setHeaderImage(HashMap<String, String> headerImage) {
        PushImage image = PushImage.image(headerImage);
        this.header_image = image;
        RealmList<PushImage> images = this.getImages();
        if(images.size() > 0 && image != null && images.get(0).url == image.url){
            images.remove(0);
            this.setImages(images);
        }
    }

    /**
     *
     * @return
     * The images
     */
    public PushImage getHeaderImage() {
        if(header_image != null && header_image.url != null) {
            return header_image;
        }

        if(images.size() > 0){
            return getImages().first();
        }

        return new PushImage();
        //return header_image;
    }

    public RealmList<PushImage> getImages() {
        return images;
    }

    public void setImages(RealmList<PushImage> images) {
        this.images = images;
    }




    /**
     *
    // * @param images
     * The images
     */
    /*public void setImages(List<HashMap<String, String>> images) {

        RealmList<PushImage> pushImages = new RealmList<PushImage>();
        for (HashMap<String, String> image: images) {
            PushImage pushImage = PushImage.image(image);
            pushImages.add(pushImage);
        }

        if(pushImages.size() > 0 && this.getHeaderImage() != null && pushImages.get(0).url == this.getHeaderImage().url){
            images.remove(0);
        }

        this.images = pushImages;
    }

    private void setImages(RealmList<PushImage> images) {
        this.images = images;
    }

    /**
     *
     * @return
     * The images
     */
   /* public RealmList<PushImage> getImages() {
        return images;
    }

    /**
     *
     * @param videos
     * The videos
     */
    public void setVideos(RealmList<PushVideo> videos) {
        this.videos = videos;
    }

    /**
     *
     * @return
     * The videos
     */
    public RealmList<PushVideo> getVideos() {
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
        RealmList<String> list = new RealmList<>();
        list.addAll(imageUrls);
        this.imageUrls = list;
    }


    /**
     *
     * @return
     * The image captions
     */
    public RealmList<String> getCaptions() {
        return captions;
    }

    /**
     *
     * @param captions
     * The image captions
     */
    public void setCaptions(List<String> captions) {
        RealmList<String> list = new RealmList<>();
        list.addAll(captions);
        this.captions = list;
    }

    /**
     *
     * @return
     * The image bylines
     */
    public RealmList<String> getPhotoBylines() {
        return photoBylines;
    }

    /**
     *
     * @param photoBylines
     * The image captions
     */
    public void setPhotoBylines(List<String> photoBylines) {
        RealmList<String> list = new RealmList<>();
        list.addAll(photoBylines);
        this.photoBylines = list;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        if(url == null){
            return "";
        }
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}