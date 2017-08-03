package com.parithi.theorangestation;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parithi.theorangestation.fragments.TaxiFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        if(TheOrangeStation.isConnected(this)){
//            AdRequest adRequest = new AdRequest.Builder().addTestDevice("E49AB252E21B9515E2B66516BE1D0542").build();
        AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }

        TaxiFragment taxiFragment = new TaxiFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, taxiFragment)
                .commit();
    }

}
