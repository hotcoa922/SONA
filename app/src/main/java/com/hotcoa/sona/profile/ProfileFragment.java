package com.hotcoa.sona.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hotcoa.sona.R;

public class ProfileFragment extends Fragment {


    EditText nickName;
    EditText diaryName;
    EditText birthYear;
    EditText birthMonth;
    EditText birthDay;

    Button save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        nickName = (EditText) rootView.findViewById(R.id.username_tv);
        diaryName = (EditText) rootView.findViewById(R.id.cng_diaryname_et);
        birthYear = (EditText) rootView.findViewById(R.id.cng_birth_year_tv);
        birthMonth = (EditText) rootView.findViewById(R.id.birth_month_tv);
        birthDay = (EditText) rootView.findViewById(R.id.birth_day_tv);
        save = (Button) rootView.findViewById(R.id.profilesave_bt);












        return rootView;
    }

}