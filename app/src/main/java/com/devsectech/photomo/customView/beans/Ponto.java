package com.devsectech.photomo.customView.beans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.core.view.InputDeviceCompat;

public class Ponto implements Parcelable {
    public static final String COLUMN_ESTATICO = "estatico";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_PROJETO = "id_projeto";
    public static final String COLUMN_XFIM = "xFim";
    public static final String COLUMN_XINIT = "xInit";
    public static final String COLUMN_YFIM = "yFim";
    public static final String COLUMN_YINIT = "yInit";
    public static String CREATE_TABLE = "CREATE TABLE tb_ponto(id INTEGER PRIMARY KEY,id_projeto INTEGER NOT NULL,xInit REAL NOT NULL,yInit REAL NOT NULL,xFim REAL NOT NULL,yFim REAL NOT NULL,estatico INTEGER NOT NULL, FOREIGN KEY(id_projeto) REFERENCES tb_projeto(id))";
    public static final Creator CREATOR = new C02561();
    public static String DROP_TABLE = "DROP TABLE IF EXISTS tb_ponto";
    private static float RAIO_PONTO_INIT = 4.8f;
    private static float STROKE_SETA_INIT = 3.2f;
    public static final String TABLE = "tb_ponto";
    private boolean estatico = false;

    private long f148id;
    private Paint paintPoint = new Paint(1);
    private Paint paintSeta = new Paint(1);
    private boolean selecionado = false;
    private float xAtual;
    private float xFim;
    private float xInit;
    private float yAtual;
    private float yFim;
    private float yInit;

    static class C02561 implements Creator {

        public Ponto createFromParcel(Parcel parcel) {
            return new Ponto(parcel);
        }

        public Ponto[] newArray(int i) {
            return new Ponto[i];
        }
    }

    public int describeContents() {
        return 0;
    }


    public Ponto clone() throws CloneNotSupportedException {
        return (Ponto) super.clone();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("P");
        sb.append(this.f148id);
        sb.append(": (");
        sb.append(getXInit());
        sb.append(",");
        sb.append(getYInit());
        sb.append(")");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        Ponto ponto = (Ponto) obj;
        if (!super.equals(obj) && !(this.xInit == ponto.xInit && this.yInit == ponto.yInit)) {
            long j = this.f148id;
            if (j == 0 || j != ponto.f148id) {
                return false;
            }
        }
        return true;
    }

    public Ponto getCopia(float f) {
        Ponto ponto = new Ponto(this.xInit * f, this.yInit * f, this.xFim * f, this.yFim * f);
        ponto.f148id = this.f148id;
        ponto.estatico = this.estatico;
        return ponto;
    }

    public Ponto(float f, float f2, boolean z) {
        this.xInit = f;
        this.yInit = f2;
        this.xFim = f;
        this.yFim = f2;
        this.xAtual = f;
        this.yAtual = f2;
        this.estatico = z;
        inicializar();
    }

    public Ponto(float f, float f2, float f3, float f4) {
        this.xInit = f;
        this.yInit = f2;
        this.xFim = f3;
        this.yFim = f4;
        this.xAtual = f;
        this.yAtual = f2;
        this.estatico = false;
        inicializar();
    }

    private void inicializar() {
        this.paintPoint.setAntiAlias(true);
        this.paintPoint.setFilterBitmap(true);
        this.paintPoint.setStyle(Style.FILL);
        this.paintSeta.setAntiAlias(true);
        this.paintSeta.setFilterBitmap(true);
        this.paintSeta.setStyle(Style.STROKE);
        this.paintSeta.setColor(InputDeviceCompat.SOURCE_ANY);
        this.paintSeta.setStrokeJoin(Join.ROUND);
        this.paintSeta.setStrokeCap(Cap.ROUND);
        this.paintSeta.setStrokeWidth(2.0f);
    }

    public float coeficienteAngular(Ponto ponto) {
        return (ponto.getYInit() - this.yInit) / (ponto.getXInit() - this.xInit);
    }

    public boolean naCircunferencia(Ponto ponto, Ponto ponto2) {
        Ponto mediana = ponto.mediana(ponto2);
        return naCircunferencia(mediana, mediana.distanciaPara(ponto));
    }

    public boolean naCircunferencia(Ponto ponto, double d) {
        return ponto.distanciaPara(this) <= d;
    }

    public double distanciaPara(Ponto ponto) {
        float xInit2 = ponto.getXInit() - getXInit();
        float yInit2 = ponto.getYInit() - getYInit();
        return Math.sqrt((double) ((xInit2 * xInit2) + (yInit2 * yInit2)));
    }

    public Ponto mediana(Ponto ponto) {
        return new Ponto((ponto.getXInit() + getXInit()) / 2.0f, (ponto.getYInit() + getYInit()) / 2.0f, true);
    }

    public Ponto sub(Ponto ponto) {
        return new Ponto(this.xInit - ponto.xInit, this.yInit - ponto.yInit, true);
    }

    public Ponto add(Ponto ponto) {
        return new Ponto(this.xInit + ponto.xInit, this.yInit + ponto.yInit, true);
    }

    public Ponto mult(double d) {
        double d2 = (double) this.xInit;
        Double.isNaN(d2);
        float f = (float) (d2 * d);
        double d3 = (double) this.yInit;
        Double.isNaN(d3);
        return new Ponto(f, (float) (d3 * d), true);
    }

    public double mag() {
        float f = this.xInit;
        float f2 = f * f;
        float f3 = this.yInit;
        return Math.sqrt((double) (f2 + (f3 * f3)));
    }

    public double dot(Ponto ponto) {
        return (double) ((this.xInit * ponto.xInit) + (this.yInit * ponto.yInit));
    }

    public double cross(Ponto ponto) {
        return (double) ((this.yInit * ponto.xInit) - (this.xInit * ponto.yInit));
    }

    public void desenharPontoMovimento(Canvas canvas) {
        if (!this.estatico) {
            this.paintPoint.setColor(-16776961);
            float xAtual2 = getXAtual();
            float yAtual2 = getYAtual();
            float f = RAIO_PONTO_INIT;
            canvas.drawCircle(xAtual2, yAtual2, Math.max(f, f / 3.0f), this.paintPoint);
        }
    }

    public void desenharPonto(Canvas canvas, int i, float f) {
        this.paintPoint.setAlpha(i);
        if (this.selecionado) {
            this.paintPoint.setColor(-7829368);
        } else {
            this.paintPoint.setColor(isEstatico() ? -65536 : -16776961);
        }
        float f2 = RAIO_PONTO_INIT;
        float max = Math.max(f2 / f, f2 / 3.0f);
        if (this.selecionado) {
            max *= 2.0f;
        }
        canvas.drawCircle(getXInit(), getYInit(), max, this.paintPoint);
    }

    public void desenharSeta(Canvas canvas, int i, float f) {
        this.paintSeta.setAlpha(i);
        if (this.selecionado) {
            this.paintSeta.setColor(-7829368);
            Paint paint = this.paintSeta;
            float f2 = STROKE_SETA_INIT;
            paint.setStrokeWidth(Math.max(f2 / f, f2 / 2.0f));
        } else {
            this.paintSeta.setColor(isEstatico() ? -65536 : InputDeviceCompat.SOURCE_ANY);
            Paint paint2 = this.paintSeta;
            float f3 = STROKE_SETA_INIT;
            paint2.setStrokeWidth(Math.max((f3 / 2.0f) / f, f3 / 4.0f));
        }
        float xInit2 = getXInit();
        float xFim2 = getXFim();
        float yInit2 = getYInit();
        float yFim2 = getYFim();
        canvas.drawLine(xInit2, yInit2, xFim2, yFim2, this.paintSeta);
        float f4 = xFim2 - xInit2;
        float f5 = yFim2 - yInit2;
        double sqrt = Math.sqrt((double) ((f4 * f4) + (f5 * f5)));
        double max = (double) Math.max(8.0f / f, 4.0f);
        Double.isNaN(max);
        float f6 = (float) (1.0d / (sqrt / max));
        float f7 = 1.0f - f6;
        float f8 = f7 * f4;
        float f9 = f6 * f5;
        float f10 = f8 + f9 + xInit2;
        float f11 = f7 * f5;
        float f12 = f6 * f4;
        float f13 = (f11 - f12) + yInit2;
        float f14 = xInit2 + (f8 - f9);
        float f15 = yInit2 + f11 + f12;
        Path path = new Path();
        path.setFillType(FillType.EVEN_ODD);
        path.moveTo(f10, f13);
        path.lineTo(xFim2, yFim2);
        path.lineTo(f14, f15);
        this.paintSeta.setStyle(Style.FILL);
        path.close();
        canvas.drawPath(path, this.paintSeta);
    }

    public void setPosicaoAtualAnim(float f, float f2) {
        this.xAtual = f;
        this.yAtual = f2;
    }

    public long getId() {
        return this.f148id;
    }

    public void setId(long j) {
        this.f148id = j;
    }

    public float getXAtual() {
        return this.xAtual;
    }

    public float getYAtual() {
        return this.yAtual;
    }

    public float getXInit() {
        return this.xInit;
    }

    public float getYInit() {
        return this.yInit;
    }

    public float getXFim() {
        return this.xFim;
    }

    public float getYFim() {
        return this.yFim;
    }

    public boolean isEstatico() {
        return this.estatico;
    }

    public void setSelecionado(boolean z) {
        this.selecionado = z;
    }

    public boolean isSelecionado() {
        return this.selecionado;
    }

    public void setOrigem(float f, float f2) {
        this.xInit = f;
        this.yInit = f2;
    }

    public void setDestino(float f, float f2) {
        this.xFim = f;
        this.yFim = f2;
        this.estatico = false;
    }

    public Ponto(Parcel parcel) {
        boolean z = false;
        this.f148id = parcel.readLong();
        this.xInit = parcel.readFloat();
        this.yInit = parcel.readFloat();
        if (parcel.readInt() == 1) {
            z = true;
        }
        this.estatico = z;
        this.xFim = parcel.readFloat();
        this.yFim = parcel.readFloat();
        inicializar();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.f148id);
        parcel.writeFloat(this.xInit);
        parcel.writeFloat(this.yInit);
        parcel.writeInt(this.estatico ? 1 : 0);
        parcel.writeFloat(this.xFim);
        parcel.writeFloat(this.yFim);
    }
}
