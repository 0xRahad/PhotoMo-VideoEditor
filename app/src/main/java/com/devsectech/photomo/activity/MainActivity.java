package com.devsectech.photomo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.devsectech.photomo.BuildConfig;
import com.devsectech.photomo.ApplicationClass;
import com.devsectech.photomo.R;
import com.devsectech.photomo.photoAlbum.MyAlbumActivity;
import com.devsectech.photomo.utils.Share;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import org.apache.http.protocol.HTTP;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.CAMERA;


public class MainActivity extends BaseParentActivity implements OnClickListener {
    private static final String TAG = "MainAct";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM = 103;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM2 = 104;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM3 = 105;
    public MainActivity moActivity;
    public String mSelectedOutputPath;
    public String mSelectedImagePath;
    public Uri mSelectedImageUri;
    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        mSelectedImagePath = mSelectedOutputPath;
                        if (stringIsNotEmpty(mSelectedImagePath)) {
                            File fileImageClick = new File(mSelectedImagePath);
                            if (fileImageClick.exists()) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    mSelectedImageUri = Uri.fromFile(fileImageClick);
                                } else {
                                    mSelectedImageUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", fileImageClick);
                                }
                                if (mSelectedImageUri == null) {
                                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Share.SAVED_BITMAP = mSelectedImageUri;
                                Share.imageUrl = mSelectedImagePath;
                                startActivity(new Intent(MainActivity.this, CropActivity.class));
                            }
                        }
                    }

                }
            });
    private LinearLayout lin_camera;
    private LinearLayout lin_gallery;
    private LinearLayout lin_my_creation;
    private LinearLayout lin_share;
    private LinearLayout lin_rate_us;


    public static boolean requestCameraPermissionApp(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(activity, CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM);
                return false;
            }

        }else {
            if (ActivityCompat.checkSelfPermission(activity, CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM);
                return false;
            }
        }

        return true;
    }

    public static boolean requestCameraPermissionApp3(Activity activity) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(activity, CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM3);
                return false;
            }

        }else {
            if (ActivityCompat.checkSelfPermission(activity, CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM3);
                return false;
            }
        }


        return true;
    }



    public static boolean checkForCameraPermission(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public boolean requestCameraPermissionApp2(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(activity, CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM2);
                return false;
            }

        }else {
            if (ActivityCompat.checkSelfPermission(activity, CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM2);
                return false;
            }
        }


        return true;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_home);
        this.moActivity = this;
        initViews();
        loadNative();
        initViewListeners();
        if (ApplicationClass.checkForStoragePermission(this)) {
            ApplicationClass.deleteTemp();
        }
    }





    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        com.google.android.gms.ads.nativead.MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        try {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.getStoreView().setVisibility(View.GONE);
        adView.getPriceView().setVisibility(View.GONE);

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
//			videoStatus.setText(String.format(Locale.getDefault(),
//					"Video status: Ad contains a %.2f:1 video asset.",
//					vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
//					refresh.setEnabled(true);
//					videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
//			videoStatus.setText("Video status: Ad does not contain a video asset.");
//			refresh.setEnabled(true);
        }
    }

    private void initViewListeners() {
        lin_gallery.setOnClickListener(this);
        lin_camera.setOnClickListener(this);
        lin_my_creation.setOnClickListener(this);
        lin_share.setOnClickListener(this);
        lin_rate_us.setOnClickListener(this);


    }

    private void loadNative() {
        AdLoader.Builder builder;
        if (BuildConfig.DEBUG) {
            builder = new AdLoader.Builder(this, getString(R.string.ad_mob_native_id));

        } else {
            String adUnitId = getString(R.string.ad_mob_native_id_live);
            if (adUnitId == null) {
                return;
            }
            if (TextUtils.isEmpty(adUnitId)) {
                return;
            }
            builder = new AdLoader.Builder(this, adUnitId);
        }


        builder.forNativeAd(nativeAd -> {
            FrameLayout frame_native = findViewById(R.id.frame_native);
            NativeAdView adView = (NativeAdView) LayoutInflater.from(this).inflate(R.layout.item_native_ad_unified, null);
            populateUnifiedNativeAdView(nativeAd, adView);
            frame_native.removeAllViews();
            frame_native.addView(adView);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.e("Ads ", "NativeAd onAdFailedToLoad: " + adError.getMessage());
            }
        }).build();

        AdRequest.Builder builerRe = new AdRequest.Builder();
        adLoader.loadAd(builerRe.build());
    }

    private void initViews() {
        lin_gallery = findViewById(R.id.lin_gallery);
        lin_camera = findViewById(R.id.lin_camera);
        lin_my_creation = findViewById(R.id.lin_my_creation);
        lin_share = findViewById(R.id.lin_share);
        lin_rate_us = findViewById(R.id.lin_rate_us);

    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(@NotNull View view) {
        switch (view.getId()) {
            case R.id.lin_camera:
                if (requestCameraPermissionApp(MainActivity.this)) {
                    toOpenCamera();
                }
                return;
            case R.id.lin_gallery:
                if (requestCameraPermissionApp2(MainActivity.this)) {
                    toGallery();
                }
                return;
            case R.id.lin_rate_us:
                final String appPackageName = getApplication().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return;

            case R.id.lin_my_creation:
                if (requestCameraPermissionApp3(MainActivity.this)) {
                    requestMyCreation();
                }

                return;
            case R.id.lin_share:
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(HTTP.PLAIN_TEXT_TYPE);
                intent.putExtra("android.intent.extra.SUBJECT", getResources().getString(R.string.app_name));
                StringBuilder sb = new StringBuilder();
                sb.append("Download this amazing ");
                sb.append(getResources().getString(R.string.app_name));
                sb.append(" app from play store\n\n");
                String sb2 = sb.toString();
                StringBuilder sb3 = new StringBuilder();
                sb3.append(sb2);
                sb3.append("https://play.google.com/store/apps/details?id=");
                sb3.append(getPackageName());
                sb3.append("\n\n");
                intent.putExtra("android.intent.extra.TEXT", sb3.toString());
                startActivity(Intent.createChooser(intent, "Choose one"));
                return;
            default:
                return;
        }
    }

    private void toGallery() {


        Intent intent =new Intent(moActivity, GalleryListActivity.class);
        if (ApplicationClass.getApplication().isAdLoaded() && ApplicationClass.getApplication().needToShowAd()) {
            ApplicationClass.getApplication().showInterstitialNewForward(MainActivity.this, intent, false);
        } else {
            startActivity(intent);
        }

    }

    private void requestMyCreation() {
        Intent intent =new Intent(moActivity, MyAlbumActivity.class);
        if (ApplicationClass.getApplication().isAdLoaded() && ApplicationClass.getApplication().needToShowAd()) {
            ApplicationClass.getApplication().showInterstitialNewForward(MainActivity.this, intent, false);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM: {
                if (!checkForCameraPermission(this)) {
                    Toast.makeText(this, R.string.phone_camera_permission_not_granted, Toast.LENGTH_SHORT).show();
                } else if (!ApplicationClass.checkForStoragePermission(this)) {
                    Toast.makeText(this, R.string.storage_permission_not_granted, Toast.LENGTH_SHORT).show();
                } else if (checkForCameraPermission(this) && ApplicationClass.checkForStoragePermission(this)) {
                    toOpenCamera();
                    return;
                }
                requestCameraPermissionApp(MainActivity.this);
                return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM2: {
                if (!ApplicationClass.checkForStoragePermission(this))
                    Toast.makeText(this, R.string.storage_permission_not_granted, Toast.LENGTH_SHORT).show();
                else {
                    toGallery();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA_PIP_CAM3: {
                if (!ApplicationClass.checkForStoragePermission(this))
                    Toast.makeText(this, R.string.storage_permission_not_granted, Toast.LENGTH_SHORT).show();
                else {
                    requestMyCreation();
                }
                return;
            }
        }
    }

    private void toOpenCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            cameraResultLauncher.launch(intent);
        }
    }

    private File createImageFile() {
        File storageDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + BuildConfig.APPLICATION_ID + "/CamPic/");
        storageDir.mkdirs();
        File image = null;
        try {
            image = new File(storageDir, getString(R.string.app_folder3));
            if (image.exists())
                image.delete();
            image.createNewFile();

            mSelectedOutputPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public boolean stringIsNotEmpty(String string) {
        if (string != null && !string.equals("null")) {
            if (!string.trim().equals("")) {
                return true;
            }
        }
        return false;
    }

}
