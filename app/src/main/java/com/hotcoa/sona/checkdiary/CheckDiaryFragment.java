package com.hotcoa.sona.checkdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CheckDiaryFragment extends Fragment {
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
        View rootView = inflater.inflate(R.layout.fragment_check_diary, container, false);

        TextView datetv = rootView.findViewById(R.id.today_tv);
        TextView showtv = rootView.findViewById(R.id.showit_tv);
        Button editbt = rootView.findViewById(R.id.button_edit);
        Button deletebt = rootView.findViewById(R.id.button_delete);
        Button sharebt = rootView.findViewById(R.id.button_share);

        datetv.setText(getTime());
        SharedPreferences saveDatePrefs = getActivity().getSharedPreferences("saveDate", Context.MODE_PRIVATE);

        onEditClick(editbt);
        onDeleteClick(deletebt);
        return rootView;
    }

    private void onEditClick(Button editButton) {
        editButton.setOnClickListener(view -> {
            mainActivity.onChangeFragment(MainActivity.Direction.CheckToWrite);
        });
    }

    private void onDeleteClick(Button deleteButton) {
        deleteButton.setOnClickListener(view -> {
            // 파일 삭제
            Toast.makeText(view.getContext(), "일기가 삭제되었습니다!", Toast.LENGTH_SHORT).show();
        });
    }

    private void onShareClick(Button shareButton) {
        shareButton.setOnClickListener(view -> {
            // 일기 내용 공유
        });
    }
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        SharedPreferences prefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        String temp = prefs.getString("curDate", dateFormat.format(date));
        return temp;
    }
}