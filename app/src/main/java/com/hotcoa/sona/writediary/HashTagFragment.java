package com.hotcoa.sona.writediary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.MainActivity;

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

    int bt1Stat=0;
    int bt2Stat=0;
    int bt3Stat=0;
    int bt4Stat=0;
    int bt5Stat=0;
    int bt6Stat=0;
    int bt7Stat=0;
    int bt8Stat=0;    //버튼의 상태용(일부로 int형 처리)

    int totDfBtStat=bt1Stat+bt2Stat+bt3Stat+bt8Stat+bt5Stat+bt6Stat+bt7Stat+bt8Stat;  //총합 3이하
    int totCustBtStat=0;

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


        int defStatTot=0;  //기본 감정 선택갯수 제한을 위한 것

        //버튼 활성화 지연
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bt1.setEnabled(true);
                bt2.setEnabled(true);
                bt3.setEnabled(true);
                bt4.setEnabled(true);
                bt5.setEnabled(true);
                bt6.setEnabled(true);
                bt7.setEnabled(true);
                bt8.setEnabled(true);
                save.setEnabled(true);
            }
        }, 1000);


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //버튼 활성화 비활성화 여부, 1은 활성화 0은 비활성
                if(bt1Stat==1) {
                    bt1Stat=0;
                } else if(bt1Stat==0) {
                    bt1Stat = 1;
                    bt1.setBackgroundColor(requireContext().getColor(R.color.red));
                }
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt2Stat==1) {
                    bt2Stat=0;
                } else if(bt2Stat==0) {
                    bt2Stat = 1;
                    bt2.setBackgroundColor(requireContext().getColor(R.color.red));
                }

            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt3Stat==1) {
                    bt3Stat=0;
                } else if(bt3Stat==0) {
                    bt3Stat = 1;
                    bt3.setBackgroundColor(requireContext().getColor(R.color.red));
                }

            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt4Stat==1) {
                    bt4Stat=0;
                } else if(bt4Stat==0) {
                    bt4Stat = 1;
                    bt4.setBackgroundColor(requireContext().getColor(R.color.red));
                }

            }
        });

        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt5Stat==1) {
                    bt5Stat=0;
                } else if(bt5Stat==0) {
                    bt5Stat = 1;
                    bt5.setBackgroundColor(requireContext().getColor(R.color.red));
                }

            }
        });

        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt6Stat==1) {
                    bt6Stat=0;
                } else if(bt6Stat==0) {
                    bt6Stat = 1;
                    bt6.setBackgroundColor(requireContext().getColor(R.color.red));
                }

            }
        });

        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt7Stat==1) {
                    bt7Stat=0;
                } else if(bt7Stat==0) {
                    bt7Stat = 1;
                    bt7.setBackgroundColor(requireContext().getColor(R.color.red));
                }

            }
        });

        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt8Stat==1) {
                    bt8Stat=0;
                } else if(bt8Stat==0) {
                    bt8Stat = 1;
                    bt8.setBackgroundColor(requireContext().getColor(R.color.red));
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totDfBtStat>3){
                    Toast.makeText(getActivity(), "기본 해쉬태그는 최대 3개까지 선택 가능합니다!", Toast.LENGTH_SHORT).show();
                }
                if(totCustBtStat>3){
                    Toast.makeText(getActivity(), "사용자 정의 해쉬태그는 최대 3개까지 선택 가능합니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });




        return rootView;

    }
}