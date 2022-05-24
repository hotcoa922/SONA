package com.hotcoa.sona.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotcoa.sona.R;



public class ProfileEditFragment extends Fragment {

    ImageView profileImg;

    EditText nickName;
    EditText diaryName;
    EditText birthYear;
    EditText birthMonth;
    EditText birthDay;

    Button save;

    SharedPreferences sPf;


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


        sPf = getActivity().getSharedPreferences("profile_info", Context.MODE_PRIVATE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sPf.edit();
                editor.putString("nickName",nickName.getText().toString());
                editor.putString("diaryName",diaryName.getText().toString());
                editor.putString("birthYear",birthYear.getText().toString());
                editor.putString("birthMonth",birthMonth.getText().toString());
                editor.putString("birthDay",birthDay.getText().toString());
                editor.commit();    //최종 커밋. 커밋을 해야 저장이 된다.
                Toast.makeText(getActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });



        return rootView;
    }
}