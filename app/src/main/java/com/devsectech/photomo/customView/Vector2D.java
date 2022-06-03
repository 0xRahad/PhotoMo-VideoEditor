package com.devsectech.photomo.customView;

import android.graphics.PointF;

public class Vector2D extends PointF {
    public Vector2D() {
    }

    public static float getAngle(Vector2D vector2D, Vector2D vector2D2) {
        vector2D.normalize();
        vector2D2.normalize();
        return (float) ((Math.atan2((double) vector2D2.y, (double) vector2D2.x) - Math.atan2((double) vector2D.y, (double) vector2D.x)) * 57.29577951308232d);
    }

    public void normalize() {
        float f = this.x;
        float f2 = f * f;
        float f3 = this.y;
        float sqrt = (float) Math.sqrt((double) ((f3 * f3) + f2));
        this.x /= sqrt;
        this.y /= sqrt;
    }

    public Vector2D(float f, float f2) {
        super(f, f2);
    }
}
