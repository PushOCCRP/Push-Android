package com.push.app.listener;


import android.os.Handler;
import android.view.View;

import com.push.app.MainActivity;
import com.push.app.model.Post;

/**
 * Listener for the action when a post should be opened in a single page
 * @author Bryan Lamtoo
 */
public class PostListener implements View.OnClickListener {
    /**
     *  The post that is going to be loaded
     */
    private Post mPostToLoad;

    /**
     *  The position of the post that is going to be loaded
     */
    private int mPostPosition;

    /**
     * <code>MainActivity</code> instance to delegate control to
     */
    private MainActivity mResponceActivity;

    /**
     * Class constructor
     *
     * @param responceActivity
     *            <code>MainActivity</code> instance to delegate control to
     * @param postToLoad
     *            the post that is going to be loaded
     * @param   postPosition
     *              the position of the post in the list
     * @param mHandler
     *            action handler
     */
    public PostListener(MainActivity responceActivity, Post postToLoad,
                        int postPosition, Handler mHandler) {
        super();
        this.mResponceActivity = responceActivity;
        this.mPostToLoad = postToLoad;
        this.mPostPosition = postPosition;

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v
     *            The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        mResponceActivity.openSelectedPost(mPostToLoad, mPostPosition);
    }

}
