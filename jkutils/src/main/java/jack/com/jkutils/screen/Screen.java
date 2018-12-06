package jack.com.jkutils.screen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

public class Screen {

    public static class Size {
        int width;
        int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;

        }

        public int getHeight() {
            return height;
        }
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static Size screenSize(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Size size = new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);
        return size;
    }

    /**
     * 获取设备屏幕密度dpi，每寸所包含的像素点
     * @param context
     * @return
     */
    public static float screenDensityDpi(Context context){
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * 获取设备屏幕密度,像素的比例
     */
    public static float screenDensity(Context context){
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 截取当前屏幕画面为bitmap图片
     * @param activity
     * @param hasStatusBar 是否包含当前状态栏,true:包含
     */
    public static Bitmap screenShot(Activity activity, boolean hasStatusBar){
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();

        Size size = screenSize(activity);

        int coordinateY = 0;
        int cutHeight = size.height;
        if(!hasStatusBar){
            Rect frame = new Rect();
            decorView.getWindowVisibleDisplayFrame(frame);
            coordinateY += frame.top;
            cutHeight -= frame.top;
        }
        Bitmap shot = Bitmap.createBitmap(bmp,0,coordinateY,size.width,cutHeight);
        decorView.destroyDrawingCache();
        return shot;
    }

}
