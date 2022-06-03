package com.devsectech.photomo.customView.controllersapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsectech.photomo.R;
import com.devsectech.photomo.activity.MotionEditActivity;
import com.devsectech.photomo.callback.OnClickListner;
import com.devsectech.photomo.callback.OnStickerClickListner;
import com.devsectech.photomo.customView.beans.Ponto;
import com.devsectech.photomo.customView.beans.TrianguloBitmap;
import com.devsectech.photomo.customView.controllersapp.AnimacaoController.IteradorDePonto;
import com.devsectech.photomo.model.EffectData;
import com.devsectech.photomo.model.StickerData;
import com.devsectech.photomo.adapter.EffectAdapter;
import com.devsectech.photomo.adapter.StickerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ToolsController implements OnClickListener {
    public static final int INIT_TAM_MOV_SEQUENCIA = Math.round(64.0f);
    public static final int MAX_TAMANHO_MOV_SEQUENCIA = Math.round(240.00002f);
    public static final int MIN_TAMANHO_MOV_SEQUENCIA = Math.round(24.0f);
    public static final int MIN_TEMPO_PREVIEW = 2000;
    public static float STROKE_INIT = 4.0f;
    public static ToolsController controller;
    public Activity activity;
    public int alphaMask = 150;
    public ImageView btApagarMask;
    public FloatingActionButton btDelete;
    public ImageView btEffect;
    public ImageView btEstabilizacao;
    public ImageView btMask;
    public ImageView btMovSequencia;
    public ImageView btMovimento;
    public ImageView btSelect;
    public ImageView btSticker;
    public ImageView btZoom;
    public ImageView detalhesTopo;
    public boolean enabled = true;
    public int ferramentaAtual = -1;
    public int ferramentaAtualT = -1;
    public boolean isApagandoMascara = false;
    public boolean isMascarando = false;
    public boolean isSelecionando = false;
    public MaskController maskController;
    public EffectAdapter moEffectAdapter;
    public EffectData moEffectData = null;
    public List<EffectData> moEffectlist = new ArrayList();
    public RecyclerView moRcvEffects;
    public RelativeLayout moRlEffects;
    public StickerAdapter moStickerAdapter;
    public StickerData moStickerData = null;
    public List<StickerData> moStickerList = new ArrayList();
    public Paint paintMascarando;
    public Paint paintRepresentacaoMascara;
    public Paint paintSelect;
    public boolean playingPreview = false;
    public int raioMask = 20;
    public SeekBar seekTamanhoPincel;
    public SeekBar seekVelocidadePreview;
    public Ponto selecaoFinal;
    public Ponto selecaoInicial;
    public boolean showDetalhes = false;
    public int stepSize = 2000;
    public RelativeLayout subToolTamanho;
    public RelativeLayout subToolVelocidade;
    public int tamMovSequencia = INIT_TAM_MOV_SEQUENCIA;
    public int tempoPreview = 10000;
    public int tipoFerramenta = 0;
    public ToolsListener toolsListener;
    public float xAtual;
    public float yAtual;

    public static int getAlphaMask() {
        ToolsController toolsController = controller;
        if (toolsController == null) {
            return 150;
        }
        return toolsController.alphaMask;
    }

    public void setAlphaMask(int i) {
        this.alphaMask = i;
    }

    public static int getColorMask() {
        ToolsController toolsController = controller;
        if (toolsController == null) {
            return -65536;
        }
        return toolsController.maskController.getColor();
    }

    public static Bitmap getMask() {
        ToolsController toolsController = controller;
        if (toolsController != null) {
            return toolsController.maskController.mask;
        }
        return null;
    }

    public static ToolsController getObject() {
        return controller;
    }

    public static int getRaioMask() {
        return controller.raioMask;
    }

    public static int getTempoPreview() {
        ToolsController toolsController = controller;
        if (toolsController != null) {
            return toolsController.tempoPreview;
        }
        return 10000;
    }

    public void setTempoPreview(int i) {
        this.tempoPreview = i;
        this.seekVelocidadePreview.setProgress(1);
        this.seekVelocidadePreview.setProgress(2);
        this.seekVelocidadePreview.setProgress(10000 - i);
    }

    public static ToolsController init(Activity activity2) {
        if (controller == null) {
            controller = new ToolsController();
        }
        ToolsController toolsController = controller;
        toolsController.activity = activity2;
        toolsController.restartDefinitions();
        controller.resetTextView(R.id.txMovimento);
        controller.resetTextView(R.id.txSequencia);
        controller.resetTextView(R.id.txEstabilizar);
        controller.resetTextView(R.id.txMask);
        controller.resetTextView(R.id.txSelect);
        controller.resetTextView(R.id.txApagar);
        controller.resetTextView(R.id.txEffect);
        controller.resetTextView(R.id.txSticker);
        if (controller.tipoFerramenta != 0) {
            Log.e("controller", "init: gyu");
            ToolsController toolsController2 = controller;
            toolsController2.setFerramentaAtual(toolsController2.ferramentaAtual, toolsController2.ferramentaAtualT);
        }
        return controller;
    }

    public Bitmap getRaioRepresentacao() {
        Bitmap createBitmap = Bitmap.createBitmap(Math.round((STROKE_INIT * 2.0f) + ((float) (getRaioMask() * 2))), Math.round((STROKE_INIT * 2.0f) + ((float) (getRaioMask() * 2))), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.paintMascarando.setStrokeWidth(STROKE_INIT);
        if (this.tipoFerramenta == 3) {
            this.paintRepresentacaoMascara.setColor(-65536);
        } else {
            this.paintRepresentacaoMascara.setColor(-16711936);
        }
        canvas.drawCircle((float) Math.round(((float) createBitmap.getWidth()) / 2.0f), (float) Math.round(((float) createBitmap.getHeight()) / 2.0f), (float) getRaioMask(), this.paintRepresentacaoMascara);
        canvas.drawCircle((float) Math.round(((float) createBitmap.getWidth()) / 2.0f), (float) Math.round(((float) createBitmap.getHeight()) / 2.0f), (float) getRaioMask(), this.paintMascarando);
        return createBitmap;
    }

    public Bitmap getSetaRepresentacao() {
        Bitmap createBitmap = Bitmap.createBitmap(this.tamMovSequencia + 10, 10, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Ponto ponto = new Ponto(5.0f, 5.0f, (float) (this.tamMovSequencia + 5), 5.0f);
        ponto.desenharPonto(canvas, 255, 1.0f);
        ponto.desenharSeta(canvas, 255, 1.0f);
        return createBitmap;
    }

    public void addMask(Bitmap bitmap) {
        this.maskController.addMask(bitmap);
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    public void apagarSelecao() {
        AnimacaoController.getInstance().iterarPontos(new C04711());
        this.btDelete.setVisibility(View.INVISIBLE);
    }

    public void deleteMask(Bitmap bitmap) {
        this.maskController.deleteMask(bitmap);
    }

    public int getCurrentStickerPosition() {
        StickerAdapter stickerAdapter = this.moStickerAdapter;
        if (stickerAdapter != null) {
            return stickerAdapter.getSelectedEffect().getStickerId();
        }
        return -1;
    }

    public EffectData getSelecetdEffect() {
        return this.moEffectAdapter.getSelectedEffect();
    }

    public int getTamMovSequencia() {
        return this.tamMovSequencia;
    }

    public int getTipoFerramenta() {
        return this.tipoFerramenta;
    }

    public void setTipoFerramenta(int i) {
        this.tipoFerramenta = i;
    }

    public boolean isApagandoMascara() {
        return this.isApagandoMascara;
    }

    public boolean isPlayingPreview() {
        return this.playingPreview;
    }

    public void lambda$setEffectAdapter$0$ToolsController(EffectData effectData, int i) {
        if (effectData.getEffectId() != -1) {
            MotionEditActivity.object.moIvGif.setSpeed(1.0f);
            MotionEditActivity.object.moIvGif.setAnimation(effectData.getEffectName());
            if (!effectData.getEffectGIFPath().isEmpty()) {
                MotionEditActivity.object.moIvGif.setImageAssetsFolder(effectData.getEffectGIFPath());
            }
            MotionEditActivity.object.moIvGif.playAnimation();
            int height = MotionEditActivity.object.imageView.getHeight();
            int width = MotionEditActivity.object.imageView.getWidth();
            int intrinsicHeight = MotionEditActivity.object.imageView.getDrawable().getIntrinsicHeight();
            int intrinsicWidth = MotionEditActivity.object.imageView.getDrawable().getIntrinsicWidth();
            int i2 = height * intrinsicWidth;
            int i3 = width * intrinsicHeight;
            if (i2 <= i3) {
                width = i2 / intrinsicHeight;
            } else {
                height = i3 / intrinsicWidth;
            }
            LayoutParams layoutParams = new LayoutParams(width, height);
            layoutParams.addRule(13, -1);
            MotionEditActivity.object.moRLEffects.setLayoutParams(layoutParams);
            MotionEditActivity.object.moIvGif.setScaleType(ScaleType.CENTER_CROP);
            MotionEditActivity.object.moIvGif.setVisibility(View.VISIBLE);
            MotionEditActivity.object.moEffectData = effectData;
            this.moEffectData = effectData;
            playPreview();
            return;
        }
        stopPreview();
    }

    public void lambda$setEffectAdapter$1$ToolsController(StickerData stickerData, int i) {
        if (stickerData.getStickerId() != -1) {
            MotionEditActivity.object.moStickerView.hideFrame(false);
            MotionEditActivity.object.moStickerView.setVisibility(View.VISIBLE);
            MotionEditActivity.object.moStickerView.setAnimation(stickerData.getStickerName(), stickerData.getStickerFolderNameFPath());
            MotionEditActivity.object.moStickerView.playAnimation();
            int height = MotionEditActivity.object.imageView.getHeight();
            int width = MotionEditActivity.object.imageView.getWidth();
            int intrinsicHeight = MotionEditActivity.object.imageView.getDrawable().getIntrinsicHeight();
            int intrinsicWidth = MotionEditActivity.object.imageView.getDrawable().getIntrinsicWidth();
            int i2 = height * intrinsicWidth;
            int i3 = width * intrinsicHeight;
            if (i2 <= i3) {
                width = i2 / intrinsicHeight;
            } else {
                height = i3 / intrinsicWidth;
            }
            LayoutParams layoutParams = new LayoutParams(width, height);
            layoutParams.addRule(13, -1);
            MotionEditActivity.object.moRLEffects.setLayoutParams(layoutParams);
            MotionEditActivity.object.moStickerView.setScaleType();
            MotionEditActivity.object.moStickerView.setVisibility(View.VISIBLE);
            MotionEditActivity.object.moStickerData = stickerData;
            this.moStickerData = stickerData;
            playPreview();
            return;
        }
        MotionEditActivity.object.moStickerView.setVisibility(View.GONE);
        stopPreview();
    }

    public void lambda$setSelecionando$3$ToolsController(Ponto ponto, Ponto ponto2) {
        if (ponto2.naCircunferencia(ponto, 24.0d)) {
            ponto2.setSelecionado(true);
            this.btDelete.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint({"WrongConstant", "RestrictedApi"})
    public void onClick(View view) {
        this.subToolTamanho.setVisibility(View.INVISIBLE);
        this.subToolVelocidade.setVisibility(View.INVISIBLE);
        this.btDelete.setVisibility(View.INVISIBLE);
        this.isApagandoMascara = false;
        switch (view.getId()) {
            case R.id.btApagarMascara:
                apagarSelecao();
                setFerramentaAtual(view.getId(), R.id.txApagar);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.isApagandoMascara = true;
                this.tipoFerramenta = 6;
                this.moRlEffects.setVisibility(View.GONE);
                this.subToolTamanho.setVisibility(View.VISIBLE);
                this.seekTamanhoPincel.setMax(97);
                this.seekTamanhoPincel.setProgress(this.raioMask - 3);
                this.toolsListener.onPressApagarMascara();
                this.toolsListener.onChangeRaioMascara(getRaioMask(), getRaioRepresentacao());
                return;
            case R.id.btDelete:
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.toolsListener.onPressDelete();
                return;
            case R.id.btEffect:
                apagarSelecao();
                setFerramentaAtual(view.getId(), R.id.txEffect);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.isApagandoMascara = false;
                this.tipoFerramenta = 10;
                this.subToolTamanho.setVisibility(View.GONE);
                this.moRlEffects.setVisibility(View.VISIBLE);
                this.moRcvEffects.setAdapter(this.moEffectAdapter);
                EffectAdapter effectAdapter = this.moEffectAdapter;
                if (!(effectAdapter == null || effectAdapter.getSelectedEffect().getEffectId() == -1)) {
                    MotionEditActivity.object.moIvGif.setVisibility(View.VISIBLE);
                    MotionEditActivity.object.moIvGif.playAnimation();
                }
                return;
            case R.id.btEstabilizar:
                apagarSelecao();
                setFerramentaAtual(view.getId(), R.id.txEstabilizar);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.moRlEffects.setVisibility(View.GONE);
                this.tipoFerramenta = 2;
                this.toolsListener.onPressEsbilizar();
                return;
            case R.id.btMask:
                apagarSelecao();
                setFerramentaAtual(view.getId(), R.id.txMask);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.tipoFerramenta = 3;
                this.moRlEffects.setVisibility(View.GONE);
                this.subToolTamanho.setVisibility(View.VISIBLE);
                this.seekTamanhoPincel.setMax(97);
                this.seekTamanhoPincel.setProgress(this.raioMask - 3);
                this.toolsListener.onPressMascara();
                this.toolsListener.onChangeRaioMascara(getRaioMask(), getRaioRepresentacao());
                return;
            case R.id.btMovSequence:
                apagarSelecao();
                setFerramentaAtual(view.getId(), R.id.txSequencia);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.tipoFerramenta = 5;
                this.moRlEffects.setVisibility(View.GONE);
                this.subToolTamanho.setVisibility(View.VISIBLE);
                this.seekTamanhoPincel.setMax(MAX_TAMANHO_MOV_SEQUENCIA - MIN_TAMANHO_MOV_SEQUENCIA);
                this.seekTamanhoPincel.setProgress(this.tamMovSequencia - MIN_TAMANHO_MOV_SEQUENCIA);
                this.toolsListener.onPressSetaMovSequencia();
                this.toolsListener.onChangeTamanhoSetaMovSequencia(this.tamMovSequencia, getSetaRepresentacao());
                return;
            case R.id.btMovimento:
                apagarSelecao();
                setFerramentaAtual(view.getId(), R.id.txMovimento);
                this.moRlEffects.setVisibility(View.GONE);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.tipoFerramenta = 1;
                this.toolsListener.onPressSetaMovimento();
                return;
            case R.id.btSelect:
                apagarSelecao();
                setFerramentaAtual(view.getId(), R.id.txSelect);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.moRlEffects.setVisibility(View.GONE);
                this.tipoFerramenta = 4;
                this.toolsListener.onPressSelecao();
                return;
            case R.id.btSticker:
                apagarSelecao();
                setFerramentaAtual(view.getId(), R.id.txSticker);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(false);
                this.isApagandoMascara = false;
                this.tipoFerramenta = 11;
                this.subToolTamanho.setVisibility(View.GONE);
                this.moRlEffects.setVisibility(View.VISIBLE);
                this.moRcvEffects.setAdapter(this.moStickerAdapter);
                return;
            case R.id.btZoom:
                setFerramentaAtual(view.getId(), R.id.txZoom);
                stopPreview();
                MotionEditActivity.object.moStickerView.hideFrame(true);
                this.moRlEffects.setVisibility(View.GONE);
                this.tipoFerramenta = 7;
                this.toolsListener.onPressZoom();
                return;
            default:
                this.tipoFerramenta = 0;
                return;
        }
    }

    public void pintar(Bitmap bitmap, float f) {
        this.maskController.pintarCor(bitmap);
        Canvas canvas = new Canvas(bitmap);
        float f2 = STROKE_INIT;
        float max = Math.max(f2 / f, f2 / 2.0f);
        if (!(this.selecaoInicial == null || this.selecaoFinal == null || !this.isSelecionando)) {
            Paint paint = this.paintSelect;
            float f3 = TrianguloBitmap.STROKE_DOT_SPACE_INIT;
            paint.setPathEffect(new DashPathEffect(new float[]{f3 / f, (f3 * 2.0f) / f}, 0.0f));
            this.paintSelect.setStrokeWidth(max);
            canvas.drawRect(this.selecaoInicial.getXInit(), this.selecaoInicial.getYInit(), this.selecaoFinal.getXInit(), this.selecaoFinal.getYInit(), this.paintSelect);
        }
        if (this.isMascarando) {
            this.paintMascarando.setStrokeWidth(max);
            canvas.drawCircle(this.xAtual, this.yAtual, ((float) getRaioMask()) / f, this.paintMascarando);
        }
    }

    @SuppressLint({"WrongConstant"})
    public void playPreview() {
        this.playingPreview = true;
        this.subToolVelocidade.setVisibility(0);
        ToolsListener toolsListener2 = this.toolsListener;
        if (toolsListener2 != null) {
            toolsListener2.onPlayPreview();
        }
    }

    public void resetStickerAdapter() {
        if (this.moStickerAdapter != null) {
            MotionEditActivity.object.moStickerView.setVisibility(View.GONE);
            this.moStickerAdapter.resetAdapter();
        }
    }

    public void resetTextView(int i) {
        ((TextView) this.activity.findViewById(i)).setTextColor(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
    }

    @SuppressLint({"WrongConstant", "RestrictedApi"})
    public void restartDefinitions() {
        this.maskController = new MaskController();
        this.detalhesTopo =  this.activity.findViewById(R.id.detalhesTopo);
        this.detalhesTopo.setVisibility(View.INVISIBLE);
        this.paintSelect = new Paint(1);
        this.paintSelect.setFilterBitmap(true);
        this.paintSelect.setStyle(Style.STROKE);
        this.paintSelect.setColor(-1);
        this.paintSelect.setStrokeWidth(STROKE_INIT);
        this.paintMascarando = new Paint(1);
        this.paintMascarando.setFilterBitmap(true);
        this.paintMascarando.setStyle(Style.STROKE);
        this.paintMascarando.setColor(-1);
        this.paintMascarando.setStrokeWidth(STROKE_INIT);
        this.paintRepresentacaoMascara = new Paint(1);
        this.paintRepresentacaoMascara.setFilterBitmap(true);
        this.paintRepresentacaoMascara.setStyle(Style.FILL);
        this.paintRepresentacaoMascara.setAlpha(this.maskController.getAlpha());
        this.paintRepresentacaoMascara.setColor(this.maskController.getColor());
        this.btMovimento = this.activity.findViewById(R.id.btMovimento);
        this.btMovimento.setEnabled(false);
        this.btMovimento.setImageResource(R.drawable.ic_menu_motion);
        this.btMovimento.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        this.btMovimento.setOnClickListener(this);
        this.btMovSequencia = this.activity.findViewById(R.id.btMovSequence);
        this.btMovSequencia.setEnabled(false);
        this.btMovSequencia.setImageResource(R.drawable.ic_menu_sequence);
        this.btMovSequencia.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        this.btMovSequencia.setOnClickListener(this);
        this.btSelect = activity.findViewById(R.id.btSelect);
        this.btSelect.setEnabled(false);
        this.btSelect.setImageResource(R.drawable.ic_menu_select);
        this.btSelect.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        this.btSelect.setOnClickListener(this);
        this.btZoom = activity.findViewById(R.id.btZoom);
        this.btZoom.setEnabled(false);
        this.btZoom.setImageResource(R.drawable.tool_zoom);
        this.btZoom.setOnClickListener(this);
        this.btEstabilizacao = activity.findViewById(R.id.btEstabilizar);
        this.btEstabilizacao.setEnabled(false);
        this.btEstabilizacao.setImageResource(R.drawable.ic_menu_stabilize);
        this.btEstabilizacao.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        this.btEstabilizacao.setOnClickListener(this);
        this.btMask = activity.findViewById(R.id.btMask);
        this.btMask.setEnabled(false);
        this.btMask.setImageResource(R.drawable.ic_menu_mask);
        this.btMask.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        this.btMask.setOnClickListener(this);
        this.btApagarMask = activity.findViewById(R.id.btApagarMascara);
        this.btApagarMask.setEnabled(false);
        this.btApagarMask.setImageResource(R.drawable.ic_menu_erase);
        this.btApagarMask.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        this.btApagarMask.setOnClickListener(this);
        this.btEffect = activity.findViewById(R.id.btEffect);
        this.btEffect.setEnabled(false);
        this.btEffect.setImageResource(R.drawable.ic_menu_effect);
        this.btEffect.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        this.btEffect.setOnClickListener(this);
        this.btSticker = activity.findViewById(R.id.btSticker);
        this.btSticker.setEnabled(false);
        this.btSticker.setImageResource(R.drawable.ic_menu_sticker);
        this.btSticker.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        this.btSticker.setOnClickListener(this);
        this.btDelete = this.activity.findViewById(R.id.btDelete);
        this.btDelete.setVisibility(View.INVISIBLE);
        this.btDelete.setImageResource(R.drawable.ic_delete2);
        this.btDelete.setOnClickListener(this);
        this.seekTamanhoPincel = (SeekBar) this.activity.findViewById(R.id.seekTamanhoPincel);
        this.seekTamanhoPincel.setOnSeekBarChangeListener(new C02614());
        this.seekVelocidadePreview = (SeekBar) this.activity.findViewById(R.id.seekTempoVelocidade);
        this.seekVelocidadePreview.setMax(8000);
        this.seekVelocidadePreview.incrementProgressBy(2000);
        this.seekVelocidadePreview.setOnSeekBarChangeListener(new C02625());
        this.subToolTamanho = (RelativeLayout) this.activity.findViewById(R.id.subToolTamMascara);
        this.subToolTamanho.setVisibility(View.INVISIBLE);
        this.moRlEffects = (RelativeLayout) this.activity.findViewById(R.id.rl_effects);
        this.moRcvEffects =  this.activity.findViewById(R.id.rcv_effects);
        this.moRlEffects.setVisibility(View.GONE);
        this.subToolVelocidade = (RelativeLayout) this.activity.findViewById(R.id.subToolVelocidadePreview);
        this.subToolVelocidade.setVisibility(View.INVISIBLE);
        this.maskController.clearMask();
        this.maskController.setAlpha(this.alphaMask);
        this.playingPreview = false;
    }

    @SuppressLint({"WrongConstant", "RestrictedApi"})
    public void setDeleteToolVisibility(boolean z) {
        this.btDelete.setVisibility(z ? 0 : 4);
    }

    public void setEffectAdapter(Bitmap bitmap) {
        this.moEffectlist.clear();
        EffectData effectData = new EffectData(-1, "", "", true, R.drawable.noeffect, "");
        EffectData effectData2 = new EffectData(0, "animation0.json", "", false, R.drawable.filteranim0, "");
        EffectData effectData3 = new EffectData(1, "animation1.json", "animation1", false, R.drawable.filteranim1, "");
        EffectData effectData4 = new EffectData(3, "animation3.json", "animation3", false, R.drawable.filteranim3, "");
        EffectData effectData5 = new EffectData(4, "animation4.json", "animation4", false, R.drawable.filteranim4, "");
        EffectData effectData6 = new EffectData(5, "animation5.json", "animation5", false, R.drawable.filteranim5, "");
        EffectData effectData7 = new EffectData(6, "animation6.json", "", false, R.drawable.filteranim6, "");
        EffectData effectData8 = new EffectData(8, "animation8.json", "animation8", false, R.drawable.filteranim8, "");
        EffectData effectData9 = new EffectData(9, "animation9.json", "", false, R.drawable.filteranim9, "");
        EffectData effectData10 = new EffectData(10, "animation10.json", "", false, R.drawable.anim10_white_hart, "");
        EffectData effectData11 = new EffectData(12, "animation12.json", "animation12", false, R.drawable.filteranim12, "");
        //EffectData effectData12 = r13;
        EffectData effectData13 = new EffectData(13, "animation13.json", "animation13", false, R.drawable.filteranim13, "");
        EffectData effectData14 = new EffectData(14, "animation14.json", "animation14", false, R.drawable.filteranim14, "");
        //EffectData effectData15 = r14;
        EffectData effectData16 = new EffectData(16, "animation16.json", "animation16", false, R.drawable.filteranim16, "");
        EffectData effectData17 = new EffectData(17, "animation17.json", "animation17", false, R.drawable.filteranim17, "");
        // EffectData effectData18 = r15;
        EffectData effectData19 = new EffectData(18, "animation18.json", "animation18", false, R.drawable.filteranim18, "");
        EffectData effectData20 = new EffectData(19, "animation19.json", "animation19", false, R.drawable.filteranim19, "");
        EffectData effectData21 = effectData20;
        EffectData effectData22 = new EffectData(26, "animation26.json", "", false, R.drawable.filteranim26, "");
        EffectData effectData23 = effectData22;
        EffectData effectData24 = new EffectData(27, "animation27.json", "animation27", false, R.drawable.filteranim27, "");
        EffectData effectData25 = effectData24;
        EffectData effectData26 = new EffectData(28, "animation28.json", "animation28", false, R.drawable.filteranim28, "");
        EffectData effectData27 = effectData26;
        EffectData effectData28 = new EffectData(29, "animation29.json", "animation29", false, R.drawable.filteranim29, "");
        EffectData effectData29 = effectData28;
        EffectData effectData30 = new EffectData(30, "animation30.json", "animation30", false, R.drawable.filteranim30, "");
        EffectData effectData31 = effectData30;
        EffectData effectData32 = new EffectData(31, "animation31.json", "animation31", false, R.drawable.filteranim31, "");
        EffectData effectData33 = effectData32;
        this.moEffectlist.add(effectData);
        this.moEffectlist.add(effectData2);
        this.moEffectlist.add(effectData3);
        this.moEffectlist.add(effectData4);
        this.moEffectlist.add(effectData5);
        this.moEffectlist.add(effectData6);
        this.moEffectlist.add(effectData7);
        this.moEffectlist.add(effectData8);
        this.moEffectlist.add(effectData9);
        this.moEffectlist.add(effectData10);
        this.moEffectlist.add(effectData11);
        // this.moEffectlist.add(effectData12);
        this.moEffectlist.add(effectData14);
        // this.moEffectlist.add(effectData15);
        this.moEffectlist.add(effectData17);
        //this.moEffectlist.add(effectData18);
        this.moEffectlist.add(effectData21);
        this.moEffectlist.add(effectData23);
        this.moEffectlist.add(effectData25);
        this.moEffectlist.add(effectData27);
        this.moEffectlist.add(effectData29);
        this.moEffectlist.add(effectData31);
        this.moEffectlist.add(effectData33);
        this.moStickerList.clear();
        StickerData stickerData = new StickerData(-1, "noSticker", "", true, R.drawable.no_sticker);
        StickerData stickerData2 = new StickerData(0, "sticker1.json", "sticker1", false, R.drawable.sticker1);
        StickerData stickerData3 = new StickerData(1, "sticker2.json", "", false, R.drawable.sticker2);
        StickerData stickerData4 = new StickerData(3, "sticker4.json", "sticker4", false, R.drawable.sticker4);
        StickerData stickerData5 = new StickerData(4, "sticker7.json", "sticker7", false, R.drawable.sticker7);
        StickerData stickerData6 = new StickerData(5, "sticker8.json", "sticker8", false, R.drawable.sticker8);
        StickerData stickerData7 = new StickerData(10, "sticker13.json", "sticker13", false, R.drawable.sticker13);
        StickerData stickerData8 = new StickerData(14, "sticker14.json", "", false, R.drawable.sticker14);
        StickerData stickerData9 = new StickerData(15, "sticker15.json", "", false, R.drawable.sticker15);
        // StickerData stickerData10 = r10;
        StickerData stickerData11 = new StickerData(16, "sticker16.json", "", false, R.drawable.sticker16);
        StickerData stickerData12 = new StickerData(17, "sticker17.json", "sticker17", false, R.drawable.sticker17);
        //StickerData stickerData13 = r11;
        StickerData stickerData14 = new StickerData(18, "sticker18.json", "sticker18", false, R.drawable.sticker18);
        StickerData stickerData15 = new StickerData(19, "sticker19.json", "sticker19", false, R.drawable.sticker19);
        // StickerData stickerData16 = r12;
        StickerData stickerData17 = new StickerData(20, "sticker20.json", "sticker20", false, R.drawable.sticker20);
        StickerData stickerData18 = new StickerData(21, "sticker21.json", "sticker21", false, R.drawable.sticker21);
        //StickerData stickerData19 = r13;
        StickerData stickerData20 = new StickerData(22, "sticker22.json", "sticker22", false, R.drawable.sticker22);
        StickerData stickerData21 = new StickerData(23, "sticker23.json", "sticker23", false, R.drawable.sticker23);
        //StickerData stickerData22 = r14;
        StickerData stickerData23 = new StickerData(25, "sticker25.json", "sticker25", false, R.drawable.sticker25);
        StickerData stickerData24 = new StickerData(26, "sticker26.json", "sticker26", false, R.drawable.sticker26);
        // StickerData stickerData25 = r15;
        StickerData stickerData26 = new StickerData(27, "sticker27.json", "sticker27", false, R.drawable.sticker27);
        StickerData stickerData27 = new StickerData(28, "sticker28.json", "sticker28", false, R.drawable.sticker28);
        StickerData stickerData28 = stickerData27;
        this.moStickerList.add(stickerData);
        this.moStickerList.add(stickerData2);
        this.moStickerList.add(stickerData3);
        this.moStickerList.add(stickerData4);
        this.moStickerList.add(stickerData5);
        this.moStickerList.add(stickerData6);
        this.moStickerList.add(stickerData7);
        this.moStickerList.add(stickerData8);
        this.moStickerList.add(stickerData9);
        //this.moStickerList.add(stickerData10);
        this.moStickerList.add(stickerData12);
        //this.moStickerList.add(stickerData13);
        this.moStickerList.add(stickerData15);
        //this.moStickerList.add(stickerData16);
        this.moStickerList.add(stickerData18);
        //this.moStickerList.add(stickerData19);
        this.moStickerList.add(stickerData21);
        // this.moStickerList.add(stickerData22);
        this.moStickerList.add(stickerData24);
        //this.moStickerList.add(stickerData25);
        this.moStickerList.add(stickerData28);
        this.moRcvEffects.setLayoutManager(new LinearLayoutManager(this.activity, RecyclerView.HORIZONTAL, false));
        Bitmap bitmap3 = bitmap;
        this.moEffectAdapter = new EffectAdapter(this.activity, this.moEffectlist, bitmap3, new OnClickListner() {
            public final void onClick(EffectData effectData, int i) {
                lambda$setEffectAdapter$0$ToolsController(effectData, i);
            }
        });
        this.moStickerAdapter = new StickerAdapter(this.activity, this.moStickerList, bitmap3, new OnStickerClickListner() {
            public final void onClick(StickerData stickerData, int i) {
                lambda$setEffectAdapter$1$ToolsController(stickerData, i);
            }
        });
    }

    @SuppressLint({"WrongConstant", "RestrictedApi"})
    public void setEnabled(boolean z) {
        if (z && !this.enabled) {
            this.playingPreview = false;
            this.subToolTamanho.setVisibility(View.INVISIBLE);
            this.subToolVelocidade.setVisibility(View.INVISIBLE);
            this.btDelete.setVisibility(View.INVISIBLE);
        }
        this.enabled = z;
        this.btEstabilizacao.setEnabled(z);
        this.btMovimento.setEnabled(z);
        this.btEffect.setEnabled(z);
        this.btSticker.setEnabled(z);
        this.btMovSequencia.setEnabled(z);
        this.btMask.setEnabled(z);
        this.btApagarMask.setEnabled(z);
        this.btSelect.setEnabled(z);
        this.btZoom.setEnabled(z);
    }

    public void setFerramentaAtual(int i, int i2) {
        ImageView imageButton = activity.findViewById(i);
        TextView textView =  this.activity.findViewById(i2);
        if (this.tipoFerramenta != 0) {
            TextView textView2 =  this.activity.findViewById(this.ferramentaAtualT);
            ((ImageView) this.activity.findViewById(this.ferramentaAtual)).setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
            textView2.setTextColor(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorToolsUnSelected, null));
        }
        imageButton.setColorFilter(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorPrimary, null));
        textView.setTextColor(ResourcesCompat.getColor(this.activity.getResources(), R.color.colorPrimary, null));
        this.ferramentaAtual = i;
        this.ferramentaAtualT = i2;
    }

    public void setMascaraInicial(Bitmap bitmap) {
        if (bitmap == null) {
            this.maskController.clearMask();
        } else {
            this.maskController.setMask(bitmap);
        }
    }

    public void setMascarando(boolean z) {
        this.isMascarando = z;
    }

    public void setSelecionando(boolean z) {
        this.isSelecionando = z;
    }

    @SuppressLint({"WrongConstant"})
    public void setSubToolTamanhoPincelVisibility(boolean z) {
        this.subToolTamanho.setVisibility(z ? 0 : 4);
    }

    @SuppressLint({"WrongConstant"})
    public void setSubToolVelocidadeVisibility(boolean z) {
        this.subToolVelocidade.setVisibility(z ? 0 : 4);
    }

    public void setToolsListener(ToolsListener toolsListener2) {
        this.toolsListener = toolsListener2;
    }

    @SuppressLint({"WrongConstant"})
    public void stopPreview() {
        this.playingPreview = false;
        this.subToolVelocidade.setVisibility(View.INVISIBLE);
        MotionEditActivity.object.moIvGif.setVisibility(View.GONE);
        ToolsListener toolsListener2 = this.toolsListener;
        if (toolsListener2 != null) {
            toolsListener2.onStopPreview();
        }
    }

    public Bitmap addMask(float f, float f2, float f3) {
        this.xAtual = f;
        this.yAtual = f2;
        return this.maskController.addMask(f, f2, f3);
    }

    public Bitmap deleteMask(float f, float f2, float f3) {
        this.xAtual = f;
        this.yAtual = f2;
        return this.maskController.deleteMask(f, f2, f3);
    }

    public void setSelecionando(Ponto ponto, final Ponto ponto2) {
        this.selecaoInicial = ponto;
        this.selecaoFinal = ponto2;
        final Rect rect = new Rect(Math.round(ponto2.getXInit() < ponto.getXInit() ? ponto2.getXInit() : ponto.getXInit()), Math.round(ponto2.getYInit() < ponto.getYInit() ? ponto2.getYInit() : ponto.getYInit()), Math.round(ponto2.getXInit() > ponto.getXInit() ? ponto2.getXInit() : ponto.getXInit()), Math.round(ponto2.getYInit() > ponto.getYInit() ? ponto2.getYInit() : ponto.getYInit()));
        this.isSelecionando = true;
        AnimacaoController.getInstance().iterarPontos(new IteradorDePonto() {
            @Override
            public void onIterate(Ponto ponto) {
                ponto.setSelecionado(rect.contains(Math.round(ponto.getXInit()), Math.round(ponto.getYInit())));
            }
        });

        if (ponto.distanciaPara(ponto2) <= 24.0d) {
            AnimacaoController.getInstance().iterarPontos(new IteradorDePonto() {

                public final void onIterate(Ponto ponto) {
                    lambda$setSelecionando$3$ToolsController(ponto2, ponto);
                }
            });
        }
    }

    public interface ToolsListener {
        void onChangeRaioMascara(int i, Bitmap bitmap);

        void onChangeTamanhoSetaMovSequencia(int i, Bitmap bitmap);

        void onPlayPreview();

        void onPressApagarMascara();

        void onPressDelete();

        void onPressEsbilizar();

        void onPressMascara();

        void onPressSelecao();

        void onPressSetaMovSequencia();

        void onPressSetaMovimento();

        void onPressZoom();

        void onStopPreview();

        void onTempoAlterado(int i);

        void onTempoChanging(int i);
    }

    public class C02614 implements OnSeekBarChangeListener {
        public C02614() {
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z) {
                int access$000 = tipoFerramenta;
                if (access$000 == 3) {
                    raioMask = i + 3;
                    toolsListener.onChangeRaioMascara(raioMask, getRaioRepresentacao());
                } else if (access$000 == 5) {
                    tamMovSequencia = ToolsController.MIN_TAMANHO_MOV_SEQUENCIA + i;
                    toolsListener.onChangeTamanhoSetaMovSequencia(tamMovSequencia, getSetaRepresentacao());
                } else if (access$000 == 6) {
                    raioMask = i + 3;
                    toolsListener.onChangeRaioMascara(raioMask, getRaioRepresentacao());
                }
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            toolsListener.onChangeTamanhoSetaMovSequencia(tamMovSequencia, null);
        }
    }

    public class C02625 implements OnSeekBarChangeListener {
        public C02625() {
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            tempoPreview = 10000 - Math.round((((float) i) / ((float) seekBar.getMax())) * 8000.0f);
            if (z) {
                seekBar.setProgress(stepSize * Math.round((float) (i / stepSize)));
                NumberFormat instance = NumberFormat.getInstance();
                instance.setMaximumFractionDigits(1);
                Bitmap createBitmap = Bitmap.createBitmap(detalhesTopo.getWidth(), detalhesTopo.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                Paint paint = new Paint(1);
                paint.setFilterBitmap(true);
                paint.setColor(ResourcesCompat.getColor(activity.getResources(), R.color.colorPrimary, null));
                paint.setStyle(Style.FILL);
                float width = (float) (createBitmap.getWidth() / 2);
                float height = (float) (createBitmap.getHeight() / 2);
                canvas.drawCircle(width, height, (float) ((canvas.getHeight() / 2) - 2), paint);
                paint.setColor(-1);
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(4.0f);
                canvas.drawCircle(width, height, (float) ((canvas.getHeight() / 2) - 2), paint);
                Paint paint2 = new Paint(1);
                paint2.setColor(-1);
                paint2.setTextAlign(Align.CENTER);
                paint2.setTextSize(50.0f);
                paint2.setFakeBoldText(true);
                StringBuilder sb = new StringBuilder();
                sb.append(instance.format((double) (((float) tempoPreview) / 1000.0f)));
                sb.append("s");
                canvas.drawText(sb.toString(), (float) Math.round(((float) canvas.getWidth()) / 2.0f), (float) Math.round((((float) canvas.getHeight()) / 2.0f) - ((paint2.ascent() + paint2.descent()) / 2.0f)), paint2);
                detalhesTopo.setImageBitmap(createBitmap);
                toolsListener.onTempoChanging(tempoPreview);
            }
        }

        @SuppressLint({"WrongConstant"})
        public void onStartTrackingTouch(SeekBar seekBar) {
            detalhesTopo.setVisibility(View.VISIBLE);
        }

        @SuppressLint({"WrongConstant"})
        public void onStopTrackingTouch(SeekBar seekBar) {
            detalhesTopo.setVisibility(View.INVISIBLE);
            toolsListener.onTempoAlterado(tempoPreview);
        }
    }

    public class C04711 implements IteradorDePonto {
        public C04711() {
        }

        public void onIterate(Ponto ponto) {
            ponto.setSelecionado(false);
        }
    }

    public class MaskController {
        public static final String BUNDLE_MASK = "BUNDLE_MASK";
        public Canvas canvasMask;
        public Bitmap mask;
        public Paint paintMask = new Paint(1);
        public Paint paintRepresentacaoMask = new Paint(1);

        public MaskController() {
            this.paintRepresentacaoMask.setAntiAlias(true);
            this.paintRepresentacaoMask.setFilterBitmap(true);
            this.paintRepresentacaoMask.setStyle(Style.FILL);
            this.paintRepresentacaoMask.setAlpha(150);
            this.paintRepresentacaoMask.setColor(-65536);
            this.paintRepresentacaoMask.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
            this.paintMask.setStyle(Style.FILL);
            this.paintMask.setFilterBitmap(true);
            this.paintMask.setColor(-65536);
        }

        public void addMask(Bitmap bitmap) {
            if (this.mask == null) {
                this.mask = Bitmap.createBitmap(AnimacaoController.getImagemWidth(), AnimacaoController.getImagemHeight(), Config.ARGB_8888);
                this.canvasMask = new Canvas(this.mask);
            }
            this.paintMask.setXfermode(null);
            this.canvasMask.drawBitmap(bitmap, 0.0f, 0.0f, this.paintMask);
        }

        public void clearMask() {
            Bitmap bitmap = this.mask;
            if (bitmap != null) {
                bitmap.recycle();
                this.mask = null;
            }
        }

        public void deleteMask(Bitmap bitmap) {
            if (this.mask != null && bitmap != null) {
                this.paintMask.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
                this.canvasMask.drawBitmap(bitmap, 0.0f, 0.0f, this.paintMask);
            }
        }

        public int getAlpha() {
            return this.paintRepresentacaoMask.getAlpha();
        }

        public void setAlpha(int i) {
            this.paintRepresentacaoMask.setAlpha(i);
        }

        public int getColor() {
            return this.paintRepresentacaoMask.getColor();
        }

        public void setColor(int i) {
            this.paintRepresentacaoMask.setColor(i);
        }

        public void pintarCor(Bitmap bitmap) {
            if (this.mask != null) {
                new Canvas(bitmap).drawBitmap(this.mask, 0.0f, 0.0f, this.paintRepresentacaoMask);
            }
        }

        public void setMask(Bitmap bitmap) {
            this.mask = bitmap.copy(Config.ARGB_8888, true);
            this.canvasMask = new Canvas(this.mask);
        }

        public Bitmap deleteMask(float f, float f2, float f3) {
            Bitmap bitmap = this.mask;
            if (bitmap == null) {
                return null;
            }
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), this.mask.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            float f4 = 1.0f / f3;
            canvas.drawCircle(f, f2, ((float) ToolsController.getRaioMask()) * f4, this.paintMask);
            this.paintMask.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            canvas.drawBitmap(this.mask, 0.0f, 0.0f, this.paintMask);
            this.paintMask.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            this.canvasMask.drawCircle(f, f2, ((float) ToolsController.getRaioMask()) * f4, this.paintMask);
            return createBitmap;
        }

        public Bitmap addMask(float f, float f2, float f3) {
            if (this.mask == null) {
                this.mask = Bitmap.createBitmap(AnimacaoController.getImagemWidth(), AnimacaoController.getImagemHeight(), Config.ARGB_8888);
                this.canvasMask = new Canvas(this.mask);
            }
            Bitmap createBitmap = Bitmap.createBitmap(this.mask.getWidth(), this.mask.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            this.paintMask.setXfermode(null);
            float f4 = 1.0f / f3;
            canvas.drawCircle(f, f2, ((float) ToolsController.getRaioMask()) * f4, this.paintMask);
            this.paintMask.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
            canvas.drawBitmap(this.mask, 0.0f, 0.0f, this.paintMask);
            this.paintMask.setXfermode(null);
            this.canvasMask.drawCircle(f, f2, ((float) ToolsController.getRaioMask()) * f4, this.paintMask);
            return createBitmap;
        }
    }
}
