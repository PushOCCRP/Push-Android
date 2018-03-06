package com.pushapp.press.util;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by christopher on 6/15/16.
 */
public class PromotionsManager {

    public static PromotionsManager promotionsManager;

    private PromotionsManager() {
        // Init
    }

    public static PromotionsManager getPromotionsManager() {
        if (promotionsManager == null) {
            promotionsManager = new PromotionsManager();
        }
        return promotionsManager;
    }

    public ArrayList promotions(Context context) {

        Map promotion = loadPromotions(context);
        ArrayList arrayList = new ArrayList();

        if(promotion == null){
            return null;
        }

        String startDateString = (String)promotion.get("start-date");
        String endDateString = (String)promotion.get("end-date");

        Calendar startDate = new GregorianCalendar();;
        Calendar endDate = new GregorianCalendar();;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try {
            startDate.setTime(format.parse(startDateString));
            endDate.setTime(format.parse(endDateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // We want it to do one extra day.
        endDate.add(Calendar.DAY_OF_MONTH, 1);

        if (Calendar.getInstance().after(startDate) && Calendar.getInstance().before(endDate)) {
            arrayList.add(promotion);
        }

        return arrayList;
    }

    private Map loadPromotions(Context context) {

        String filePath = "promotions.json";

        StringBuilder buf=new StringBuilder();

        InputStream htmlStream = null;
        try {

            htmlStream = context.getAssets().open(filePath);

            BufferedReader in = new BufferedReader(new InputStreamReader(htmlStream, "UTF-8"));
            String str;

            while ((str=in.readLine()) != null) {
                buf.append(str+"\n");
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Gson gson = new Gson();
        Map map = (Map) gson.fromJson(buf.toString(), Map.class);
//        Yaml yaml = new Yaml();
//        Map map = (Map) yaml.load(buf.toString());

        return map;
    }


}
