package com.devsectech.photomo.customView.controllersapp;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.util.Log;
import com.devsectech.photomo.ApplicationClass;
import com.devsectech.photomo.callback.OnProgressReceiver;
import com.devsectech.photomo.callback.onVideoSaveListener;
import com.devsectech.photomo.customView.Delaunay;
import com.devsectech.photomo.customView.FFMPEGSaver;
import com.devsectech.photomo.customView.ShareClass;
import com.devsectech.photomo.customView.beans.Ponto;
import com.devsectech.photomo.customView.beans.Projeto;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class VideoSaver extends AsyncTask<Object, Integer, String> {
    public boolean comLogo = true;
    public Activity context;
    public int fps = 30;
    public int miHeight;
    public int miWidth;
    public String path;
    public Projeto projeto;
    public int resolucao;
    public boolean salvando = false;
    public onVideoSaveListener saveListener;
    public int tempoAnimacao = 2000;
    public int totalFrames;
    public File videoFile;
    public ApplicationClass application;
    public String videoPath;
    String videoFileName;
    private String pathTxtFile = "";
    private float toatalSecond = 6;

    public VideoSaver(Activity context2, Projeto projeto2, int i, int i2) {
        context = context2;
        projeto = projeto2;
        miWidth = i;
        miHeight = i2;
        application = new ApplicationClass();
        resolucao = projeto2.getWidth() > projeto2.getHeight() ? projeto2.getWidth() : projeto2.getHeight();
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Matrix matrix = new Matrix();
        matrix.setScale(((float) i) / ((float) bitmap.getWidth()), ((float) i2) / ((float) bitmap.getHeight()));
        canvas.drawBitmap(bitmap, matrix, new Paint());
        return createBitmap;
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    private String saveToInternalStorage(String text) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("localStore", Context.MODE_PRIVATE);
        File file = new File(directory, "video.txt");
        Log.e("path: ", file.toString());
        try {

            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(text);
            buf.newLine();
            buf.close();
            return file.getAbsolutePath();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Log.e("path: Error: ", e.getMessage());
            return "";
        }

    }

    public void appendVideoLog(String text) {
        pathTxtFile = saveToInternalStorage(text);
    }

    public boolean isSalvando() {
        return salvando;
    }

    public void onPreExecute() {
        super.onPreExecute();
        totalFrames = Math.round(((float) (tempoAnimacao * fps)) / 1000.0f);
        onVideoSaveListener videoSaveListener = saveListener;
        if (videoSaveListener != null) {
            videoSaveListener.onStartSave(totalFrames);
        }
        salvando = true;
    }

    public String doInBackground(Object... objArr) {
        Paint paint;
        Object[] objArr2 = objArr;
        path = (String) objArr2[0];
        Bitmap bitmap = objArr2.length > 1 ? (Bitmap) objArr2[1] : null;
        videoFile = new File(path);
        if (videoFile.exists()) {
            videoFile.delete();
        }
        // try {
        int round = projeto.getWidth() > projeto.getHeight() ? resolucao : Math.round((((float) projeto.getWidth()) / ((float) projeto.getHeight())) * ((float) resolucao));
        if (round % 2 != 0) {
            round++;
        }
        int round2 = projeto.getWidth() < projeto.getHeight() ? resolucao : Math.round((((float) projeto.getHeight()) / ((float) projeto.getWidth())) * ((float) resolucao));
        if (round2 % 2 != 0) {
            round2++;
        }
        int i = round2;
        float height = ((float) bitmap.getHeight()) / ((float) bitmap.getWidth());
        float f = (round > i ? (float) round : (float) i) * 0.15f;
        float f2 = 80.0f;
        if (f >= 80.0f) {
            f2 = f;
        }
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(f2), Math.round(height * f2), true);
        Paint paint2 = new Paint();
        paint2.setFilterBitmap(true);
        Paint paint3 = new Paint(1);
        paint3.setFilterBitmap(true);
        paint3.setStyle(Style.FILL);
        paint3.setColor(-1);
        LinearGradient linearGradient = new LinearGradient(0.0f, (float) createScaledBitmap.getHeight(), (float) (round / 2), (float) createScaledBitmap.getHeight(), -1, Color.argb(0, 255, 255, 255), TileMode.CLAMP);
        paint3.setShader(linearGradient);
        Bitmap createScaledBitmap2 = Bitmap.createScaledBitmap(projeto.getImagem(), round, i, true);
        float f3 = (float) round;
        float width = f3 / ((float) projeto.getWidth());
        Bitmap createScaledBitmap3 = projeto.getMascara() != null ? Bitmap.createScaledBitmap(projeto.getMascara(), Math.round(((float) projeto.getMascara().getWidth()) * width), Math.round(((float) projeto.getMascara().getHeight()) * width), true) : null;
        Delaunay delaunay = new Delaunay(Utils.getImagemSemMascara(createScaledBitmap2, createScaledBitmap3, Config.ARGB_8888));
        for (Ponto copia : projeto.getListaPontos()) {
            delaunay.addPonto(copia.getCopia(width));
        }
        delaunay.setImagemDelaunay(Utils.getImagemSemMascara(createScaledBitmap2, createScaledBitmap3, Config.ARGB_8888));
        Bitmap createBitmap = Bitmap.createBitmap(delaunay.getTriangulosEstaticos(Config.ARGB_8888));
        new Canvas(createBitmap).drawBitmap(Utils.getMascaraDaImagem(createScaledBitmap2, createScaledBitmap3, Config.ARGB_8888), 0.0f, 0.0f, null);
        Paint paint4 = new Paint(1);
        paint4.setFilterBitmap(true);
        paint4.setAntiAlias(true);
        Bitmap createBitmap2 = Bitmap.createBitmap(round, i, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap2);
        canvas.drawBitmap(createScaledBitmap2, 0.0f, 0.0f, null);
        int i2 = 0;
        int i3 = 0;
        int immimi = 0;

        toatalSecond = totalFrames / fps;
        while (i2 < totalFrames) {
            int i4 = i2;
            Bitmap bitmap2 = createBitmap2;
            float f4 = (((float) i2) * 1000.0f) / ((float) fps);
            Bitmap frameEmMovimento = FrameController.getInstance().getFrameEmMovimento(delaunay, tempoAnimacao, fps, f4, Config.ARGB_8888);
            float f5 = f4;
            float f6 = ((((float) tempoAnimacao) / 2.0f) + f5) % ((float) tempoAnimacao);
            Paint paint5 = paint2;
            float f7 = f5;
            Bitmap frameEmMovimento2 = FrameController.getInstance().getFrameEmMovimento(delaunay, tempoAnimacao, fps, f6, Config.ARGB_8888);
            paint4.setAlpha(FrameController.getInstance().getAlpha(tempoAnimacao, f7));
            canvas.drawBitmap(frameEmMovimento, 0.0f, 0.0f, null);
            paint4.setAlpha(FrameController.getInstance().getAlpha(tempoAnimacao, f6));
            canvas.drawBitmap(frameEmMovimento2, 0.0f, 0.0f, paint4);
            canvas.drawBitmap(createBitmap, 0.0f, 0.0f, null);
            if (!ShareClass.foBitmapList.isEmpty() && i3 <= 59) {
                canvas.drawBitmap(scaleBitmap((Bitmap) ShareClass.foBitmapList.get(i3), miWidth, miHeight), 0.0f, 0.0f, null);
                i3 = i3 == 59 ? 0 : i3 + 1;
            }
            if (comLogo) {
                float f8 = 0.1f * f2;
                canvas.drawRect(0.0f, f8 - 5.0f, f3, ((float) createScaledBitmap.getHeight()) + f8 + 5.0f, paint4);
                paint = paint5;
                canvas.drawBitmap(createScaledBitmap, f8, f8, paint);
            } else {
                paint = paint5;
            }
            Bitmap bitmap3 = bitmap2;

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            immimi = immimi + 1;
            String fname = "image-" + immimi + ".jpg";


            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("localStore", Context.MODE_PRIVATE);
            File file = new File(directory, fname);
            Log.d("path Image: ", file.toString());
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
                Log.e("Error: ", "File Creation: " + fname + " " + e.getMessage());
            }

            publishProgress(new Integer[]{Integer.valueOf(i4), Integer.valueOf(totalFrames)});
            paint2 = paint;
            i2 = i4 + 1;
            createBitmap2 = bitmap3;
        }

        Log.e("createVideo", "video create start");


        for (int asd = 0; asd < totalFrames; asd++) {
            String fname = "image-" + (asd + 1) + ".jpg";
            Log.i("jj", "createVideo: " + String.format("file '%s'", fname));
            appendVideoLog(String.format("file '%s'", fname));
        }


        videoFileName = "video_" + System.currentTimeMillis() + ".mp4";

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("files", Context.MODE_PRIVATE);
        File file = new File(directory, videoFileName);
        Log.d("path Image: ", file.toString());

        videoPath = file.getAbsolutePath();


        String[] inputCode;
        inputCode = new String[]{
                "-safe", "0", "-f", "concat", "-i", pathTxtFile,
                "-c:v", "mpeg4", videoPath};
        Log.e("TAG", "ffmpeg Arr : " + Arrays.toString(inputCode));


        System.gc();
//        return "-safe -0 -f concat -i " + pathTxtFile + " -c:v mpeg4 -t " + toatalSecond + " -r 30 " + videoPath;
        return "-safe -0 -f concat -i " + pathTxtFile + " -t " + toatalSecond + " -r 30 " + videoPath;

    }

    public void onCancelled() {
        super.onCancelled();
        salvando = true;
        onVideoSaveListener videoSaveListener = saveListener;
        if (videoSaveListener != null) {
            videoSaveListener.onError("Cancelado");
        }
    }

    public void onPostExecute(String inputCode) {
        super.onPostExecute(inputCode);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FFMPEGSaver ffmpegSaver = new FFMPEGSaver(context, videoPath, videoFileName);
                ffmpegSaver.setSaveListener(saveListener);
                ffmpegSaver.execute(inputCode);
            }
        }).start();
    }

    public void setComLogo(boolean z) {
        comLogo = z;
    }

    public void setContext(Activity context2) {
        context = context2;
    }

    public void setResolucao(int i) {
        resolucao = i;
    }

    public void setSaveListener(onVideoSaveListener videoSaveListener) {
        saveListener = videoSaveListener;
    }

    public void setTempoAnimacao(int i) {
        tempoAnimacao = i;
    }

    public void onProgressUpdate(Integer... numArr) {
        super.onProgressUpdate(numArr);
        OnProgressReceiver receiver = application.getOnProgressReceiver();
        if (receiver != null) {
            receiver.onImageProgressFrameUpdate(Math.round((((float) (numArr[0].intValue() + 1)) / ((float) totalFrames)) * 100.0f));
            Log.i("jj", "run: updateimageprogress " + Math.round((((float) (numArr[0].intValue() + 1)) / ((float) totalFrames)) * 100.0f));
        }
        onVideoSaveListener videoSaveListener = saveListener;
        if (videoSaveListener != null) {
            videoSaveListener.onSaving(numArr[0].intValue() + 1);
            StringBuilder sb = new StringBuilder();
            sb.append("SALVANDO FRAME ");
            sb.append(numArr[0].intValue() + 1);
            sb.append(" DE ");
            sb.append(numArr[1]);
            Log.i("INFO", sb.toString());
        }
    }


}
