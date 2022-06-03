package com.devsectech.photomo.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomImageView extends AppCompatImageView {
    public static final int CLICK = 3;
    public static final int DRAG = 1;
    public static final int NONE = 0;
    public static final int ZOOM = 2;
    public OnTouchListener _onTouchListener;
    public Context context;
    public float[] f47m;
    public PointF last = new PointF();
    public ScaleGestureDetector mScaleDetector;
    public Matrix matrix;
    public float maxScale = 5.0f;
    public float minScale = 1.0f;
    public int mode = 0;
    public int oldMeasuredHeight;
    public int oldMeasuredWidth;
    public OnTouchListener onTouchListenerZoom = new C02681();
    public float origHeight;
    public float origWidth;
    public Matrix originalMatrix;
    public float saveScale = 1.0f;
    public SimpleOnScaleGestureListener scaleListenerExterno;
    public PointF start = new PointF();
    public int viewHeight;
    public int viewWidth;
    public boolean zoomActivated = false;

    public class C02681 implements OnTouchListener {
        public C02681() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            mScaleDetector.onTouchEvent(motionEvent);
            PointF pointF = new PointF(motionEvent.getX(), motionEvent.getY());
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1) {
                    mode = 0;
                    int abs = (int) Math.abs(pointF.y - start.y);
                    if (((int) Math.abs(pointF.x - start.x)) < 3 && abs < 3) {
                        performClick();
                    }
                } else if (action != 2) {
                    if (action == 6) {
                        mode = 0;
                    }
                }
                if (mode == 1) {
                    float f = pointF.y - last.y;
                    Matrix access$900 = matrix;
                    ZoomImageView zoomImageView = ZoomImageView.this;
                    float fixDragTrans = zoomImageView.getFixDragTrans(pointF.x - zoomImageView.last.x, (float) viewWidth, saveScale * origWidth);
                    ZoomImageView zoomImageView2 = ZoomImageView.this;
                    access$900.postTranslate(fixDragTrans, zoomImageView2.getFixDragTrans(f, (float) zoomImageView2.viewHeight, saveScale * origHeight));
                    fixTrans();
                    last.set(pointF.x, pointF.y);
                }
            } else {
                last.set(pointF);
                start.set(last);
                mode = 1;
            }
            ZoomImageView zoomImageView3 = ZoomImageView.this;
            zoomImageView3.setImageMatrix(zoomImageView3.matrix);
            invalidate();
            return true;
        }
    }

    public class ScaleListener extends SimpleOnScaleGestureListener {
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float access$1200;
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float access$600 = saveScale;
            ZoomImageView zoomImageView = ZoomImageView.this;
            zoomImageView.saveScale = zoomImageView.saveScale * scaleFactor;
            if (saveScale > maxScale) {
                ZoomImageView zoomImageView2 = ZoomImageView.this;
                zoomImageView2.saveScale = zoomImageView2.maxScale;
                access$1200 = maxScale;
            } else {
                if (saveScale < minScale) {
                    ZoomImageView zoomImageView3 = ZoomImageView.this;
                    zoomImageView3.saveScale = zoomImageView3.minScale;
                }
                if (saveScale * origWidth > ((float) viewWidth)) {
                    if (saveScale * origHeight > ((float) viewHeight)) {
                        matrix.postScale(scaleFactor, scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                        fixTrans();
                        scaleListenerExterno.onScale(scaleGestureDetector);
                        return true;
                    }
                }
                matrix.postScale(scaleFactor, scaleFactor, (float) (viewWidth / 2), (float) (viewHeight / 2));
                fixTrans();
                scaleListenerExterno.onScale(scaleGestureDetector);
                return true;
            }
            scaleFactor = access$1200 / access$600;
            if (saveScale * origWidth > ((float) viewWidth)) {
            }
            matrix.postScale(scaleFactor, scaleFactor, (float) (viewWidth / 2), (float) (viewHeight / 2));
            fixTrans();
            scaleListenerExterno.onScale(scaleGestureDetector);
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            mode = 2;
            scaleListenerExterno.onScaleBegin(scaleGestureDetector);
            return true;
        }
    }

    public ZoomImageView(Context context2) {
        super(context2);
        sharedConstructing(context2);
    }

    private void sharedConstructing(Context context2) {
        this.context = context2;
        setClickable(true);
        this.context = context2;
        this.saveScale = 1.0f;
        this.mScaleDetector = new ScaleGestureDetector(context2, new ScaleListener());
        this.matrix = new Matrix();
        this.f47m = new float[9];
        setImageMatrix(this.matrix);
        setScaleType(ScaleType.MATRIX);
        super.setOnTouchListener(this.onTouchListenerZoom);
    }

    public void fixTrans() {
        this.matrix.getValues(this.f47m);
        float[] fArr = this.f47m;
        float f = fArr[2];
        float f2 = fArr[5];
        float fixTrans = getFixTrans(f, (float) this.viewWidth, this.origWidth * this.saveScale);
        float fixTrans2 = getFixTrans(f2, (float) this.viewHeight, this.origHeight * this.saveScale);
        if (fixTrans != 0.0f || fixTrans2 != 0.0f) {
            this.matrix.postTranslate(fixTrans, fixTrans2);
        }
    }

    public float getFixDragTrans(float f, float f2, float f3) {
        if (f3 <= f2) {
            return 0.0f;
        }
        return f;
    }

    public float getFixTrans(float f, float f2, float f3) {
        float f4;
        float f5;
        if (f3 <= f2) {
            f4 = f2 - f3;
            f5 = 0.0f;
        } else {
            f5 = f2 - f3;
            f4 = 0.0f;
        }
        if (f < f5) {
            return (-f) + f5;
        }
        if (f > f4) {
            return (-f) + f4;
        }
        return 0.0f;
    }

    public float getZoomScale() {
        return this.saveScale;
    }

    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.viewWidth = MeasureSpec.getSize(i);
        this.viewHeight = MeasureSpec.getSize(i2);
        int i3 = this.oldMeasuredHeight;
        if (!(i3 == this.viewWidth && i3 == this.viewHeight)) {
            int i4 = this.viewWidth;
            if (i4 != 0) {
                int i5 = this.viewHeight;
                if (i5 != 0) {
                    this.oldMeasuredHeight = i5;
                    this.oldMeasuredWidth = i4;
                    if (this.saveScale == 1.0f) {
                        Drawable drawable = getDrawable();
                        if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
                            int intrinsicWidth = drawable.getIntrinsicWidth();
                            float f = (float) intrinsicWidth;
                            float intrinsicHeight = (float) drawable.getIntrinsicHeight();
                            float min = Math.min(((float) this.viewWidth) / f, ((float) this.viewHeight) / intrinsicHeight);
                            this.matrix.setScale(min, min);
                            float f2 = (((float) this.viewHeight) - (intrinsicHeight * min)) / 2.0f;
                            float f3 = (((float) this.viewWidth) - (f * min)) / 2.0f;
                            this.matrix.postTranslate(f3, f2);
                            this.origWidth = ((float) this.viewWidth) - (f3 * 2.0f);
                            this.origHeight = ((float) this.viewHeight) - (f2 * 2.0f);
                            setImageMatrix(this.matrix);
                        } else {
                            return;
                        }
                    }
                    fixTrans();
                }
            }
        }
    }

    public void restartZoom() {
        this.saveScale = 1.0f;
        this.matrix = new Matrix();
        this.f47m = new float[9];
        setScaleType(ScaleType.FIT_CENTER);
        setImageMatrix(this.matrix);
        setScaleType(ScaleType.MATRIX);
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        this.originalMatrix = new Matrix(getImageMatrix());
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this._onTouchListener = onTouchListener;
    }

    public void setScaleListener(SimpleOnScaleGestureListener simpleOnScaleGestureListener) {
        this.scaleListenerExterno = simpleOnScaleGestureListener;
    }

    public void setZoomActivated(boolean z) {
        this.zoomActivated = z;
        if (z) {
            super.setOnTouchListener(this.onTouchListenerZoom);
        } else {
            super.setOnTouchListener(this._onTouchListener);
        }
    }

    public ZoomImageView(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        sharedConstructing(context2);
    }
}
