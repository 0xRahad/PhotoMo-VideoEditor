package com.devsectech.photomo.customView.controllersapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.Log;
import com.devsectech.photomo.customView.Delaunay;
import com.devsectech.photomo.customView.beans.Ponto;
import com.devsectech.photomo.customView.beans.Projeto;
import com.devsectech.photomo.customView.beans.TrianguloBitmap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AnimacaoController {
    public static final int ANIM_ALPHA_PONTOS = 255;
    public static final int ANIM_FPS = 30;
    public static AnimacaoController controller;
    public CountDownTimer animCount;
    public Bitmap apresentacaoImage;
    public Canvas canvas;
    public Delaunay delaunayApresentacao;
    public FrameController frameController;
    public AnimateListener listenerAnim = null;
    public Paint paintmask = new Paint(1);
    public Projeto projetoAtual;
    public int tempoAnimacao = 10000;

    public interface AnimateListener {
        void onAnimate(Bitmap bitmap);
    }

    public interface IteradorDePonto {
        void onIterate(Ponto ponto);
    }

    public interface ManipuladorDePonto {
        void onFinish(Bitmap bitmap);

        void onManipulate(Ponto ponto);
    }

    public AnimacaoController(Projeto projeto) {
        setNovoProjeto(projeto);
        this.frameController = FrameController.getInstance();
        this.paintmask.setAntiAlias(true);
        this.paintmask.setFilterBitmap(true);
        this.paintmask.setStyle(Style.FILL);
        this.paintmask.setColor(-16777216);
        this.paintmask.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    }

    public static int getImagemHeight() {
        return controller.apresentacaoImage.getHeight();
    }

    public static int getImagemWidth() {
        return controller.apresentacaoImage.getWidth();
    }

    public static AnimacaoController getInstance() {
        return controller;
    }

    public static AnimacaoController init(Projeto projeto) {
        AnimacaoController animacaoController = controller;
        if (animacaoController == null) {
            controller = new AnimacaoController(projeto);
        } else {
            animacaoController.setNovoProjeto(projeto);
        }
        return controller;
    }

    private void setNovoProjeto(Projeto projeto) {
        this.projetoAtual = projeto;
        float width = (float) projeto.getImagem().getWidth();
        float height = (float) projeto.getImagem().getHeight();
        float f = width > height ? 800.0f / width : 800.0f / height;
        this.apresentacaoImage = Bitmap.createScaledBitmap(projeto.getImagem(), Math.round(width * f), Math.round(height * f), true);
        Delaunay delaunay = this.delaunayApresentacao;
        if (delaunay != null) {
            delaunay.clear();
        }
        this.delaunayApresentacao = new Delaunay(this.apresentacaoImage);
        for (Ponto copia : projeto.getListaPontos()) {
            this.delaunayApresentacao.addPonto(copia.getCopia(1.0f / projeto.getProporcaoApresentacao()));
        }
    }

    public void addPonto(Ponto ponto) {
        this.delaunayApresentacao.addPonto(ponto);
    }

    public void deletePonto(Ponto ponto) {
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        copyOnWriteArrayList.add(ponto);
        this.delaunayApresentacao.deletePontos(copyOnWriteArrayList);
    }

    public void deletePontos(List<Ponto> list) {
        this.delaunayApresentacao.deletePontos(list);
    }

    public void deletePontosSelecionados() {
        deletePontos(getListaPontosSelecionados());
    }

    public boolean existePonto(Ponto ponto) {
        for (Ponto equals : this.delaunayApresentacao.getListaPontos()) {
            if (equals.equals(ponto)) {
                return true;
            }
        }
        return false;
    }

    public Bitmap getImagemRepresentacao(boolean z, float f) {
        Bitmap copy = this.apresentacaoImage.copy(Config.ARGB_8888, true);
        this.delaunayApresentacao.reiniciarPontos();
        this.canvas = new Canvas(copy);
        if (z) {
            for (TrianguloBitmap desenhaTrajeto : this.delaunayApresentacao.getListaTriangulos()) {
                desenhaTrajeto.desenhaTrajeto(this.canvas, f);
            }
        }
        for (Ponto ponto : this.delaunayApresentacao.getListaPontos()) {
            if (!ponto.isEstatico()) {
                ponto.desenharSeta(this.canvas, 255, f);
            }
            ponto.desenharPonto(this.canvas, 255, f);
        }
        return copy;
    }

    public List<Ponto> getListaPontosSelecionados() {
        LinkedList linkedList = new LinkedList();
        for (Ponto ponto : this.delaunayApresentacao.getListaPontos()) {
            if (ponto.isSelecionado()) {
                linkedList.add(ponto);
            }
        }
        return linkedList;
    }

    public int getTempoAnimacao() {
        return this.tempoAnimacao;
    }

    public void iterarPontos(IteradorDePonto iteradorDePonto) {
        for (Ponto onIterate : this.delaunayApresentacao.getListaPontos()) {
            iteradorDePonto.onIterate(onIterate);
        }
    }

    public Bitmap mo6902a(int i, Bitmap bitmap) {
        Rect rect = this.projetoAtual.getRect();
        if (rect == null) {
            return bitmap;
        }
        Paint paint = new Paint(2);
        paint.setColor(i);
        Bitmap copy = bitmap.copy(Config.ARGB_8888, true);
        Canvas canvas2 = new Canvas(copy);
        canvas2.drawPaint(paint);
        float proporcaoApresentacao = 1.0f / this.projetoAtual.getProporcaoApresentacao();
        canvas2.drawBitmap(Bitmap.createBitmap(bitmap, (int) (((float) rect.left) * proporcaoApresentacao), (int) (((float) rect.top) * proporcaoApresentacao), (int) (((float) rect.width()) * proporcaoApresentacao), (int) (((float) rect.height()) * proporcaoApresentacao)), ((float) rect.left) * proporcaoApresentacao, ((float) rect.top) * proporcaoApresentacao, new Paint(2));
        return copy;
    }

    public void setOnAnimateListener(AnimateListener animateListener) {
        this.listenerAnim = animateListener;
    }

    public void setTempoAnimacao(int i) {
        this.tempoAnimacao = i;
    }

    public void startAnimation() {
        CountDownTimer countDownTimer = this.animCount;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        final Config config = Config.ARGB_8888;
        this.delaunayApresentacao.setImagemDelaunay(Utils.getImagemSemMascara(this.apresentacaoImage, ToolsController.getMask(), config));
        Bitmap triangulosEstaticos = this.delaunayApresentacao.getTriangulosEstaticos(Config.ARGB_8888);
        Bitmap mascaraDaImagem = Utils.getMascaraDaImagem(this.apresentacaoImage, ToolsController.getMask(), Config.ARGB_8888);
        final Bitmap createBitmap = Bitmap.createBitmap(triangulosEstaticos);
        new Canvas(createBitmap).drawBitmap(mascaraDaImagem, 0.0f, 0.0f, null);
        final Bitmap createBitmap2 = Bitmap.createBitmap(this.apresentacaoImage.getWidth(), this.apresentacaoImage.getHeight(), config);
        final Canvas canvas2 = new Canvas(createBitmap2);
        canvas2.drawBitmap(this.apresentacaoImage, 0.0f, 0.0f, null);
        final Paint paint = new Paint(1);
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setDither(true);
        CountDownTimer r1 = new CountDownTimer((long) this.tempoAnimacao, 33) {
            public void onFinish() {
                startAnimation();
            }

            public void onTick(long j) {
                System.currentTimeMillis();
                AnimacaoController animacaoController = AnimacaoController.this;
                float f = (float) (((long) animacaoController.tempoAnimacao) - j);
                Bitmap frameEmMovimento = animacaoController.frameController.getFrameEmMovimento(delaunayApresentacao, tempoAnimacao, 30, f, config);
                AnimacaoController animacaoController2 = AnimacaoController.this;
                int i = animacaoController2.tempoAnimacao;
                float f2 = ((((float) i) / 2.0f) + f) % ((float) i);
                Bitmap frameEmMovimento2 = animacaoController2.frameController.getFrameEmMovimento(delaunayApresentacao, tempoAnimacao, 30, f2, config);
                int alpha = frameController.getAlpha(tempoAnimacao, f);
                paint.setAlpha(alpha);
                canvas2.drawBitmap(frameEmMovimento, 0.0f, 0.0f, null);
                int alpha2 = frameController.getAlpha(tempoAnimacao, f2);
                paint.setAlpha(alpha2);
                canvas2.drawBitmap(frameEmMovimento2, 0.0f, 0.0f, paint);
                StringBuilder sb = new StringBuilder();
                sb.append("ALPHA1: ");
                sb.append(alpha);
                sb.append(" ALPHA2:");
                sb.append(alpha2);
                Log.i("ANIMACAO", sb.toString());
                canvas2.drawBitmap(createBitmap, 0.0f, 0.0f, null);
                if (listenerAnim != null) {
                    listenerAnim.onAnimate(createBitmap2);
                }
            }
        };
        this.animCount = r1.start();
    }

    public void stopAnimation(Context context) {
        CountDownTimer countDownTimer = this.animCount;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        this.delaunayApresentacao.clear();
    }

    public boolean temPontoSelecionado() {
        for (Ponto isSelecionado : this.delaunayApresentacao.getListaPontos()) {
            if (isSelecionado.isSelecionado()) {
                return true;
            }
        }
        return false;
    }
}
