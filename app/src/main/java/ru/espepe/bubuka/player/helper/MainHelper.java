package ru.espepe.bubuka.player.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.TypedValue;

import java.io.File;
import java.util.Locale;

/**
 * Created by wolong on 30/07/14.
 */
public class MainHelper {
    public static String humanReadableByteCountOld(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());

    }

    public static int spToPx(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static Bitmap loadImage(Context context, File file, int requiredSizePx) {
        //decode image size
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        //final int requiredSizePx = dpToPx(context, requiredSizeDp);

        options.inSampleSize = calculateInSampleSize(options, requiredSizePx, requiredSizePx);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        float scaleX = (float) requiredSizePx / (float) bitmap.getWidth();
        float scaleY = (float) requiredSizePx / (float) bitmap.getHeight();

        //float scale = scaleX > scaleY ? scaleY : scaleX;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
