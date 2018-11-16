package jack.com.jkutils.view.imageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class RoundCornerImageView extends android.support.v7.widget.AppCompatImageView {

    private int roundCorner;

    public RoundCornerImageView(Context context) {
        this(context,null);
    }

    public RoundCornerImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundCornerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setRoundCorner(int roundCorner) {
        this.roundCorner = roundCorner;

        postInvalidate();
    }

    public int getRoundCorner() {
        return roundCorner;
    }

    Paint mPaint;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 可操作区域
        int width = getWidth();
        int height = getHeight();
        RectF rect = new RectF(0,0,width,height);


        // 原始图片
        Drawable drawable = getDrawable();
        Bitmap src = ((BitmapDrawable)drawable).getBitmap();

        // 先缩放原图，适配View大小
        Bitmap scaleSrc = scaleBitmap(src, width, height);

        // 创建Mask
        Bitmap mask=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas=new Canvas(mask);
        maskCanvas.drawRoundRect(rect,roundCorner,roundCorner,mPaint);

        /**
         *
         * 产生新的layer。新layer相当于一个区域为传递的bounds的“新画布”，它关联一个bitmap（an offscreen bitmap，它是完全透明的），
         * 之后的绘制操作都在此bitmap上执行。每个layer可以看做一个独立的画布，所有layer形成一个栈，栈底是初始的layer。
         * 每次在栈顶产生的新layer，任何时候都在栈顶的layer上执行绘图，
         * 调用restoreToCount()后栈顶layer出栈，其对应的bitmap的内容合并（进行像素的argb混合）到之前layer中。
         *
         * */
//        int sc = canvas.saveLayer(rect,mPaint);

        /**
         *
         * 根据saveLayer方法的文档介绍，可以去掉saveLayer()/restoreToCount()的调用，
         * 只需要在onDraw()中开启硬件加速就可以实现相同的目标了，性能会更好
         *
         * */
        setLayerType(LAYER_TYPE_HARDWARE, mPaint); // 开启硬件加速

        canvas.drawBitmap(mask, 0, 0, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(scaleSrc, 0, 0, mPaint);

        mPaint.setXfermode(null);

//        canvas.restoreToCount(sc);

    }

    private Bitmap scaleBitmap(Bitmap src, int width, int height) {

        if (src == null || width <= 0 || height <= 0) {
            return src;
        }

        Rect rct = new Rect(0,0,width,height);

        Bitmap scaleSrc = Bitmap.createBitmap(width, height, src.getConfig());

        Canvas scaleCanvas = new Canvas(scaleSrc);

        scaleCanvas.drawBitmap(src,null,rct,mPaint);

        return scaleSrc;
    }

}
