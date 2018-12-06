package jack.com.jkutils.view.dateTimePicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import jack.com.jkutils.R;
import jack.com.jkutils.screen.Screen;
import jack.com.jkutils.view.dialog.BasicDialog;

public class DateTimePickerDialog extends BasicDialog {

    private Context mContext;
    private DateTimePickerDialog self = this;
    private boolean initialized = false;

    public enum DateTimePickerMode {
        DATE_TIME_PICKER_MODE_DATE,
        DATE_TIME_PICKER_MODE_TIME,
        DATE_TIME_PICKER_MODE_DATETIME
    }

    public interface DateTimePickerClickListener {

        void dateTimePickerDialogCanceled(DateTimePickerDialog dateTimePickerDialog);
        void dateTimePickerDialogConfirmed(DateTimePickerDialog dateTimePickerDialog, Calendar calendar);
    }

    private DateTimePickerMode mMode = DateTimePickerMode.DATE_TIME_PICKER_MODE_DATE;
    private DateTimePickerClickListener mClickListener = null;

    public static DateTimePickerDialog dateTimePickerDialog(Context context, DateTimePickerMode mode, DateTimePickerClickListener listener) {

        DateTimePickerDialog dialog = new DateTimePickerDialog(context);
        dialog.mClickListener = listener;
        dialog.mMode = mode;
        dialog.init();

        return dialog;
    }

    private DateTimePickerDialog(@NonNull final Context context) {
        super(context, new BasicDialog.BasicDialogSetupCallBack() {

            @Override
            public View createContentView(BasicDialog dialog) {
                return setupRootView(context);
            }

            @Override
            public int dialogGravity(BasicDialog dialog) {
                return BasicDialogContentGravityCenter;
            }
        });

        mContext = context;
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        mTitleTv.setText(title);
    }

    public void setMode(DateTimePickerMode mode) {
        mMode = mode;
    }

    private TextView mTitleTv;
    private DatePickerView mDatePicker;
    private TimePickerView mTimePicker;
    private Button mCancelBtn, mOkBtn;

    private void init() {

        View rootView = getRootView();

        mTitleTv = rootView.findViewById(R.id.jk_date_time_picker_title_tv);

        LinearLayout loopPanel = rootView.findViewById(R.id.jk_date_time_picker_loop_panel);
        setupPicker(loopPanel);

        mCancelBtn = rootView.findViewById(R.id.jk_date_time_picker_cancel_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mClickListener != null) {
                    mClickListener.dateTimePickerDialogCanceled(self);
                }

                dismiss();
            }
        });

        mOkBtn = rootView.findViewById(R.id.jk_date_time_picker_ok_btn);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mClickListener != null) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();

                    int year, month, day, hour, minute, second;
                    year = month = day = hour = minute = second = 0;

                    if (mDatePicker != null) {
                        year = mDatePicker.getYear();
                        month = mDatePicker.getMonth();
                        day = mDatePicker.getDay();
                    }
                    if (mTimePicker != null) {
                        hour = mTimePicker.getHour();
                        minute = mTimePicker.getMinute();
                        second = mTimePicker.getSecond();
                    }

                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, second);

                    mClickListener.dateTimePickerDialogConfirmed(self, calendar);
                }

                dismiss();
            }
        });


        initialized = true;
    }

    private void setupPicker(LinearLayout panel) {
        if (panel != null) {

            switch (mMode) {
                case DATE_TIME_PICKER_MODE_DATE: {

                    mDatePicker = DatePickerView.buildDatePickerView(mContext, DatePickerView.DatePickerViewLayoutMode.DATE_PICKER_VIEW_LAYOUT_MODE_REGULAR);

                    LinearLayout.LayoutParams dateLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    dateLayoutParams.setMargins(Screen.dp2px(mContext, 20),0,Screen.dp2px(mContext, 20), 0);
                    panel.addView(mDatePicker, dateLayoutParams);
                }
                break;
                case DATE_TIME_PICKER_MODE_TIME: {

                    mTimePicker = TimePickerView.buildTimePickerView(mContext);
                    LinearLayout.LayoutParams timeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    timeLayoutParams.setMargins(Screen.dp2px(mContext, 20),0,Screen.dp2px(mContext, 20), 0);
                    panel.addView(mTimePicker, timeLayoutParams);
                }
                break;
                case DATE_TIME_PICKER_MODE_DATETIME: {

                    // Date
                    mDatePicker = DatePickerView.buildDatePickerView(mContext, DatePickerView.DatePickerViewLayoutMode.DATE_PICKER_VIEW_LAYOUT_MODE_COMPACT);
                    LinearLayout.LayoutParams dateLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    dateLayoutParams.setMargins(0,0,Screen.dp2px(mContext, 5), 0);
                    panel.addView(mDatePicker, dateLayoutParams);


                    // Time
                    mTimePicker = TimePickerView.buildTimePickerView(mContext);
                    LinearLayout.LayoutParams timeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    panel.addView(mTimePicker, timeLayoutParams);
                }
                break;
            }
        }
    }


    @Override
    public void show() {

        if (!initialized) {
            init();
        }

        super.show();
    }

    public void setCalendar(Calendar calendar) {
        if (calendar != null) {

            if (mDatePicker != null) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                mDatePicker.setYear(year);
                mDatePicker.setMonth(month);
                mDatePicker.setDay(day);
            }

            if (mTimePicker != null) {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);

                mTimePicker.setHour(hour);
                mTimePicker.setMinute(minute);
                mTimePicker.setSecond(second);
            }

        }
    }

    private static View setupRootView(Context context) {

        if (context == null) {
            return null;
        }

        RelativeLayout root = new RelativeLayout(context);

        int margin = Screen.dp2px(context, 20);
        int widthPixels = Screen.screenSize(context).getWidth();
        widthPixels = widthPixels - 2 * margin;
        int heightPixels = widthPixels + Screen.dp2px(context, 25);

        View contentView = (View)LayoutInflater.from(context).inflate(R.layout.jk_date_time_picker_dialog_content_view,null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widthPixels, heightPixels);
        layoutParams.setMargins(margin, 0 , margin, 0);

        root.addView(contentView, layoutParams);

        return root;
    }


}
