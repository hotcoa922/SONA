package com.hotcoa.sona.checkdiary;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

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

public class CheckDiaryFragment extends BaseFragment {

    TextView datetv;
    TextView showtv;
    Button editbt;
    Button deletebt;
    Button sharebt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_diary, container, false);

        datetv = rootView.findViewById(R.id.today_tv);
        showtv = rootView.findViewById(R.id.showit_tv);
        showtv.setMovementMethod(new ScrollingMovementMethod());
        editbt = rootView.findViewById(R.id.button_edit);
        deletebt = rootView.findViewById(R.id.button_delete);
        sharebt = rootView.findViewById(R.id.button_share);
        HashtagView hashtagView = rootView.findViewById(R.id.hashtag_1);

        List<String> tag = Arrays.asList("#tag1", "#tag2");
        hashtagView.setData(tag);

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


        sharebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScreenShot();

            }
        });

        return rootView;
    }



    public void ScreenShot(){

        View view = getActivity().getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        //캐시를 비트맵으로 변환
        Bitmap screenBitmap = Bitmap.createBitmap(view.getDrawingCache());

        try {

            File cachePath = new File(getActivity().getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();



            File newFile = new File(cachePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                    "com.hotcoa.sona.fileprovider", newFile);

            Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
            Sharing_intent.setType("image/png");
            Sharing_intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(Sharing_intent, "Share image"));

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if(inFileExist("SONA/text", fileName)) {
                try {
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    Log.d("delete_check", "=========================");
                    Log.d("delete_check", deleteCheck);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deleteButton.setEnabled(true);
                        }
                    }, 100);
                    contentResolver.delete(Uri.parse(SharedPrefs.getString(getActivity(), "ScopeContent_" + fileName)), null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(view.getContext(), "일기가 삭제되었습니다!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(view.getContext(), "삭제할 일기가 없습니다!", Toast.LENGTH_SHORT).show();
            }
            mainActivity.onChangeFragment(MainActivity.Direction.calendarGo);
            onDestroy();
        });
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