package com.hotcoa.sona.calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener;

import com.applikeysolutions.cosmocalendar.settings.appearance.ConnectedDayIconPosition;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.MainActivity;
import com.hotcoa.sona.utility.SharedPrefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class CalendarFragment extends Fragment implements OnDaySelectedListener{

    private com.applikeysolutions.cosmocalendar.view.CalendarView calendarView;
    private static final long dayMilliSec = 86400000L;
    private static final long curTime = System.currentTimeMillis();
    private static Set<Long> disabledDaysSet;
    private final Calendar calendar = Calendar.getInstance();
    private int resId;
    private final static HashMap<String, String> mName = new HashMap<>();
    private static Set<String> sDate = new HashSet<>();;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat df = new SimpleDateFormat("MM");
    private final Date curDate = new Date();

    MainActivity mainActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

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
        disabledDaysSet = new HashSet<>(Arrays.asList(temp));
        Log.d("calendar_log", term + " disabled");
    }

    private void setDisabledDays(com.applikeysolutions.cosmocalendar.view.CalendarView calendarView) {
        calendarView.setDisabledDays(disabledDaysSet);
        Log.d("calendar_log", "Disabled complete");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        SharedPreferences saveDatePrefs = getActivity().getSharedPreferences("saveDate", Context.MODE_PRIVATE);
        SharedPreferences saveDaysPrefs = getActivity().getSharedPreferences("saveDays", Context.MODE_PRIVATE);
        SharedPreferences.Editor saveDaysEditor = saveDaysPrefs.edit();

        if(!saveDaysPrefs.getStringSet("saveDays", new HashSet<>()).isEmpty()) {
            Log.d("calendar_log", "null");

            Set<String> temp = saveDaysPrefs.getStringSet("saveDays", new HashSet<>());
            sDate.addAll(temp);
        }
        sDate.add(saveDatePrefs.getString("saveDate", "1999년 12월 17일"));

        SharedPrefs.setInt(rootView.getContext(), "screenshotCounter", 1);

        String curMonth = df.format(curDate);
        calendarView = rootView.findViewById(R.id.cosmo_calendar);
        resId = calendarView.getNextMonthIconRes();
        calendarView.setNextMonthIconRes(R.color.white);
        setDisabledDays(calendarView);
        onMonthChange(curMonth);

        Set<Long> days = new TreeSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        try {
            Iterator<String> it = sDate.iterator();
            while(it.hasNext()) {
                days.add(sdf.parse(it.next()).getTime());
            }
            saveDaysEditor.putStringSet("saveDays", sDate);
            saveDaysEditor.commit();
        }catch(Exception e) {
            Log.d("calendar_log", e.getMessage());
        }
        ConnectedDays connectedDays = new ConnectedDays(days, R.color.red, R.color.teal_200, R.color.purple_500);
        calendarView.addConnectedDays(connectedDays);
        calendarView.setConnectedDayIconRes(R.drawable.ic_baseline_stars_24);   // Drawable
        calendarView.setConnectedDayIconPosition(ConnectedDayIconPosition.TOP);// TOP & BOTTOM
        calendarView.update();
        Button button_writeDiary = rootView.findViewById(R.id.button_write);
        Button button_contents = rootView.findViewById(R.id.button_contents);
        Button button_share = rootView.findViewById(R.id.button_share);
        Button button_checkDairy = rootView.findViewById(R.id.button_check);

        onWriteClick(button_writeDiary);
        onContentsClick(button_contents);
        onShareClick(button_share);
        onCheckClick(button_checkDairy);
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
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SONA/screenshot";
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

    private void onShareClick(Button shareButton) {
        shareButton.setOnClickListener(view -> {
            try {
                screenshot(calendarView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void onContentsClick(Button contentsButton) {
        contentsButton.setOnClickListener(view -> {
            if(calendarView.getSelectedDates().size() <= 0) {
                alertDialog();
            }
            else mainActivity.onChangeFragment(MainActivity.Direction.CalendarToContents);
        });
    }

    private void onWriteClick(Button writeButton) {
        writeButton.setOnClickListener(view -> {
            if(calendarView.getSelectedDates().size() <= 0) {
                alertDialog();
            }
            else {
                daySave();
                pathSave();
                mainActivity.onChangeFragment(MainActivity.Direction.CalendarToWrite);
            }
        });
    }

        private void onCheckClick(Button checkButton) {
            checkButton.setOnClickListener(view -> {
                if(calendarView.getSelectedDates().size() <= 0) {
                    alertDialog();
                }
                else {
                    daySave();
                    mainActivity.onChangeFragment(MainActivity.Direction.CalendarToCheck);
                }
            });
        }


    private void daySave() {
        List<Day> day = calendarView.getSelectedDays();
        String date = day.get(0).getCalendar().get(Calendar.YEAR) + "년 " + (day.get(0).getCalendar().get(Calendar.MONTH) + 1) + "월 " + day.get(0).getDayNumber() + "일";
        Log.d("calendar_log", "Selected Days : " + date);
        SharedPreferences prefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("curDate", date);
        editor.apply();
    }
    private void pathSave() {
        SharedPreferences pathPrefs = getActivity().getSharedPreferences("curDateTxtPath", Context.MODE_PRIVATE);
        SharedPreferences datePrefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pathPrefs.edit();
        String curDate = datePrefs.getString("curDate","");
        String diary_path = "/storage/emulated/0/SONA/text/" + curDate + ".txt";
        editor.putString("curDateTxtPath", diary_path);
        editor.apply();
    }

    private void onMonthChange(String curMonth) {
        calendarView.setOnMonthChangeListener(month -> {
            String tmp = month.getMonthName();
            String[] m = tmp.split(" ");

            /*if(!curMonth.equals(mName.get(m[0]))) {
                if(Integer.parseInt(curMonth) < Integer.parseInt(Objects.requireNonNull(mName.get(m[0])))
                && m[1].equals(String.valueOf(calendar.get(Calendar.YEAR))))
                    calendarView.goToPreviousMonth();
                calendarView.setNextMonthIconRes(resId);
                Log.d("calendar_log", "different");
            }
            else {
                calendarView.setNextMonthIconRes(R.color.white);
                Log.d("calendar_log", "same(disable button)");
            }*/
            if(!curMonth.equals(mName.get(m[0]))) {
                calendarView.setNextMonthIconRes(resId);
            }
            else calendarView.setNextMonthIconRes(R.color.white);
            Log.d("calendar_log", "same(disable button)");
        });
    }

    @Override
    public void onDaySelected() {
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