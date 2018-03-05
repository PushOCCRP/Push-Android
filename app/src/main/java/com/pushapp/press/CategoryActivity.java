package com.pushapp.press;

import android.annotation.TargetApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.pushapp.press.fragment.CategoryFragment;
import com.pushapp.press.interfaces.CategoryFragmentInterface;
import com.pushapp.press.model.Article;

import java.util.ArrayList;

/**
 * Created by christopher on 10/29/16.
 */

public class CategoryActivity extends AppCompatActivity implements CategoryFragmentInterface {
    private Toolbar mToolbar;

    private ArrayList<Article> articles;

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
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
    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(com.pushapp.press.R.id.toolbar);
        setSupportActionBar(mToolbar);

        Intent i = this.getIntent();
        getSupportActionBar().setTitle((String)i.getExtras().get("category_name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Article> getArticles(){
        return articles;
    }
}
