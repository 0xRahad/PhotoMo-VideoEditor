package com.devsectech.photomo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.util.Log;
import com.devsectech.photomo.customView.beans.Ponto;
import com.devsectech.photomo.customView.beans.Projeto;
import com.devsectech.photomo.customView.controllersapp.Utils;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MotionInPhoto";
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHandler db;

    public static DatabaseHandler getInstance(Context context) {
        if (db == null) {
            db = new DatabaseHandler(context);
        }
        return db;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Log.i("INFO", "CRIANDO BASE DE DADOS ....");
        sQLiteDatabase.execSQL(Projeto.CREATE_TABLE);
        sQLiteDatabase.execSQL(Ponto.CREATE_TABLE);
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        onUpgrade(sQLiteDatabase, i, i2);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        StringBuilder sb = new StringBuilder();
        sb.append("ATUALIZANDO BASE DE DADOS DA VERSÃO ");
        sb.append(i);
        sb.append(" PARA A ");
        sb.append(i2);
        Log.i("INFO", sb.toString());
        sQLiteDatabase.execSQL(Ponto.DROP_TABLE);
        sQLiteDatabase.execSQL(Projeto.DROP_TABLE);
        onCreate(sQLiteDatabase);
    }

    public Projeto getProjeto(long j) {
        Projeto projeto;
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor query = readableDatabase.query(Projeto.TABLE, new String[]{"id", Projeto.COLUMN_DESCRICAO, Projeto.COLUMN_MASCARA, Projeto.COLUMN_URI, Projeto.COLUMN_RESOLUCAO, Projeto.COLUMN_TEMPO}, "id=?", new String[]{String.valueOf(j)}, null, null, "id", null);
        if (query != null ? query.moveToFirst() : false) {
            projeto = new Projeto((long) Integer.parseInt(query.getString(0)), query.getString(1), null, Uri.parse(query.getString(3)), query.getInt(4), query.getInt(5));
            if (query.getBlob(2) != null) {
                projeto.setMascara(Utils.bytesToBitmap(query.getBlob(2)).copy(Config.ARGB_8888, true));
            }
            projeto.carregarPontos(this);
        } else {
            projeto = null;
        }
        query.close();
        readableDatabase.close();
        return projeto;
    }

    public Projeto getUltimoProjeto() {
        Projeto projeto;
        SQLiteDatabase readableDatabase = getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = readableDatabase;
        Cursor query = sQLiteDatabase.query(Projeto.TABLE, new String[]{"id", Projeto.COLUMN_DESCRICAO, Projeto.COLUMN_MASCARA, Projeto.COLUMN_URI, Projeto.COLUMN_RESOLUCAO, Projeto.COLUMN_TEMPO}, "id=(SELECT MAX(id)  FROM tb_projeto)", null, null, null, null, null);
        if (query.moveToFirst()) {
            projeto = new Projeto((long) Integer.parseInt(query.getString(0)), query.getString(1), null, Uri.parse(query.getString(3)), query.getInt(4), query.getInt(5));
            projeto.setResolucaoSave(query.getInt(4));
            projeto.setTempoSave(query.getInt(5));
            if (query.getBlob(2) != null) {
                projeto.setMascara(Utils.bytesToBitmap(query.getBlob(2)).copy(Config.ARGB_8888, true));
            }
            projeto.carregarPontos(this);
        } else {
            projeto = null;
        }
        query.close();
        readableDatabase.close();
        return projeto;
    }


}
