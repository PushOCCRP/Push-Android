package com.push.app.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.push.app.interfaces.ScrollViewMovable;

/**
 * Created by CEO on 03/06/2015.
 */
public class CustomScrollView extends ScrollView {

    /**
     * Object that has inherited <code>ScrollViewMovable</code> interface.
     */
    private ScrollViewMovable mMovable;

    /**
     * Class constructor
     *
     * @param context
     *            application context
     */
    public CustomScrollView(Context context) {
        super(context);
    }

    /**
     * Class constructor
     *
     * @param context
     *            application context
     * @param attrs
     *            attributes
     */
    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    /**
     * Class constructor
     *
     * @param context
     *            application context
     * @param attrs
     *            attributes
     * @param defStyle
     *            styles
     */
    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    /**
     * Setter for the movable.
     *
     * @param movable
     */
    public void setMovable(ScrollViewMovable movable) {
        this.mMovable = movable;
    }

    /**
     * This is called in response to an internal scroll in this view (i.e., the
     * view scrolled its own contents). This is typically as a result of
     * scrollBy(int, int) or scrollTo(int, int) having been called.
     *
     * @param currentHorizontal
     *            Current horizontal scroll origin.
     * @param currentVertical
     *            Current vertical scroll origin.
     * @param previourHorizontal
     *            Previous horizontal scroll origin.
     * @param previousVertical
     *            Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int currentHorizontal, int currentVertical,
                                   int previourHorizontal, int previousVertical) {

        if (mMovable != null) {
            mMovable.onScrollChanged(currentHorizontal, currentVertical,
                    previourHorizontal, previousVertical);
        }
    }
}
