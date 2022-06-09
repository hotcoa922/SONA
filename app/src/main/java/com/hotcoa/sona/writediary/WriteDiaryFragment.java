package com.hotcoa.sona.writediary;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.greenfrvr.hashtagview.HashtagView;
import com.hotcoa.sona.R;
import com.hotcoa.sona.leacrypto.LEA_Crypto;
import com.hotcoa.sona.main.BaseFragment;
import com.hotcoa.sona.main.MainActivity;
import com.hotcoa.sona.utility.SharedPrefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WriteDiaryFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_write_diary, container, false);

        TextView datetv      = rootView.findViewById(R.id.today_tv);
        Button savebt      = rootView.findViewById(R.id.save_bt);
        EditText writetxt    = rootView.findViewById(R.id.writeit_et);
        Button hashtag     = rootView.findViewById(R.id.hashtag_bt);
        HashtagView hashtagView = rootView.findViewById(R.id.hashtag_1);

        List<String> tag = Arrays.asList("#tag1", "#tag2");
        hashtagView.setData(tag);

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
        }
        datetv.setText(getTime());
        String finalDecrypted = decrypted;
        writetxt.setText(finalDecrypted);
        Log.d("writeDiary","curDate_diarytxt : " + decrypted);

        savebt.setOnClickListener(view -> {
            try{
                String android_id = idPrefs.getString("android_id","");
                byte[] pbkdf_id = LEA_Crypto.PBKDF(android_id);

                String saveData = LEA_Crypto.encode(writetxt.getText().toString(), pbkdf_id);
                String date = datePrefs.getString("curDate", "");
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

                Toast.makeText(getActivity(), "일기 저장 완료!", Toast.LENGTH_LONG).show();

            }
            catch (Exception e){

            }
            mainActivity.onChangeFragment(MainActivity.Direction.calendarGo);
            writetxt.setText(null);
            onDestroy();

            //저장하고 나가면 기억하지 못하게 하기

        });
        hashtag.setOnClickListener(view -> {
            mainActivity.onChangeFragment(MainActivity.Direction.hashGo);
            onDestroy();
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
    private boolean inFileExist(String folderName, String fileName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName;
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
    private void saveFile(String fileName, String fileData) {
        String deleteCheck = SharedPrefs.getString(getActivity(), "ScopeContent_" + fileName);
        if(deleteCheck != null && deleteCheck.length() > 0){
            try {
                ContentResolver contentResolver = getActivity().getContentResolver();
                contentResolver.delete(Uri.parse(SharedPrefs.getString(getActivity(), "ScopeContent_" + fileName)),null,null);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName+".txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "plain/text");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, "SONA/text");
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri item = contentResolver.insert(MediaStore.Files.getContentUri("external"), values);
        try {
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);
            if (pdf == null) {
                Log.d("","\n"+"[A_ScopeTxt > saveFile() 메소드 : MediaStore 파일 저장 실패]");
                Log.d("","\n"+"[원인 : "+String.valueOf("ParcelFileDescriptor 객체 null")+"]");
            } else {
                byte[] strToByte = fileData.getBytes();
                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                fos.write(strToByte);
                fos.close();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    contentResolver.update(item, values, null, null);
                }
                SharedPrefs.setString(getActivity(), "ScopeContent_" + fileName, String.valueOf(item));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String readFile(String path) {
        Uri externalUri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
        };

        Cursor cursor = getActivity().getContentResolver().query(externalUri, projection, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            return "";
        }
        String absoluteUrl = path;
        String line = "";
        try {
            BufferedReader buf = new BufferedReader(new FileReader(absoluteUrl));
            while((line=buf.readLine())!=null){
                return line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

