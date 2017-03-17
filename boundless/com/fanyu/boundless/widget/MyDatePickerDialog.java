package com.fanyu.boundless.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.util.StringUtils;
import com.xiaomi.mipush.sdk.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyDatePickerDialog extends AlertDialog implements OnClickListener, OnValueChangeListener, Formatter {
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private Button btn_cancel;
    private Button btn_sure;
    private final Context context;
    private final Calendar mCalendar;
    private final OnDateSetListener mDateSetListener;
    private final NumberPicker mDay;
    private final NumberPicker mMonth;
    private final NumberPicker mYear;
    private View view;

    public interface OnDateSetListener {
        void onDateSet(View view, String str, int i, int i2, int i3);
    }

    public MyDatePickerDialog(Context context, String title, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, title, callBack, null, year, monthOfYear, dayOfMonth);
    }

    public MyDatePickerDialog(Context context, String title, OnDateSetListener callBack, String dateStr) {
        this(context, title, callBack, dateStr, -1, -1, -1);
    }

    public MyDatePickerDialog(Context context, String title, OnDateSetListener listener, String dateStr, int year, int monthOfYear, int dayOfMonth) {
        super(context);
        this.context = context;
        this.mDateSetListener = listener;
        this.mCalendar = Calendar.getInstance();
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        if (year < 0 || monthOfYear < 0 || dayOfMonth < 0) {
            if (!TextUtils.isEmpty(dateStr)) {
                try {
                    this.mCalendar.setTime(new SimpleDateFormat(StringUtils.DEFAULT_FORMAT_DATE).parse(dateStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            year = this.mCalendar.get(1);
            monthOfYear = this.mCalendar.get(2);
            dayOfMonth = this.mCalendar.get(5);
        }
        this.view = LayoutInflater.from(getContext()).inflate(R.layout.date_picker_dialog, null);
        setView(this.view);
        this.mYear = (NumberPicker) this.view.findViewById(R.id.year);
        this.mMonth = (NumberPicker) this.view.findViewById(R.id.month);
        this.mDay = (NumberPicker) this.view.findViewById(R.id.day);
        this.btn_sure = (Button) this.view.findViewById(R.id.btn_datetime_sure);
        this.btn_cancel = (Button) this.view.findViewById(R.id.btn_datetime_cancel);
        this.btn_sure.setOnClickListener(this);
        this.btn_cancel.setOnClickListener(this);
        this.mYear.setMaxValue(2100);
        this.mYear.setMinValue(1900);
        this.mYear.setOnLongPressUpdateInterval(100);
        this.mYear.setFocusable(true);
        this.mYear.setFocusableInTouchMode(true);
        this.mYear.setFormatter(this);
        this.mYear.setValue(year);
        this.mYear.setOnValueChangedListener(this);
        this.mMonth.setMaxValue(12);
        this.mMonth.setMinValue(1);
        this.mMonth.setFocusable(true);
        this.mMonth.setFocusableInTouchMode(true);
        this.mMonth.setFormatter(this);
        this.mMonth.setValue(monthOfYear + 1);
        this.mMonth.setOnValueChangedListener(this);
        this.mDay.setMaxValue(this.mCalendar.getActualMaximum(5));
        this.mDay.setMinValue(1);
        this.mDay.setOnLongPressUpdateInterval(200);
        this.mDay.setFocusable(true);
        this.mDay.setFocusableInTouchMode(true);
        this.mDay.setFormatter(this);
        this.mDay.setValue(dayOfMonth);
        this.mDay.setOnValueChangedListener(this);
    }

    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            return "0" + tmpStr;
        }
        return tmpStr;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker == this.mDay) {
            int maxDayOfMonth = this.mCalendar.getActualMaximum(5);
            if (oldVal == maxDayOfMonth && newVal == 1) {
                this.mCalendar.add(5, 1);
            } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                this.mCalendar.add(5, -1);
            } else {
                this.mCalendar.add(5, newVal - oldVal);
            }
        } else if (picker == this.mMonth) {
            if (oldVal == 12 && newVal == 1) {
                this.mCalendar.add(2, 1);
            } else if (oldVal == 1 && newVal == 12) {
                this.mCalendar.add(2, -1);
            } else {
                this.mCalendar.add(2, newVal - oldVal);
            }
        } else if (picker == this.mYear) {
            this.mCalendar.set(1, newVal);
        } else {
            throw new IllegalArgumentException();
        }
        setDate(this.mCalendar.get(1), this.mCalendar.get(2), this.mCalendar.get(5));
    }

    public void onClick(View v) {
        if (v == this.btn_cancel) {
            dismiss();
        } else if (v == this.btn_sure) {
            this.mDateSetListener.onDateSet(this.view, format(getYear()) + Constants.ACCEPT_TIME_SEPARATOR_SERVER + format(getMonth()) + Constants.ACCEPT_TIME_SEPARATOR_SERVER + format(getDayOfMonth()), getYear(), getMonth(), getDayOfMonth());
            dismiss();
        }
    }

    private void setDate(int year, int month, int dayOfMonth) {
        this.mCalendar.set(year, month, dayOfMonth);
        this.mDay.setMaxValue(this.mCalendar.getActualMaximum(5));
        this.mDay.setValue(this.mCalendar.get(5));
        this.mMonth.setValue(this.mCalendar.get(2) + 1);
        this.mYear.setValue(this.mCalendar.get(1));
    }

    public void setMinYear(int minYear) {
        this.mYear.setMinValue(minYear);
    }

    public void setMaxYear(int maxYear) {
        this.mYear.setMaxValue(maxYear);
    }

    public void setMinMonth(int minMonth) {
        this.mMonth.setMinValue(minMonth);
    }

    public void setMaxMonth(int maxMonth) {
        this.mMonth.setMaxValue(maxMonth);
    }

    public void setMinDay(int minDay) {
        this.mMonth.setMinValue(minDay);
    }

    public void setMaxDay(int maxDay) {
        this.mMonth.setMaxValue(maxDay);
    }

    public int getYear() {
        return this.mCalendar.get(1);
    }

    public int getMonth() {
        return this.mCalendar.get(2) + 1;
    }

    public int getDayOfMonth() {
        return this.mCalendar.get(5);
    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        this.mYear.setValue(year);
        this.mMonth.setValue(monthOfYear);
        this.mDay.setValue(dayOfMonth);
    }

    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, getYear());
        state.putInt(MONTH, getMonth());
        state.putInt(DAY, getDayOfMonth());
        return state;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        this.mYear.setValue(year);
        this.mMonth.setValue(month);
        this.mDay.setValue(day);
    }
}
