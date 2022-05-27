package com.hotcoa.sona.writediary;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.hotcoa.sona.R;
import com.hotcoa.sona.leacrypto.LEA_Crypto;
import com.hotcoa.sona.main.MainActivity;
import com.hotcoa.sona.utility.SharedPrefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteDiaryFragment extends Fragment {
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
        View rootView = inflater.inflate(R.layout.fragment_write_diary, container, false);

        TextView datetv      = rootView.findViewById(R.id.today_tv);
        Button savebt      = rootView.findViewById(R.id.save_bt);
        EditText writetxt    = rootView.findViewById(R.id.writeit_et);
        Button hashtag     = rootView.findViewById(R.id.hashtag_bt);

        SharedPreferences idPrefs           = getActivity().getSharedPreferences("android_id", Context.MODE_PRIVATE);
        SharedPreferences datePrefs         = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        SharedPreferences saveDatePrefs     = getActivity().getSharedPreferences("saveDate", Context.MODE_PRIVATE);
        SharedPreferences diaryCountPrefs   = getActivity().getSharedPreferences("diaryCounter", Context.MODE_PRIVATE);

        // 일기 파일 존재할 시 EditText에 일기 내용 입력하기
        String curDate = datePrefs.getString("curDate", "");
        String curDateTxtPath = "/storage/emulated/0/SONA/text/" + curDate + ".txt";
        Log.d("writeDiary", "curDateTxtPath : " + curDateTxtPath);

        String curDateTxt = readFile(curDateTxtPath);
        Log.d("writeDiary", "curDateTxt : " + curDateTxt);

        String decrypted = "";
        try {
            String android_id = idPrefs.getString("android_id", "");
            byte[] pbkdf_id = LEA_Crypto.PBKDF(android_id);
            decrypted = LEA_Crypto.decode(curDateTxt, pbkdf_id);
        }
        catch(Exception e){
            // Navi 바에서 오늘의 일기 작성 메뉴 동작 시 예외 처리
            try{
                String today_diary_path = "/storage/emulated/0/SONA/text/" + getTime() + ".txt";
                curDateTxt = readFile(today_diary_path);
                String android_id = idPrefs.getString("android_id", "");
                byte[] pbkdf_id = LEA_Crypto.PBKDF(android_id);
                decrypted = LEA_Crypto.decode(curDateTxt, pbkdf_id);
            }
            catch(Exception f){
                Log.d("writeDiary", e.toString());
            }
        }
        datetv.setText(getTime());
        writetxt.setText(null);
        String finalDecrypted = decrypted;
        writetxt.setText(finalDecrypted);
        Log.d("writeDiary","curDate_diarytxt : " + decrypted);

        savebt.setOnClickListener(view -> {
            try{
                String android_id = idPrefs.getString("android_id","");
                byte[] pbkdf_id = LEA_Crypto.PBKDF(android_id);

                Log.d("WriteDiary", "----------------------------");
                Log.d("WriteDiary_원본 내용", writetxt.getText().toString());
                Log.d("WriteDiary_ByteArray", LEA_Crypto.toHexString(LEA_Crypto.toByteArray(writetxt.getText().toString())));

                String saveData = LEA_Crypto.encode(writetxt.getText().toString(), pbkdf_id);
                String date = datePrefs.getString("curDate", "");
                Log.d("WriteDiary", "date : " + date);
                saveFile(date, saveData);

                //저장한 일기 날짜 확인을 위해 추가
                SharedPreferences.Editor saveEditor = saveDatePrefs.edit();
                saveEditor.putString("saveDate", date);
                saveEditor.apply();

                //저장한 일기 개수 확인을 위해 추가
                SharedPreferences.Editor countEditor = diaryCountPrefs.edit();
                int cnt = diaryCountPrefs.getInt("diaryCounter", 0);
                countEditor.putInt("diaryCounter", cnt + 1);
                countEditor.apply();

                Log.d("WriteDiary_saveData","\n"+"[일기 내용 확인 : " + saveData + "]");
                Toast.makeText(getActivity(), "일기 저장 완료!", Toast.LENGTH_LONG).show();

                //Log.d("WriteDiary_복호화 내용", LEA_Crypto.decode(saveData, pbkdf_id));
            }
            catch (Exception e){
                Log.e("WriteDiary_PBKDF ERROR", e.toString());
                Log.d("WriteDiary", "----------------------------");
            }
            mainActivity.onChangeFragment(MainActivity.Direction.WriteToCalendar);
            writetxt.setText(null);
        });

        hashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onChangeFragment(MainActivity.Direction.WriteToHashTag);
            }
        });


        return rootView;
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월 dd일");
        SharedPreferences prefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        String temp = prefs.getString("curDate", dateFormat.format(date));
        Log.d("writeDiary", "curDate : " + temp);
        return temp;
    }
    private boolean inFileExist(String path, String fileName) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        boolean exist = false;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(fileName + ".txt")) {
                exist = true;
            }
        }
        return exist;
    }
    //MediaStore 이용
    //TODO [MediaStore 파일 저장 실시]
    private void saveFile(String fileName, String fileData) {
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[A_ScopeTxt > saveFile() 메소드 : MediaStore 파일 저장 실시]");
        Log.d("","\n"+"[파일 이름 : "+String.valueOf(fileName)+"]");
        Log.d("","\n"+"[파일 데이터 : "+String.valueOf(fileData)+"]");
        Log.d("//===========//","================================================");
        Log.d("---","---");

        //TODO [저장하려는 파일 타입, 이름 지정]
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName+".txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "plain/text");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, "SONA/text");
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri item = contentResolver.insert(MediaStore.Files.getContentUri("external"), values);

        try {
            //TODO [쓰기 모드 지정]
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

            if (pdf == null) {
                Log.d("---","---");
                Log.e("//===========//","================================================");
                Log.d("","\n"+"[A_ScopeTxt > saveFile() 메소드 : MediaStore 파일 저장 실패]");
                Log.d("","\n"+"[원인 : "+String.valueOf("ParcelFileDescriptor 객체 null")+"]");
                Log.e("//===========//","================================================");
                Log.d("---","---");
            } else {
                //TODO [텍스트 파일 쓰기 실시]
                byte[] strToByte = fileData.getBytes();
                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                fos.write(strToByte);
                fos.close();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    contentResolver.update(item, values, null, null);
                }

                //TODO [경로 저장 실시]
                //[콘텐츠 : 텍스트 경로 저장] - 안드 보안정책땜에 이거 있음
                SharedPrefs.setString(getActivity(), "saveScopeContentTxt", String.valueOf(item));

                //[절대 : 텍스트 경로 저장]
                Cursor c = getActivity().getContentResolver().query(Uri.parse(String.valueOf(item)), null,null,null,null);
                c.moveToNext();
                @SuppressLint("Range") String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                SharedPrefs.setString(getActivity(), "saveScopeAbsoluteTxt", absolutePath);

                Log.d("---","---");
                Log.w("//===========//","================================================");
                Log.d("","\n"+"[A_ScopeTxt > saveFile() 메소드 : MediaStore 파일 저장 성공]");
                Log.d("","\n"+"[콘텐츠 파일 경로 : "+SharedPrefs.getString(getActivity(), "saveScopeContentTxt")+"]");
                Log.d("","\n"+"[절대 파일 경로 : "+SharedPrefs.getString(getActivity(), "saveScopeAbsoluteTxt")+"]");
                Log.w("//===========//","================================================");
                Log.d("---","---");

                //TODO [다시 텍스트 파일 불러오기 실시]
                readFile(SharedPrefs.getString(getActivity(), "saveScopeAbsoluteTxt"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

