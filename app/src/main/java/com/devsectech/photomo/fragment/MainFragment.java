package com.devsectech.photomo.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
//import com.appcenter.utils.Permission;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.CropImageView.CropMode;
import com.isseiaoki.simplecropview.CropImageView.RotateDegrees;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.devsectech.photomo.R;
import com.devsectech.photomo.activity.CropActivity;
import com.devsectech.photomo.activity.AlbumListActivity;
import com.devsectech.photomo.utils.Share;
import java.io.File;

public class MainFragment extends Fragment {
    private static final String PROGRESS_DIALOG = "ProgressDialog";
    private final OnClickListener btnListener = new OnClickListener() {
        public void onClick(View view) {
            int id = view.getId();
            if (id != R.id.tv_back) {
                switch (id) {
                    case R.id.button16_9 :
                        changecolor();
                        moView.findViewById(R.id.button16_9).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCropMode(CropMode.RATIO_16_9);
                        return;
                    case R.id.button1_1:
                        changecolor();
                        moView.findViewById(R.id.button1_1).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCropMode(CropMode.SQUARE);
                        return;
                    case R.id.button3_4:
                        changecolor();
                        moView.findViewById(R.id.button3_4).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCropMode(CropMode.RATIO_3_4);
                        return;
                    case R.id.button4_3:
                        changecolor();
                        moView.findViewById(R.id.button4_3).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCropMode(CropMode.RATIO_4_3);
                        return;
                    case R.id.button9_16:
                        changecolor();
                        moView.findViewById(R.id.button9_16).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCropMode(CropMode.RATIO_9_16);
                        return;
                    case R.id.buttonCircle:
                        changecolor();
                        moView.findViewById(R.id.buttonCircle).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCropMode(CropMode.CIRCLE);
                        return;
                    case R.id.buttonCustom:
                        changecolor();
                        moView.findViewById(R.id.buttonCustom).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCustomRatio(7, 5);
                        return;
                    case R.id.buttonDone:
                            cropImage();
                    case R.id.buttonFitImage:
                        changecolor();
                        moView.findViewById(R.id.buttonFitImage).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCropMode(CropMode.FIT_IMAGE);
                        return;
                    case R.id.buttonFree:
                        changecolor();
                        moView.findViewById(R.id.buttonFree).setBackgroundResource(R.drawable.crop_select);
                        mCropView.setCropMode(CropMode.FREE);
                        return;
                    default:
                        switch (id) {
                            case R.id.buttonRotateLeft:
                                mCropView.rotateImage(RotateDegrees.ROTATE_M90D);
                                return;
                            case R.id.buttonRotateRight:
                                mCropView.rotateImage(RotateDegrees.ROTATE_90D);
                                return;
                            case R.id.buttonShowCircleButCropAsSquare:
                                changecolor();
                                moView.findViewById(R.id.buttonShowCircleButCropAsSquare).setBackgroundResource(R.drawable.crop_select);
                                mCropView.setCropMode(CropMode.CIRCLE_SQUARE);
                                return;
                            default:
                                return;
                        }
                }
            } else {
                Log.e("MainFrag", "--> tv_back");
                ((CropActivity) getActivity()).finish();
            }
        }
    };
    private final CropCallback mCropCallback = new CropCallback() {
        public void onSuccess(Bitmap bitmap) {
            StringBuilder sb = new StringBuilder();
            sb.append("mCropCallback:==>");
            sb.append(bitmap);
            Log.e("TAG", sb.toString());
            String str = "mCropCallback";
            Log.e(str, str);
            dismissProgress();
            String saveFaceInternalStorage = Share.saveFaceInternalStorage(bitmap, getContext());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("onSuccess: cropimage ");
            sb2.append(saveFaceInternalStorage);
            String str2 = "cropfile";
            Log.e(str2, sb2.toString());
            Share.croppedImage = saveFaceInternalStorage;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("onSuccess: Shrea.cropimage ");
            sb3.append(Share.croppedImage);
            Log.e(str2, sb3.toString());
            if (AlbumListActivity.activity != null) {
                AlbumListActivity.activity.finish();
            }
            CropActivity cropImageActivity = (CropActivity) getActivity();
            StringBuilder sb4 = new StringBuilder();
            sb4.append("file:///");
            sb4.append(saveFaceInternalStorage);
            cropImageActivity.startResultActivity(Uri.parse(sb4.toString()));
        }

        public void onError(Throwable th) {
            dismissProgress();
        }
    };

    public CropImageView mCropView;
    private final LoadCallback mLoadCallback = new LoadCallback() {
        public void onSuccess() {
            dismissProgress();
        }

        public void onError(Throwable th) {
            dismissProgress();
        }
    };
    private LinearLayout mRootLayout;
    private final SaveCallback mSaveCallback = new SaveCallback() {
        public void onSuccess(Uri uri) {
            String str = "mSaveCallback";
            Log.e(str, str);
            dismissProgress();
        }

        public void onError(Throwable th) {
            dismissProgress();
        }
    };

    public View moView;
    private SharedPreferences preferences;

    public static MainFragment getInstance() {
        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(new Bundle());
        return mainFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        Log.e("MainFrag", "onCreate");
        showProgress();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_main, null, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.moView = view;
        bindViews(view);
        StringBuilder sb = new StringBuilder();
        sb.append("Fragment:==>");
        sb.append(Share.imageUrl);
        Log.e("TAG", sb.toString());
        if (this.mCropView.getImageBitmap() == null) {
            if (Share.imageUrl == null || Share.imageUrl.equals("")) {
                dismissProgress();
                Share.RestartApp((Activity) getContext());
            } else {
                ((RequestBuilder) ((RequestBuilder) Glide.with((Fragment) this).load(Share.imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE)).skipMemoryCache(true)).into((ImageView) this.mCropView);
                dismissProgress();
            }
        }
    }

    private void bindViews(View view) {
        this.mCropView = (CropImageView) view.findViewById(R.id.cropImageView);
        view.findViewById(R.id.buttonDone).setOnClickListener(this.btnListener);
        view.findViewById(R.id.buttonFitImage).setOnClickListener(this.btnListener);
        view.findViewById(R.id.button1_1).setOnClickListener(this.btnListener);
        view.findViewById(R.id.button3_4).setOnClickListener(this.btnListener);
        view.findViewById(R.id.button4_3).setOnClickListener(this.btnListener);
        view.findViewById(R.id.button9_16).setOnClickListener(this.btnListener);
        view.findViewById(R.id.button16_9).setOnClickListener(this.btnListener);
        view.findViewById(R.id.buttonFree).setOnClickListener(this.btnListener);
        view.findViewById(R.id.buttonRotateLeft).setOnClickListener(this.btnListener);
        view.findViewById(R.id.buttonRotateRight).setOnClickListener(this.btnListener);
        view.findViewById(R.id.buttonCustom).setOnClickListener(this.btnListener);
        view.findViewById(R.id.buttonCircle).setOnClickListener(this.btnListener);
        view.findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(this.btnListener);
        view.findViewById(R.id.tv_back).setOnClickListener(this.btnListener);
        this.mRootLayout = (LinearLayout) view.findViewById(R.id.layout_root);
    }

    public void cropImage() {
        String str = "cropImage";
        Log.e(str, str);
        showProgress();
        this.mCropView.startCrop(createSaveUri(), this.mCropCallback, this.mSaveCallback);
    }

    public void showProgress() {
        getFragmentManager().beginTransaction().add((Fragment) ProgressDialogFragment.getInstance(), PROGRESS_DIALOG).commitAllowingStateLoss();
    }

    public void dismissProgress() {
        if (isAdded()) {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                ProgressDialogFragment progressDialogFragment = (ProgressDialogFragment) fragmentManager.findFragmentByTag(PROGRESS_DIALOG);
                if (progressDialogFragment != null) {
                    getFragmentManager().beginTransaction().remove(progressDialogFragment).commitAllowingStateLoss();
                }
            }
        }
    }

    public Uri createSaveUri() {
        return Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
    }

    @SuppressLint({"NeedOnRequestPermissionsResult"})
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 23) {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", getActivity().getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mCropView.getImageBitmap() == null && Share.imageUrl != null && !Share.imageUrl.equals("")) {
            StringBuilder sb = new StringBuilder();
            sb.append("image uri1111:==>");
            sb.append(Share.imageUrl);
            Log.e("TAG", sb.toString());
            ((RequestBuilder) ((RequestBuilder) Glide.with((Fragment) this).load(Share.imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE)).skipMemoryCache(true)).into((ImageView) this.mCropView);
            dismissProgress();
        }
    }


    public void changecolor() {
        this.moView.findViewById(R.id.buttonFitImage).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.button1_1).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.button3_4).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.button4_3).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.button9_16).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.button16_9).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.buttonFree).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.buttonCustom).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.buttonCircle).setBackgroundResource(R.drawable.crop_notselect);
        this.moView.findViewById(R.id.buttonShowCircleButCropAsSquare).setBackgroundResource(R.drawable.crop_notselect);
    }

    public void onPause() {
        super.onPause();
        this.mCropView.setImageDrawable(null);
    }
}
