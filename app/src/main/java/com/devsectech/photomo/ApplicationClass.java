package com.devsectech.photomo;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy.Builder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.devsectech.photomo.callback.OnProgressReceiver;
import com.devsectech.photomo.utils.AppHelper;
import com.devsectech.photomo.utils.TypefaceUtil;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.File;

public class ApplicationClass extends Application {
    public static Context context;
    private static ApplicationClass myApplication;
    private static OnProgressReceiver onProgressReceiver;
    public InterstitialAd mInterstitialAd;
    Intent intent;
    boolean isFinished;

    public static ApplicationClass getApplication() {
        return myApplication;
    }

    public static void setApplication(ApplicationClass application) {
        myApplication = application;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ApplicationClass.context = context;
    }

    public void onCreate() {
        super.onCreate();
        setApplication(this);
        MobileAds.initialize(this);
        StrictMode.setVmPolicy(new Builder().build());
        TypefaceUtil.overrideFont(getApplicationContext(), "serif", "fonts/OpenSans-Regular.ttf");
        setContext(getApplicationContext());
        loadFullScreenAdInsider();
        new AppOpenAdsManager(this);
    }

    public static boolean checkForStoragePermission(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        }else {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }

        }
    }

    public static void deleteTemp() {
        ContextWrapper cw = new ContextWrapper(context);
        File localStore = cw.getDir("localStore", Context.MODE_PRIVATE);
        File files = cw.getDir("files", Context.MODE_PRIVATE);
        if (localStore.exists()) {
            deleteDir(localStore);
        }
        if (files.exists()) {
            deleteDir(files);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }



    public OnProgressReceiver getOnProgressReceiver() {
        return this.onProgressReceiver;
    }

    public void setOnProgressReceiver(OnProgressReceiver onProgressReceiver) {
        this.onProgressReceiver = onProgressReceiver;
    }

    public void loadFullScreenAdInsider() {


        if (AppHelper.checkConnection(context)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            String adUnitId = "";
            if (BuildConfig.DEBUG)
                adUnitId = getString(R.string.ad_mob_interstitial_id);
            else {
                adUnitId = getString(R.string.ad_mob_interstitial_id_live);
            }
            if (adUnitId == null) {
                return;
            }
            Log.e("Ads ", "FullScreenAd adUnitId:  " + adUnitId);
            if (TextUtils.isEmpty(adUnitId)) {
                return;
            }

            InterstitialAd.load(context, adUnitId, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    Log.e("Ads ", "FullScreenAd: onAdLoaded");
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.e("Ads ", "FullScreenAd: onAdFailedToLoad: " + loadAdError.getMessage());
                    mInterstitialAd = null;
                }
            });
        }
    }

    public boolean isAdLoaded() {
        if (mInterstitialAd != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needToShowAd() {

        int ads_per_click = 2;
        int getCount = AppHelper.getClickCount();
        int newCount = getCount + 1;
        AppHelper.setClickCount(newCount);
        if (getCount != 0 && getCount % ads_per_click == 0) {
            return true;
        }
        return false;
    }

    public void showInterstitialNewForward(final Activity act, final Intent intent, final boolean isFinished) {
        this.intent = intent;
        this.isFinished = isFinished;

        if (!AppHelper.checkConnection(act)) {
            if (intent != null)
                act.startActivity(intent);
            if (isFinished) {
                if (act != null && !act.isFinishing())
                    act.finish();
            }
            return;
        }
        if (mInterstitialAd != null) {
            mInterstitialAd.show(act);
            AppHelper.setFullScreenIsInView(true);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                    doNextAction(act, intent, isFinished);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                    AppHelper.setFullScreenIsInView(false);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    Log.d("TAG", "The ad was shown.");
                }
            });
        } else {
            AppHelper.setFullScreenIsInView(false);
            if (intent != null)
                act.startActivity(intent);
            if (isFinished) {
                if (act != null && !act.isFinishing())
                    act.finish();
            }
        }
    }

    public void doNextAction(final Activity act, final Intent intent, final boolean isFinished) {
        loadFullScreenAdInsider();
        AppHelper.setFullScreenIsInView(false);
        if (intent != null)
            act.startActivity(intent);
        if (isFinished) {
            if (act != null && !act.isFinishing())
                act.finish();
        }
    }


}