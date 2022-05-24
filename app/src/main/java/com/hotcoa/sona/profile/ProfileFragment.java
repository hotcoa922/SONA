package com.hotcoa.sona.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotcoa.sona.R;

public class ProfileFragment extends Fragment {

    ImageView profileImg;

    TextView nickName;
    TextView diaryName;
    TextView birthYear;
    TextView birthMonth;
    TextView birthDay;

    Button edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImg = (ImageView) rootView.findViewById(R.id.profile_img);

        nickName = (TextView) rootView.findViewById(R.id.username_tv);
        diaryName = (TextView) rootView.findViewById(R.id.diaryname_tv);
        birthYear = (TextView) rootView.findViewById(R.id.birth_year_tv);
        birthMonth = (TextView) rootView.findViewById(R.id.birth_month_tv);
        birthDay = (TextView) rootView.findViewById(R.id.birth_day_tv);

        edit = (Button) rootView.findViewById(R.id.profilesave_bt);












        return rootView;
    }

}