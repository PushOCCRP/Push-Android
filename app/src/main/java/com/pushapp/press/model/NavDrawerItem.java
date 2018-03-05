package com.pushapp.press.model;

/**
 * @author Bryan Lamtoo
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private String iconID;

    public String getIconID() {
        return iconID;
    }

    public void setIconID(String iconID) {
        this.iconID = iconID;
    }

    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title,String iconID) {
        this.showNotify = showNotify;
        this.title = title;
        this.iconID = iconID;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
