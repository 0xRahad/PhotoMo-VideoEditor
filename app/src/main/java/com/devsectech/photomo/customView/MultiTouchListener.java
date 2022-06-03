package com.devsectech.photomo.customView;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.devsectech.photomo.R;
import com.devsectech.photomo.utils.TransformInfo;
import com.devsectech.photomo.customView.ScaleGestureDetector.SimpleOnScaleGestureListener;

public class MultiTouchListener implements OnTouchListener {
    public static final int INVALID_POINTER_ID = -1;
    public static TransformInfo info = new TransformInfo();
    public boolean isRotateEnabled = true;
    public boolean isScaleEnabled = true;
    public boolean isTranslateEnabled = true;
    public int mActivePointerId = -1;
    public float mPrevX;
    public float mPrevY;
    public ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
    public float maximumScale = 10.0f;
    public float minimumScale = 0.5f;

    public class ScaleGestureListener extends SimpleOnScaleGestureListener {
        public float mPivotX;
        public float mPivotY;
        public Vector2D mPrevSpanVector;

        public ScaleGestureListener() {
            this.mPrevSpanVector = new Vector2D();
        }

        public boolean onScale(View view, ScaleGestureDetector scaleGestureDetector) {
            MultiTouchListener.info.deltaScale = isScaleEnabled ? scaleGestureDetector.getScaleFactor() : 1.0f;
            float f = 0.0f;
            MultiTouchListener.info.deltaAngle = isRotateEnabled ? Vector2D.getAngle(this.mPrevSpanVector, scaleGestureDetector.getCurrentSpanVector()) : 0.0f;
            MultiTouchListener.info.deltaX = isTranslateEnabled ? scaleGestureDetector.getFocusX() - this.mPivotX : 0.0f;
            TransformInfo access$100 = MultiTouchListener.info;
            if (isTranslateEnabled) {
                f = scaleGestureDetector.getFocusY() - this.mPivotY;
            }
            access$100.deltaY = f;
            MultiTouchListener.info.pivotX = this.mPivotX;
            MultiTouchListener.info.pivotY = this.mPivotY;
            MultiTouchListener.info.minimumScale = minimumScale;
            MultiTouchListener.info.maximumScale = maximumScale;
            MultiTouchListener.move(view, MultiTouchListener.info);
            return false;
        }

        public boolean onScaleBegin(View view, ScaleGestureDetector scaleGestureDetector) {
            this.mPivotX = scaleGestureDetector.getFocusX();
            this.mPivotY = scaleGestureDetector.getFocusY();
            MultiTouchListener.info.x = this.mPivotX;
            MultiTouchListener.info.y = this.mPivotY;
            this.mPrevSpanVector.set(scaleGestureDetector.getCurrentSpanVector());
            return true;
        }
    }

    public static float adjustAngle(float f) {
        return f > 180.0f ? f - 360.0f : f < -180.0f ? f + 360.0f : f;
    }

    public static void adjustTranslation(View view, float f, float f2) {
        float[] fArr = {f, f2};
        view.getMatrix().mapVectors(fArr);
        view.setTranslationX(view.getTranslationX() + fArr[0]);
        view.setTranslationY(view.getTranslationY() + fArr[1]);
    }

    public static void computeRenderOffset(View view, float f, float f2) {
        if (view.getPivotX() != f || view.getPivotY() != f2) {
            float[] fArr = {0.0f, 0.0f};
            view.getMatrix().mapPoints(fArr);
            view.setPivotX(f);
            view.setPivotY(f2);
            float[] fArr2 = {0.0f, 0.0f};
            view.getMatrix().mapPoints(fArr2);
            float f3 = fArr2[0] - fArr[0];
            float f4 = fArr2[1] - fArr[1];
            view.setTranslationX(view.getTranslationX() - f3);
            view.setTranslationY(view.getTranslationY() - f4);
        }
    }

    public static void move(View view, TransformInfo transformInfo) {
        computeRenderOffset(view, transformInfo.pivotX, transformInfo.pivotY);
        adjustTranslation(view, transformInfo.deltaX, transformInfo.deltaY);
        float max = Math.max(transformInfo.minimumScale, Math.min(transformInfo.maximumScale, view.getScaleX() * transformInfo.deltaScale));
        view.setScaleX(max);
        view.setScaleY(max);
        float adjustAngle = adjustAngle(view.getRotation() + transformInfo.deltaAngle);
        transformInfo.rotation = adjustAngle;
        view.setRotation(adjustAngle);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.clView) {
            this.mScaleGestureDetector.onTouchEvent(view, motionEvent);
            if (!this.isTranslateEnabled) {
                return true;
            }
            int action = motionEvent.getAction();
            int actionMasked = motionEvent.getActionMasked() & action;
            int i = 0;
            if (actionMasked == 0) {
                this.mPrevX = motionEvent.getX();
                this.mPrevY = motionEvent.getY();
                this.mActivePointerId = motionEvent.getPointerId(0);
            } else if (actionMasked == 1) {
                this.mActivePointerId = -1;
            } else if (actionMasked == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex != -1) {
                    float x = motionEvent.getX(findPointerIndex);
                    float y = motionEvent.getY(findPointerIndex);
                    if (!this.mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, x - this.mPrevX, y - this.mPrevY);
                    }
                }
            } else if (actionMasked == 3) {
                this.mActivePointerId = -1;
            } else if (actionMasked == 6) {
                int i2 = (65280 & action) >> 8;
                if (motionEvent.getPointerId(i2) == this.mActivePointerId) {
                    if (i2 == 0) {
                        i = 1;
                    }
                    this.mPrevX = motionEvent.getX(i);
                    this.mPrevY = motionEvent.getY(i);
                    this.mActivePointerId = motionEvent.getPointerId(i);
                }
            }
        }
        return true;
    }
}
