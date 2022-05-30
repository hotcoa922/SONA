package com.hotcoa.sona.checkdiary;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.hotcoa.sona.R;
import com.hotcoa.sona.leacrypto.LEA_Crypto;
import com.hotcoa.sona.main.BaseFragment;
import com.hotcoa.sona.main.MainActivity;
import com.hotcoa.sona.utility.SharedPrefs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CheckDiaryFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_diary, container, false);

        TextView datetv = rootView.findViewById(R.id.today_tv);
        TextView showtv = rootView.findViewById(R.id.showit_tv);
        Button editbt = rootView.findViewById(R.id.button_edit);
        Button deletebt = rootView.findViewById(R.id.button_delete);
        Button sharebt = rootView.findViewById(R.id.button_share);

        datetv.setText(getTime());
        String diary_path = "/storage/emulated/0/SONA/text/" + getTime() + ".txt";
        String saveData = readFile(diary_path);
        Log.d("showtv 경로", diary_path);
        Log.d("showtv 내용", readFile(diary_path));

        SharedPreferences idPrefs = getActivity().getSharedPreferences("android_id", Context.MODE_PRIVATE);
        String android_id = idPrefs.getString("android_id","");
        try {
            byte[] pbkdf_id = LEA_Crypto.PBKDF(android_id);
            Log.d("checkDiary", pbkdf_id.toString());

            String decrypted = LEA_Crypto.decode(saveData, pbkdf_id);
            Log.d("checkDiary", decrypted);
            showtv.setText(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("checkDiary", e.toString());
        }
        onEditClick(editbt);
        onDeleteClick(deletebt, getTime());
        return rootView;
    }

    private void onEditClick(Button editButton) {
        editButton.setOnClickListener(view -> {
            pathSave();
            mainActivity.onChangeFragment(MainActivity.Direction.writeGo);
            onDestroy();
        });
    }

    private void onDeleteClick(Button deleteButton, String fileName) {
        deleteButton.setOnClickListener(view -> {
            // 파일 삭제
            String deleteCheck = SharedPrefs.getString(getActivity(), "ScopeContent_" + fileName);
            if(deleteCheck != null && deleteCheck.length() > 0) {
                try {
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    contentResolver.delete(Uri.parse(SharedPrefs.getString(getActivity(), "ScopeContent_" + fileName)), null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(view.getContext(), "일기가 삭제되었습니다!", Toast.LENGTH_SHORT).show();
            mainActivity.onChangeFragment(MainActivity.Direction.calendarGo);
            onDestroy();
        });
    }

    private void onShareClick(Button shareButton) {
        shareButton.setOnClickListener(view -> {
            // 일기 내용 공유
        });
    }
    private void pathSave() {
        SharedPreferences pathPrefs = getActivity().getSharedPreferences("curDateTxtPath", Context.MODE_PRIVATE);
        SharedPreferences datePrefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pathPrefs.edit();
        String curDate = datePrefs.getString("curDate","");
        String diary_path = "/storage/emulated/0/SONA/text/" + curDate + ".txt";
        editor.putString("curDateTxtPath", diary_path);
        editor.apply();
    }
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월 dd일");
        SharedPreferences prefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        String temp = prefs.getString("curDate", dateFormat.format(date));
        return temp;
    }

    //TODO [MediaStore 파일 불러오기 실시]
    private String readFile(String path) {
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[A_ScopeTxt > readFile() 메소드 : MediaStore 파일 불러오기 실시]");
        Log.d("","\n"+"[절대 파일 경로 : "+String.valueOf(path)+"]");
        Log.d("//===========//","================================================");
        Log.d("---","---");
        Uri externalUri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
        };

        Cursor cursor = getActivity().getContentResolver().query(externalUri, projection, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            Log.d("---","---");
            Log.e("//===========//","================================================");
            Log.d("","\n"+"[A_ScopeTxt > readFile() 메소드 : MediaStore 파일 불러오기 실패]");
            Log.d("","\n"+"[원인 : "+String.valueOf("Cursor 객체 null")+"]");
            Log.e("//===========//","================================================");
            Log.d("---","---");
            return "";
        }

        //TODO [텍스트 파일 불러오기 실시]
        String absoluteUrl = path;
        String line = ""; //TODO [한줄씩 읽기]
        try {
            BufferedReader buf = new BufferedReader(new FileReader(absoluteUrl));
            while((line=buf.readLine())!=null){
                Log.d("---","---");
                Log.w("//===========//","================================================");
                Log.d("showtv_내용","\n"+"[A_ScopeTxt > readFile() 메소드 : MediaStore 파일 불러오기 성공]");
                Log.d("showtv_경로","\n"+"[절대 파일 경로 : "+String.valueOf(absoluteUrl)+"]");
                Log.d("showtv_내용","\n"+"[절대 파일 내용 : "+String.valueOf(line)+"]");
                Log.w("//===========//","================================================");
                Log.d("---","---");
                return String.valueOf(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}