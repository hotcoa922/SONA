package com.hotcoa.sona.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hotcoa.sona.R;
import com.hotcoa.sona.calendar.CalendarFragment;
import com.hotcoa.sona.writediary.WriteDiaryFragment;

import java.util.Stack;


public class MainFragment extends BaseFragment {


    TextView diaryname;
    Button diarybt;
    CalendarFragment calendarFragment;

    SharedPreferences sPf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        diarybt = rootView.findViewById(R.id.diary_bt);
        diaryname = rootView.findViewById(R.id.main_diary_name_tv);
        calendarFragment = new CalendarFragment();

        sPf = getActivity().getSharedPreferences("profile_info",Context.MODE_PRIVATE);  //PREFS 파일 이름

        diaryname.setText(sPf.getString("diaryName","SONA(프로필에서 변경가능)"));

        diarybt.setOnClickListener(view -> {
            mainActivity.onChangeFragment(MainActivity.Direction.calendarGo);    //인자값을 넣어도 된다.
        });



        return rootView;
    }
}