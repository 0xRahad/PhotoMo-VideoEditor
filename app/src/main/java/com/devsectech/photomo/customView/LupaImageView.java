package com.devsectech.photomo.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import com.devsectech.photomo.R;

public class LupaImageView extends AppCompatImageView {
    public static int TAMANHO_LUPA = 300;
    public static float ZOOM_INIT = 1.5f;
    public Canvas canvasResultado;
    public Bitmap imagemZoom;
    public Bitmap lupa;
    public Bitmap mascara;
    public Paint paintMask;
    public Bitmap resultadoLupa;
    public float zoom = ZOOM_INIT;

    public LupaImageView(Context context) {
        super(context);
        inicializar();
    }

    private void inicializar() {
        if (!isInEditMode()) {
            this.paintMask = new Paint(1);
            this.paintMask.setFilterBitmap(true);
            this.paintMask.setStyle(Style.FILL);
            this.paintMask.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.lupa);
            int i = TAMANHO_LUPA;
            this.lupa = Bitmap.createScaledBitmap(decodeResource, i, i, true);
            Bitmap decodeResource2 = BitmapFactory.decodeResource(getResources(), R.drawable.lupa_mask);
            int i2 = TAMANHO_LUPA;
            this.mascara = Bitmap.createScaledBitmap(decodeResource2, i2, i2, true);
            int i3 = TAMANHO_LUPA;
            this.resultadoLupa = Bitmap.createBitmap(i3, i3, Config.ARGB_8888);
            this.canvasResultado = new Canvas(this.resultadoLupa);
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setImageBitmap(Bitmap bitmap, float f) {
        this.zoom = Math.min(ZOOM_INIT * f, 4.0f);
        setImageBitmap(bitmap);
    }

    public void setPosicaoLupa(final float f, final float f2) {
        if (this.imagemZoom != null) {
            new AsyncTask() {
                public Object doInBackground(Object[] objArr) {
                    Bitmap createBitmap = Bitmap.createBitmap(LupaImageView.TAMANHO_LUPA, LupaImageView.TAMANHO_LUPA, Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    canvas.drawColor(-16777216);
                    canvas.drawBitmap(imagemZoom, (zoom * f * -1.0f) + ((float) (LupaImageView.TAMANHO_LUPA / 2)), (zoom * f2 * -1.0f) + ((float) (LupaImageView.TAMANHO_LUPA / 2)), null);
                    canvas.drawBitmap(mascara, 0.0f, 0.0f, paintMask);
                    canvasResultado.drawBitmap(createBitmap, 0.0f, 0.0f, null);
                    canvasResultado.drawBitmap(lupa, 0.0f, 0.0f, null);
                    return null;
                }

                public void onPostExecute(Object obj) {
                    invalidate();
                    super.onPostExecute(obj);
                }
            }.execute(new Object[0]);
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.imagemZoom = Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) * this.zoom), (int) (((float) bitmap.getHeight()) * this.zoom), true);
        super.setImageBitmap(this.resultadoLupa);
    }

    public LupaImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inicializar();
    }

    public LupaImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        inicializar();
    }
}
