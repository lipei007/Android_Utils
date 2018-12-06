package jack.com.jkutils.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import jack.com.jkutils.R;


public class BasicDialog extends Dialog {

    private Context mCtx;
    private View mRootView;
    private BasicDialogSetupCallBack mCallBack;

    public final static int BasicDialogContentGravityTop = 0;
    public final static int BasicDialogContentGravityCenter = 1;
    public final static int BasicDialogContentGravityBottom = 2;

    public BasicDialog(@NonNull Context context, BasicDialogSetupCallBack callBack) {
        this(context);

        mCallBack = callBack;

        mCtx = context;
        init();
    }

    private BasicDialog(@NonNull Context context) {
        this(context, R.style.JKDialog);
    }

    private BasicDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private void init() {
        if (mCallBack != null) {
            mRootView = mCallBack.createContentView(this);
            if (mRootView != null) {
                setContentView(mRootView);
            }
        }
    }

    public View getRootView() {
        return mRootView;
    }

    @Override
    public void show() {

        if (mRootView == null) {
            super.show();
            return;
        }

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {

            int gravity = BasicDialogContentGravityCenter;
            if (mCallBack != null) {
                gravity = mCallBack.dialogGravity(this);
            }

            switch (gravity) {
                case BasicDialogContentGravityTop: {
                    dialogWindow.setGravity(Gravity.TOP);
                }
                break;
                case BasicDialogContentGravityCenter: {
                    dialogWindow.setGravity(Gravity.CENTER);
                }
                break;
                case BasicDialogContentGravityBottom: {
                    dialogWindow.setGravity(Gravity.BOTTOM);
                }
                break;
                default: {
                    dialogWindow.setGravity(Gravity.CENTER);
                }
                break;
            }

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

    public interface BasicDialogSetupCallBack {

        View createContentView(final BasicDialog dialog);
        int dialogGravity(final BasicDialog dialog);

    }
}
