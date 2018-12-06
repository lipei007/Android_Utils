package jack.com.jkutils.view.dateTimePicker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import jack.com.jkutils.JKUtils;
import jack.com.jkutils.screen.Screen;

import static jack.com.jkutils.view.dateTimePicker.DatePickerView.DatePickerViewLayoutMode.DATE_PICKER_VIEW_LAYOUT_MODE_REGULAR;

public class DatePickerView extends LinearLayout {

    public enum DatePickerViewLayoutMode {
        DATE_PICKER_VIEW_LAYOUT_MODE_COMPACT,
        DATE_PICKER_VIEW_LAYOUT_MODE_REGULAR
    }

    DatePickerViewLayoutMode mMode = DATE_PICKER_VIEW_LAYOUT_MODE_REGULAR;

    public static DatePickerView buildDatePickerView(Context context, DatePickerViewLayoutMode layoutMode) {

        DatePickerView datePickerView = new DatePickerView(context);
        datePickerView.mMode = layoutMode;
        datePickerView.init();
        return datePickerView;
    }

    private DatePickerView(Context context) {
        super(context);
    }

    private DatePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private DatePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Calendar
     * year:    从1开始
     * month:   从0开始
     * day:     从1开始
     * */
    private Calendar mCalendar = Calendar.getInstance();
    private boolean initialized = false;
    private void init() {
        if (!initialized) {
            setupView();
        }
        initialized = true;
    }

    NumberPicker mYearPicker, mMonthPicker, mDayPicker;
    private void setupView() {

//        String languageCode = RAUtil.getSystemLanguageCode(getContext());
//        Log.d("DatePicker", "setupView: " + languageCode);
//
//        if (languageCode.equals("zh")) {
//
//        } else {
//
//        }

        // year
        mYearPicker = new NumberPicker(getContext());
        int yearWidth = Screen.dp2px(getContext(), 60);
        int yearWeight = 1;
        if (mMode == DATE_PICKER_VIEW_LAYOUT_MODE_REGULAR) {
            yearWidth = 0;
            yearWeight = 2;
        }

        setupPicker(mYearPicker, 10000, yearWidth, yearWeight,"-");
        setYear(mCalendar.get(Calendar.YEAR));
        mYearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                p_setYear(newVal + 1);
            }
        });

        // month
        mMonthPicker = new NumberPicker(getContext());
        int monthWidth = Screen.dp2px(getContext(), 30);
        int monthWeight = 1;
        if (mMode == DATE_PICKER_VIEW_LAYOUT_MODE_REGULAR) {
            monthWidth = 0;
            monthWeight = 1;
        }

        setupPicker(mMonthPicker, 12, monthWidth, monthWeight, "-");
        setMonth(mCalendar.get(Calendar.MONTH) + 1);
        mMonthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                p_setMonth(newVal + 1);
            }
        });

        // day
        mDayPicker = new NumberPicker(getContext());
        int dayWidth = Screen.dp2px(getContext(), 30);
        int dayWeight = 1;
        if (mMode == DATE_PICKER_VIEW_LAYOUT_MODE_REGULAR) {
            dayWidth = 0;
            dayWeight = 1;
        }

        setupPicker(mDayPicker, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), dayWidth, dayWeight, "");
        mDayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                p_setDay(newVal + 1);
            }
        });
        setDay(mCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setupPicker(NumberPicker picker, int max, int width, int weight , String unit) {

        initPicker(picker, max);

        TextView unitTv = new TextView(getContext());
        unitTv.setTextColor(Color.BLACK);
        unitTv.setGravity(Gravity.CENTER);
        unitTv.setText(unit);

        LayoutParams pickerParams;
        if (width != 0) {
            pickerParams = new LayoutParams(width, LayoutParams.MATCH_PARENT);
        } else {
            pickerParams = new LayoutParams(width, LayoutParams.MATCH_PARENT,weight);
        }

        addView(picker, pickerParams);

        LayoutParams unitParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        int margin= Screen.dp2px(getContext(), 5);
        unitParams.setMargins(margin,margin,margin,margin);

        addView(unitTv, unitParams);
    }

    private void initPicker(NumberPicker picker, int max) {

        ArrayList<String> hourArr = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            hourArr.add(String.format("%d",i + 1));
        }
        String[] values = JKUtils.list2Array(hourArr);

        int oldMax = picker.getMaxValue();

        picker.setMinValue(0);
        picker.setMaxValue(0);
        if (oldMax > max) {

            picker.setMaxValue(max - 1);
            picker.setDisplayedValues(values);
        } else {

            picker.setDisplayedValues(values);
            picker.setMaxValue(max - 1);
        }
        picker.setValue(0);


        // 禁止编辑输入
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private int year, month, day;

    private int getNumberOfDays(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.clear();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);

        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void resetCalendar() {
        mCalendar.clear();
        int maxDay = getNumberOfDays(year, month);
        if (maxDay < day) {
            day = maxDay;
        }

        int curMaxDay = mDayPicker.getMaxValue() + 1;
        if (maxDay != curMaxDay) {
            initPicker(mDayPicker, maxDay);
        }
        mDayPicker.setValue(day - 1);

        mCalendar.set(year, month - 1, day);
    }

    private void p_setYear(int year) {
        this.year = year;

        if (initialized) {
            resetCalendar();
        }
    }

    private void p_setMonth(int month) {
        this.month = month;

        if (initialized) {
            resetCalendar();
        }
    }

    private void p_setDay(int day) {
        this.day = day;

        if (initialized) {
            resetCalendar();
        }
    }


    public void setYear(int year) {
        p_setYear(year);
        mYearPicker.setValue(year - 1);
    }

    public void setMonth(int month) {
        p_setMonth(month);
        mMonthPicker.setValue(month - 1);
    }

    public void setDay(int day) {
        p_setDay(day);
        mDayPicker.setValue(day - 1);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
