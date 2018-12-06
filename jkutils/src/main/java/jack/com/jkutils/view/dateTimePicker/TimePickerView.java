package jack.com.jkutils.view.dateTimePicker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jack.com.jkutils.R;

public class TimePickerView extends LinearLayout {

    public static TimePickerView buildTimePickerView(Context context) {

        TimePickerView pickerView = (TimePickerView)LayoutInflater.from(context).inflate(R.layout.jk_time_picker_view,null);
        pickerView.init();
        return pickerView;
    }

    public TimePickerView(Context context) {
        super(context);
    }

    public TimePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private NumberPicker mHourPicker, mMinutePicker, mSecondPicker;
    private void init() {

        initView();

        initTime();
    }

    private void initTime() {

        Calendar calendar = Calendar.getInstance();
        setHour(calendar.get(Calendar.HOUR_OF_DAY));
        setMinute(calendar.get(Calendar.MINUTE));
        setSecond(calendar.get(Calendar.SECOND));
    }

    private void initView() {

        // hour
        mHourPicker = findViewById(R.id.jk_time_picker_hour);
        initPicker(mHourPicker, 24);

        // minute
        mMinutePicker = findViewById(R.id.jk_time_picker_minute);
        initPicker(mMinutePicker, 60);

        // second
        mSecondPicker = findViewById(R.id.jk_time_picker_second);
        initPicker(mSecondPicker, 60);
    }

    private void initPicker(NumberPicker picker, int max) {

        ArrayList<String> hourArr = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            hourArr.add(String.format("%02d",i));
        }
        String[] values = list2Array(hourArr);
        picker.setDisplayedValues(values);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setMinValue(0);
        picker.setMaxValue(max - 1);
        picker.setValue(0);
    }

    private String[] list2Array(List list) {
        if (list != null) {
            return (String[])list.toArray(new String[list.size()]);
        }
        return null;
    }

    private int hour, minute, second;

    public void setHour(int hour) {
        if (hour >= 24) {
            hour = hour % 24;
        }
        this.hour = hour;

        mHourPicker.setValue(hour);
    }

    public void setMinute(int minute) {
        if (minute >= 60) {
            minute = minute % 60;
        }
        this.minute = minute;

        mMinutePicker.setValue(minute);
    }

    public void setSecond(int second) {
        if (second >= 60) {
            second = second % 60;
        }
        this.second = second;

        mSecondPicker.setValue(second);
    }

    public int getHour() {
        if (mHourPicker != null) {
            hour = mHourPicker.getValue();
        }
        return hour;
    }

    public int getMinute() {
        if (mMinutePicker != null) {
            minute = mMinutePicker.getValue();
        }
        return minute;
    }

    public int getSecond() {
        if (mSecondPicker != null) {
            second = mSecondPicker.getValue();
        }
        return second;
    }
}
