package com.hotcoa.sona.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotcoa.sona.R;



public class ProfileEditFragment extends Fragment {

    ImageView profileImg;

    EditText nickName;
    EditText diaryName;
    EditText birthYear;
    EditText birthMonth;
    EditText birthDay;

    Button save;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        profileImg = (ImageView) rootView.findViewById(R.id.cng_profile_img);

        nickName = (EditText) rootView.findViewById(R.id.cng_username_et);
        diaryName = (EditText) rootView.findViewById(R.id.cng_diaryname_et);
        birthYear = (EditText) rootView.findViewById(R.id.cng_birth_year_et);
        birthMonth = (EditText) rootView.findViewById(R.id.cng_birth_month_et);
        birthDay = (EditText) rootView.findViewById(R.id.cng_birth_day_et);

        save = (Button) rootView.findViewById(R.id.profilesave_bt);

        return rootView;
    }
}