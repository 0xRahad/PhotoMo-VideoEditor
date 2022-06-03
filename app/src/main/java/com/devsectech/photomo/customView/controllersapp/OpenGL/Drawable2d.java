package com.devsectech.photomo.customView.controllersapp.OpenGL;


import java.nio.FloatBuffer;

public class Drawable2d {
    public static final float[] FULL_RECTANGLE_COORDS = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
    public static final FloatBuffer FULL_RECTANGLE_BUF = GlUtil.createFloatBuffer(FULL_RECTANGLE_COORDS);
    public static final float[] FULL_RECTANGLE_TEX_COORDS = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
    public static final FloatBuffer FULL_RECTANGLE_TEX_BUF = GlUtil.createFloatBuffer(FULL_RECTANGLE_TEX_COORDS);
    public static final float[] RECTANGLE_COORDS = {-0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f};
    public static final FloatBuffer RECTANGLE_BUF = GlUtil.createFloatBuffer(RECTANGLE_COORDS);
    public static final float[] RECTANGLE_TEX_COORDS = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    public static final FloatBuffer RECTANGLE_TEX_BUF = GlUtil.createFloatBuffer(RECTANGLE_TEX_COORDS);
    public static final float[] TRIANGLE_COORDS = {0.0f, 0.57735026f, -0.5f, -0.28867513f, 0.5f, -0.28867513f};
    public static final FloatBuffer TRIANGLE_BUF = GlUtil.createFloatBuffer(TRIANGLE_COORDS);
    public static final int SIZEOF_FLOAT = 4;
    public static final float[] TRIANGLE_TEX_COORDS = {0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
    public static final FloatBuffer TRIANGLE_TEX_BUF = GlUtil.createFloatBuffer(TRIANGLE_TEX_COORDS);
    public int mCoordsPerVertex;
    public Prefab mPrefab;
    public FloatBuffer mTexCoordArray;
    public int mTexCoordStride;
    public FloatBuffer mVertexArray;
    public int mVertexCount;
    public int mVertexStride;

    public static  class AnonymousClass1 {
        public static final  int[] $SwitchMap$com$photo$motion$pictures$music$effect$editor$controllers$OpenGL$Drawable2d$Prefab = new int[Prefab.values().length];

        static {
            $SwitchMap$com$photo$motion$pictures$music$effect$editor$controllers$OpenGL$Drawable2d$Prefab[Prefab.TRIANGLE.ordinal()] = 1;
            $SwitchMap$com$photo$motion$pictures$music$effect$editor$controllers$OpenGL$Drawable2d$Prefab[Prefab.RECTANGLE.ordinal()] = 2;
            try {
                $SwitchMap$com$photo$motion$pictures$music$effect$editor$controllers$OpenGL$Drawable2d$Prefab[Prefab.FULL_RECTANGLE.ordinal()] = 3;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    public enum Prefab {
        TRIANGLE,
        RECTANGLE,
        FULL_RECTANGLE
    }

    public Drawable2d(Prefab prefab) {
        int ordinal = prefab.ordinal();
        if (ordinal == 0) {
            this.mVertexArray = TRIANGLE_BUF;
            this.mTexCoordArray = TRIANGLE_TEX_BUF;
            this.mCoordsPerVertex = 2;
            int i = this.mCoordsPerVertex;
            this.mVertexStride = i * 4;
            this.mVertexCount = TRIANGLE_COORDS.length / i;
        } else if (ordinal == 1) {
            this.mVertexArray = RECTANGLE_BUF;
            this.mTexCoordArray = RECTANGLE_TEX_BUF;
            this.mCoordsPerVertex = 2;
            int i2 = this.mCoordsPerVertex;
            this.mVertexStride = i2 * 4;
            this.mVertexCount = RECTANGLE_COORDS.length / i2;
        } else if (ordinal == 2) {
            this.mVertexArray = FULL_RECTANGLE_BUF;
            this.mTexCoordArray = FULL_RECTANGLE_TEX_BUF;
            this.mCoordsPerVertex = 2;
            int i3 = this.mCoordsPerVertex;
            this.mVertexStride = i3 * 4;
            this.mVertexCount = FULL_RECTANGLE_COORDS.length / i3;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown shape ");
            sb.append(prefab);
            throw new RuntimeException(sb.toString());
        }
        this.mTexCoordStride = 8;
        this.mPrefab = prefab;
    }

    public int getCoordsPerVertex() {
        return this.mCoordsPerVertex;
    }

    public FloatBuffer getTexCoordArray() {
        return this.mTexCoordArray;
    }

    public int getTexCoordStride() {
        return this.mTexCoordStride;
    }

    public FloatBuffer getVertexArray() {
        return this.mVertexArray;
    }

    public int getVertexCount() {
        return this.mVertexCount;
    }

    public int getVertexStride() {
        return this.mVertexStride;
    }

    public String toString() {
        if (this.mPrefab == null) {
            return "[Drawable2d: ...]";
        }
        StringBuilder outline24 = new StringBuilder();
        outline24.append(this.mPrefab);
        outline24.append("]");
        return outline24.toString();
    }
}
