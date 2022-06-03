package com.devsectech.photomo.customView.controllersapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.devsectech.photomo.BuildConfig;
import com.devsectech.photomo.R;
import com.devsectech.photomo.activity.MotionEditActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static final Paint pSemMask = new Paint(1);
    public static final Paint paintMask = new Paint(1);

    public static class ScannFile implements OnScanCompletedListener {
        public void onScanCompleted(String str, Uri uri) {
            StringBuilder sb = new StringBuilder();
            sb.append("Scanned ");
            sb.append(str);
            sb.append(":");
            String sb2 = sb.toString();
            String str2 = "ExternalStorage";
            Log.i(str2, sb2);
            StringBuilder sb3 = new StringBuilder();
            sb3.append("-> uri=");
            sb3.append(uri);
            Log.i(str2, sb3.toString());
        }
    }


    static {
        paintMask.setAntiAlias(true);
        paintMask.setFilterBitmap(true);
        paintMask.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        pSemMask.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
        pSemMask.setFilterBitmap(true);
    }

    public static void BackButtonDialog(final Activity activity, final ToolsController toolsController, HistoryController historyController) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_exit);
        TextView textView =  dialog.findViewById(R.id.tv_yes);
        ((TextView) dialog.findViewById(R.id.tv_no)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toolsController.apagarSelecao();
                toolsController.setTipoFerramenta(0);
                ToolsController.init(activity);
                dialog.dismiss();
                activity.finish();
            }
        });
        dialog.show();
    }


    public static void abrirURL(String str, Activity activity) {
        activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, int i) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, i, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void bordaInfinita(Bitmap bitmap) {
        while (contemTransparente(bitmap)) {
            for (int i = 0; i < bitmap.getWidth(); i++) {
                for (int i2 = 0; i2 < bitmap.getHeight(); i2++) {
                    int pixel = bitmap.getPixel(i, i2);
                    if (pixel != Color.argb(255, 0, 0, 0)) {
                        int max = Math.max(i2 - 1, 0);
                        int min = Math.min(i + 1, bitmap.getWidth() - 1);
                        int min2 = Math.min(i2 + 1, bitmap.getHeight() - 1);
                        for (int max2 = Math.max(i - 1, 0); max2 <= min; max2++) {
                            for (int i3 = max; i3 <= min2; i3++) {
                                if (!(max2 == i && i3 == i2) && bitmap.getPixel(max2, i3) == Color.argb(255, 0, 0, 0)) {
                                    bitmap.setPixel(max2, i3, pixel);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static Bitmap bytesToBitmap(byte[] bArr) {
        return BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
    }

    public static int calculateInSampleSize(Options options, int i, int i2) {
        int i3 = options.outHeight;
        int i4 = options.outWidth;
        int i5 = 1;
        if (i3 > i2 || i4 > i) {
            int i6 = i3 / 2;
            int i7 = i4 / 2;
            while (i6 / i5 >= i2 && i7 / i5 >= i) {
                i5 *= 2;
            }
        }
        return i5;
    }

    public static boolean contemTransparente(Bitmap bitmap) {
        boolean z = false;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            int i2 = 0;
            while (true) {
                if (i2 >= bitmap.getHeight()) {
                    break;
                } else if (bitmap.getPixel(i, i2) == Color.argb(255, 0, 0, 0)) {
                    z = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (z) {
                break;
            }
        }
        return z;
    }

    public static void copyFile(File file, File file2) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read > 0) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileOutputStream.close();
                    fileInputStream.close();
                    return;
                }
            }
        } catch (Throwable unused) {
            fileInputStream.close();
        }
    }

    public static float dpiToPixels(float f) {
        return TypedValue.applyDimension(1, f, MotionEditActivity.metrics);
    }

    public static int getColor(Context context, int i) {
        if (VERSION.SDK_INT >= 23) {
            return context.getColor(i);
        }
        return ResourcesCompat.getColor(context.getResources(), i, null);
    }

    public static Drawable getDrawable(Context context, int i, int i2) {
        Drawable drawable = getDrawable(context, i);
        pintarIcone(drawable, i2);
        return drawable;
    }

    public static Bitmap getImagemSemMascara(Bitmap bitmap, Bitmap bitmap2, Config config) {
        Bitmap copy = bitmap.copy(config, true);
        if (bitmap2 != null) {
            Canvas canvas = new Canvas(copy);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
            canvas.drawBitmap(bitmap2, 0.0f, 0.0f, pSemMask);
        }
        return copy;
    }

    public static String getImgPath(Context context, Uri uri) {
        String str = "_data";
        Cursor query = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", str}, null, null, "_id DESC");
        try {
            query.moveToFirst();
            return query.getString(query.getColumnIndexOrThrow(str));
        } finally {
            query.close();
        }
    }

    public static Bitmap getMascaraDaImagem(Bitmap bitmap, Bitmap bitmap2, Config config) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(createBitmap);
        if (bitmap2 != null) {
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
            canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paintMask);
        }
        return createBitmap;
    }

    public static File getPrivateAlbumStorageDir(Context context, String str) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), str);
        file.mkdirs();
        return file;
    }

    public static File getPublicAlbumStorageDir(String str) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), str);
        if (!file.exists() && !file.mkdirs()) {
            Log.i("INFO", "Directory not created");
        }
        return file;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static void pintarIcone(Drawable drawable, int i) {
        int i2 = VERSION.SDK_INT;
        drawable.setTint(i);
    }

    @SuppressLint({"WrongConstant"})
    public static Bitmap resizeImagem(Context context, Uri uri, int i, int i2) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(openInputStream, null, options);
            openInputStream.close();
            options.inSampleSize = calculateInSampleSize(options, i, i2);
            options.inJustDecodeBounds = false;
            InputStream openInputStream2 = context.getContentResolver().openInputStream(uri);
            Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream2, null, options);
            openInputStream2.close();
            return decodeStream;
        } catch (Exception unused) {
            Toast.makeText(context, context.getResources().getText(R.string.load_image_fail), Toast.LENGTH_LONG);
            return null;
        }
    }

    public static void scannFile(Context context, File file) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");

        Uri mSelectedImageUri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mSelectedImageUri = Uri.fromFile(file);
        } else {
            mSelectedImageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        }
        intent.setData(mSelectedImageUri);
        context.sendBroadcast(intent);
    }

    public static void scannFiles(Context context) {
        MediaScannerConnection.scanFile(context, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new ScannFile());
    }
    public static Uri writeImageAndGetPathUri(Context context, Bitmap bitmap, String str) throws Exception {
        File file = new File(getPrivateAlbumStorageDir(context, context.getResources().getString(R.string.images_folder)), str+".png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();
        return Uri.parse(file.getPath());
    }

    public static Drawable getDrawable(Context context, int i) {
        int i2 = VERSION.SDK_INT;
        return context.getDrawable(i);
    }
}
