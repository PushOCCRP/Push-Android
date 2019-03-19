package com.push.occrpnews;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.push.occrpnews.model.Article;
import com.push.occrpnews.util.AuthenticationManager;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.push.occrpnews.R.layout.activity_about);
        initActionBar();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int color = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = this.getColor(R.color.primary_dark_material_dark);
        } else {
            //noinspection deprecation
            color = this.getResources().getColor(R.color.primary_dark_material_dark);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }

        String donationURL = getString(R.string.donation_url);

    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(com.push.occrpnews.R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.nav_item_about);
        TextView aboutButton = (TextView) findViewById(R.id.action_donate);

        //aboutButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        String donationURL = getString(R.string.donation_url);

        if(!donationURL.equals("") || AuthenticationManager.getAuthenticationManager().isLoggedIn(getApplicationContext())) {
            getMenuInflater().inflate(R.menu.menu_about, menu);

            if (donationURL.equals("")) {
                menu.getItem(0).setVisible(false);
            }

            if (!AuthenticationManager.getAuthenticationManager().isLoggedIn(getApplicationContext())) {
                menu.getItem(1).setVisible(false);
            }

        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id== R.id.action_donate) {
            showDonate();
        }else if(id==R.id.action_logout) {
            logout();
        }else if(id==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDonate() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.donation_url)));
        startActivity(browserIntent);
    }

    public void logout() {
        AuthenticationManager.getAuthenticationManager().logout(getApplicationContext());
        onBackPressed();
    }
}
