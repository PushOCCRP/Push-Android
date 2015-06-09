package com.push.app.util;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.push.app.NavigationDrawerElements.MaterialSection;


/**
 * Created by neokree on 14/02/15.
 */
public class MaterialActionBarDrawerToggle<Fragment> extends ActionBarDrawerToggle {

    private MaterialSection<Fragment> requestedSection;
    private boolean request;

    public MaterialActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        request = false;
    }

    public void addRequest(MaterialSection section) {
        request = true;
        requestedSection = section;
    }

    public void removeRequest() {
        request = false;
        requestedSection = null;
    }

    public boolean hasRequest() {
        return request;
    }

    public MaterialSection getRequestedSection() {
        return requestedSection;
    }

}
