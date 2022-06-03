package com.devsectech.photomo.customView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.devsectech.photomo.R;
import com.devsectech.photomo.customView.controllersapp.ToolsController;
import com.devsectech.photomo.utils.BitmapHelper;
import com.devsectech.photomo.callback.OnBitmapListCreated;
import java.util.ArrayList;
import java.util.List;

public class CustomAnimationView extends ConstraintLayout implements OnClickListener {
    public static final float DEFAULT_ICON_RADIUS = 20.0f;
    public LottieAnimationView animationView;
    public ImageView ivRemoveView;
    public View mCustomView;
    public LayoutInflater mInflater;
    public Paint mPaint;
    public Activity moActivity;
    public List<Bitmap> moBitmapList = new ArrayList();
    public Context moContext;
    public OnBitmapListCreated moOnBitmapListCreated;
    public RelativeLayout moRlMainLayout;

    @SuppressLint({"StaticFieldLeak"})
    public class BitmapTask extends AsyncTask<Void, Void, Boolean> {
        public BitmapTask() {
        }

        public  void lambda$doInBackground$0$CustomAnimationView$BitmapTask(int i) {
            animationView.setFrame(i + 1);
            moBitmapList.add(BitmapHelper.getBitmap2(moRlMainLayout));
        }

        public void onPreExecute() {
            super.onPreExecute();
        }

        public Boolean doInBackground(Void... voidArr) {
            int i = 0;
            while (i < 30) {
                try {
                    final int finalI = i;
                    moActivity.runOnUiThread(new Runnable() {
                        public final void run() {
                            BitmapTask.this.lambda$doInBackground$0$CustomAnimationView$BitmapTask(finalI);
                        }
                    });
                    Thread.sleep(100);
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    return Boolean.valueOf(false);
                }
            }
            return Boolean.valueOf(true);
        }

        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            moOnBitmapListCreated.onBitmapRecived(moBitmapList);
            StringBuilder sb = new StringBuilder();
            sb.append("onPostExecute: ");
            sb.append(moBitmapList.size());
            Log.e("Test", sb.toString());
        }
    }

    public CustomAnimationView(Context context) {
        super(context);
        this.moContext = context;
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public void hideFrame(boolean z) {
        if (z) {
            this.animationView.setBackground(null);
            this.ivRemoveView.setVisibility(View.GONE);
            this.mCustomView.setOnTouchListener(null);
            return;
        }
        this.animationView.setBackgroundDrawable(ContextCompat.getDrawable(this.moContext, R.drawable.bg_animation_layout));
        this.ivRemoveView.setVisibility(View.VISIBLE);
        this.mCustomView.setOnTouchListener(new MultiTouchListener());
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void init() {
        this.mPaint = new Paint();
        this.mCustomView = this.mInflater.inflate(R.layout.custom_sticker_animation_view, this, true);
        this.moRlMainLayout = (RelativeLayout) this.mCustomView.findViewById(R.id.main_layout);
        this.animationView = (LottieAnimationView) this.mCustomView.findViewById(R.id.lottie_animation);
        this.ivRemoveView =  this.mCustomView.findViewById(R.id.iv_close_icon);
        this.ivRemoveView.setOnClickListener(this);
        ((ConstraintLayout) this.mCustomView.findViewById(R.id.clView)).setOnTouchListener(new MultiTouchListener());
    }

    public void init1(Activity activity) {
        this.moActivity = activity;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.iv_close_icon) {
            ShareClass.isViewRemoved = true;
            ToolsController.controller.resetStickerAdapter();
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("OnDraw", "onDraw: ");
        canvas.drawCircle(0.0f, 0.0f, 20.0f, this.mPaint);
    }

    public void pauseAnimation() {
        this.animationView.pauseAnimation();
    }

    public void playAnimation() {
        this.animationView.playAnimation();
    }

    public void setAnimation(String str, String str2) {
        this.animationView.setAnimation(str);
        if (!str2.isEmpty()) {
            this.animationView.setImageAssetsFolder(str2);
        }
    }

    public void setFrame(int i) {
        this.animationView.setFrame(i);
    }

    public void setScaleType() {
        this.animationView.setScaleType(ScaleType.CENTER_CROP);
    }

    public CustomAnimationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.moContext = context;
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public CustomAnimationView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.moContext = context;
        this.mInflater = LayoutInflater.from(context);
        init();
    }
}
