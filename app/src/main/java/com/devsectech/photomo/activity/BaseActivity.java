package com.devsectech.photomo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import com.devsectech.photomo.BuildConfig;
import com.devsectech.photomo.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.devsectech.photomo.utils.AppHelper;
import com.google.android.gms.ads.LoadAdError;

public abstract class BaseActivity extends Activity {
    public Context mContext;
    public abstract Context getContext();

    public abstract void initActions();

    public abstract void initData();

    public abstract void initViews();

    public void log(int i) {
    }

    public void log(Exception exc) {
    }

    public void log(String str) {
    }

    public void log(String str, int i) {
    }

    public void log(String str, Exception exc) {
    }

    public void log(String str, String str2) {
    }




    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
    }

    public void setContentView(int i) {
        super.setContentView(i);
        initViews();
        initData();
        initActions();
    }


    public void loadBannerAdsApp(RelativeLayout adContainerView) {
        if (AppHelper.checkConnection(this)) {
            AdView mAdView = new AdView(this);

            String adId = "";
            if (BuildConfig.DEBUG) {
                adId = getString(R.string.ad_mob_banner_id);
            } else {
                adId = getString(R.string.ad_mob_banner_id_live);
            }

            if(adId == null){
                return;
            }
            if(adId.isEmpty()){
                return;
            }
            Log.e("Ads ", "Banner id: " + adId);
            mAdView.setAdUnitId(adId);

            adContainerView.removeAllViews();
            adContainerView.addView(mAdView);

            AdSize adSize = getAdSize(this);
            mAdView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    Log.e("Ads ", "Banner onAdFailedToLoad()" + loadAdError.getCode());
                    adContainerView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    Log.e("Ads ", "Banner onAdLoaded()");
                    adContainerView.setVisibility(View.VISIBLE);
                }
            });
        }else {
            adContainerView.setVisibility(View.GONE);
        }

    }
    private AdSize getAdSize(Activity activity) {

        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }


    public void startActivity(Class<?> cls) {
        startActivity(new Intent(this.mContext, cls));
    }

    public void onPause() {
        super.onPause();
    }
}
