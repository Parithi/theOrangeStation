package com.parithi.theorangestation;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.gms.ads.MobileAds;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by earul on 19/12/16.
 */

public class TheOrangeStation extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4511313557400931~3397613993");

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Bariol.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager conMgr = null;
            if (context != null)
                conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            return (conMgr != null && conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr
                    .getActiveNetworkInfo().isConnected());
        }catch (Exception ignored){
            return false;
        }
    }
}
