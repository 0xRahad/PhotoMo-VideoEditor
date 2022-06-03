package com.devsectech.photomo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.devsectech.photomo.BuildConfig;
import com.devsectech.photomo.ApplicationClass;
import com.devsectech.photomo.R;
import com.devsectech.photomo.callback.OnProgressReceiver;
import com.devsectech.photomo.callback.onVideoSaveListener;
import com.devsectech.photomo.customView.beans.Projeto;
import com.devsectech.photomo.customView.controllersapp.ToolsController;
import com.devsectech.photomo.customView.controllersapp.Utils;
import com.devsectech.photomo.customView.controllersapp.VideoSaver;
import com.devsectech.photomo.photoAlbum.VideoPreviewActivity;
import com.devsectech.photomo.utils.DatabaseHandler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.io.File;
import java.text.NumberFormat;

public class SavingActivity extends Activity {
    public static String INTENT_PROJETO = "PROJETO";
    public static int MAX_RESOLUTION_SAVE = 1080;
    public static int MIN_RESOLUTION_SAVE = 600;
    static int total = MIN_RESOLUTION_SAVE + MAX_RESOLUTION_SAVE;
    public static int MAX_RESOLUTION_SAVE_FREE = (total / 2);
    public DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);
    public NumberFormat f147nf;
    public Projeto projetoToSave;
    public float resolucaoOriginal;
    public SeekBar seekResolucao;
    public TextView txResolucao;
    public TextView txTempoSave;
    public VideoSaver videoSaver;
    public int miHeight;
    public int miWidth;
    VideoSaver videoSaver2;
    ApplicationClass application;
    private Button btSave, btClose;
    private SeekBar seekTempo;
    private LinearLayout rel_process;
    private RelativeLayout rel_save;


    public void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        setContentView(R.layout.activity_save);

        rel_process = findViewById(R.id.rel_process);
        rel_save = findViewById(R.id.rel_save);
        rel_process.setVisibility(View.GONE);
        rel_save.setVisibility(View.VISIBLE);

        this.setFinishOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        projetoToSave = (Projeto) getIntent().getParcelableExtra(INTENT_PROJETO);
        projetoToSave = databaseHandler.getProjeto(projetoToSave.getId());
        projetoToSave.reloadBitmapUri(this, databaseHandler);
        btClose = (Button) findViewById(R.id.btClose);
        btClose.setOnClickListener(new SaveCloseClick());
        seekTempo = (SeekBar) findViewById(R.id.seekTempoSave);
        seekTempo.setMax(8000);
        txTempoSave =  findViewById(R.id.txTempoSave);
        seekTempo.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                projetoToSave.setTempoSave(10000 - Math.round((((float) i) / ((float) seekBar.getMax())) * 8000.0f));
                TextView access$200 = txTempoSave;
                StringBuilder sb = new StringBuilder();
                sb.append(f147nf.format((double) (((float) projetoToSave.getTempoSave()) / 1000.0f)));
                access$200.setText(sb.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                projetoToSave.updateProjeto(databaseHandler);
            }
        });
        f147nf = NumberFormat.getInstance();
        f147nf.setMaximumFractionDigits(1);
        int tempoSave = projetoToSave.getTempoSave();
        if (tempoSave < 6000) {
            tempoSave = 6000;
        }
        seekTempo.setProgress(1);
        seekTempo.setProgress(2);
        seekTempo.setProgress(10000 - tempoSave);
        seekResolucao = (SeekBar) findViewById(R.id.seekResolucaoSave);
        seekResolucao.setMax(MAX_RESOLUTION_SAVE - MIN_RESOLUTION_SAVE);
        txResolucao =  findViewById(R.id.txResolucaoSave);
        seekResolucao.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                String str;
                int round = Math.round((((float) i) / ((float) seekBar.getMax())) * ((float) (SavingActivity.MAX_RESOLUTION_SAVE - SavingActivity.MIN_RESOLUTION_SAVE))) + SavingActivity.MIN_RESOLUTION_SAVE;
                if (round % 2 != 0) {
                    round++;
                }
                if (projetoToSave.getWidth() > projetoToSave.getHeight()) {
                    miWidth = round;
                    SavingActivity saveActivity = SavingActivity.this;
                    float f = (float) round;
                    saveActivity.miHeight = Math.round((((float) saveActivity.projetoToSave.getHeight()) / ((float) projetoToSave.getWidth())) * f);
                    StringBuilder sb = new StringBuilder();
                    sb.append(round);
                    sb.append("x");
                    sb.append(Math.round((((float) projetoToSave.getHeight()) / ((float) projetoToSave.getWidth())) * f));
                    str = sb.toString();
                } else {
                    SavingActivity saveActivity2 = SavingActivity.this;
                    float f2 = (float) round;
                    saveActivity2.miWidth = Math.round((((float) saveActivity2.projetoToSave.getWidth()) / ((float) projetoToSave.getHeight())) * f2);
                    miHeight = round;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(Math.round((((float) projetoToSave.getWidth()) / ((float) projetoToSave.getHeight())) * f2));
                    sb2.append(" x ");
                    sb2.append(round);
                    str = sb2.toString();
                }
                txResolucao.setText(str);
                pintarTextoIdeal(round);
                projetoToSave.setResolucaoSave(round);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                projetoToSave.updateProjeto(databaseHandler);
            }
        });
        resolucaoOriginal = (float) Math.min(Math.max(projetoToSave.getHeight(), projetoToSave.getWidth()), MAX_RESOLUTION_SAVE);
        float f = resolucaoOriginal;
        if (f % 2.0f != 0.0f) {
            f += 1.0f;
        }
        resolucaoOriginal = f;
        int resolucaoSave = projetoToSave.getResolucaoSave();
        if (resolucaoSave > MAX_RESOLUTION_SAVE_FREE) {
            seekResolucao.setProgress(1);
            seekResolucao.setProgress(2);
            seekResolucao.setProgress(resolucaoSave - MIN_RESOLUTION_SAVE);
            (findViewById(R.id.txTamanhoIdeal)).setOnClickListener(new tamanhoIdealOnClickListener());
            btSave = findViewById(R.id.btSalvar);
            btSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    rel_process.setVisibility(View.VISIBLE);
                    rel_save.setVisibility(View.GONE);
                    salvarVideo(false);
                }
            });
            setFinishOnTouchOutside(false);
            videoSaver2 = (VideoSaver) getLastNonConfigurationInstance();
        } else {
            seekResolucao.setProgress(1);
            seekResolucao.setProgress(2);
            seekResolucao.setProgress(resolucaoSave - MIN_RESOLUTION_SAVE);
            (findViewById(R.id.txTamanhoIdeal)).setOnClickListener(new tamanhoIdealOnClickListener());
            btSave = findViewById(R.id.btSalvar);

            btSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    rel_process.setVisibility(View.VISIBLE);
                    rel_save.setVisibility(View.GONE);
                    salvarVideo(false);
                }
            });
            setFinishOnTouchOutside(false);
            videoSaver2 = (VideoSaver) getLastNonConfigurationInstance();
        }
        if (videoSaver2 != null) {
            videoSaver = videoSaver2;
            videoSaver.setContext(this);
            if (videoSaver.isSalvando()) {
                setFinishOnTouchOutside(false);
            }
        }


        application = new ApplicationClass();
        application.setOnProgressReceiver(new OnProgressReceiver() {
            @Override
            public void onImageProgressFrameUpdate(final float progress) {
                int p = (int) ((25.0f * progress) / 100.0f);
                Log.e("TAG", "Image Progress = " + ((int) progress) + " || P = " + p);
                int ddd = (int) progress;
            }

        });


        loadNative();
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
//            mNativeAdsGHome.add(nativeAd);
            FrameLayout fl_adplaceholder = findViewById(R.id.fl_adplaceholder);

            NativeAdView adView = (NativeAdView) LayoutInflater.from(this).inflate(R.layout.item_native_ad_unified, null);
            populateUnifiedNativeAdView(nativeAd, adView);
            fl_adplaceholder.removeAllViews();
            fl_adplaceholder.addView(adView);


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


    public void pintarTextoIdeal(int i) {
        TextView textView =  findViewById(R.id.txTamanhoIdeal);
        if (i == ((int) resolucaoOriginal)) {
            textView.setTextColor(Utils.getColor(this, R.color.colorPrimary));
        } else {
            textView.setTextColor(Utils.getColor(this, R.color.color_dialog_hint_text));
        }
    }

    @SuppressLint({"WrongConstant"})
    public void salvarVideo(boolean z) {
        setFinishOnTouchOutside(false);
        setRequestedOrientation(5);
        getWindow().setFlags(16, 16);
        videoSaver = new VideoSaver(this, this.projetoToSave, this.miWidth, this.miHeight);
        videoSaver.setTempoAnimacao(projetoToSave.getTempoSave());
        videoSaver.setResolucao(projetoToSave.getResolucaoSave());
        videoSaver.setComLogo(z);
        getResources().getString(R.string.project_folder);
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.getPublicAlbumStorageDir(getResources().getString(R.string.videos_folder)).getPath());
        sb.append("/");
        sb.append(projetoToSave.getTitulo());
        sb.append(".mp4");
        File file = new File(sb.toString());
        videoSaver.setSaveListener(new onVideoSaveListener() {
            @Override
            public void onError(String str) {
                runOnUiThread(new Runnable() {
                    @SuppressLint({"WrongConstant"})
                    public void run() {
                        Toast.makeText(SavingActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                });
                setFinishOnTouchOutside(false);
                setRequestedOrientation(4);
                finish();
            }

            @Override
            public void onSaved(String filePath) {
                videoSaver = null;
                getWindow().clearFlags(16);
                setFinishOnTouchOutside(false);
                setRequestedOrientation(4);
                ToolsController object = ToolsController.getObject();
                object.apagarSelecao();
                object.setTipoFerramenta(0);
                ToolsController.init(MotionEditActivity.object);


                Intent intent2 = new Intent(SavingActivity.this, VideoPreviewActivity.class);
                intent2.putExtra("video_path", filePath);

                if (ApplicationClass.getApplication().isAdLoaded() && ApplicationClass.getApplication().needToShowAd()) {
                    ApplicationClass.getApplication().showInterstitialNewForward(SavingActivity.this, intent2, true);
                    if (MotionEditActivity.object != null) {
                        MotionEditActivity.object.finish();
                    }
                    if (AlbumListActivity.activity != null) {
                        AlbumListActivity.activity.finish();
                        return;
                    }
                } else {
                    startActivity(intent2);
                    finish();
                    if (MotionEditActivity.object != null) {
                        MotionEditActivity.object.finish();
                    }
                    if (AlbumListActivity.activity != null) {
                        AlbumListActivity.activity.finish();
                        return;
                    }
                }
            }

            @Override
            public void onSaving(int i) {

            }

            @Override
            public void onStartSave(int i) {

            }
        });
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.nome_logo, null);


        videoSaver.execute(new Object[]{file.getPath(), decodeResource});
    }

    class tamanhoIdealOnClickListener implements OnClickListener {
        public void onClick(View view) {
            seekResolucao.setProgress(((int) resolucaoOriginal) - SavingActivity.MIN_RESOLUTION_SAVE);
        }
    }


    class SaveCloseClick implements OnClickListener {
        public void onClick(View view) {
            finish();
        }
    }
}
