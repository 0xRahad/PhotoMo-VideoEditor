package com.devsectech.photomo.customView.controllersapp;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import com.devsectech.photomo.customView.Delaunay;
import com.devsectech.photomo.customView.beans.Ponto;
import com.devsectech.photomo.customView.beans.TrianguloBitmap;

public class FrameController {
    public static FrameController controller;
    public static Bitmap imagemManipulada;
    public int ANIM_ALPHA_MAX = 255;
    public int ANIM_ALPHA_PERCENT = 50;
    public Canvas canvas;

    public static FrameController getInstance() {
        if (controller == null) {
            controller = new FrameController();
        }
        return controller;
    }

    public int getAlpha(int i, float f) {
        float f2 = (float) ((this.ANIM_ALPHA_PERCENT * i) / 100);
        int i2 = this.ANIM_ALPHA_MAX;
        float f3 = ((float) i2) / f2;
        float f4 = ((float) i) - f2;
        if (f >= f4) {
            return Math.round((f4 - f) * f3) + i2;
        }
        return f < f2 ? Math.round(f3 * f) : i2;
    }

    public Bitmap getFrameEmMovimento(Delaunay delaunay, int i, int i2, float f, Config config) {
        int round = Math.round(((float) (i * i2)) / 1000.0f);
        int floor = (int) Math.floor((double) ((f / 1000.0f) * ((float) i2)));
        for (Ponto ponto : delaunay.getListaPontos()) {
            if (!ponto.isEstatico()) {
                float xInit = ponto.getXInit() - ((ponto.getXFim() - ponto.getXInit()) / 1.0f);
                float yInit = ponto.getYInit() - ((ponto.getYFim() - ponto.getYInit()) / 1.0f);
                float f2 = (float) floor;
                float f3 = (float) round;
                ponto.setPosicaoAtualAnim((((ponto.getXFim() - xInit) / f3) * f2) + xInit, (((ponto.getYFim() - yInit) / f3) * f2) + yInit);
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(delaunay.getImagemDelaunay().getWidth(), delaunay.getImagemDelaunay().getHeight(), config);
        Canvas canvas2 = new Canvas(createBitmap);
        for (TrianguloBitmap trianguloBitmap : delaunay.getListaTriangulos()) {
            if (!trianguloBitmap.isEstatico()) {
                trianguloBitmap.desenhaDistorcao(canvas2, config);
            }
        }
        return createBitmap;
    }
}
