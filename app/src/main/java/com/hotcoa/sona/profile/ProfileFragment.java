package com.hotcoa.sona.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.BaseFragment;
import com.hotcoa.sona.main.MainActivity;

public class ProfileFragment extends BaseFragment {

    ImageView profileImg;

    TextView nickName;
    TextView diaryName;
    TextView birthYear;
    TextView birthMonth;
    TextView birthDay;

    Button edit;

    SharedPreferences sPf;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImg = (ImageView) rootView.findViewById(R.id.profile_img);

        nickName = (TextView) rootView.findViewById(R.id.newHashtagName_et);
        diaryName = (TextView) rootView.findViewById(R.id.diaryname_tv);
        birthYear = (TextView) rootView.findViewById(R.id.birth_year_tv);
        birthMonth = (TextView) rootView.findViewById(R.id.birth_month_tv);
        birthDay = (TextView) rootView.findViewById(R.id.birth_day_tv);

        edit = (Button) rootView.findViewById(R.id.profileedit_bt);

        sPf = getActivity().getSharedPreferences("profile_info",Context.MODE_PRIVATE);  //PREFS 파일 이름

        //이미지
        String image = sPf.getString("profileImg", "");
        Bitmap bitmap = StringToBitMap(image);

        nickName.setText(sPf.getString("nickName",""));
        diaryName.setText(sPf.getString("diaryName",""));
        birthYear.setText(sPf.getString("birthYear",""));
        birthMonth.setText(sPf.getString("birthMonth",""));
        birthDay.setText(sPf.getString("birthDay",""));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onChangeFragment(MainActivity.Direction.profileeditGo);
            }
        });

        return rootView;
    }

    private Bitmap StringToBitMap(String encodedString) {
        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}