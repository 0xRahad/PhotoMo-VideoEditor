package com.devsectech.photomo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.devsectech.photomo.BuildConfig;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.devsectech.photomo.R;
import com.devsectech.photomo.fragment.PhotoFragment;
import com.devsectech.photomo.utils.Share;
import com.devsectech.photomo.utils.AppHelper;
import com.google.android.gms.ads.LoadAdError;


public class GalleryListActivity extends FragmentActivity implements OnClickListener {
    private static GalleryListActivity galleryActivity;
    public Activity mContext;
    public TextView tv_title;
    private ImageView iv_close;
    private long mLastClickTime = 0;

    public static GalleryListActivity getGalleryActivity() {
        return galleryActivity;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_gallery_album);
        galleryActivity = this;
        mContext = this;
        initView();
        initViewAction();

    }

    private void initView() {
        tv_title =  findViewById(R.id.tv_title);
        iv_close =  findViewById(R.id.iv_close);

        RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAdsApp(mAdView);

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



    private void initViewAction() {
        iv_close.setOnClickListener(this);
        updateFragment(PhotoFragment.newInstance());
    }


    public void updateFragment(Fragment fragment) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.simpleFrameLayout, fragment);
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        beginTransaction.commit();
    }

    public void onBackPressed() {
        Share.lst_album_image.clear();
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
            mLastClickTime = SystemClock.elapsedRealtime();
            int id = view.getId();
            if (id == R.id.iv_close) {
                onBackPressed();
            }
        }
    }




    public void onPause() {

        super.onPause();
    }

    public void onResume() {
        super.onResume();
        findViewById(R.id.fl_adplaceholder).setVisibility(View.GONE);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
