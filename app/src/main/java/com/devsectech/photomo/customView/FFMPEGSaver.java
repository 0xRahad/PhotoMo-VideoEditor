package com.devsectech.photomo.customView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.devsectech.photomo.ApplicationClass;
import com.devsectech.photomo.callback.onVideoSaveListener;
import com.devsectech.photomo.customView.controllersapp.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class FFMPEGSaver extends AsyncTask<String, Integer, Integer> {

    public Activity context;
    public String path;
    public onVideoSaveListener saveListener;
    public ApplicationClass application;
    String videoPath;
    String videoFileName;

    public FFMPEGSaver(Activity context2, String mVideoPath, String mVideoFileName) {
        context = context2;
        application = new ApplicationClass();
        videoPath = mVideoPath;
        videoFileName = mVideoFileName;
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

    public void onPreExecute() {
        super.onPreExecute();
    }

    public Integer doInBackground(String... command) {
        String ffmpegCommand = String.format("%s", command);
        Log.e("ffmpeg: ", String.format("Current log level is %s.", com.arthenica.mobileffmpeg.Config.getLogLevel()));
        Log.e("ffmpeg: ", "Testing FFmpeg COMMAND synchronously.");
        Log.e("ffmpeg: ", String.format("FFmpeg process started with arguments\n\'%s\'", ffmpegCommand));
        int result = FFmpeg.execute(ffmpegCommand);
        Log.e("ffmpeg: ", String.format("FFmpeg process exited with rc %d.", result));
        return result;
    }

    public void onCancelled() {
        super.onCancelled();
        onVideoSaveListener videoSaveListener = saveListener;
        if (videoSaveListener != null) {
            videoSaveListener.onError("Cancelado");
        }
    }

    public void onPostExecute(Integer result) {
        super.onPostExecute(result);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (result != 0) {
                                Log.e("ffmpeg: ", "Command failed. Please check output for the details." + result);
                            } else {
                                Log.e("ffmpeg: ", "Success");

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    ContentValues valuesvideos;
                                    valuesvideos = new ContentValues();
                                    valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + File.separator + "PhotoMotion");
                                    valuesvideos.put(MediaStore.Video.Media.TITLE, videoFileName);
                                    valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, videoFileName);
                                    valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                                    valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                                    valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
                                    valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 1);
                                    ContentResolver resolver = context.getContentResolver();
                                    Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                                    Uri uriSavedVideo = resolver.insert(collection, valuesvideos);

                                    try {
                                        OutputStream out = resolver.openOutputStream(uriSavedVideo);
                                        ContextWrapper cw = new ContextWrapper(context);
                                        File directory = cw.getDir("files", Context.MODE_PRIVATE);
                                        File file = new File(directory, videoFileName);
                                        FileInputStream in = new FileInputStream(file);
                                        byte[] buf = new byte[8192];
                                        int len;
                                        while ((len = in.read(buf)) > 0) {
                                            out.write(buf, 0, len);
                                        }
                                        out.close();
                                        in.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    valuesvideos.clear();
                                    valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 0);
                                    context.getContentResolver().update(uriSavedVideo, valuesvideos, null, null);
                                    onVideoSaveListener videoSaveListener = saveListener;

                                    if (videoSaveListener != null) {
                                        File myDir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_MOVIES + File.separator + "PhotoMotion");
                                        File file = new File(myDir, videoFileName);
                                        Log.d("path Image: ", file.toString());

                                        videoPath = file.getAbsolutePath();
                                        Utils.scannFile(context, file);
                                        videoSaveListener.onSaved(videoPath);
                                    }
                                } else {
                                    ContextWrapper cw = new ContextWrapper(context);
                                    File directory = cw.getDir("files", Context.MODE_PRIVATE);
                                    File sourceLocation = new File(directory, videoFileName);
                                    File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "PhotoMotion");
                                    if (!myDir.exists() && !myDir.mkdirs()) {
                                        Log.i("INFO", "Directory not created");
                                    }
                                    File targetLocation = new File(myDir, videoFileName);
                                    try {

                                        // moving the file to another directory

                                        // make sure the target file exists

                                        if (sourceLocation.exists()) {

                                            InputStream in = new FileInputStream(sourceLocation);
                                            OutputStream out = new FileOutputStream(targetLocation);

                                            // Copy the bits from instream to outstream
                                            byte[] buf = new byte[8192];
                                            int len;

                                            while ((len = in.read(buf)) > 0) {
                                                out.write(buf, 0, len);
                                            }

                                            in.close();
                                            out.close();

                                            Log.e("Error copy", "Copy file successful.");

                                            onVideoSaveListener videoSaveListener = saveListener;

                                            if (videoSaveListener != null) {
                                                videoPath = targetLocation.getAbsolutePath();
                                                Utils.scannFile(context, targetLocation);
                                                videoSaveListener.onSaved(videoPath);
                                            }
                                        } else {
                                            Log.e("Error copy", "Copy file failed. Source file missing.");
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        Log.e("Error Null: ", e.getMessage());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("Error Exception: ", e.getMessage());
                                    }

                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage());
                }

            }
        }.start();
    }

    public void setContext(Activity context2) {
        context = context2;
    }

    public void setSaveListener(onVideoSaveListener videoSaveListener) {
        saveListener = videoSaveListener;
    }

    public void onProgressUpdate(Integer... numArr) {
        super.onProgressUpdate(numArr);
    }

}
