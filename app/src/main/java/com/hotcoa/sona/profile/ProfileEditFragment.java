package com.hotcoa.sona.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.BaseFragment;
import com.hotcoa.sona.main.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class ProfileEditFragment extends BaseFragment {

    ImageView profileImg;

    Button profileCng;

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

        profileCng = (Button) rootView.findViewById(R.id.cng_profile_bt);

        nickName = (EditText) rootView.findViewById(R.id.cng_username_et);
        diaryName = (EditText) rootView.findViewById(R.id.cng_diaryname_et);
        birthYear = (EditText) rootView.findViewById(R.id.cng_birth_year_et);
        birthMonth = (EditText) rootView.findViewById(R.id.cng_birth_month_et);
        birthDay = (EditText) rootView.findViewById(R.id.cng_birth_day_et);

        nickName.setTypeface(Typeface.DEFAULT);
        diaryName.setTypeface(Typeface.DEFAULT);
        birthYear.setTypeface(Typeface.DEFAULT);
        birthMonth.setTypeface(Typeface.DEFAULT);
        birthDay.setTypeface(Typeface.DEFAULT);

        save = (Button) rootView.findViewById(R.id.profilesave_bt);


        sPf = getActivity().getSharedPreferences("profile_info", Context.MODE_PRIVATE); //이름 동일하게 해야함


        //연하게 보여줌
        nickName.setHint(sPf.getString("nickName","")); //첫번째 값은 저장해둔 값 불러오기, 두번째 값은 값이 없으면 보여줄 값
        diaryName.setHint(sPf.getString("diaryName",""));
        birthYear.setHint(sPf.getString("birthYear",""));
        birthMonth.setHint(sPf.getString("birthMonth",""));
        birthDay.setHint(sPf.getString("birthDay",""));


        nickName.setText(sPf.getString("nickName",""));
        diaryName.setText(sPf.getString("diaryName",""));
        birthYear.setText(sPf.getString("birthYear",""));
        birthMonth.setText(sPf.getString("birthMonth",""));
        birthDay.setText(sPf.getString("birthDay",""));
        /*
        profileCng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sPf.edit();
                //String image = BitMapToString(thePic);
                editor.putString("profileImg", "");
                editor.putString("nickName",nickName.getText().toString());
                editor.putString("diaryName",diaryName.getText().toString());
                editor.putString("birthYear",birthYear.getText().toString());
                editor.putString("birthMonth",birthMonth.getText().toString());
                editor.putString("birthDay",birthDay.getText().toString());
                editor.commit();    //최종 커밋. 커밋을 해야 저장이 된다.
                Toast.makeText(getActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                mainActivity.onChangeFragment(MainActivity.Direction.profileGo);

            }
        });



        return rootView;
    }

    private String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}