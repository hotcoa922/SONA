package com.hotcoa.sona.calendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.applikeysolutions.cosmocalendar.listeners.OnMonthChangeListener;
import com.applikeysolutions.cosmocalendar.model.Month;
import com.applikeysolutions.cosmocalendar.settings.lists.DisabledDaysCriteria;
import com.applikeysolutions.cosmocalendar.settings.lists.DisabledDaysCriteriaType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.hotcoa.sona.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CalendarFragment extends Fragment {

    private com.applikeysolutions.cosmocalendar.view.CalendarView calendarView;
    private final static HashMap<String, Integer> mName = new HashMap<String, Integer>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        Calendar cal = Calendar.getInstance();
        final int cur_MONTH = cal.get(cal.MONTH) + 1;
        calendarView = (com.applikeysolutions.cosmocalendar.view.CalendarView) rootView.findViewById(R.id.cosmo_calendar);
        Log.d("cal_currentMonth", String.valueOf(cur_MONTH));
        Log.d("cal_DAY_OF_MONTH", String.valueOf(cal.DAY_OF_MONTH));

        mName.put("January", 1);
        mName.put("February", 2);
        mName.put("March", 3);
        mName.put("April", 4);
        mName.put("May", 5);
        mName.put("June", 6);
        mName.put("July", 7);
        mName.put("August", 8);
        mName.put("September", 9);
        mName.put("October", 10);
        mName.put("November", 11);
        mName.put("December", 12);


        disable(calendarView, cal, cal.get(cal.DAY_OF_MONTH) + 1, cal.getActualMaximum(cal.DAY_OF_MONTH));
        calendarView.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChanged(Month month) {
                String tmp = month.getMonthName();
                String[] m = tmp.split(" ");
                if(cur_MONTH < mName.get(m[0])) {
                    Log.d("cal_", "false");
                }
            }
        });
        return rootView;
    }


    private void disable(com.applikeysolutions.cosmocalendar.view.CalendarView calendarView, Calendar cal, int start, int end) {
        calendarView.setDisabledDaysCriteria(
                new DisabledDaysCriteria(
                        start,
                        end,
                        DisabledDaysCriteriaType.DAYS_OF_MONTH)
        );
    }
}