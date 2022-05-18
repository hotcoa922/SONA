package com.hotcoa.sona.calendar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.applikeysolutions.cosmocalendar.listeners.OnMonthChangeListener;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.model.Month;
import com.applikeysolutions.cosmocalendar.selection.BaseCriteriaSelectionManager;
import com.applikeysolutions.cosmocalendar.settings.lists.DisabledDaysCriteria;
import com.applikeysolutions.cosmocalendar.settings.lists.DisabledDaysCriteriaType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.hotcoa.sona.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CalendarFragment extends Fragment {

    private static com.applikeysolutions.cosmocalendar.view.CalendarView calendarView;
    private static final long dayMilliSec = 86400000L;
    private static final long curTime = System.currentTimeMillis();
    private static Set<Long> disabledDaysSet;
    private final Calendar calendar = Calendar.getInstance();
    private int resId;
    private final static HashMap<String, String> mName = new HashMap<String, String>();
    private final SimpleDateFormat df = new SimpleDateFormat("MM");
    private Date curDate = new Date();


    public CalendarFragment() {
        disabledTimeSetting();
        mName.put("January", "01");
        mName.put("February", "02");
        mName.put("March", "03");
        mName.put("April", "04");
        mName.put("May", "05");
        mName.put("June", "06");
        mName.put("July", "07");
        mName.put("August", "08");
        mName.put("September", "09");
        mName.put("October", "10");
        mName.put("November", "11");
        mName.put("December", "12");
    }

    private void disabledTimeSetting() {
        int currentDay = calendar.get(Calendar.DATE);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int term = lastDay - currentDay;
        /*Log.d("calendar_log", "currentDay: " + currentDay);
        Log.d("calendar_log", "lastDay: " + lastDay);
        Log.d("calendar_log", "term: " + (lastDay - currentDay));*/

        Long[] temp = new Long[term];
        long nextTime = curTime + dayMilliSec;
        for(int i = 0; i < term; ++i) {
            temp[i] = nextTime;
            nextTime += dayMilliSec;
        }
        disabledDaysSet = new HashSet<Long>(Arrays.asList(temp));
        Log.d("calendar_log", term + " disabled");
    }

    private void setDisabledDays(com.applikeysolutions.cosmocalendar.view.CalendarView calendarView) {
        calendarView.setDisabledDays(disabledDaysSet);
        Log.d("calendar_log", "Disabled complete");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        String curMonth = df.format(curDate);
        calendarView = (com.applikeysolutions.cosmocalendar.view.CalendarView) rootView.findViewById(R.id.cosmo_calendar);
        resId = calendarView.getNextMonthIconRes();
        calendarView.setNextMonthIconRes(R.color.white);
        setDisabledDays(calendarView);
        onMonthChange(curMonth);
        return rootView;
    }

    private void onMonthChange(String curMonth) {
        calendarView.setOnMonthChangeListener(month -> {
            String tmp = month.getMonthName();
            String[] m = tmp.split(" ");

            if(!curMonth.equals(mName.get(m[0]))) {
                if(Integer.valueOf(curMonth) < Integer.valueOf(mName.get(m[0]))) {
                    calendarView.goToPreviousMonth();
                }
                calendarView.setNextMonthIconRes(resId);
                Log.d("calendar_log", "different");
            }
            else {
                calendarView.setNextMonthIconRes(R.color.white);
                Log.d("calendar_log", "same(disable button)");
            }
        });
    }
}