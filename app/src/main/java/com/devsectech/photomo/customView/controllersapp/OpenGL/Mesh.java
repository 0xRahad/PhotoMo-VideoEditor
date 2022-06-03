package com.devsectech.photomo.customView.controllersapp.OpenGL;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;

public class Mesh {
    public float f11x = 0.0f;
    public float f12y = 0.0f;
    public float f13z = 0.0f;
    public Bitmap mBitmap;
    public FloatBuffer mColorBuffer = null;
    public ShortBuffer mIndicesBuffer = null;
    public int mNumOfIndices = -1;
    public final float[] mRGBA = {1.0f, 1.0f, 1.0f, 1.0f};
    public boolean mShouldLoadTexture = false;
    public FloatBuffer mTextureBuffer;
    public int mTextureId = -1;
    public FloatBuffer mVerticesBuffer = null;
    public float rx = 0.0f;
    public float ry = 0.0f;
    public float rz = 0.0f;

    private void loadGLTexture(GL10 gl10) {
        int[] iArr = new int[1];
        gl10.glGenTextures(1, iArr, 0);
        this.mTextureId = iArr[0];
        gl10.glBindTexture(3553, this.mTextureId);
        gl10.glTexParameterf(3553, 10241, 9729.0f);
        gl10.glTexParameterf(3553, 10240, 9729.0f);
        gl10.glTexParameterf(3553, 10242, 33071.0f);
        gl10.glTexParameterf(3553, 10243, 10497.0f);
        GLUtils.texImage2D(3553, 0, this.mBitmap, 0);
    }

    public void draw(GL10 gl10) {
        gl10.glFrontFace(2305);
        gl10.glEnable(2884);
        gl10.glCullFace(1029);
        gl10.glEnableClientState(32884);
        gl10.glVertexPointer(3, 5126, 0, this.mVerticesBuffer);
        float[] fArr = this.mRGBA;
        gl10.glColor4f(fArr[0], fArr[1], fArr[2], fArr[3]);
        if (this.mColorBuffer != null) {
            gl10.glEnableClientState(32886);
            gl10.glColorPointer(4, 5126, 0, this.mColorBuffer);
        }
        if (this.mShouldLoadTexture) {
            loadGLTexture(gl10);
            this.mShouldLoadTexture = false;
        }
        if (!(this.mTextureId == -1 || this.mTextureBuffer == null)) {
            gl10.glEnable(3553);
            gl10.glEnableClientState(32888);
            gl10.glTexCoordPointer(2, 5126, 0, this.mTextureBuffer);
            gl10.glBindTexture(3553, this.mTextureId);
        }
        gl10.glTranslatef(this.f11x, this.f12y, this.f13z);
        gl10.glRotatef(this.rx, 1.0f, 0.0f, 0.0f);
        gl10.glRotatef(this.ry, 0.0f, 1.0f, 0.0f);
        gl10.glRotatef(this.rz, 0.0f, 0.0f, 1.0f);
        gl10.glDrawElements(4, this.mNumOfIndices, 5123, this.mIndicesBuffer);
        gl10.glDisableClientState(32884);
        if (!(this.mTextureId == -1 || this.mTextureBuffer == null)) {
            gl10.glDisableClientState(32888);
        }
        gl10.glDisable(2884);
    }

    public void loadBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.mShouldLoadTexture = true;
    }

    public void setColor(float f, float f2, float f3, float f4) {
        float[] fArr = this.mRGBA;
        fArr[0] = f;
        fArr[1] = f2;
        fArr[2] = f3;
        fArr[3] = f4;
    }

    public void setColors(float[] fArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        this.mColorBuffer = allocateDirect.asFloatBuffer();
        this.mColorBuffer.put(fArr);
        this.mColorBuffer.position(0);
    }

    public void setIndices(short[] sArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(sArr.length * 2);
        allocateDirect.order(ByteOrder.nativeOrder());
        this.mIndicesBuffer = allocateDirect.asShortBuffer();
        this.mIndicesBuffer.put(sArr);
        this.mIndicesBuffer.position(0);
        this.mNumOfIndices = sArr.length;
    }

    public void setTextureCoordinates(float[] fArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        this.mTextureBuffer = allocateDirect.asFloatBuffer();
        this.mTextureBuffer.put(fArr);
        this.mTextureBuffer.position(0);
    }

    public void setVertices(float[] fArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        this.mVerticesBuffer = allocateDirect.asFloatBuffer();
        this.mVerticesBuffer.put(fArr);
        this.mVerticesBuffer.position(0);
    }
}
