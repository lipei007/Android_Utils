package jack.com.jkutils.view.button;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import java.util.HashMap;

public class JKButton extends android.support.v7.widget.AppCompatButton {

    private HashMap<JKButtonState,Drawable> stateDrawableHashMap = new HashMap<>();
    private HashMap<JKButtonState,String> stateTitleHashMap = new HashMap<>();
    private HashMap<JKButtonState,Integer>  stateTitleColorHashMap = new HashMap<>();

    private JKButtonState state = JKButtonState.JKButtonStateNormal;
    private boolean mSelected = false;

    private final static String TAG = "JKButton";


    public JKButton(Context context) {
        super(context);

        init();
    }

    public JKButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public JKButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        setAllCaps(false);

        String normalTitle = getText().toString();
        Drawable normalDrawable = getBackground();
        Integer normalTitleColor = getTextColors().getDefaultColor();

        String title = stateTitleHashMap.get(JKButtonState.JKButtonStateNormal);
        if (title == null && normalTitle != null) {
            stateTitleHashMap.put(JKButtonState.JKButtonStateNormal,normalTitle);
        }

        Drawable background = stateDrawableHashMap.get(JKButtonState.JKButtonStateNormal);
        if (background == null && normalDrawable != null) {
            stateDrawableHashMap.put(JKButtonState.JKButtonStateNormal,normalDrawable);
        }

        stateTitleColorHashMap.put(JKButtonState.JKButtonStateNormal,normalTitleColor);

    }

    public void setBackgroundDrawableForState(JKButtonState state, Drawable drawable) {

        if (state == null) {
            return;
        }

        if (drawable != null) {
            stateDrawableHashMap.put(state,drawable);
        } else {
            stateDrawableHashMap.remove(state);
        }
    }

    public void setTitleForState(JKButtonState state,String title) {

        if (state == null) {
            return;
        }
        if (title == null) {
            stateTitleHashMap.remove(state);
        } else {
            stateTitleHashMap.put(state,title);
        }
    }

    public void setTitleColorForState(JKButtonState state, int color) {

        if (state == null) {
            return;
        }

        stateTitleColorHashMap.put(state,Integer.valueOf(color));
    }

    public void setText(String text) {
        super.setText(text);

        setTitleForState(state,text);
    }

    public void setTextColor(int color) {
        super.setTextColor(color);

        setTitleColorForState(state,color);
    }

    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);

        setBackgroundDrawableForState(state,drawable);
    }

    private void refreshUI() {

        Drawable drawable = stateDrawableHashMap.get(state);
        if (drawable == null) {
            drawable = stateDrawableHashMap.get(JKButtonState.JKButtonStateNormal);
        }
        super.setBackgroundDrawable(drawable);

        String title = stateTitleHashMap.get(state);
        if (title == null) {
            title = stateTitleHashMap.get(JKButtonState.JKButtonStateNormal);
        }
        super.setText(title);

        Integer colorInteger = stateTitleColorHashMap.get(state);
        if (colorInteger != null) {
            super.setTextColor(colorInteger.intValue());
        }
    }

    public void setSelection(boolean selection) {
        mSelected = selection;

        if (selection) {
            state = JKButtonState.JKButtonStateSelected;
        } else {
            state = JKButtonState.JKButtonStateNormal;
        }

        refreshUI();
    }

    public boolean getSelection() {
        return mSelected;
    }

    /**
     * 按钮触摸状态改变回调
     * */
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        boolean pressed = isPressed();

        if (pressed) {
            state = JKButtonState.JKButtonStateHighlight;
        } else {

            if (mSelected) {
                state = JKButtonState.JKButtonStateSelected;
            } else {
                state = JKButtonState.JKButtonStateNormal;
            }

        }

        refreshUI();

    }

    public static enum JKButtonState {
        JKButtonStateNormal,
        JKButtonStateHighlight,
        JKButtonStateSelected
    }
}
