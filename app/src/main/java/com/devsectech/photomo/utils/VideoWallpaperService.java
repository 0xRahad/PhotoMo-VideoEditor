package com.devsectech.photomo.utils;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.core.content.FileProvider;

import java.io.File;

public class VideoWallpaperService extends WallpaperService {

    String path;

    @Override
    public Engine onCreateEngine() {
        return new LoveEngine();
    }

    @Override
    public void onCreate() {
        path = SharedPref.getInstance().getString("live_wall_path", "");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private class LoveEngine extends Engine {
        private MediaPlayer mp;

        public LoveEngine() {
            super();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                if (mp != null)
                    mp.start();
            } else {
                if (mp != null)
                    mp.pause();
            }
            Log.e("VideoWallpaperService", "onVisibilityChanged:");
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            try {
                Log.e("VideoWallpaperService", "onSurfaceCreated:" + mp);
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                path = SharedPref.getInstance().getString("live_wall_path", "");
                if (!TextUtils.isEmpty(path)) {
                    Uri videoUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        videoUri = FileProvider.getUriForFile(VideoWallpaperService.this, getPackageName() + ".provider", new File(path));
                    } else {
                        videoUri = Uri.parse(path);
                    }
                    mp = MediaPlayer.create(getApplicationContext(), videoUri);
                    mp.setDisplay(new VideoSurfaceHolder(holder));
                    mp.setLooping(true);
                    mp.setVolume(0, 0);
                    mp.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e("VideoWallpaperService", "onSurfaceChanged");
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (mp != null)
                mp.pause();
            Log.e("VideoWallpaperService", "onSurfaceDestroyed");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.e("VideoWallpaperService", "onDestroy");
        }
    }
}
