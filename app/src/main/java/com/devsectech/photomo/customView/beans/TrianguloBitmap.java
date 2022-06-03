package com.devsectech.photomo.customView.beans;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Shader.TileMode;
import android.util.Log;
import com.devsectech.photomo.customView.beans.Vertice.PacoteDistanciaVertice;
import com.devsectech.photomo.customView.controllersapp.ToolsController;
import java.util.ArrayList;
import java.util.Arrays;

public class TrianguloBitmap {
    public static float STROKE_DOT_SPACE_INIT = 8.0f;
    private static float STROKE_INIT = 0.8f;
    private float altura;
    private Bitmap bitmapTriangulo;
    private float largura;
    private BitmapShader mBitmapShader;
    private Paint mPaintShader = new Paint(1);
    private boolean maskInicial = false;
    private Bitmap originalImage;

    private Ponto f150p1;
    private Ponto f151p2;
    private Ponto f152p3;
    private Paint paintPathMove = new Paint(1);
    private Paint paintPathStatic = new Paint(1);
    private Path pathTriangulo = new Path();
    private float xMaior = 0.0f;
    private float xMenor = Float.MAX_VALUE;
    private float yMaior = 0.0f;
    private float yMenor = Float.MAX_VALUE;


    public TrianguloBitmap clone() throws CloneNotSupportedException {
        return (TrianguloBitmap) super.clone();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.f150p1);
        String str = " ";
        sb.append(str);
        sb.append(this.f151p2);
        sb.append(str);
        sb.append(this.f152p3);
        return sb.toString();
    }

    public static ArrayList<TrianguloBitmap> cutInitialBitmap(Bitmap bitmap) {
        ArrayList<TrianguloBitmap> arrayList = new ArrayList<>();
        int width = bitmap.getWidth() / bitmap.getHeight();
        float width2 = ((float) bitmap.getWidth()) * 0.1f;
        float f = -1.0f * width2;
        Ponto ponto = new Ponto(f, f, true);
        Ponto ponto2 = new Ponto(((float) bitmap.getWidth()) + width2, f, true);
        Ponto ponto3 = new Ponto(((float) bitmap.getWidth()) + width2, ((float) bitmap.getHeight()) + width2, true);
        Ponto ponto4 = new Ponto(f, ((float) bitmap.getHeight()) + width2, true);
        TrianguloBitmap trianguloBitmap = new TrianguloBitmap(bitmap, ponto, ponto2, ponto3);
        TrianguloBitmap trianguloBitmap2 = new TrianguloBitmap(bitmap, ponto3, ponto4, ponto);
        trianguloBitmap.setMaskInicial(true);
        trianguloBitmap2.setMaskInicial(true);
        arrayList.add(trianguloBitmap);
        arrayList.add(trianguloBitmap2);
        return arrayList;
    }

    public TrianguloBitmap(Bitmap bitmap, Ponto ponto, Ponto ponto2, Ponto ponto3) {
        this.originalImage = bitmap;
        this.mBitmapShader = new BitmapShader(bitmap, TileMode.REPEAT, TileMode.REPEAT);
        this.mPaintShader.setFilterBitmap(true);
        this.mPaintShader.setAntiAlias(true);
        this.mPaintShader.setShader(this.mBitmapShader);
        this.mPaintShader.setStrokeWidth(10.0f);
        this.f150p1 = ponto;
        this.f151p2 = ponto2;
        this.f152p3 = ponto3;
        Ponto[] pontoArr = {ponto, ponto2, ponto3};
        for (int i = 0; i < 3; i++) {
            this.xMenor = this.xMenor < pontoArr[i].getXInit() ? this.xMenor : pontoArr[i].getXInit();
            this.yMenor = this.yMenor < pontoArr[i].getYInit() ? this.yMenor : pontoArr[i].getYInit();
            this.xMaior = this.xMaior > pontoArr[i].getXInit() ? this.xMaior : pontoArr[i].getXInit();
            this.yMaior = this.yMaior > pontoArr[i].getYInit() ? this.yMaior : pontoArr[i].getYInit();
        }
        float f = this.xMenor;
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.xMenor = f;
        float f2 = this.yMenor;
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        this.yMenor = f2;
        this.paintPathMove.setStyle(Style.STROKE);
        this.paintPathMove.setFilterBitmap(true);
        Paint paint = this.paintPathMove;
        float f3 = STROKE_DOT_SPACE_INIT;
        paint.setPathEffect(new DashPathEffect(new float[]{f3, f3 * 2.0f}, 0.0f));
        this.paintPathMove.setStrokeWidth(STROKE_INIT);
        this.paintPathMove.setColor(-16776961);
        this.paintPathStatic.setStrokeWidth(STROKE_INIT);
        this.paintPathMove.setFilterBitmap(true);
        this.paintPathStatic.setColor(ToolsController.getColorMask());
        this.paintPathStatic.setFilterBitmap(true);
        this.paintPathStatic.setStyle(Style.FILL);
        this.paintPathStatic.setAntiAlias(true);
    }

    public void setOriginalImage(Bitmap bitmap) {
        clear();
        this.originalImage = bitmap;
        this.mBitmapShader = new BitmapShader(this.originalImage, TileMode.REPEAT, TileMode.REPEAT);
        this.mPaintShader.setShader(this.mBitmapShader);
    }

    public boolean contemVertice(Ponto ponto, Ponto ponto2) {
        return contemPonto(ponto) && contemPonto(ponto2);
    }

    public boolean contemPonto(Ponto ponto) {
        return ponto.equals(this.f150p1) || ponto.equals(this.f151p2) || ponto.equals(this.f152p3);
    }

    public boolean pontoNoCircunscrito(Ponto ponto) {
        return ponto.naCircunferencia(circuncentro(), this.f150p1.distanciaPara(circuncentro()));
    }

    public Vertice getArestaComum(TrianguloBitmap trianguloBitmap) {
        if (contemVertice(trianguloBitmap.getP1(), trianguloBitmap.getP2())) {
            return new Vertice(trianguloBitmap.getP1(), trianguloBitmap.getP2());
        }
        if (contemVertice(trianguloBitmap.getP2(), trianguloBitmap.getP3())) {
            return new Vertice(trianguloBitmap.getP2(), trianguloBitmap.getP3());
        }
        if (contemVertice(trianguloBitmap.getP3(), trianguloBitmap.getP1())) {
            return new Vertice(trianguloBitmap.getP3(), trianguloBitmap.getP1());
        }
        return null;
    }

    public boolean eVizinho(TrianguloBitmap trianguloBitmap) {
        return contemVertice(trianguloBitmap.getP1(), trianguloBitmap.getP2()) || contemVertice(trianguloBitmap.getP2(), trianguloBitmap.getP3()) || contemVertice(trianguloBitmap.getP3(), trianguloBitmap.getP1());
    }

    public PacoteDistanciaVertice getVerticeMaisProximo(Ponto ponto) {
        PacoteDistanciaVertice[] pacoteDistanciaVerticeArr = {new PacoteDistanciaVertice(new Vertice(this.f150p1, this.f151p2), computeClosestPoint(new Vertice(this.f150p1, this.f151p2), ponto).sub(ponto).mag()), new PacoteDistanciaVertice(new Vertice(this.f151p2, this.f152p3), computeClosestPoint(new Vertice(this.f151p2, this.f152p3), ponto).sub(ponto).mag()), new PacoteDistanciaVertice(new Vertice(this.f152p3, this.f150p1), computeClosestPoint(new Vertice(this.f152p3, this.f150p1), ponto).sub(ponto).mag())};
        Arrays.sort(pacoteDistanciaVerticeArr);
        return pacoteDistanciaVerticeArr[0];
    }

    private Ponto computeClosestPoint(Vertice vertice, Ponto ponto) {
        Ponto sub = vertice.f154p2.sub(vertice.f153p1);
        double dot = ponto.sub(vertice.f153p1).dot(sub) / sub.dot(sub);
        if (dot < 0.0d) {
            dot = 0.0d;
        } else if (dot > 1.0d) {
            dot = 1.0d;
        }
        return vertice.f153p1.add(sub.mult(dot));
    }

    public Ponto getPontoForaVertice(Vertice vertice) {
        if (!this.f150p1.equals(vertice.f153p1) && !this.f150p1.equals(vertice.f154p2)) {
            return this.f150p1;
        }
        if (!this.f151p2.equals(vertice.f153p1) && !this.f151p2.equals(vertice.f154p2)) {
            return this.f151p2;
        }
        if (this.f152p3.equals(vertice.f153p1) || this.f152p3.equals(vertice.f154p2)) {
            return null;
        }
        return this.f152p3;
    }

    public double getAnguloPontoOpostoAresta(Vertice vertice) {
        float f;
        Ponto pontoForaVertice = getPontoForaVertice(vertice);
        float f2 = 0.0f;
        if (pontoForaVertice != null) {
            f2 = Math.abs(new Vertice(pontoForaVertice, vertice.f153p1).coeficienteAngular());
            f = Math.abs(new Vertice(pontoForaVertice, vertice.f154p2).coeficienteAngular());
        } else {
            f = 0.0f;
        }
        return Math.atan((double) (f2 + f));
    }

    public Ponto mediana() {
        return new Ponto(((this.f150p1.getXInit() + this.f151p2.getXInit()) + this.f152p3.getXInit()) / 3.0f, ((this.f150p1.getYInit() + this.f151p2.getYInit()) + this.f152p3.getYInit()) / 3.0f, true);
    }

    public Ponto circuncentro() {
        Ponto mediana = this.f150p1.mediana(this.f151p2);
        Ponto mediana2 = this.f151p2.mediana(this.f152p3);
        float coeficienteAngular = -1.0f / this.f150p1.coeficienteAngular(this.f151p2);
        if (Float.isInfinite(coeficienteAngular)) {
            mediana = this.f152p3.mediana(this.f150p1);
            coeficienteAngular = -1.0f / this.f152p3.coeficienteAngular(this.f150p1);
        }
        float coeficienteAngular2 = -1.0f / this.f151p2.coeficienteAngular(this.f152p3);
        if (Float.isInfinite(coeficienteAngular2)) {
            mediana2 = this.f152p3.mediana(this.f150p1);
            coeficienteAngular2 = -1.0f / this.f152p3.coeficienteAngular(this.f150p1);
        }
        float yInit = mediana.getYInit() - (mediana.getXInit() * coeficienteAngular);
        float yInit2 = (yInit - (mediana2.getYInit() - (mediana2.getXInit() * coeficienteAngular2))) / (coeficienteAngular2 - coeficienteAngular);
        return new Ponto(yInit2, (coeficienteAngular * yInit2) + yInit, true);
    }

    public boolean isEstatico() {
        return this.f150p1.isEstatico() && this.f151p2.isEstatico() && this.f152p3.isEstatico();
    }

    private void desenharVertice(Canvas canvas, Ponto ponto, Ponto ponto2, boolean z) {
        if (ponto.isEstatico() && ponto2.isEstatico()) {
            canvas.drawLine(ponto.getXInit(), ponto.getYInit(), ponto2.getXInit(), ponto2.getYInit(), this.paintPathStatic);
        } else if (z) {
            canvas.drawLine(ponto.getXInit(), ponto.getYInit(), ponto2.getXInit(), ponto2.getYInit(), this.paintPathMove);
        }
    }

    public void desenhaTrajeto(Canvas canvas, float f) {
        if (!isEstatico()) {
            Paint paint = this.paintPathMove;
            float f2 = STROKE_DOT_SPACE_INIT;
            paint.setPathEffect(new DashPathEffect(new float[]{f2 / f, (f2 * 2.0f) / f}, 0.0f));
            this.paintPathMove.setStrokeWidth(STROKE_INIT / f);
            desenharVertice(canvas, this.f150p1, this.f151p2, false);
            desenharVertice(canvas, this.f151p2, this.f152p3, false);
            desenharVertice(canvas, this.f152p3, this.f150p1, false);
        } else if (!this.maskInicial) {
            this.paintPathStatic.setStrokeWidth(STROKE_INIT / f);
            this.paintPathStatic.setAlpha(ToolsController.getAlphaMask());
            this.pathTriangulo.reset();
            this.pathTriangulo.moveTo(this.f150p1.getXInit(), this.f150p1.getYInit());
            this.pathTriangulo.lineTo(this.f151p2.getXInit(), this.f151p2.getYInit());
            this.pathTriangulo.lineTo(this.f152p3.getXInit(), this.f152p3.getYInit());
            this.pathTriangulo.close();
            canvas.drawPath(this.pathTriangulo, this.paintPathStatic);
        }
    }

    public void setMaskInicial(boolean z) {
        this.maskInicial = z;
    }

    public void desenhaEstatico(Canvas canvas, Config config) {
        canvas.drawBitmap(getBitmap(config), 0.0f, 0.0f, null);
    }

    public void desenhaPontos(Canvas canvas) {
        this.f150p1.desenharPontoMovimento(canvas);
    }

    public void desenhaDistorcao(Canvas canvas, Config config) {
        Matrix matrix = new Matrix();
        float[] fArr = {this.f150p1.getXInit() - this.xMenor, this.f150p1.getYInit() - this.yMenor, this.f151p2.getXInit() - this.xMenor, this.f151p2.getYInit() - this.yMenor, this.f152p3.getXInit() - this.xMenor, this.f152p3.getYInit() - this.yMenor};
        float[] fArr2 = {this.f150p1.getXAtual(), this.f150p1.getYAtual(), this.f151p2.getXAtual(), this.f151p2.getYAtual(), this.f152p3.getXAtual(), this.f152p3.getYAtual()};
        Bitmap bitmap = getBitmap(config);
        matrix.setPolyToPoly(fArr, 0, fArr2, 0, 3);
        matrix.preScale((((float) bitmap.getWidth()) + 3.0f) / ((float) bitmap.getWidth()), (((float) bitmap.getHeight()) + 3.0f) / ((float) bitmap.getHeight()), (float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2));
        canvas.drawBitmap(bitmap, matrix, null);
    }

    public void desenhaMascara(Canvas canvas) {
        Path path = new Path();
        path.moveTo(this.f150p1.getXInit(), this.f150p1.getYInit());
        path.lineTo(this.f151p2.getXInit(), this.f151p2.getYInit());
        path.lineTo(this.f152p3.getXInit(), this.f152p3.getYInit());
        path.close();
        canvas.drawPath(path, this.mPaintShader);
    }

    private Bitmap getBitmap(Config config) {
        if (this.bitmapTriangulo == null) {
            Bitmap createBitmap = Bitmap.createBitmap(this.originalImage.getWidth(), this.originalImage.getHeight(), config);
            Path path = new Path();
            path.moveTo(this.f150p1.getXInit(), this.f150p1.getYInit());
            path.lineTo(this.f151p2.getXInit(), this.f151p2.getYInit());
            path.lineTo(this.f152p3.getXInit(), this.f152p3.getYInit());
            path.close();
            new Canvas(createBitmap).drawPath(path, this.mPaintShader);
            try {
                this.largura = this.xMaior - this.xMenor;
                this.altura = this.yMaior - this.yMenor;
                this.largura = this.largura < 1.0f ? 1.0f : this.largura;
                this.altura = this.altura < 1.0f ? 1.0f : this.altura;
                this.altura = Math.round(this.altura) + Math.round(this.yMenor) > createBitmap.getHeight() ? (float) (createBitmap.getHeight() - Math.round(this.yMenor)) : this.altura;
                this.largura = Math.round(this.largura) + Math.round(this.xMenor) > createBitmap.getWidth() ? (float) (createBitmap.getWidth() - Math.round(this.xMenor)) : this.largura;
                if (this.largura < 1.0f) {
                    this.xMenor = (float) (createBitmap.getWidth() - 1);
                    this.largura = 1.0f;
                }
                if (this.altura < 1.0f) {
                    this.yMenor = (float) (createBitmap.getHeight() - 1);
                    this.altura = 1.0f;
                }
                this.bitmapTriangulo = Bitmap.createBitmap(createBitmap, (int) this.xMenor, (int) this.yMenor, (int) this.largura, (int) this.altura);
            } catch (Exception e) {
                Log.e("INFO", e.getMessage());
            }
        }
        return this.bitmapTriangulo;
    }

    private float sign(Ponto ponto, Ponto ponto2, Ponto ponto3) {
        return ((ponto.getXInit() - ponto3.getXInit()) * (ponto2.getYInit() - ponto3.getYInit())) - ((ponto2.getXInit() - ponto3.getXInit()) * (ponto.getYInit() - ponto3.getYInit()));
    }

    private boolean hasSameSign(double d, double d2) {
        return Math.signum(d) == Math.signum(d2);
    }

    public boolean contains(Ponto ponto) {
        double cross = ponto.sub(this.f150p1).cross(this.f151p2.sub(this.f150p1));
        return hasSameSign(cross, ponto.sub(this.f151p2).cross(this.f152p3.sub(this.f151p2))) && hasSameSign(cross, ponto.sub(this.f152p3).cross(this.f150p1.sub(this.f152p3)));
    }

    public boolean contemPontoDentro(Ponto ponto) {
        if(ponto != null){
            boolean z = sign(ponto, this.f150p1, this.f151p2) < 0.0f;
            boolean z2 = sign(ponto, this.f151p2, this.f152p3) < 0.0f;
            return z == z2 && z2 == ((sign(ponto, this.f152p3, this.f150p1) > 0.0f ? 1 : (sign(ponto, this.f152p3, this.f150p1) == 0.0f ? 0 : -1)) < 0);
        }
        return false;
    }

    public Ponto getP1() {
        return this.f150p1;
    }

    public Ponto getP2() {
        return this.f151p2;
    }

    public Ponto getP3() {
        return this.f152p3;
    }

    public void clear() {
        this.bitmapTriangulo = null;
    }
}
