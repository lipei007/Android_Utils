package jack.com.jkutils.image;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static int drawableIdFromString(Context context, String name) {

        if (context == null) {
            return 0;
        }

        if (context == null || TextUtils.isEmpty(name)) {
            return 0;
        }

        int drawableId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());

        return drawableId;
    }


    public static Bitmap bitmapFromFile(File file) {
        if (file == null) {
            return null;
        }
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //避免出现内存溢出的情况，进行相应的属性设置。
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);

        return bitmap;
    }

    public static void renderingDrawable(Drawable drawable, Resources res, int colorId) {
        if (drawable == null || res == null) {
            return;
        }

        ColorStateList colorStateList = ColorStateList.valueOf(res.getColor(colorId));
        DrawableCompat.setTintList(drawable, colorStateList);
    }

    public static void clearDrawableRendering(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        DrawableCompat.setTintList(drawable, null);
    }

    public enum ImageType {
        IMAGE_TYPE_PNG,
        IMAGE_TYPE_JPG,
        IMAGE_TYPE_BMP,
        IMAGE_TYPE_GIF,
        IMAGE_TYPE_UNKNOWN
    }

    public static ImageType imageType(byte[] imgBytes) {

        if (imgBytes != null) {

            if (imgBytes.length > 4) {

                // jpeg/jpg
                // ff d8
                if (imgBytes[0] == 0xff && imgBytes[1] == 0xd8 && imgBytes[2] == 0xff) {
                    return ImageType.IMAGE_TYPE_JPG;
                }
                // png
                // 89 50 4e 47
                if (imgBytes[0] == 0x89 && imgBytes[1] == 0x50 && imgBytes[2] == 0x4e && imgBytes[3] == 0x47) {
                    return ImageType.IMAGE_TYPE_PNG;
                }
                // bmp
                // 42 4D
                if (imgBytes[0] == 0x42 && imgBytes[1] == 0x4d) {
                    return ImageType.IMAGE_TYPE_BMP;
                }

                // gif
                // GIF比对[47][49][46]与第五个字符39(37)
                if (imgBytes[0] == 0x47 && imgBytes[1] == 0x49 && imgBytes[2] == 0x46 && (imgBytes[4] == 0x39 || imgBytes[4] == 0x37)) {
                    return ImageType.IMAGE_TYPE_GIF;
                }
            }

        }
        return ImageType.IMAGE_TYPE_UNKNOWN;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmap(int angle , Bitmap bitmap) {

        // 旋转图片
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 纠正图像在ImageView中的显示方法
     * */
    public static Bitmap adjustPhotoOrientation(String filename) {
        if (filename == null) {
            return null;
        }

        int degree = readPictureDegree(filename);

        Bitmap bitmap = BitmapFactory.decodeFile(filename);

        if (degree != 0) {

            return rotateBitmap(degree,bitmap);
        }

        return bitmap;
    }

    /**
     * 将图像以正方向的形态保存
     * */
    public static File routeBitmap(File sourceFile) {

        if (!sourceFile.exists() || sourceFile.isDirectory()) {
            return sourceFile;
        }

        /**
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
         */
        Bitmap bitmap = bitmapFromFile(sourceFile);

        /**
         * 把图片旋转为正的方向
         */
        int degree = readPictureDegree(sourceFile.getAbsolutePath());

        Bitmap rotationBitmap = rotateBitmap(degree, bitmap);

        // 删除源文件
        File routedFile = sourceFile;
        if (routedFile.exists()) {
            routedFile.delete();
        }

        try {

            FileOutputStream outputStream = new FileOutputStream(routedFile);
            rotationBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

            return routedFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void savePNGToFile(Bitmap bitmap, File photoFile) {
        if (photoFile == null || bitmap == null) {
            return;
        }
        try {

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(photoFile));
            Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
            bitmap.compress(format, 100, bos); // 使用JPG保存，若背景透明的PNG原图，读取出来后背景是黑色的
            bos.flush();
            bos.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void saveJPGToFile(Bitmap bitmap, File photoFile) {
        if (photoFile == null || bitmap == null) {
            return;
        }
        try {

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(photoFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * 缩小图片
     * */
    public Bitmap zoomOutBitmap(Bitmap bitmap, int width, int height) {

        BitmapZoomOutHelper bitmapZoomOutHelper = new BitmapZoomOutHelper(bitmap);
        bitmap = bitmapZoomOutHelper.zoomOutBitmap(width, height);

        return bitmap;
    }
}
