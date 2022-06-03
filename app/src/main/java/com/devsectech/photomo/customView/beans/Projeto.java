package com.devsectech.photomo.customView.beans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.devsectech.photomo.activity.SavingActivity;
import com.devsectech.photomo.customView.controllersapp.ToolsController;
import com.devsectech.photomo.customView.controllersapp.Utils;
import com.devsectech.photomo.utils.DatabaseHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Projeto implements Parcelable {
    public static final String COLUMN_DESCRICAO = "descricao";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMAGEM = "imagem";
    public static final String COLUMN_MASCARA = "mascara";
    public static final String COLUMN_RESOLUCAO = "resolucao";
    public static final String COLUMN_TEMPO = "tempo";
    public static final String COLUMN_URI = "uri";
    public static String CREATE_TABLE = "CREATE TABLE tb_projeto(id INTEGER PRIMARY KEY,descricao TEXT,imagem BLOB,mascara BLOB,uri TEXT,resolucao INTEGER,tempo INTEGER)";
    public static final Creator CREATOR = new AppCreator();
    public static String DROP_TABLE = "DROP TABLE IF EXISTS tb_projeto";
    public static final String TABLE = "tb_projeto";
    public static final int TAMANHO_APRESENTACAO = 800;
    public static final int TEMPO_SAVE_INIT = 6000;
    private long id;
    private Bitmap imagem;
    private List<Ponto> listaPontos = new CopyOnWriteArrayList();
    private List<Ponto> listaPontosApresentacao = new CopyOnWriteArrayList();
    private Bitmap mascara;
    private float proporcaoApresentacao;
    private Rect rect;
    private int resolucaoSave;
    private int tempoSave;
    private String titulo;
    private Uri uri;

    static class AppCreator implements Creator {
        AppCreator() {
        }

        public Projeto createFromParcel(Parcel parcel) {
            return new Projeto(parcel);
        }

        public Projeto[] newArray(int i) {
            return new Projeto[i];
        }
    }

    public int describeContents() {
        return 0;
    }

    public Projeto(String str, Bitmap bitmap, Uri uri2) {
        this.titulo = str;
        setImagem(bitmap);
        this.uri = uri2;
        setTempoSave(6000);
        setResolucaoSave(Math.max(bitmap.getHeight(), bitmap.getWidth()));
    }

    public Projeto(long j, String str, Bitmap bitmap, Uri uri2, int i, int i2) {
        this.id = j;
        this.titulo = str;
        if (bitmap != null) {
            setImagem(bitmap);
        }
        this.uri = uri2;
        setResolucaoSave(i);
        setTempoSave(i2);
    }

    public void refreshTempoResolucao(DatabaseHandler databaseHandler) {
        SQLiteDatabase readableDatabase = databaseHandler.getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = readableDatabase;
        Cursor query = sQLiteDatabase.query(TABLE, new String[]{COLUMN_RESOLUCAO, COLUMN_TEMPO}, "id=?", new String[]{String.valueOf(this.id)}, null, null, "id", null);
        if (query != null) {
            if (query.moveToFirst()) {
                setResolucaoSave(query.getInt(0));
                setTempoSave(query.getInt(1));
            }
            query.close();
            readableDatabase.close();
        }
    }

    public boolean reloadBitmapUri(Context context, DatabaseHandler databaseHandler) {
        boolean z;
        Cursor query = context.getContentResolver().query(this.uri, new String[]{"_data"}, null, null, null);
        if (query != null) {
            if (query.moveToFirst() && new File(query.getString(0)).exists()) {
                z = true;
            } else {
                z = false;
            }
            query.close();
        } else {
            z = false;
        }
        if (z) {
            try {
                Options options = new Options();
                options.inJustDecodeBounds = false;
                InputStream openInputStream = context.getContentResolver().openInputStream(this.uri);
                setImagem(BitmapFactory.decodeStream(openInputStream, null, options));
                openInputStream.close();
                File file = new File(Utils.getImgPath(context, this.uri));
                boolean delete = file.delete();
                if (!delete) {
                    delete = file.getCanonicalFile().delete();
                    if (!delete) {
                        delete = context.getApplicationContext().deleteFile(file.getName());
                        if (!delete) {
                            delete = context.getContentResolver().delete(this.uri, null, null) > 0;
                        }
                    }
                }
                if (delete) {
                    this.uri = Utils.writeImageAndGetPathUri(context, getImagem(), this.titulo);
                    updateProjeto(databaseHandler);
                }
                Utils.scannFiles(context);
                return true;
            } catch (Exception unused) {
                return false;
            }
        } else {
            try {
                setImagem(BitmapFactory.decodeFileDescriptor(ParcelFileDescriptor.open(new File(this.uri.getPath()), 268435456).getFileDescriptor()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public float getProporcaoApresentacao() {
        return this.proporcaoApresentacao;
    }

    public Bitmap getMascara() {
        return this.mascara;
    }

    public void setMascara(Bitmap bitmap) {
        this.mascara = bitmap;
    }

    public void setImagem(Bitmap bitmap) {
        this.imagem = bitmap;
        if (bitmap != null) {
            this.proporcaoApresentacao = ((float) (bitmap.getWidth() > bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight())) / 800.0f;
            Rect rect2 = this.rect;
            if (rect2 == null || rect2.bottom == 0 || this.rect.right == 0) {
                this.rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            }
        }
    }

    public int getTempoSave() {
        return this.tempoSave;
    }

    public void setTempoSave(int i) {
        if (i < 2000) {
            this.tempoSave = ToolsController.MIN_TEMPO_PREVIEW;
        } else if (i > 10000) {
            this.tempoSave = 10000;
        } else {
            this.tempoSave = i;
        }
    }

    public Rect getRect() {
        return this.rect;
    }

    public void setRect(Rect rect2) {
        this.rect = rect2;
    }

    public int getResolucaoSave() {
        return this.resolucaoSave;
    }

    public void setResolucaoSave(int i) {
        if (i < SavingActivity.MIN_RESOLUTION_SAVE) {
            this.resolucaoSave = SavingActivity.MIN_RESOLUTION_SAVE;
        } else if (i > SavingActivity.MAX_RESOLUTION_SAVE) {
            this.resolucaoSave = SavingActivity.MAX_RESOLUTION_SAVE;
        } else {
            this.resolucaoSave = i;
        }
    }

    public Bitmap getImagem() {
        return this.imagem;
    }

    public int getWidth() {
        Bitmap bitmap = this.imagem;
        if (bitmap != null) {
            return bitmap.getWidth();
        }
        return 0;
    }

    public int getHeight() {
        Bitmap bitmap = this.imagem;
        if (bitmap != null) {
            return bitmap.getHeight();
        }
        return 0;
    }

    public byte[] getMascaraBytes() {
        Bitmap bitmap = this.mascara;
        if (bitmap != null) {
            return Utils.bitmapToBytes(bitmap, 0);
        }
        return null;
    }

    public List<Ponto> getListaPontos() {
        return this.listaPontos;
    }


    public String getTitulo() {
        return this.titulo;
    }

    public void setUri(Uri uri2) {
        this.uri = uri2;
    }

    public Uri getUri() {
        return this.uri;
    }

    public synchronized int updateProjeto(DatabaseHandler databaseHandler) {
        int update;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DESCRICAO, getTitulo());
        contentValues.put(COLUMN_URI, getUri().toString());
        contentValues.put(COLUMN_MASCARA, getMascaraBytes());
        contentValues.put(COLUMN_RESOLUCAO, Integer.valueOf(getResolucaoSave()));
        contentValues.put(COLUMN_TEMPO, Integer.valueOf(getTempoSave()));
        SQLiteDatabase writableDatabase = databaseHandler.getWritableDatabase();
        update = writableDatabase.update(TABLE, contentValues, "id = ?", new String[]{String.valueOf(getId())});
        writableDatabase.close();
        return update;
    }

    public synchronized void deleteProjeto(DatabaseHandler databaseHandler) {
        deletePontos(databaseHandler, this.listaPontos);
        SQLiteDatabase writableDatabase = databaseHandler.getWritableDatabase();
        writableDatabase.delete(TABLE, "id = ?", new String[]{String.valueOf(getId())});
        writableDatabase.close();
    }

    public synchronized void deletePontos(DatabaseHandler databaseHandler, List<Ponto> list) {
        if (list != null) {
            if (list.size() > 0) {
                this.listaPontos.removeAll(list);
                Long[] lArr = new Long[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    lArr[i] = Long.valueOf(((Ponto) list.get(i)).getId());
                }
                SQLiteDatabase writableDatabase = databaseHandler.getWritableDatabase();
                writableDatabase.execSQL(String.format("DELETE FROM tb_ponto WHERE id IN (%s);", new Object[]{TextUtils.join(", ", lArr)}));
                writableDatabase.close();
            }
        }
    }

    public synchronized void deletePonto(DatabaseHandler databaseHandler, Ponto ponto) {
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        copyOnWriteArrayList.add(ponto);
        deletePontos(databaseHandler, copyOnWriteArrayList);
    }

    public synchronized void addProjeto(DatabaseHandler databaseHandler) {
        SQLiteDatabase writableDatabase = databaseHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DESCRICAO, getTitulo());
        contentValues.put(COLUMN_URI, getUri().toString());
        contentValues.put(COLUMN_MASCARA, getMascaraBytes());
        contentValues.put(COLUMN_RESOLUCAO, Integer.valueOf(getResolucaoSave()));
        contentValues.put(COLUMN_TEMPO, Integer.valueOf(getTempoSave()));
        setId(writableDatabase.insert(TABLE, null, contentValues));
        writableDatabase.close();
    }


    public synchronized int updatePonto(DatabaseHandler databaseHandler, Ponto ponto) {
        int update;
        synchronized (this) {
            Ponto copia = ponto.getCopia(this.proporcaoApresentacao);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Ponto.COLUMN_XINIT, Float.valueOf(copia.getXInit()));
            contentValues.put(Ponto.COLUMN_YINIT, Float.valueOf(copia.getYInit()));
            contentValues.put(Ponto.COLUMN_XFIM, Float.valueOf(copia.getXFim()));
            contentValues.put(Ponto.COLUMN_YFIM, Float.valueOf(copia.getYFim()));
            contentValues.put(Ponto.COLUMN_ESTATICO, Integer.valueOf(copia.isEstatico() ? 1 : 0));
            contentValues.put(Ponto.COLUMN_ID_PROJETO, Long.valueOf(getId()));
            SQLiteDatabase writableDatabase = databaseHandler.getWritableDatabase();
            update = writableDatabase.update(Ponto.TABLE, contentValues, "id = ?", new String[]{String.valueOf(copia.getId())});
            StringBuilder sb = new StringBuilder();
            sb.append("UPD ");
            sb.append(copia);
            Log.i("INFOX", sb.toString());
            writableDatabase.close();
        }
        return update;
    }

    public synchronized void addPonto(DatabaseHandler databaseHandler, Ponto ponto) {
        Ponto copia = ponto.getCopia(this.proporcaoApresentacao);
        SQLiteDatabase writableDatabase = databaseHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Ponto.COLUMN_XINIT, Float.valueOf(copia.getXInit()));
        contentValues.put(Ponto.COLUMN_YINIT, Float.valueOf(copia.getYInit()));
        contentValues.put(Ponto.COLUMN_XFIM, Float.valueOf(copia.getXFim()));
        contentValues.put(Ponto.COLUMN_YFIM, Float.valueOf(copia.getYFim()));
        contentValues.put(Ponto.COLUMN_ESTATICO, Integer.valueOf(copia.isEstatico() ? 1 : 0));
        contentValues.put(Ponto.COLUMN_ID_PROJETO, Long.valueOf(getId()));
        ponto.setId(writableDatabase.insert(Ponto.TABLE, null, contentValues));
        copia.setId(ponto.getId());
        this.listaPontos.add(copia);
        StringBuilder sb = new StringBuilder();
        sb.append("ADD ");
        sb.append(copia);
        Log.i("INFOX", sb.toString());
        writableDatabase.close();
    }

    public synchronized void carregarPontos(DatabaseHandler databaseHandler) {
        Ponto ponto;
        this.listaPontos.clear();
        SQLiteDatabase readableDatabase = databaseHandler.getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = readableDatabase;
        Cursor query = sQLiteDatabase.query(Ponto.TABLE, new String[]{"id", Ponto.COLUMN_ESTATICO, Ponto.COLUMN_XINIT, Ponto.COLUMN_YINIT, Ponto.COLUMN_XFIM, Ponto.COLUMN_YFIM}, "id_projeto=?", new String[]{String.valueOf(this.id)}, null, null, null, null);
        if (query.moveToFirst()) {
            do {
                if (query.getInt(1) == 1) {
                    ponto = new Ponto(query.getFloat(2), query.getFloat(3), true);
                } else {
                    ponto = new Ponto(query.getFloat(2), query.getFloat(3), query.getFloat(4), query.getFloat(5));
                }
                ponto.setId(query.getLong(0));
                this.listaPontos.add(ponto);
            } while (query.moveToNext());
        }
        query.close();
        readableDatabase.close();
    }

    public Projeto(Parcel parcel) {
        this.id = parcel.readLong();
        this.titulo = parcel.readString();
        this.uri = Uri.parse(parcel.readString());
        this.resolucaoSave = parcel.readInt();
        this.tempoSave = parcel.readInt();
        parcel.readList(this.listaPontos, Ponto.class.getClassLoader());
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.titulo);
        parcel.writeString(this.uri.toString());
        parcel.writeInt(this.resolucaoSave);
        parcel.writeInt(this.tempoSave);
        parcel.writeList(this.listaPontos);
    }
}
