package jack.com.jkutils.view.actionSheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashMap;

import jack.com.jkutils.R;
import jack.com.jkutils.view.button.JKButton;

public class ActionSheet extends Dialog implements View.OnClickListener {

    private Context mCtx;
    private LinearLayout mRootView;
    private HashMap<Button,View.OnClickListener> buttonListenerMap = new HashMap<>();

    public ActionSheet(@NonNull Context context) {
        this(context,R.style.actionSheet);

    }

    public ActionSheet(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        mCtx = context;
        init();
    }

    protected ActionSheet(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

        mCtx = context;
        init();
    }

    private void init() {
        mRootView = (LinearLayout) LayoutInflater.from(mCtx).inflate(R.layout.action_sheet, null);
        setContentView(mRootView);
    }

    public void addAction(String title, ActionType actionType, View.OnClickListener clickListener) {

        JKButton button = new JKButton(mCtx);
        button.setText(title);
        button.setTextColor(Color.parseColor("#2577ff"));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        button.setAllCaps(false);
        button.setBackgroundResource(R.drawable.actionsheet_round_corner_normal_bg);
        button.setOnClickListener(this);
        if (clickListener != null) {
            buttonListenerMap.put(button,clickListener);
        }

        button.setTitleColorForState(JKButton.JKButtonState.JKButtonStateHighlight,Color.RED);
        button.setBackgroundDrawableForState(JKButton.JKButtonState.JKButtonStateNormal,mCtx.getResources().getDrawable(R.drawable.actionsheet_round_corner_normal_bg));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp2px(mCtx,50));

        int marginH = dp2px(mCtx,10);
        int marginV = dp2px(mCtx,5);
        switch (actionType) {
            case ACtionTypeCancel: {
                layoutParams.setMargins(marginH,marginV,marginH,marginV);

                TextPaint tp = button.getPaint();
                tp.setFakeBoldText(true);
            }
            break;
            case ActionTypeDefault: {
                layoutParams.setMargins(marginH,0,marginH,marginV);
            }
            break;
        }

        mRootView.addView(button,layoutParams);
    }

    private static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void show() {

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {

            dialogWindow.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            layoutParams.x = 0; // 新位置X坐标
            layoutParams.y = 0; // 新位置Y坐标
            layoutParams.width = (int) mCtx.getResources().getDisplayMetrics().widthPixels; // 宽度
            mRootView.measure(0, 0);
            layoutParams.height = mRootView.getMeasuredHeight();

            layoutParams.alpha = 9f; // 透明度
            dialogWindow.setAttributes(layoutParams);
        }

        super.show();
    }

    @Override
    public void onClick(View v) {

        dismiss();

        View.OnClickListener listener = buttonListenerMap.get(v);
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public enum ActionType {
        ActionTypeDefault,
        ACtionTypeCancel
    }
}
