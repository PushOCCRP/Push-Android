package com.push.cins;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.push.cins.adapter.PostFragmentAdapter;
import com.push.cins.interfaces.SyncManager.ArticleDelegate;
import com.push.cins.model.Article;
import com.push.cins.util.AnalyticsManager;
import com.push.cins.util.SyncManager;
import com.push.cins.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Zed on 7/15/2015.
 */
public class NotificationHandler extends Activity implements ArticleDelegate {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        setContentView(com.push.cins.R.layout.notification_handler);
        final String articleId = getIntent().getExtras().getString("articleId");
        String message = getIntent().getStringExtra("message");

        Boolean shownInForeground = getIntent().getBooleanExtra("inForeground", false);

        final NotificationHandler notificationHandler = this;
        if(shownInForeground){
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Dialog))
                    .setTitle(getApplication().getText(R.string.notification_header))
                    .setMessage(message)
                    .setPositiveButton(getApplication().getText(R.string.notification_read_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SyncManager.getSyncManager().articleForId(articleId, getApplicationContext(), notificationHandler);
                        }
                    })
                    .setNegativeButton(getApplication().getText(R.string.notification_cancel_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            SyncManager.getSyncManager().articleForId(articleId, getApplicationContext(), this);
        }
    }

    public void didRetrieveArticle(Article article){
        Intent i = new Intent(this, DetailPostActivity.class);
        if(PostFragmentAdapter.postItems != null) {
            PostFragmentAdapter.postItems.clear();
        } else {
            PostFragmentAdapter.postItems = new ArrayList<>();
        }

        PostFragmentAdapter.postItems.add(article);
        i.putExtra("postPosition", 0);
        i.putExtra("postTitle", article.getHeadline());
        i.putExtra("description", article.getDescription());

        AnalyticsManager.logContentView(article.getHeadline(),
                "Search Result Opened", Integer.toString(article.getId()));

        // store the new time in the preferences file
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificationClicked", true);
        editor.commit();

        startActivity(i);
    }
}
