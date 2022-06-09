package com.hotcoa.sona.writediary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hotcoa.sona.R;
import com.hotcoa.sona.main.BaseFragment;
import com.hotcoa.sona.main.MainActivity;

import java.util.ArrayList;
import java.util.Queue;

public class HashTagFragment extends BaseFragment {

    private int dx = 100;//초기 x
    private int dy = 100;//초기 y
    private String hashtagName;//새로운 hashtagName
    private Canvas canvas;
    private double userPropensity;//사용자 성향 변수
    private SharedPreferences psPrefs;//사용자 성향 변수를 저장하는 SharedPreference 객체
    SharedPreferences diaryCountPrefs;//일기 저장 개수를 세는 Counter

    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    Button save;

    Button bt1;
    Button bt2;
    Button bt3;
    Button bt4;
    Button bt5;
    Button bt6;
    Button bt7;
    Button bt8;

    Button cstbt1;
    Button cstbt2;
    Button cstbt3;

    Button addHashtagNameBtn;
    EditText newHashtag;

    SeekBar seekBar1;
    SeekBar seekBar2;

    ImageView imgView;



    SharedPreferences sPf;


    int bt1Stat=0;
    int bt2Stat=0;
    int bt3Stat=0;
    int bt4Stat=0;
    int bt5Stat=0;
    int bt6Stat=0;
    int bt7Stat=0;
    int bt8Stat=0;    //버튼의 상태용(일부로 int형 처리)

    int cnt = 0;    //새로 추가된 해시테그 갯수

    //Button cursor;

    ArrayList<Button> addcsthasgtagbt = new ArrayList<>();

    private void drawImgView(Bitmap bitmap, Paint myPaint) {
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        myPaint.setStrokeWidth(1f);
        myPaint.setColor(Color.BLACK);
        canvas.drawLine(0, 100, 200, 100, myPaint);
        canvas.drawLine(0, 0, 200, 200, myPaint);
        canvas.drawLine(100, 0, 100, 200, myPaint);
        canvas.drawLine(0, 200, 200, 0, myPaint);
    }

    //FireBase - Class
    public static class HashtagInfo {
        public int category;
        public int x;
        public int y;
        public HashtagInfo(int c, int x, int y) {
            this.category = c;
            this.x = x;
            this.y = y;
        }
        public int getX() { return this.x; }
        public int getY() { return this.y; }
        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
    }

    //Hashtag 값이 어디 속해 있는지 판단하는 함수
    private int calcCategory(int x, int y) {
        int ret = 10;
        if(x > 100 && y < 100) {
            //우측 상단
            if(y < (200 - x)) {
                //0번인 경우
                ret = 0;
            }
            else if(y > (200 - x)) {
                //1번인 경우
                ret = 1;
            }
            else {
                //경계 상에 있는 경우
                ret = 0;
            }
        }
        else if(x > 100 && y > 100) {
            //우측 하단
            if(y < x) {
                //2번인 경우
                ret = 2;
            }
            else if(y > x) {
                //3번인 경우
                ret = 3;
            }
            else {
                //경계 상에 있는 경우
                ret = 10;
            }
        }
        else if(x < 100 && y > 100) {
            //좌측 하단
            if(y > (200 - x)) {
                //4번인 경우
                ret = 4;
            }
            else if(y < (200 - x)) {
                //5번인 경우
                ret = 5;
            }
            else {
                //경계 상에 있는 경우
                ret = 10;
            }
        }
        else if(x < 100 && y < 100) {
            //좌측 상단
            if(y < x) {
                //6번인 경우
                ret = 6;
            }
            else if(y > x) {
                //7번인 경우
                ret = 7;
            }
            else {
                //경계 상에 있는 경우
                ret = 10;
            }
        }
        else {
            //원점인 경우 - 기본값으로 설정해줘야 함
            ret = 11;
        }
        return ret;
    }

    //FireBase에 좌표 저장을 위한 Function
    private void writeNewCoordinate(String curDate, String[] htn, int x, int y, View rootView) {//일기 저장 개수, x좌표, y좌표
        //일기 저장 개수를 이용하여 데이터 베이스 저장시 새로운 범주를 형성하기 위함 - 자세한 동작은 직접 FireBase 참조 바람
        HashtagInfo hashtagInfo = new HashtagInfo(calcCategory(x, y), x, y); //Category (0 ~ 7), x, y는 원점 좌표 (100, 100)을 기준으로 한 값
        SharedPreferences prefs = rootView.getContext().getSharedPreferences("android_id", Context.MODE_PRIVATE);
        String user = prefs.getString("android_id", "");
        for(String h : htn) {
            if (h == "") continue;
            else {
                database.child("HashtagInfo").child(user).child(curDate).child(h).setValue(hashtagInfo)
                        .addOnSuccessListener(unused -> {
                            //Success Case
                            Log.d("firebase_log", "Success");
                        })
                        .addOnFailureListener(e -> {
                            //Failure Case
                            Log.d("firebase_log", "Fail");
                            Log.d("firebase_log", e.getMessage());
                        });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_hash_tag, container, false);

        //사용자 성향 변수를 위한 SharedPreference
        psPrefs = getActivity().getSharedPreferences("userPropensity", Context.MODE_PRIVATE);
        SharedPreferences.Editor psEditor = psPrefs.edit();

        //일기 저장 개수 확인을 위한 SharedPreference
        diaryCountPrefs = getActivity().getSharedPreferences("diaryCounter", Context.MODE_PRIVATE);

        save = (Button) rootView.findViewById(R.id.hashtag_save_bt);

        bt1 = (Button) rootView.findViewById(R.id.df_hstag_bt1);
        bt2 = (Button) rootView.findViewById(R.id.df_hstag_bt2);
        bt3 = (Button) rootView.findViewById(R.id.df_hstag_bt3);
        bt4 = (Button) rootView.findViewById(R.id.df_hstag_bt4);
        bt5 = (Button) rootView.findViewById(R.id.df_hstag_bt5);
        bt6 = (Button) rootView.findViewById(R.id.df_hstag_bt6);
        bt7 = (Button) rootView.findViewById(R.id.df_hstag_bt7);
        bt8 = (Button) rootView.findViewById(R.id.df_hstag_bt8);

        cstbt1 = (Button) rootView.findViewById(R.id.cs_hstag_bt1);
        cstbt2 = (Button) rootView.findViewById(R.id.cs_hstag_bt2);
        cstbt3 = (Button) rootView.findViewById(R.id.cs_hstag_bt3);

        //ArrayList에 추가
        addcsthasgtagbt.add(cstbt1);
        addcsthasgtagbt.add(cstbt2);
        addcsthasgtagbt.add(cstbt3);

        addHashtagNameBtn = (Button) rootView.findViewById(R.id.addHashtagNameButton);
        newHashtag = (EditText) rootView.findViewById(R.id.newHashtagName_et);
        seekBar1 = (SeekBar) rootView.findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) rootView.findViewById(R.id.seekBar2);

        //좌표 표시할 imageview 생성 - canvas
        imgView = rootView.findViewById(R.id.imageView);
        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Paint myPaint = new Paint();
        imgView.setImageBitmap(bitmap);
        drawImgView(bitmap, myPaint);

        //첫번째 seekbar - x축
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //SeekBar 터치 종료 시
                drawImgView(bitmap, myPaint);
                Log.d("seekbar_log", "seekbar 1 현재 값: " + ((seekBar.getProgress() - 10) / 10.0));
                dx = 100 + (int) (seekBar.getProgress() - 10) * 10;
                Log.d("seekbar_log", "좌표 평면 x값: " + dx);
                myPaint.setColor(Color.RED);
                myPaint.setAntiAlias(true);
                canvas.drawCircle(dx, dy, 4, myPaint);
            }
        });

        //2번째 seekbar - y축
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //SeekBar 터치 종료 시
                drawImgView(bitmap, myPaint);
                Log.d("seekbar_log", "seekbar 2 현재 값: " + ((seekBar.getProgress() - 10) / 10.0));
                dy = 100 - (int) (seekBar.getProgress() - 10) * 10;
                Log.d("seekbar_log", "좌표 평면 y값: " + dy);
                myPaint.setColor(Color.RED);
                myPaint.setAntiAlias(true);
                canvas.drawCircle(dx, dy, 4, myPaint);
            }
        });

        sPf = getActivity().getSharedPreferences("tmp_custom_hashtag", Context.MODE_PRIVATE); //이름 동일하게 해야함
        SharedPreferences.Editor editor = sPf.edit();

        //새로운 hashtag 이름(String)입력하고 추가하기 눌렀을 때 event 처리
        addHashtagNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String temp = newHashtag.getText().toString();
                if(temp.length() <= 0) {
                    Toast.makeText(rootView.getContext(), "Hashtag 이름을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                }
                else {
                    hashtagName = temp; //쓰고 있는 코드
                    /*
                    if(cursor!=null){
                        cursor.setText(newHashtag.getText().toString());
                        editor.putString("hashname"+cnt,newHashtag.getText().toString());
                    }
                    */
                    int idx = 0;
                    for(Button button:addcsthasgtagbt){
                        if(button.getText().toString().equals("-")){
                            button.setText(newHashtag.getText().toString());
                            button.setBackgroundColor(requireContext().getColor(R.color.button_select));
                            editor.putString("hashname"+idx,newHashtag.getText().toString());
                            editor.apply();
                            cnt++;

                            Toast.makeText(rootView.getContext(), "새로운 해시태그 추가완료 현재" +cnt+"개!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        idx++;
                    }


                    if(cnt>2){
                        Toast.makeText(rootView.getContext(), "안돼 돌아가.", Toast.LENGTH_SHORT).show();
                        return;
                    }



                }
            }
        });

        //처리
        cstbt1.setOnClickListener((View.OnClickListener) view -> {
            if(((Button)view).getText().toString().equals("-")){        //(Button)view는 누른 애 인자
                return;
            }
            cstbt1.setBackgroundColor(requireContext().getColor(R.color.button_dark_color));
            addcsthasgtagbt.get(0).setText("-");
            editor.putString("hashname0", "");
            cnt--;
            //cursor = (Button)view;
        });

        cstbt2.setOnClickListener((View.OnClickListener) view -> {
            if(((Button)view).getText().toString().equals("-")){        //(Button)view는 누른 애 인자
                return;
            }
            cstbt2.setBackgroundColor(requireContext().getColor(R.color.button_dark_color));
            addcsthasgtagbt.get(1).setText("-");
            editor.putString("hashname1", "");
            cnt--;
            //cursor = (Button)view;
        });

        cstbt3.setOnClickListener((View.OnClickListener) view -> {
            if(((Button)view).getText().toString().equals("-")){        //(Button)view는 누른 애 인자
                return;
            }
            cstbt3.setBackgroundColor(requireContext().getColor(R.color.button_dark_color));
            addcsthasgtagbt.get(2).setText("-");
            editor.putString("hashname2", "");
            cnt--;
            //cursor = (Button)view;
        });


        save = (Button) rootView.findViewById(R.id.hashtag_save_bt);

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
        //.


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //버튼 활성화 비활성화 여부, 1은 활성화 0은 비활성
                if(bt1Stat==1) {
                    bt1Stat=0;
                    bt1.setBackgroundColor(requireContext().getColor(R.color.black));
                } else if(bt1Stat==0) {
                    bt1Stat = 1;
                    bt1.setBackgroundColor(requireContext().getColor(R.color.button_select));
                }
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt2Stat==1) {
                    bt2Stat=0;
                    bt2.setBackgroundColor(requireContext().getColor(R.color.black));
                } else if(bt2Stat==0) {
                    bt2Stat = 1;
                    bt2.setBackgroundColor(requireContext().getColor(R.color.button_select));
                }

            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt3Stat==1) {
                    bt3Stat=0;
                    bt3.setBackgroundColor(requireContext().getColor(R.color.black));
                } else if(bt3Stat==0) {
                    bt3Stat = 1;
                    bt3.setBackgroundColor(requireContext().getColor(R.color.button_select));
                }

            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt4Stat==1) {
                    bt4Stat=0;
                    bt4.setBackgroundColor(requireContext().getColor(R.color.black));
                } else if(bt4Stat==0) {
                    bt4Stat = 1;
                    bt4.setBackgroundColor(requireContext().getColor(R.color.button_select));
                }

            }
        });

        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt5Stat==1) {
                    bt5Stat=0;
                    bt5.setBackgroundColor(requireContext().getColor(R.color.black));
                } else if(bt5Stat==0) {
                    bt5Stat = 1;
                    bt5.setBackgroundColor(requireContext().getColor(R.color.button_select));
                }

            }
        });

        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt6Stat==1) {
                    bt6Stat=0;
                    bt6.setBackgroundColor(requireContext().getColor(R.color.black));
                } else if(bt6Stat==0) {
                    bt6Stat = 1;
                    bt6.setBackgroundColor(requireContext().getColor(R.color.button_select));
                }

            }
        });

        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt7Stat==1) {
                    bt7Stat=0;
                    bt7.setBackgroundColor(requireContext().getColor(R.color.black));
                } else if(bt7Stat==0) {
                    bt7Stat = 1;
                    bt7.setBackgroundColor(requireContext().getColor(R.color.button_select));
                }

            }
        });

        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt8Stat==1) {
                    bt8Stat=0;
                    bt8.setBackgroundColor(requireContext().getColor(R.color.black));
                } else if(bt8Stat==0) {
                    bt8Stat = 1;
                    bt8.setBackgroundColor(requireContext().getColor(R.color.button_select));
                }

            }
        });
    //
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totDfBtStat=bt1Stat+bt2Stat+bt3Stat+bt4Stat+bt5Stat+bt6Stat+bt7Stat+bt8Stat;  //총합 3이하
                int totCustBtStat=0;

                if(totDfBtStat>3){
                    Toast.makeText(getActivity(), "기본 해쉬태그는 최대 3개까지 선택 가능합니다! 현재 "+totDfBtStat+"개 선택", Toast.LENGTH_SHORT).show();
                }
                if(totCustBtStat>3){
                    Toast.makeText(getActivity(), "사용자 정의 해쉬태그는 최대 3개까지 선택 가능합니다!", Toast.LENGTH_SHORT).show();
                }
                if(totCustBtStat<3 && totDfBtStat<3){
                    //Firebase에 좌표값 저장
                    if(newHashtag.getText().toString().length() <= 0) {
                        Toast.makeText(rootView.getContext(), "Hashtag 이름을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SharedPreferences prefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
                        sPf = getActivity().getSharedPreferences("tmp_custom_hashtag", Context.MODE_PRIVATE);
                        String curDate = prefs.getString("curDate", "");
                        String[] htn = new String[3];
                        for (int i = 0; i < 3; ++i) {
                            htn[i] = sPf.getString("hashname"+i, "");
                            Log.d("firebase_log", htn[i]);
                        }
                        int cnt = diaryCountPrefs.getInt("diaryCounter", 0);
                        writeNewCoordinate(curDate, htn, dx, dy, rootView);
                        onBackpress();
                        onDestroy();
                        mainActivity.onChangeFragment(MainActivity.Direction.writeGo);
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //저장하고 나가면 기억하지 못하게 하기
                        for(int i= 0; i<3; i++){
                            editor.putString("hashname"+i, "");
                            addcsthasgtagbt.get(i).setText("-");
                        }
                        editor.apply();
                    }
                },200);

            }
        });


        return rootView;
    }
}