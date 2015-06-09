package com.push.app.util;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * An improved version of DrawerLayout that will ensure that a locked open DrawerLayout can be used without capturing all touches into the client
 * area.
 * <p>
 *     To activate, call {@link #requestDisallowInterceptTouchEvent(boolean)} on the layout. Note that this should only ever be done in case when
 *     the drawer is to be locked open.
 * </p>
 *
 * Created by neokree on 04/01/15.
 *
 * Original source code by Rainer Burgstaller
 */
public class MaterialDrawerLayout extends DrawerLayout {
    private boolean multipaneSupport = false;

    public MaterialDrawerLayout(Context context) {
        super(context);
    }

    public MaterialDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        if (multipaneSupport) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setMultipaneSupport(boolean support) {
        if(Utils.isTablet(this.getResources())) {
            // custom implementation only for tablets
            multipaneSupport = support;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(multipaneSupport) {
            return false;
        }
        else {
            return super.onKeyUp(keyCode, event);
        }
    }
}
