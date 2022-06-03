package com.devsectech.photomo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.devsectech.photomo.BuildConfig;
import com.devsectech.photomo.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.devsectech.photomo.utils.AppHelper;
import com.google.android.gms.ads.LoadAdError;

public abstract class BaseParentActivity extends AppCompatActivity {

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
