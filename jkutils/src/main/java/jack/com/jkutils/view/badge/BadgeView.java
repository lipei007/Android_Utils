package jack.com.jkutils.view.badge;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;


public class BadgeView extends android.support.v7.widget.AppCompatTextView {


    private int mBadgeNumber;
    private int mTextW,mTextH;
    private int backgroundColor = Color.RED;

    public BadgeView(Context context) {
        this(context,null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        setTextColor(Color.WHITE);
        setTypeface(Typeface.DEFAULT);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        setGravity(Gravity.CENTER);

        setBadgeCount(0);

    }


    public void setBackground(int dipRadius, int badgeColor) {
        int radius = dip2Px(dipRadius);
        float[] radiusArray = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };

        RoundRectShape roundRect = new RoundRectShape(radiusArray, null, null);
        ShapeDrawable bgDrawable = new ShapeDrawable(roundRect);
        bgDrawable.getPaint().setColor(badgeColor);
//        setBackground(bgDrawable);

        setBackgroundDrawable(bgDrawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mTextW == 0 && mTextH == 0) { // constraintLayout 会调用一次，显示正确。否则RelativeLayout会调用多次，导致Max + 20多次。

            int w = getMeasuredWidth();
            int h = getMeasuredHeight();

            mTextW = w;
            mTextH = h;

            int max = Math.max(w,h);

            getLayoutParams().height = max;
            getLayoutParams().width = max;

            Integer count = getBadgeCount();
            if (count != null && count >= 10) {
                getLayoutParams().width = max + 20;
            }

            setLayoutParams(getLayoutParams());

            setBackground(max / 2, backgroundColor);
        }

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text == null || text.toString().equalsIgnoreCase("0") || text.length() == 0) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
        }


        super.setText(text, type);

        clear();
        requestLayout();
    }

    private void clear() {


        if (getLayoutParams() != null) {
            getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            setLayoutParams(getLayoutParams());
        }

        mTextW = 0;
        mTextH = 0;
    }

    public void setBadgeCount(int count) {
        mBadgeNumber = count;
        if (count > 99) {
            setText("99+");
        } else {
            setText(String.valueOf(count));
        }
    }

    public Integer getBadgeCount() {
        return mBadgeNumber;
    }


    /*
     * converts dip to px
     */
    private int dip2Px(float dip) {
        return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

}
