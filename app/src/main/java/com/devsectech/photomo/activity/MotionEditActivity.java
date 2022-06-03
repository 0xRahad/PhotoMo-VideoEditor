package com.devsectech.photomo.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.devsectech.photomo.ApplicationClass;
import com.devsectech.photomo.R;
import com.devsectech.photomo.callback.OnBitmapListCreated;
import com.devsectech.photomo.customView.CustomAnimationView;
import com.devsectech.photomo.customView.LupaImageView;
import com.devsectech.photomo.customView.ShareClass;
import com.devsectech.photomo.customView.ZoomImageView;
import com.devsectech.photomo.customView.beans.Ponto;
import com.devsectech.photomo.customView.beans.Projeto;
import com.devsectech.photomo.customView.controllersapp.AnimacaoController;
import com.devsectech.photomo.customView.controllersapp.AnimacaoController.AnimateListener;
import com.devsectech.photomo.customView.controllersapp.HistoryController;
import com.devsectech.photomo.customView.controllersapp.HistoryController.ObjetoHistoria;
import com.devsectech.photomo.customView.controllersapp.ToolsController;
import com.devsectech.photomo.customView.controllersapp.ToolsController.ToolsListener;
import com.devsectech.photomo.customView.controllersapp.Utils;
import com.devsectech.photomo.model.EffectData;
import com.devsectech.photomo.model.StickerData;
import com.devsectech.photomo.utils.BitmapHelper;
import com.devsectech.photomo.utils.DatabaseHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class MotionEditActivity extends BaseParentActivity {
    public static DisplayMetrics metrics;
    public static MotionEditActivity object;
    public static RelativeLayout transparentLayout;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public AnimacaoController animacaoController;
    public DatabaseHandler db = DatabaseHandler.getInstance(this);
    public HistoryController historyController;
    public boolean imageLoaded = false;
    public ZoomImageView imageView;
    public Bitmap imagemRepresentacao;
    public LinearLayout imgbtnPlayStop;
    public ImageView ibPlayPause;
    public LupaImageView lupaImageView;
    public float mScaleFactor = 1.0f;
    public Menu menuSecundario;
    public List<Bitmap> moBitmapList = new ArrayList();
    public ProgressDialog moDialog;
    public EffectData moEffectData = null;
    public LottieAnimationView moIvGif;
    public OnBitmapListCreated moOnBitmapListCreated;
    public RelativeLayout moRLEffects;
    public StickerData moStickerData = null;
    public CustomAnimationView moStickerView;
    public boolean mouseDragging = false;
    public Ponto pontoInicial;
    public List<Ponto> pontosSequencia = new CopyOnWriteArrayList();
    public Projeto projetoAtual;
    public boolean showDetalhes = false;
    public RelativeLayout toolBar;
    public ToolsController toolsController;
    public ListenerPermissao ultimoPedidoPermissao;
    public float xInitMouse;
    public float yInitMouse;
    ImageView btnNext;
    HorizontalScrollView horizontalScrollView;

    public void abrirNovoProjeto() {
        toolsController.stopPreview();
        imageView.restartZoom();
        ultimoPedidoPermissao = new C04688();
    }

    @SuppressLint({"WrongConstant"})
    public void abrirUltimoProjeto() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), 0);
        String str = "idUltimoProjeto";
        long j = sharedPreferences.getLong(str, -1);
        if (j != -1) {
            Projeto projeto = db.getProjeto(j);
            if (projeto != null) {
                loadProjeto(projeto);
                return;
            }
            sharedPreferences.edit().remove(str).commit();
        }
        Projeto ultimoProjeto = db.getUltimoProjeto();
        if (ultimoProjeto != null) {
            loadProjeto(ultimoProjeto);
        }
    }

    public void atualizarMascaraDataBase(Bitmap bitmap) {
        if (bitmap != null) {
            projetoAtual.setMascara(Bitmap.createScaledBitmap(bitmap, Math.round(projetoAtual.getProporcaoApresentacao() * ((float) bitmap.getWidth())), Math.round(projetoAtual.getProporcaoApresentacao() * ((float) bitmap.getHeight())), true));
            new C02407().execute(new Object[0]);
        }
    }

    public void carregarMenus(Menu menu) {
        if (menu != null) {
            MenuItem findItem = menu.findItem(R.id.action_save);
            findItem.setIcon(Utils.getDrawable(this, R.drawable.ic_done_black_24dp, -1));
            findItem.setVisible(projetoAtual != null);

            MenuItem findItem1 = menu.findItem(R.id.action_detalhes);
            findItem1.setIcon(Utils.getDrawable(this, R.drawable.detalhes_inativo, -1));

            MenuItem findItem2 = menu.findItem(R.id.action_undo);
            findItem2.setIcon(Utils.getDrawable(this, R.drawable.ic_rotate_left_black_24dp, (projetoAtual == null || !historyController.temDesfazer()) ? Utils.getColor(this, R.color.colorMenuDisabled) : -1));
            findItem2.setEnabled(projetoAtual != null && historyController.temDesfazer());
            int i = -1;
            if (projetoAtual == null || !historyController.temRefazer()) {
                i = Utils.getColor(this, R.color.colorMenuDisabled);
            }

            MenuItem findItem3 = menu.findItem(R.id.action_redo);
            findItem3.setIcon(Utils.getDrawable(this, R.drawable.ic_rotate_right_black_24dp, i));
            boolean z = true;
            if (projetoAtual == null || !historyController.temRefazer()) {
                z = false;
            }
            findItem3.setEnabled(z);
        }
    }

    private void loadProjeto(Projeto projeto) {
        restartDefinitions();
        if (projeto.getImagem() != null || projeto.reloadBitmapUri(this, db)) {
            Editor edit = getSharedPreferences(getString(R.string.preference_file_key), 0).edit();
            edit.putLong("idUltimoProjeto", projeto.getId());
            edit.commit();
            projetoAtual = projeto;
            imageLoaded = true;
            toolsController.setEnabled(true);
            if (projeto.getMascara() != null) {
                toolsController.setMascaraInicial(Bitmap.createScaledBitmap(projeto.getMascara(), Math.round((1.0f / projeto.getProporcaoApresentacao()) * ((float) projeto.getMascara().getWidth())), Math.round((1.0f / projeto.getProporcaoApresentacao()) * ((float) projeto.getMascara().getHeight())), true));
            }
            toolBar.setVisibility(View.VISIBLE);
            carregarMenus(menuSecundario);
            animacaoController = AnimacaoController.init(projetoAtual);
            animacaoController.setOnAnimateListener(new C04699());
            carregarImagemRepresentacao();
            return;
        }
        projeto.deleteProjeto(db);
    }

    public void reposicionarLupa(float f, float f2) {
        int width = lupaImageView.getWidth() / 2;
        if (f > ((float) ((imageView.getWidth() - lupaImageView.getWidth()) - width)) && f2 < imageView.getY() + ((float) lupaImageView.getHeight()) + ((float) width)) {
            LayoutParams layoutParams = (LayoutParams) lupaImageView.getLayoutParams();
            layoutParams.removeRule(21);
            layoutParams.addRule(20);
            lupaImageView.setLayoutParams(layoutParams);
        }
        if (f < ((float) (lupaImageView.getWidth() + width)) && f2 < imageView.getY() + ((float) lupaImageView.getHeight()) + ((float) width)) {
            LayoutParams layoutParams2 = (LayoutParams) lupaImageView.getLayoutParams();
            layoutParams2.removeRule(20);
            layoutParams2.addRule(21);
            lupaImageView.setLayoutParams(layoutParams2);
        }
    }

    private void restartDefinitions() {
        AnimacaoController animacaoController2 = animacaoController;
        if (animacaoController2 != null) {
            animacaoController2.stopAnimation(this);
            toolsController.stopPreview();
        }
        imageLoaded = false;
        toolsController.restartDefinitions();
        toolsController.setEnabled(false);
        imageView.restartZoom();
    }

    @SuppressLint({"WrongConstant"})
    public void carregarImagemRepresentacao() {
        if (imageLoaded) {
            imagemRepresentacao = animacaoController.getImagemRepresentacao(showDetalhes, imageView.getZoomScale());
            toolsController.pintar(imagemRepresentacao, imageView.getZoomScale());
            if (toolsController.getTipoFerramenta() == 3 || toolsController.getTipoFerramenta() == 6 || toolsController.getTipoFerramenta() == 2 || toolsController.getTipoFerramenta() == 1 || toolsController.getTipoFerramenta() == 5) {
                lupaImageView.setImageBitmap(imagemRepresentacao, imageView.getZoomScale());
            }
            imageView.setImageBitmap(imagemRepresentacao);
        }
    }

    public void getBitmapList(OnBitmapListCreated onBitmapListCreated) {
        moOnBitmapListCreated = onBitmapListCreated;
        moIvGif.setVisibility(View.VISIBLE);
        moStickerView.setVisibility(View.VISIBLE);
        moBitmapList = new ArrayList();
        moStickerView.pauseAnimation();
        moIvGif.pauseAnimation();
        new Handler().postDelayed(new Runnable() {
            public final void run() {
                new BitmapTask().execute(new Void[0]);
            }
        }, 1000);
    }

    @SuppressLint({"WrongConstant"})
    public void loadImagem(Uri uri) {
        imageLoaded = false;
        historyController.clear();
        Options options = new Options();
        options.inJustDecodeBounds = true;
        try {
            InputStream openInputStream = getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(openInputStream, null, options);
            openInputStream.close();
            int i = options.outWidth > options.outHeight ? options.outWidth : options.outHeight;
            if (i > SavingActivity.MAX_RESOLUTION_SAVE) {
                i = SavingActivity.MAX_RESOLUTION_SAVE;
            }
            options.inSampleSize = Utils.calculateInSampleSize(options, i, i);
            options.inJustDecodeBounds = false;
            InputStream openInputStream2 = getContentResolver().openInputStream(uri);
            Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream2, null, options);
            toolsController.setEffectAdapter(decodeStream);
            openInputStream2.close();
            StringBuilder sb = new StringBuilder();
            sb.append(getResources().getString(R.string.project_name));
//            sb.append(EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR);
            sb.append((int) (Math.random() * 1000000.0d));
            String sb2 = sb.toString();
            Projeto projeto = new Projeto(sb2, decodeStream.copy(Config.ARGB_8888, true), Utils.writeImageAndGetPathUri(this, decodeStream, sb2));
            projeto.setResolucaoSave(i);
            projeto.addProjeto(db);
            loadProjeto(projeto);
            // Toast.makeText(this, getResources().getText(R.string.load_image_fail), 1).show();
        } catch (Exception unused) {
            Toast.makeText(this, getResources().getText(R.string.load_image_fail), Toast.LENGTH_LONG);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (intent != null) {
            String str = "idProjeto";
            if (intent.hasExtra(str)) {
                historyController.clear();
                loadProjeto(db.getProjeto(intent.getLongExtra(str, -1)));
            } else if (i == 1 && i2 == -1) {
                historyController.clear();
                loadImagem(intent.getData());
            }
        }
        Projeto projeto = projetoAtual;
        if (projeto != null && db.getProjeto(projeto.getId()) == null) {
            historyController.clear();
            abrirUltimoProjeto();
        }
        super.onActivityResult(i, i2, intent);
    }

    public void onBackPressed() {
        if (GalleryListActivity.getGalleryActivity() != null) {
            GalleryListActivity.getGalleryActivity().finish();
        }
        AlbumListActivity albumImagesActivity = AlbumListActivity.activity;
        if (albumImagesActivity != null) {
            albumImagesActivity.finish();
        }
        Utils.BackButtonDialog(this, toolsController, historyController);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @SuppressLint({"WrongConstant"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_photo_edit);
        object = this;
        RelativeLayout mAdView = findViewById(R.id.adView);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        loadBannerAdsApp(mAdView);
        metrics = getResources().getDisplayMetrics();
        transparentLayout = (RelativeLayout) findViewById(R.id.transparentLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.menuBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar();
        Utils.getDrawable(this, R.drawable.nome_topo, -1);
        imageView = (ZoomImageView) findViewById(R.id.imageView);
        toolBar = (RelativeLayout) findViewById(R.id.toolBar);
        toolBar.setVisibility(View.INVISIBLE);
        lupaImageView = (LupaImageView) findViewById(R.id.imageZoom);
        lupaImageView.setVisibility(View.INVISIBLE);
        imageView.setLayerType(2, null);
        imgbtnPlayStop =  findViewById(R.id.btPlayPause);
        ibPlayPause =  findViewById(R.id.ibPlayPause);
        imgbtnPlayStop.setOnClickListener(new playPauseClick());
        historyController = HistoryController.getInstance();
        toolsController = ToolsController.init(this);
        toolsController.setToolsListener(new toolsListener());
        imageView.setScaleListener(new simpleOnScaleGestureListener());
        imageView.setOnTouchListener(new onTouchListener());
        moIvGif = (LottieAnimationView) findViewById(R.id.gifView);
        moStickerView = (CustomAnimationView) findViewById(R.id.customView);
        moStickerView.init1(this);
        moRLEffects = (RelativeLayout) findViewById(R.id.rl_effects_container);
        if (toolsController.getTipoFerramenta() == 7) {
            imageView.setZoomActivated(true);
        } else {
            imageView.setZoomActivated(false);
        }

        if (bundle == null) {
            Intent intent = getIntent();
            intent.getAction();
            intent.getType();
            Log.e("savedInstanceState", "onCreate: in loop");
            historyController.clear();
            loadImagem(intent.getData());
            return;
        }
        ultimoPedidoPermissao = new C04676();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menuSecundario = menu;
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        carregarMenus(menu);
        return true;
    }

    public boolean onMenuOpened(int i, Menu menu) {
        return super.onMenuOpened(i, menu);
    }

    @SuppressLint({"WrongConstant"})
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }




    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_detalhes:
                toolsController.stopPreview();
                showDetalhes = !showDetalhes;
                menuItem.setChecked(showDetalhes);
                if (showDetalhes) {
                    menuItem.setIcon(Utils.getDrawable(this, R.drawable.ic_detalhes_ativo, -1));
                } else {
                    menuItem.setIcon(Utils.getDrawable(this, R.drawable.detalhes_inativo, -1));
                }
                carregarImagemRepresentacao();
                return true;
            case R.id.action_redo:
                toolsController.stopPreview();
                for (ObjetoHistoria objetoHistoria : historyController.refazer()) {
                    if (objetoHistoria.mo6833a()) {
                        toolsController.addMask(objetoHistoria.getMask());
                        atualizarMascaraDataBase(objetoHistoria.getMask());
                    } else if (!objetoHistoria.mo6834b()) {
                        Ponto ponto = objetoHistoria.getPonto();
                        projetoAtual.deletePonto(db, ponto);
                        animacaoController.deletePonto(ponto);
                    } else {
                        Ponto ponto2 = objetoHistoria.getPonto();
                        projetoAtual.addPonto(db, ponto2);
                        animacaoController.addPonto(ponto2);
                    }
                }
                carregarImagemRepresentacao();
                carregarMenus(menuSecundario);
                return true;
            case R.id.action_save:
                if (ToolsController.controller.getCurrentStickerPosition() == -1 && ToolsController.controller.getSelecetdEffect().getEffectId() == -1) {
                    ShareClass.foBitmapList = new ArrayList();
                    getWindow().setFlags(16, 16);
                    toolsController.apagarSelecao();
                    toolsController.stopPreview();
                    imageView.restartZoom();
                    toolsController.setTipoFerramenta(0);
                    Intent intent = new Intent(MotionEditActivity.this, SavingActivity.class);
                    intent.putExtra(SavingActivity.INTENT_PROJETO, projetoAtual);

                    if (ApplicationClass.getApplication().isAdLoaded() && ApplicationClass.getApplication().needToShowAd()) {
                        ApplicationClass.getApplication().showInterstitialNewForward(MotionEditActivity.this, intent, false);
                    } else {
                        startActivity(intent);
                    }

                } else {
                    moStickerView.hideFrame(true);
                    moDialog = new ProgressDialog(this);
                    moDialog.setMessage("Loading...");
                    moDialog.setCancelable(false);
                    moDialog.show();
                    getBitmapList(new OnBitmapListCreated() {
                        public final void onBitmapRecived(List list) {
                            ShareClass.foBitmapList = new ArrayList();
                            ShareClass.foBitmapList = list;
                            getWindow().setFlags(16, 16);
                            toolsController.apagarSelecao();
                            toolsController.stopPreview();
                            imageView.restartZoom();
                            toolsController.setTipoFerramenta(0);
                            Intent intent = new Intent(MotionEditActivity.this, SavingActivity.class);
                            intent.putExtra(SavingActivity.INTENT_PROJETO, projetoAtual);
                            startActivity(intent);
                            moDialog.dismiss();
                        }
                    });
                }
                return true;
            case R.id.action_undo:
                toolsController.stopPreview();
                List<ObjetoHistoria> desfazer = historyController.desfazer();
                CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
                for (ObjetoHistoria objetoHistoria2 : desfazer) {
                    if (!objetoHistoria2.mo6833a()) {
                        Ponto ponto3 = objetoHistoria2.getPonto();
                        if (objetoHistoria2.mo6834b()) {
                            copyOnWriteArrayList.add(ponto3);
                        } else if (!animacaoController.existePonto(ponto3)) {
                            projetoAtual.addPonto(db, ponto3);
                            animacaoController.addPonto(ponto3);
                        } else {
                            projetoAtual.addPonto(db, ponto3);
                            animacaoController.addPonto(ponto3);
                        }
                    } else {
                        toolsController.addMask(objetoHistoria2.getMask());
                        atualizarMascaraDataBase(objetoHistoria2.getMask());
                    }
                }
                if (!copyOnWriteArrayList.isEmpty()) {
                    projetoAtual.deletePontos(db, copyOnWriteArrayList);
                    animacaoController.deletePontos(copyOnWriteArrayList);
                }
                carregarImagemRepresentacao();
                carregarMenus(menuSecundario);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void onPause() {
        super.onPause();
        toolsController.stopPreview();
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 2) {
            int length = iArr.length;
            boolean z = false;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    z = true;
                    break;
                } else if (iArr[i2] != 0) {
                    break;
                } else {
                    i2++;
                }
            }
            if (z) {
                ultimoPedidoPermissao.onPermissionGranted();
            }
        }
    }

    public void onResume() {
        if (transparentLayout.getVisibility() == View.VISIBLE) {
            transparentLayout.setVisibility(View.GONE);
        }
        getWindow().clearFlags(16);

        if (ToolsController.controller != null) {
            if (ToolsController.controller.getCurrentStickerPosition() != -1) {
                moStickerView.hideFrame(false);
            }

            if (ToolsController.controller.getSelecetdEffect() != null ) {
                if (ToolsController.controller.getSelecetdEffect().getEffectId() != -1) {
                    moIvGif.setVisibility(View.VISIBLE);
                    moIvGif.playAnimation();
                }
            }

        }


        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    public void resetSelection() {
        toolsController = ToolsController.init(this);
        toolsController.setEnabled(true);
        toolsController.apagarSelecao();
        toolsController.setTipoFerramenta(0);
    }

    public interface ListenerPermissao {
        void onPermissionGranted();
    }

    @SuppressLint({"StaticFieldLeak"})
    public class BitmapTask extends AsyncTask<Void, Void, Boolean> {

        public void onPreExecute() {
            super.onPreExecute();
        }

        public Boolean doInBackground(Void... voidArr) {
            int i = 0;
            while (i < 60) {
                try {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        public final void run() {
                            int i2 = finalI + 1;
                            moIvGif.setFrame(i2);
                            moStickerView.setFrame(i2);
                            moBitmapList.add(BitmapHelper.getBitmap2(moRLEffects));
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

    public class simpleOnScaleGestureListener extends SimpleOnScaleGestureListener {
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            carregarImagemRepresentacao();
            return super.onScale(scaleGestureDetector);
        }
    }

    public class onTouchListener implements OnTouchListener {
        @SuppressLint({"WrongConstant"})
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (imageLoaded) {
                Rect bounds = imageView.getDrawable().getBounds();
                int width = (imageView.getWidth() - bounds.right) / 2;
                int height = (imageView.getHeight() - bounds.bottom) / 2;
                if (!toolsController.isPlayingPreview()) {
                    int actionIndex = motionEvent.getActionIndex();
                    float[] fArr = {motionEvent.getX(actionIndex), motionEvent.getY(actionIndex)};
                    Matrix matrix = new Matrix();
                    imageView.getImageMatrix().invert(matrix);
                    matrix.postTranslate((float) imageView.getScrollX(), (float) imageView.getScrollY());
                    matrix.mapPoints(fArr);
                    float f = fArr[0];
                    float f2 = fArr[1];
                    Ponto ponto = new Ponto(f, f2, true);
                    toolsController.setSubToolTamanhoPincelVisibility(false);
                    toolsController.setSubToolVelocidadeVisibility(false);
                    Paint paint = new Paint(1);
                    paint.setStyle(Style.FILL);
                    paint.setFilterBitmap(true);
                    paint.setColor(-65536);
                    if (motionEvent.getAction() == 0) {
                        mouseDragging = true;
                        xInitMouse = f;
                        yInitMouse = f2;
                        MotionEditActivity editActivity = MotionEditActivity.this;
                        editActivity.pontoInicial = new Ponto(editActivity.xInitMouse, yInitMouse, true);
                        switch (toolsController.getTipoFerramenta()) {
                            case 1:
                                MotionEditActivity editActivity2 = MotionEditActivity.this;
                                editActivity2.pontoInicial = new Ponto(editActivity2.xInitMouse, yInitMouse, false);
                                if (!animacaoController.existePonto(pontoInicial)) {
                                    projetoAtual.addPonto(db, pontoInicial);
                                    animacaoController.addPonto(pontoInicial);
                                    break;
                                }
                                break;
                            case 2:
                                MotionEditActivity editActivity3 = MotionEditActivity.this;
                                editActivity3.reposicionarLupa(editActivity3.xInitMouse, yInitMouse);
                                carregarImagemRepresentacao();
                                lupaImageView.setPosicaoLupa(f, f2);
                                lupaImageView.setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                reposicionarLupa(f, f2);
                                toolsController.setMascarando(true);
                                toolsController.addMask(xInitMouse, yInitMouse, imageView.getZoomScale());
                                carregarImagemRepresentacao();
                                lupaImageView.setPosicaoLupa(f, f2);
                                lupaImageView.setVisibility(View.VISIBLE);
                                break;
                            case 4:
                                toolsController.apagarSelecao();
                                break;
                            case 5:
                                MotionEditActivity editActivity4 = MotionEditActivity.this;
                                editActivity4.pontoInicial = new Ponto(editActivity4.xInitMouse, yInitMouse, false);
                                if (!animacaoController.existePonto(pontoInicial)) {
                                    projetoAtual.addPonto(db, pontoInicial);
                                    animacaoController.addPonto(pontoInicial);
                                    pontosSequencia = new CopyOnWriteArrayList();
                                    pontosSequencia.add(pontoInicial);
                                    break;
                                }
                                break;
                            case 6:
                                reposicionarLupa(f, f2);
                                toolsController.setMascarando(true);
                                toolsController.deleteMask(xInitMouse, yInitMouse, imageView.getZoomScale());
                                carregarImagemRepresentacao();
                                lupaImageView.setPosicaoLupa(f, f2);
                                lupaImageView.setVisibility(View.VISIBLE);
                                break;
                        }
                        carregarImagemRepresentacao();
                        return true;
                    } else if (motionEvent.getAction() == 2) {
                        if (mouseDragging) {
                            switch (toolsController.getTipoFerramenta()) {
                                case 1:
                                    pontoInicial.setDestino(f, f2);
                                    break;
                                case 2:
                                    reposicionarLupa(motionEvent.getX(), motionEvent.getY());
                                    carregarImagemRepresentacao();
                                    lupaImageView.setPosicaoLupa(f, f2);
                                    break;
                                case 3:
                                    reposicionarLupa(motionEvent.getX(), motionEvent.getY());
                                    toolsController.addMask(f, f2, imageView.getZoomScale());
                                    carregarImagemRepresentacao();
                                    lupaImageView.setPosicaoLupa(f, f2);
                                    break;
                                case 4:
                                    toolsController.setSelecionando(pontoInicial, ponto);
                                    if (animacaoController.temPontoSelecionado()) {
                                        toolsController.setDeleteToolVisibility(true);
                                        break;
                                    } else {
                                        toolsController.setDeleteToolVisibility(false);
                                        break;
                                    }
                                case 5:
                                    pontoInicial.setDestino(f, f2);
                                    if (pontoInicial.distanciaPara(ponto) >= ((double) (((float) toolsController.getTamMovSequencia()) / imageView.getZoomScale()))) {
                                        projetoAtual.updatePonto(db, pontoInicial);
                                        pontoInicial = new Ponto(f, f2, false);
                                        projetoAtual.addPonto(db, pontoInicial);
                                        animacaoController.addPonto(pontoInicial);
                                        pontosSequencia.add(pontoInicial);
                                        break;
                                    }
                                    break;
                                case 6:
                                    reposicionarLupa(motionEvent.getX(), motionEvent.getY());
                                    toolsController.deleteMask(f, f2, imageView.getZoomScale());
                                    carregarImagemRepresentacao();
                                    lupaImageView.setPosicaoLupa(f, f2);
                                    break;
                            }
                            carregarImagemRepresentacao();
                        }
                        return true;
                    } else if (motionEvent.getAction() == 1) {
                        mouseDragging = false;
                        lupaImageView.setVisibility(View.INVISIBLE);
                        switch (toolsController.getTipoFerramenta()) {
                            case 1:
                                pontoInicial.setDestino(f, f2);
                                projetoAtual.updatePonto(db, pontoInicial);
                                historyController.addHistoria(pontoInicial, true);
                                break;
                            case 2:
                                projetoAtual.addPonto(db, ponto);
                                animacaoController.addPonto(ponto);
                                historyController.addHistoria(ponto, true);
                                break;
                            case 3:
                                toolsController.setMascarando(false);
                                atualizarMascaraDataBase(ToolsController.getMask());
                                Paint paint2 = new Paint(1);
                                paint2.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
                                paint2.setFilterBitmap(true);
                                paint2.setStyle(Style.FILL);
                                break;
                            case 4:
                                toolsController.setSelecionando(pontoInicial, ponto);
                                toolsController.setSelecionando(false);
                                break;
                            case 5:
                                pontoInicial.setDestino(f, f2);
                                projetoAtual.updatePonto(db, pontoInicial);
                                historyController.addHistoria(pontosSequencia, true);
                                break;
                            case 6:
                                toolsController.setMascarando(false);
                                atualizarMascaraDataBase(ToolsController.getMask());
                                break;
                        }
                        carregarImagemRepresentacao();
                        MotionEditActivity editActivity5 = MotionEditActivity.this;
                        editActivity5.carregarMenus(editActivity5.menuSecundario);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public class C02407 extends AsyncTask {
        public C02407() {
        }

        public Object doInBackground(Object[] objArr) {
            projetoAtual.updateProjeto(db);
            return null;
        }
    }

    public class toolsListener implements ToolsListener {
        @SuppressLint({"WrongConstant"})
        public void onChangeRaioMascara(int i, Bitmap bitmap) {
            if (bitmap != null) {
                Bitmap createBitmap = Bitmap.createBitmap(imagemRepresentacao.getWidth(), imagemRepresentacao.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                float width = (((float) createBitmap.getWidth()) / 2.0f) - (((float) bitmap.getWidth()) / 2.0f);
                float height = (((float) createBitmap.getHeight()) / 2.0f) - (((float) bitmap.getHeight()) / 2.0f);
                Paint paint = new Paint(1);
                paint.setFilterBitmap(true);
                paint.setAlpha(ToolsController.getAlphaMask());
                canvas.drawBitmap(bitmap, width, height, paint);
            }
        }

        @SuppressLint({"WrongConstant"})
        public void onChangeTamanhoSetaMovSequencia(int i, Bitmap bitmap) {
            if (bitmap != null) {
                Bitmap createBitmap = Bitmap.createBitmap(imagemRepresentacao.getWidth(), 50, Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                canvas.drawColor(Color.argb(ToolsController.getAlphaMask(), 0, 0, 0));
                canvas.drawBitmap(bitmap, (float) ((createBitmap.getWidth() / 2) - (bitmap.getWidth() / 2)), (float) (createBitmap.getHeight() / 2), null);
                return;
            }
            imageView.setZoomActivated(false);
            carregarImagemRepresentacao();
        }

        @SuppressLint({"WrongConstant"})
        public void onPlayPreview() {
            imageView.restartZoom();
            if (toolsController.getTipoFerramenta() == 7) {
                imageView.setZoomActivated(false);
            }
            projetoAtual.refreshTempoResolucao(db);
            toolsController.setTempoPreview(projetoAtual.getTempoSave());
            animacaoController.setTempoAnimacao(projetoAtual.getTempoSave());
            animacaoController.startAnimation();
            ibPlayPause.setImageResource(R.drawable.ic_menu_stop);
        }

        public void onPressApagarMascara() {
            imageView.setZoomActivated(false);
            carregarImagemRepresentacao();
        }

        public void onPressDelete() {
            List listaPontosSelecionados = animacaoController.getListaPontosSelecionados();
            if (listaPontosSelecionados != null && !listaPontosSelecionados.isEmpty()) {
                projetoAtual.deletePontos(db, listaPontosSelecionados);
                animacaoController.deletePontosSelecionados();
                historyController.addHistoria(listaPontosSelecionados, false);
            }
            toolsController.setDeleteToolVisibility(false);
            imageView.setZoomActivated(false);
            carregarImagemRepresentacao();
            MotionEditActivity editActivity = MotionEditActivity.this;
            editActivity.carregarMenus(editActivity.menuSecundario);
        }

        public void onPressEsbilizar() {
            imageView.setZoomActivated(false);
            carregarImagemRepresentacao();
        }

        public void onPressMascara() {
            imageView.setZoomActivated(false);
            carregarImagemRepresentacao();
        }

        public void onPressSelecao() {
            imageView.setZoomActivated(false);
            carregarImagemRepresentacao();
        }

        public void onPressSetaMovSequencia() {
            imageView.setZoomActivated(false);
            carregarImagemRepresentacao();
        }

        public void onPressSetaMovimento() {
            imageView.setZoomActivated(false);
            carregarImagemRepresentacao();
        }

        public void onPressZoom() {
            imageView.setZoomActivated(true);
            carregarImagemRepresentacao();
        }

        public void onStopPreview() {
            if (toolsController.getTipoFerramenta() == 7) {
                imageView.setZoomActivated(true);
            }
            if (animacaoController != null) {
                animacaoController.stopAnimation(MotionEditActivity.this);
            }
            ibPlayPause.setImageResource(R.drawable.ic_menu_play);
            carregarImagemRepresentacao();
        }

        public void onTempoAlterado(int i) {
            animacaoController.setTempoAnimacao(i);
            projetoAtual.setTempoSave(i);
            projetoAtual.updateProjeto(db);
        }

        public void onTempoChanging(int i) {
            animacaoController.setTempoAnimacao(i);
        }
    }

    public class C04676 implements ListenerPermissao {
        public C04676() {
        }

        public void onPermissionGranted() {
            Log.i("INFO", "LOADING ULTIMO PROJETO ....");
            abrirUltimoProjeto();
        }
    }

    public class C04688 implements ListenerPermissao {
        public C04688() {
        }

        public void onPermissionGranted() {
            Intent intent = new Intent("android.intent.action.PICK", Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                MotionEditActivity editActivity = MotionEditActivity.this;
                editActivity.startActivityForResult(Intent.createChooser(intent, editActivity.getResources().getString(R.string.txt_select_an_image)), 1);
            }
        }
    }

    public class C04699 implements AnimateListener {
        public C04699() {
        }

        public void onAnimate(Bitmap bitmap) {
            if (imageLoaded && toolsController.isPlayingPreview()) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public class playPauseClick implements OnClickListener {
        public playPauseClick() {
        }

        public void onClick(View view) {
            toolsController.apagarSelecao();
            if (toolsController.isPlayingPreview()) {
                toolsController.stopPreview();
                CustomAnimationView customAnimationView = moStickerView;
                if (customAnimationView != null) {
                    customAnimationView.setVisibility(View.GONE);
                    return;
                }
                return;
            }
            toolsController.playPreview();
            if (toolsController.getSelecetdEffect().getEffectId() != -1) {
                toolsController.playPreview();
                moIvGif.setSpeed(1.0f);
                MotionEditActivity editActivity = MotionEditActivity.this;
                editActivity.moIvGif.setAnimation(editActivity.moEffectData.getEffectName());
                if (!moEffectData.getEffectGIFPath().isEmpty()) {
                    MotionEditActivity editActivity2 = MotionEditActivity.this;
                    editActivity2.moIvGif.setImageAssetsFolder(editActivity2.moEffectData.getEffectGIFPath());
                }
                moIvGif.playAnimation();
                moIvGif.setVisibility(View.VISIBLE);
            }
            if (toolsController.getCurrentStickerPosition() != -1) {
                moStickerView.setVisibility(View.VISIBLE);
            } else {
                moStickerView.setVisibility(View.GONE);
            }
        }
    }


    public class playNextClick implements OnClickListener {
        public playNextClick() {
        }

        public void onClick(View view) {
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }
    }
}
