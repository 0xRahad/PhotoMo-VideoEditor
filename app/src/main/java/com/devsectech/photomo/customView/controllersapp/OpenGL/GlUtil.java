package com.devsectech.photomo.customView.controllersapp.OpenGL;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GlUtil {
    public static final float[] IDENTITY_MATRIX = new float[16];
    public static final int SIZEOF_FLOAT = 4;
    public static final String TAG = "Grafika";

    static {
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }

    public static void checkGlError(String str) {
        int glGetError = GLES20.glGetError();
        if (glGetError != 0) {
            StringBuilder outline27 = new StringBuilder();
            outline27.append(Integer.toHexString(glGetError));
            String sb = outline27.toString();
            Log.e("Grafika", sb);
            throw new RuntimeException(sb);
        }
    }

    public static void checkLocation(int i, String str) {
        if (i < 0) {
        }
    }

    public static FloatBuffer createFloatBuffer(float[] fArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        asFloatBuffer.put(fArr);
        asFloatBuffer.position(0);
        return asFloatBuffer;
    }

    public static int createImageTexture(ByteBuffer byteBuffer, int i, int i2, int i3) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        int i4 = iArr[0];
        checkGlError("glGenTextures");
        GLES20.glBindTexture(3553, i4);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        String str = "loadImageTexture";
        checkGlError(str);
        GLES20.glTexImage2D(3553, 0, i3, i, i2, 0, i3, 5121, byteBuffer);
        checkGlError(str);
        return i4;
    }

    public static int createProgram(String str, String str2) {
        int loadShader = loadShader(35633, str);
        if (loadShader == 0) {
            return 0;
        }
        int loadShader2 = loadShader(35632, str2);
        if (loadShader2 == 0) {
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        String str3 = "Grafika";
        if (glCreateProgram == 0) {
            Log.e(str3, "Could not create program");
        }
        GLES20.glAttachShader(glCreateProgram, loadShader);
        String str4 = "glAttachShader";
        checkGlError(str4);
        GLES20.glAttachShader(glCreateProgram, loadShader2);
        checkGlError(str4);
        GLES20.glLinkProgram(glCreateProgram);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
        if (iArr[0] == 1) {
            return glCreateProgram;
        }
        Log.e(str3, "Could not link program: ");
        Log.e(str3, GLES20.glGetProgramInfoLog(glCreateProgram));
        GLES20.glDeleteProgram(glCreateProgram);
        return 0;
    }

    public static int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        StringBuilder sb = new StringBuilder();
        sb.append("glCreateShader type=");
        sb.append(i);
        checkGlError(sb.toString());
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Could not compile shader ");
        sb2.append(i);
        sb2.append(":");
        String sb3 = sb2.toString();
        String str2 = "Grafika";
        Log.e(str2, sb3);
        StringBuilder sb4 = new StringBuilder();
//        sb4.append(MatchRatingApproachEncoder.SPACE);
        sb4.append(" ");
        sb4.append(GLES20.glGetShaderInfoLog(glCreateShader));
        Log.e(str2, sb4.toString());
        GLES20.glDeleteShader(glCreateShader);
        return 0;
    }
}
