package com.hotcoa.sona.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener;
import com.hotcoa.sona.R;
import com.hotcoa.sona.contents.ContentsFragment;
import com.hotcoa.sona.utility.SharedPrefs;
import com.hotcoa.sona.writediary.WriteDiaryFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CalendarFragment extends Fragment implements OnDaySelectedListener{

    private static com.applikeysolutions.cosmocalendar.view.CalendarView calendarView;
    private static final long dayMilliSec = 86400000L;
    private static final long curTime = System.currentTimeMillis();
    private static Set<Long> disabledDaysSet;
    private final Calendar calendar = Calendar.getInstance();
    private int resId;
    private final static HashMap<String, String> mName = new HashMap<String, String>();
    private final SimpleDateFormat df = new SimpleDateFormat("MM");
    private final Date curDate = new Date();
    private WriteDiaryFragment writeDiaryFragment;
    private ContentsFragment contentsFragment;

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

        SharedPrefs.setInt(rootView.getContext(), "screenshotCounter", 1);
        writeDiaryFragment = new WriteDiaryFragment();


        String curMonth = df.format(curDate);
        calendarView = (com.applikeysolutions.cosmocalendar.view.CalendarView) rootView.findViewById(R.id.cosmo_calendar);
        resId = calendarView.getNextMonthIconRes();
        calendarView.setNextMonthIconRes(R.color.white);
        setDisabledDays(calendarView);
        onMonthChange(curMonth);

        Button button_writeDiary = rootView.findViewById(R.id.button_write);
        Button button_contents = rootView.findViewById(R.id.button_contents);
        Button button_share = rootView.findViewById(R.id.button_share);
        onWriteClick(button_writeDiary);
        onContentsClick(button_contents);
        onShareClick(button_share, rootView);
        return rootView;
    }

    private void screenshot(View view) throws Exception {
        view.setDrawingCacheEnabled(true);
        Bitmap screenshot = view.getDrawingCache();
        SharedPreferences prefs = getActivity().getSharedPreferences("screenshotCounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int counter = prefs.getInt("screenshotCounter", 1);
        editor.putInt("screenshotCounter", counter + 1);
        editor.apply();
        String filename = "screenshot"+"_"+counter+".png";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SONA/image";
        File f = new File(path, filename);
        if(f.createNewFile()) {
            Log.d("calendar_log", "make file success");
            Toast.makeText(view.getContext(), "스크린샷이 저장되었습니다!", Toast.LENGTH_SHORT).show();
        }
        else Log.d("calendar_log", "make file fail");
        OutputStream outStream = new FileOutputStream(f);
        screenshot.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        outStream.close();
        view.setDrawingCacheEnabled(false);
    }

    private void onShareClick(Button shareButton, View rootView) {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    screenshot(calendarView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onContentsClick(Button contentsButton) {
        contentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendarView.getSelectedDates().size() <= 0) {
                    alertDialog();
                    return;
                }
                else getParentFragmentManager().beginTransaction().replace(R.id.fragment_container_x, contentsFragment).commit();
            }
        });
    }

    private void onWriteClick(Button writeButton) {
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendarView.getSelectedDates().size() <= 0) {
                    alertDialog();
                    return;
                }
                else getParentFragmentManager().beginTransaction().replace(R.id.fragment_container_x, writeDiaryFragment).commit();
            }
        });
    }

    private void onMonthChange(String curMonth) {
        calendarView.setOnMonthChangeListener(month -> {
            String tmp = month.getMonthName();
            String[] m = tmp.split(" ");

            if(!curMonth.equals(mName.get(m[0]))) {
                if(Integer.parseInt(curMonth) < Integer.parseInt(mName.get(m[0]))) {
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

    @Override
    public void onDaySelected() {
        List<Calendar> day = calendarView.getSelectedDates();
        Log.d("calendar_log", "Selected Dates : " + calendarView.getSelectedDates().size());
        if (calendarView.getSelectedDates().size() <= 0) {
            return;
        }
        Log.d("calendar_log", "Selected Days : " + calendarView.getSelectedDays());
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("달력에서 날짜를 먼저 선택해주세요!").setTitle("Calendar");
        builder.setPositiveButton("확인", (dialogInterface, i) -> {});
        builder.setIcon(R.drawable.ic_circle_info_solid);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}