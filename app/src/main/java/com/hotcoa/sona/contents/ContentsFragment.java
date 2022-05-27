package com.hotcoa.sona.contents;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hotcoa.sona.R;

import java.util.HashMap;
import java.util.Map;


public class ContentsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contents, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Button showbt = (Button) rootView.findViewById(R.id.ShowButton);
        WebView ContentsWebView = (WebView) rootView.findViewById(R.id.ContentsWebView);
        String TAG = "Contents_db";

        // String으로 선언하면 변경이 안돼서 StringBuilder 메소드 사용
        StringBuilder url = new StringBuilder();

        // hashtag로부터 추출한 감정 (0~7)
        int hashtagFeel = 0;

        db.collection("music")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // hashtagFeel과 일치하는 document 탐색
                            if(document.getId().equals(Integer.toString(hashtagFeel))){
                                // document 정보 불러오기
                                int docSize = document.getData().size();

                                // 0 ~ docsize의 난수 생성
                                double rand = Math.random();
                                int randField = (int)(rand * docSize);
                                Log.d(TAG, String.valueOf(randField));

                                // 난수에 해당되는 url 불러오기
                                int count = 0;
                                for(Map.Entry<String, Object> e : document.getData().entrySet()){
                                    if(count == randField){
                                        Log.d(TAG,"Key : " + e.getKey() + ", Value : " + e.getValue());
                                        Log.d(TAG,e.getValue().toString());
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
        showbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "url : " + url);
                ContentsWebView.loadUrl(url.toString());
                Toast.makeText(getActivity(), "콘텐츠!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "----------------------------");

            }
        });
        return rootView;
    }
}
