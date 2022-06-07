package com.hotcoa.sona.contents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hotcoa.sona.R;
import com.hotcoa.sona.main.BaseFragment;
import com.hotcoa.sona.writediary.HashTagFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ContentsFragment extends BaseFragment {

    private WebView web;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_contents, container, false);
        FirebaseFirestore db    = FirebaseFirestore.getInstance();
        Button showBt           =  rootView.findViewById(R.id.ShowButton);
        Button centerBt         = rootView.findViewById(R.id.CenterButton);
        RatingBar ratingBar     = rootView.findViewById(R.id.ratingBar);
        WebView web             = rootView.findViewById(R.id.ContentsWebView);
        TextView phraseWords    = rootView.findViewById(R.id.phraseWords);
        TextView personWords    = rootView.findViewById(R.id.personWords);

        SharedPreferences datePrefs         = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        //String curDate = datePrefs.getString("curDate", "");
        /*
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월 dd일");
        String curDate = datePrefs.getString("curDate", dateFormat.format(date));


         */

        String TAG = "Contents_db";
        String DOC = "phrase";
        String curDate = "2022년 6월 4일";

        // String으로 선언하면 변경이 안돼서 StringBuilder 메소드 사용
        StringBuilder url = new StringBuilder();
        StringBuilder prs = new StringBuilder();
        StringBuilder person = new StringBuilder();

        // hashtag로부터 추출한 감정 (0~7) *****
        //int hashtagFeel = 0;
        StringBuilder hashtagFeel = new StringBuilder();


        db.collection("HashtagInfo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                           if (document.getId().equals(curDate)) {
                                    for (Map.Entry<String, Object> e : document.getData().entrySet()) {
                                        Log.d(TAG, "Key : " + e.getKey() + ", Value : " + e.getValue());
                                        Log.d(TAG, e.getValue().toString());
                                        hashtagFeel.append(e.getValue().toString());
                                    }
                           }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


        db.collection("music")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // hashtagFeel과 일치하는 document 탐색
                            if (document.getId().equals(hashtagFeel)) {
                                // document 정보 불러오기
                                int docSize = document.getData().size();

                                // 0 ~ docsize의 난수 생성
                                double rand = Math.random();
                                int randField = (int) (rand * docSize);
                                Log.d(TAG, String.valueOf(randField));

                                // 난수에 해당되는 url 불러오기
                                int count = 0;
                                for (Map.Entry<String, Object> e : document.getData().entrySet()) {
                                    if (count == randField) {
                                        Log.d(TAG, "Key : " + e.getKey() + ", Value : " + e.getValue());
                                        Log.d(TAG, e.getValue().toString());
                                        url.append(e.getValue().toString());
                                    }
                                    count++;
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        showBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "url : " + url);
                web.loadUrl(url.toString());
                Toast.makeText(getActivity(), "오늘의 추천 음악입니다 :)", Toast.LENGTH_LONG).show();
                Log.d(TAG, "----------------------------");
            }
        });

        // 웹 세팅 작업하기
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(true);

        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());

        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 오늘의 별점 평가
        // SharedPrference에 id:"(날짜)_rating", key:(rating) 형식으로 저장
        SharedPreferences curDatePrefs          = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        //String curDate = curDatePrefs.getString("curDate","");
        Log.d("Rating", "curDate        : " + curDate);

        String ratingKey = curDate + "_rating";

        SharedPreferences curDateRatingPrefs    = getActivity().getSharedPreferences(ratingKey, Context.MODE_PRIVATE);
        Float curRating = curDateRatingPrefs.getFloat(ratingKey, 0);
        Log.d("Rating", "curDateRating  : " + curRating);

        SharedPreferences.Editor curDateRatingEditor = curDateRatingPrefs.edit();
        curDateRatingEditor.putFloat(ratingKey, curRating);
        curDateRatingEditor.commit();
        ratingBar.setRating(curRating);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, b) -> {
            Log.d("Rating", "rating         : " + rating);
            curDateRatingEditor.putFloat(ratingKey, rating);
            curDateRatingEditor.commit();
        });
        ratingBar.setRating(curRating);

        centerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gangnam.go.kr/office/smilegn/main.do"));
                Toast.makeText(getActivity(), "심리상담센터 사이트로 연결합니다 :)", Toast.LENGTH_LONG).show();
                startActivity(myIntent);
            }
        });


        //랜덤 명언 추출하기
        db.collection("phrase")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // hashtagFeel과 일치하는 document 탐색
                            if (document.getId().equals(DOC)) {
                                // document 정보 불러오기
                                int docSize = document.getData().size();

                                // 0 ~ docsize의 난수 생성
                                double rand = Math.random();
                                int randField = (int) (rand * docSize);
                                Log.d(TAG, String.valueOf(randField));

                                // 난수에 해당되는 명언 불러오기
                                int count = 0;
                                for (Map.Entry<String, Object> e : document.getData().entrySet()) {
                                    if (count == randField) {
                                        Log.d(TAG, "Key : " + e.getKey() + ", Value : " + e.getValue());
                                        Log.d(TAG, e.getValue().toString());
                                        Log.d(TAG, e.getKey().toString());
                                        prs.append(e.getValue().toString());
                                        person.append(e.getKey().toString());
                                        phraseWords.setText(prs.toString());
                                        personWords.setText(person.toString());
                                    }
                                    count++;
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    Log.d("Contents_db", "hi: "+prs);
                    Log.d("Contents_db", "hi: "+person);
                    phraseWords.setText(prs.toString());
                    personWords.setText(person.toString());
                });
        return rootView;
    }
}

