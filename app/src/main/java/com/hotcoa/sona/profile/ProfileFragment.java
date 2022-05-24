package com.hotcoa.sona.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.MainActivity;

public class ProfileFragment extends Fragment {

    ImageView profileImg;

    TextView nickName;
    TextView diaryName;
    TextView birthYear;
    TextView birthMonth;
    TextView birthDay;

    Button edit;

    SharedPreferences sPf;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImg = (ImageView) rootView.findViewById(R.id.profile_img);

        nickName = (TextView) rootView.findViewById(R.id.username_tv);
        diaryName = (TextView) rootView.findViewById(R.id.diaryname_tv);
        birthYear = (TextView) rootView.findViewById(R.id.birth_year_tv);
        birthMonth = (TextView) rootView.findViewById(R.id.birth_month_tv);
        birthDay = (TextView) rootView.findViewById(R.id.birth_day_tv);

        edit = (Button) rootView.findViewById(R.id.profileedit_bt);



        sPf = getActivity().getSharedPreferences("profile_info",Context.MODE_PRIVATE);  //PREFS 파일 이름

        //이미지 추후 구현
        nickName.setText(sPf.getString("nickName","")); //첫번째 값은 저장해둔 값 불러오기, 두번째 값은 값이 없으면 보여줄 값
        diaryName.setText(sPf.getString("diaryName",""));
        birthYear.setText(sPf.getString("birthYear",""));
        birthMonth.setText(sPf.getString("birthMonth",""));
        birthDay.setText(sPf.getString("birthDay",""));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onChangeFragment(MainActivity.Direction.ProfileToProfileEdit);
            }
        });

        return rootView;
    }

}