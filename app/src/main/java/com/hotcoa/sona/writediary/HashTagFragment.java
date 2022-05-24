package com.hotcoa.sona.writediary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotcoa.sona.R;

public class HashTagFragment extends Fragment {

    Button save;

    Button bt1;
    Button bt2;
    Button bt3;
    Button bt4;
    Button bt5;
    Button bt6;
    Button bt7;
    Button bt8;

    SharedPreferences sPf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_hash_tag, container, false);

        save = (Button) rootView.findViewById(R.id.hashtag_save_bt);

        bt1 = (Button) rootView.findViewById(R.id.df_hstag_bt1);
        bt2 = (Button) rootView.findViewById(R.id.df_hstag_bt2);
        bt3 = (Button) rootView.findViewById(R.id.df_hstag_bt3);
        bt4 = (Button) rootView.findViewById(R.id.df_hstag_bt4);
        bt5 = (Button) rootView.findViewById(R.id.df_hstag_bt5);
        bt6 = (Button) rootView.findViewById(R.id.df_hstag_bt6);
        bt7 = (Button) rootView.findViewById(R.id.df_hstag_bt7);
        bt8 = (Button) rootView.findViewById(R.id.df_hstag_bt8);

        sPf = getActivity().getSharedPreferences("Hashtag_info", Context.MODE_PRIVATE);

        int bt1Stat=0,bt2Stat=0,bt3Stat=0,bt4Stat=0,bt5Stat=0,bt6Stat=0,bt7Stat=0,bt8Stat=0;    //버튼의 상태용(일부로 int형 처리)
        int defStatTot=0;  //기본 감정 선택갯수 제한을 위한 것

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //버튼 활성화 비활성화 여부, 1은 활성화 0은 비활성
                /*
                if(bt1Stat==1) {
                    bt1Stat=0;
                } else if(bt1Stat==0){
                    bt1Stat=1;
                }
                */

            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });











        return rootView;

    }
}