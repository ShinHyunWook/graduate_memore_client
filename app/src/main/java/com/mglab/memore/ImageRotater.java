package com.mglab.memore;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by 신현욱 on 2017-04-30.
 */

public class ImageRotater {

    public static Bitmap rotateBitmap(Bitmap sourceBitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
    }
}
