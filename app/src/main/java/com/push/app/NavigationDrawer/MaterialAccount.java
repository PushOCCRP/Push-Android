package com.push.app.NavigationDrawer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;

import com.push.app.NavigationDrawerElements.MaterialSection;
import com.push.app.listener.MaterialSectionListener;
import com.push.app.util.Utils;


/**
 * Created by neokree on 11/12/14.
 */
public class MaterialAccount {

    // datas

    private Drawable photo;
    private Drawable background;
    private Drawable circularPhoto;
    private String title;
    private String subTitle;
    private int accountNumber;
    private String notifications;

    private boolean hasNotifications;

    private Resources resources;
    private OnAccountDataLoaded listener;
    private MaterialSection sectionView;

    public static final int FIRST_ACCOUNT = 0;
    public static final int SECOND_ACCOUNT = 1;
    public static final int THIRD_ACCOUNT = 2;

    // constructors

    public MaterialAccount(Resources resources,String title, String subTitle, int photo,Bitmap background) {
        this.title = title;
        this.subTitle = subTitle;
        this.resources = resources;

        // resize and caching bitmap
        new ResizePhotoResource().execute(photo);
        if(background != null)
            new ResizeBackgroundBitmap().execute(background);

    }

    public MaterialAccount(Resources resources,String title, String subTitle, int photo,int background) {
        this.title = title;
        this.subTitle = subTitle;
        this.resources = resources;

        // resize and caching bitmap
        new ResizePhotoResource().execute(photo);
        new ResizeBackgroundResource().execute(background);
    }

    public MaterialAccount(Resources resources,String title, String subTitle, Bitmap photo, int background) {
        this.title = title;
        this.subTitle = subTitle;
        this.resources = resources;

        // resize and caching bitmap
        if(photo != null)
            new ResizePhotoBitmap().execute(photo);
        new ResizeBackgroundResource().execute(background);
    }

    public MaterialAccount(Resources resources,String title, String subTitle, Bitmap photo, Bitmap background) {
        this.title = title;
        this.subTitle = subTitle;
        this.resources = resources;

        // resize and caching bitmap
        if(photo != null)
            new ResizePhotoBitmap().execute(photo);
        if (background != null)
            new ResizeBackgroundBitmap().execute(background);
    }

    // setter

    public void setPhoto(int photo){
        new ResizePhotoResource().execute(photo);
    }

    public void setPhoto(Bitmap photo) {
        new ResizePhotoBitmap().execute(photo);
    }

    public void setBackground(Bitmap background) {
        new ResizeBackgroundBitmap().execute(background);
    }

    public void setBackground(int background) {
        new ResizeBackgroundResource().execute(background);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setAccountNumber(int number) {
        this.accountNumber = number;
    }

    public void setAccountListener(OnAccountDataLoaded listener) {
        this.listener = listener;
    }

    public MaterialAccount setNotifications(int number) {
        hasNotifications = true;
        notifications = String.valueOf(number);

        if(number >= 100) {
            notifications = "99+";
        }
        if(number < 0) {
            notifications = "0";
        }

        return this;

    }

    // getter

    public Drawable getPhoto() {
        return photo;
    }

    public Drawable getBackground() {
        return background;
    }

    public Drawable getCircularPhoto() {
        return circularPhoto;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public View getSectionView(Context ctx,  MaterialSectionListener listener,int position) {
        if(sectionView == null) {
            sectionView = new MaterialSection(ctx, MaterialSection.ICON_40DP,MaterialSection.TARGET_LISTENER);
            sectionView.useRealColor();
        }

        // set dei dati passati
//        sectionView.setTypeface(font);
        sectionView.setOnClickListener(listener);

        // set dei dati dell'account
        sectionView.setIcon(getCircularPhoto());
        sectionView.setTitle(getTitle());
        if(hasNotifications) {
            sectionView.setNotificationsText(notifications);
        }
        sectionView.setAccountPosition(position);

        return sectionView.getView();
    }

    // custom

    public void recycle() {
        Utils.recycleDrawable(photo);
        Utils.recycleDrawable(circularPhoto);
        Utils.recycleDrawable(background);
    }

    public interface OnAccountDataLoaded {

        public void onUserPhotoLoaded(MaterialAccount account);

        public void onBackgroundLoaded(MaterialAccount account);
    }

    // asynctasks

    private class ResizePhotoResource extends  AsyncTask<Integer, Void, BitmapDrawable> {

        @Override
        protected BitmapDrawable doInBackground(Integer... params) {
            Point photoSize = Utils.getUserPhotoSize(resources);

            Bitmap photo = Utils.resizeBitmapFromResource(resources,params[0],photoSize.x,photoSize.y);

            circularPhoto = new BitmapDrawable(resources,Utils.getCroppedBitmapDrawable(photo));
            return new BitmapDrawable(resources,photo);
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            photo = drawable;

            if(listener != null)
                listener.onUserPhotoLoaded(MaterialAccount.this);
        }
    }

    private class ResizePhotoBitmap extends AsyncTask<Bitmap, Void, BitmapDrawable> {

        @Override
        protected BitmapDrawable doInBackground(Bitmap... params) {
            Point photoSize = Utils.getUserPhotoSize(resources);


            Bitmap photo = Utils.resizeBitmap(params[0],photoSize.x,photoSize.y);
//            params[0].recycle();

            circularPhoto = new BitmapDrawable(resources,Utils.getCroppedBitmapDrawable(photo));
            return new BitmapDrawable(resources,photo);
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            photo = drawable;

            if(listener != null)
                listener.onUserPhotoLoaded(MaterialAccount.this);
        }
    }

    private class ResizeBackgroundResource extends AsyncTask<Integer, Void, BitmapDrawable> {
        @Override
        protected BitmapDrawable doInBackground(Integer... params) {
            Point backSize = Utils.getBackgroundSize(resources);

            Bitmap back = Utils.resizeBitmapFromResource(resources,params[0],backSize.x,backSize.y);

            return new BitmapDrawable(resources,back);
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            background = drawable;

            if(listener != null)
                listener.onBackgroundLoaded(MaterialAccount.this);
        }
    }

    private class ResizeBackgroundBitmap extends AsyncTask<Bitmap, Void, BitmapDrawable> {

        @Override
        protected BitmapDrawable doInBackground(Bitmap... params) {
            Point backSize = Utils.getBackgroundSize(resources);

            Bitmap back = Utils.resizeBitmap(params[0],backSize.x,backSize.y);
//            params[0].recycle();

            return new BitmapDrawable(resources,back);
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            background = drawable;

            if(listener != null)
                listener.onBackgroundLoaded(MaterialAccount.this);
        }
    }
}
