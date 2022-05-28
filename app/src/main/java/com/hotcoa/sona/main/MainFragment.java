package com.hotcoa.sona.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotcoa.sona.R;
import com.hotcoa.sona.calendar.CalendarFragment;
import com.hotcoa.sona.writediary.WriteDiaryFragment;

import java.util.Stack;


public class MainFragment extends BaseFragment {


    Button diarybt;
    CalendarFragment calendarFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        diarybt = rootView.findViewById(R.id.diary_bt);
        calendarFragment = new CalendarFragment();

        diarybt.setOnClickListener(view -> {
            mainActivity.onChangeFragment(MainActivity.Direction.calendarGo);    //인자값을 넣어도 된다.
        });



        return rootView;
    }
}