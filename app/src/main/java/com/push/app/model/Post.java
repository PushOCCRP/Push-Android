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

    /**
     * List of Post Attachments
     */
    private List<Attachment> mAttachments;

    /**
     * List of Post Categories
     */
    private List<Category> mCategories;

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

    /**
     * Status of the Post
     */
    private String mStatus;

    /**
     * Title of the Post
     */
    private String mTitle;



    /**
     * URL of the Post
     */
    private String mUrl;

    public Post(){}

    /**
     * Class constructor
     *
     * @param jsonObject
     *            JSON Object
     * @throws Exception
     */
    public Post(JSONObject jsonObject) throws Exception {
        this.mid = jsonObject.getString("ID");
        this.mTitle = Html.fromHtml(jsonObject.getString("title"))
                .toString();
        this.except = Html.fromHtml(jsonObject.getString("excerpt"))
                .toString();
        String text = embedYoutubeVideos(jsonObject.getString("content"));
        this.mContent = text;
//
//        removeAdsense();
//        this.mStatus = jsonObject.getString("status");
        this.mUrl = jsonObject.getString("link");
        this.mPublishedDate = DateUtil.postsDatePublishedFormatter
                .parse(jsonObject.getString("date").replace("T"," "));
//
//        JSONArray categoriesJson = jsonObject.getJSONArray("categories");
//
//        mCategories = new ArrayList<Category>();
//
//        for (int i = 0; i < categoriesJson.length(); i++) {
//            mCategories.add(new Category(categoriesJson.getJSONObject(i)));
//        }
//
//        //JSONArray attachmentsJson = jsonObject.getJSONArray("attachments");
//
        mAttachments = new ArrayList<Attachment>();
//
//		/*for (int i = 0; i < attachmentsJson.length(); i++) {
//
//			mAttachments.add(new Attachment(attachmentsJson.getJSONObject(i)));
//		}*/
//
//
//        addAttachmentFromThumbnailImages(jsonObject);
        addAttachmentFromAttachments(jsonObject);
//
//        for (Attachment attachment : mAttachments) {
//            if (attachment.getFullSize() == null && attachment.getLargeSize() == null && attachment.getMediumSize() == null && attachment.getNotSizedAttachment() == null) {
//                mAttachments.remove(attachment);
//            }
//        }
//
//        Log.d("Adding Post", "Title - " + mTitle);

    }

    /**
     * Getter of the Post Attachments
     *
     * @return Post Attachments
     */
    public List<Attachment> getAttachments() {
        return mAttachments;
    }

    /**
     * Getter of the Post Categories
     *
     * @return Post Categories
     */
    public List<Category> getCategories() {
        return mCategories;
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
     * Getter of the Post Status
     *
     * @return Post Status
     */
    public String getStatus() {
        return mStatus;
    }

    /**
     * Getter of the Post Title
     *
     * @return Post Title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Getter of the Post URL
     *
     * @return Post URL
     */
    public String getUrl() {
        return mUrl;
    }


    private void addAttachmentFromAttachments(JSONObject jsonObject) throws JSONException {

        if (jsonObject.has("featured_image")) {


//            JSONObject mAttachment = jsonObject.getJSONObject("featured_image");
//
                JSONObject attachment = jsonObject.getJSONObject("featured_image").getJSONObject("attachment_meta");

//
                if (attachment.has("sizes") && attachment.optJSONObject("sizes") != null && attachment.optJSONObject("sizes").length() > 0) {
                  mAttachments.add(new Attachment(attachment));
                }
        }
    }

    private void removeAdsense()throws Exception{
        String adSensePrefix = "(adsbygoogle";
        String adSenseSuffix = ".push({});";
        if(mContent.indexOf(adSensePrefix) > -1){
            int startSuffixPosition = mContent.indexOf(adSensePrefix);
            int endSuffixPosition = mContent.indexOf(adSenseSuffix, startSuffixPosition);

            mContent = mContent.replace(mContent.substring(startSuffixPosition, endSuffixPosition + adSenseSuffix.length()), "");;

        }
    }

    private String embedYoutubeVideos(String text)throws Exception{
        String adSensePrefix = "<iframe";
        String adSenseSuffix = "</iframe>";
        String sourceStart = "src=\"";
        String sourceEnd = "\"";
        String source = "";

        if(text.indexOf(adSensePrefix) > -1){
            int startSuffixPosition = text.indexOf(adSensePrefix);
            int endSuffixPosition = text.indexOf(adSenseSuffix, startSuffixPosition);
            if(text.indexOf(sourceStart) > -1){
                int sourceStartPosition = text.indexOf(sourceStart) + sourceStart.length();
                int sourceEndPosition = text.indexOf(sourceEnd, sourceStartPosition) + sourceEnd.length();
                source = text.substring(sourceStartPosition, sourceEndPosition - 1);
                source = source.replaceFirst("//", "");
                source = "<a href=\"http://" + source+ "\">"+ source + "</a>";
            }

            text = text.replace(text.substring(startSuffixPosition, endSuffixPosition + adSenseSuffix.length()), source);;
        }
        return text;
    }
}
