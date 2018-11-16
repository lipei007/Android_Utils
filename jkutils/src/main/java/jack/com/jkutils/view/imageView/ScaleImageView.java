package jack.com.jkutils.view.imageView;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;


/**
 * layout中，
 *
 * 如果 ScaleType 为 ScaleTypeKeepWidth：
 *
 *  android:layout_height="wrap_content"
 *
 * 如果 ScaleType 为 ScaleTypeKeepHeight：
 *
 *  android:layout_width="wrap_content"
 *
 * */
public class ScaleImageView extends android.support.v7.widget.AppCompatImageView {

    public enum ScaleType {
        ScaleTypeNone,
        ScaleTypeKeepWidth,
        ScaleTypeKeepHeight
    }

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float mScale = 1;

    public ScaleType mScaleType = ScaleType.ScaleTypeNone;

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {

        Rect bounds = drawable.getBounds();
        int w = bounds.right - bounds.left;
        int h = bounds.bottom - bounds.top;

        if (mScaleType == ScaleType.ScaleTypeKeepWidth) {

            mScale = h / w;

        } else if (mScaleType == ScaleType.ScaleTypeKeepHeight) {

            mScale = w / h;
        }

        super.setImageDrawable(drawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mScaleType == ScaleType.ScaleTypeKeepWidth) {

            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * mScale);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);

        } else if (mScaleType == ScaleType.ScaleTypeKeepHeight) {

            int height = MeasureSpec.getSize(heightMeasureSpec);
            int width = (int)(height * mScale);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
