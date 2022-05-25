package com.hotcoa.sona.writediary;

import static com.hotcoa.sona.leacrypto.LEA_Crypto.decode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hotcoa.sona.R;
import com.hotcoa.sona.calendar.CalendarFragment;
import com.hotcoa.sona.leacrypto.LEA_Crypto;
import com.hotcoa.sona.main.MainActivity;
import com.hotcoa.sona.utility.SharedPrefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

public class WriteDiaryFragment extends Fragment {

    TextView datetv;
    Button savebt;
    EditText writetxt;
    Button hashtag;

    //얘네도 일단 지워...
    String saveData = ""; //저장된 파일 내용
    //String saveStorage = ""; //저장된 파일 경로

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

        datetv = (TextView)rootView.findViewById(R.id.today_tv);
        savebt = (Button)rootView.findViewById(R.id.save_bt);
        writetxt = (EditText)rootView.findViewById(R.id.writeit_et);
        hashtag = (Button)rootView.findViewById(R.id.hashtag_bt);

        datetv.setText(getTime());
        SharedPreferences idPrefs = getActivity().getSharedPreferences("android_id", Context.MODE_PRIVATE);
        SharedPreferences datePrefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        SharedPreferences saveDatePrefs = getActivity().getSharedPreferences("saveDate", Context.MODE_PRIVATE);

        CalendarFragment calendarFragment = new CalendarFragment();

        savebt.setOnClickListener(view -> {
            try{
                String android_id = idPrefs.getString("android_id","");
                byte[] pbkdf_id = LEA_Crypto.PBKDF(android_id);

                Log.d("WriteDiary", "----------------------------");
                Log.d("WriteDiary_원본 내용", writetxt.getText().toString());
                Log.d("WriteDiary_ByteArray", LEA_Crypto.toHexString(LEA_Crypto.toByteArray(writetxt.getText().toString())));

                saveData = LEA_Crypto.encode(writetxt.getText().toString(), pbkdf_id);
                String date = datePrefs.getString("curDate", "");
                Log.d("WriteDiary", "date : " + date);
                saveFile(date, saveData);

                //저장한 일기 날짜 확인을 위해 추가
                SharedPreferences.Editor editor = saveDatePrefs.edit();
                editor.putString("saveDate", date);
                editor.apply();

                Log.d("WriteDiary_saveData","\n"+"[일기 내용 확인 : " + saveData + "]");
                Toast.makeText(getActivity(), "일기 저장 완료!", Toast.LENGTH_LONG).show();

                //Log.d("WriteDiary_복호화 내용", LEA_Crypto.decode(saveData, pbkdf_id));
                Log.d("WriteDiary", "----------------------------");

            }
            catch (Exception e){
                Log.e("WriteDiary_PBKDF ERROR", e.toString());
                Log.d("WriteDiary", "----------------------------");
            }
            mainActivity.onChangeFragment(MainActivity.Direction.WriteToCalendar);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        SharedPreferences prefs = getActivity().getSharedPreferences("curDate", Context.MODE_PRIVATE);
        String temp = prefs.getString("curDate", dateFormat.format(date));
        return temp;
    }

    /* 비상비상 이거 sdk30, 안드 11이후 안됨...
    //텍스트 저장 메소드
    public void setSaveText(String data){
        try {
            saveData = data; //TODO 변수에 값 대입
            String textFileName = "/Data.txt";

            //TODO 파일 생성
            File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SaveStorage"); //TODO 저장 경로
            //TODO 폴더 생성
            if(!storageDir.exists()){ //TODO 폴더 없을 경우
                storageDir.mkdir(); //TODO 폴더 생성
            }

            long now = System.currentTimeMillis(); //TODO 현재시간 받아오기
            Date date = new Date(now); //TODO Date 객체 생성
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            String nowTime = sdf.format(date);

            //BufferedWriter buf = new BufferedWriter(new FileWriter(storageDir+textFileName, true)); //TODO 다중으로 내용적음 (TRUE)
            BufferedWriter buf = new BufferedWriter(new FileWriter(storageDir+textFileName, false)); //TODO 한개 내용만 표시됨 (FALSE)
            buf.append("["+nowTime+"]" + "\n["+saveData+"]"); //TODO 날짜 쓰기
            buf.newLine(); //TODO 개행
            buf.close();

            saveStorage = String.valueOf(storageDir+textFileName); //TODO 경로 저장 /storage 시작
            //saveStorage = String.valueOf(storageDir.toURI()+textFileName); //TODO 경로 저장 file:/ 시작
            SharedPrefs.setString(getActivity(), "saveStorage", String.valueOf(saveStorage)); //TODO 프리퍼런스에 경로 저장한다
            SharedPrefs.setString(getActivity(), "saveStorage", String.valueOf(saveStorage)); //TODO 프리퍼런스에 경로 저장한다

            Log.d("---","---");
            Log.w("//===========//","================================================");
            Log.d("","\n"+"[A_TextFile > 저장한 텍스트 파일 확인 실시]");
            Log.d("","\n"+"[경로 : "+String.valueOf(saveStorage)+"]");
            Log.d("","\n"+"[제목 : "+String.valueOf(nowTime)+"]");
            Log.d("","\n"+"[내용 : "+String.valueOf(saveData)+"]");
            Log.w("//===========//","================================================");
            Log.d("---","---");

            Toast.makeText(getActivity(),"텍스트 파일이 저장되었습니다",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(getActivity(),"텍스트 파일이 저장실패",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }
    */

    //SAF 구현 -> 취소, 이유는 이미지 까지 하고 적합한 코드를 찾았으므로 mediastore 이용
    /*
    private int WRITE_REQUEST_CODE = 43;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void StartRecord(){
        try {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow
                    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatDate = sdfNow.format(date);

            String fileName = formatDate+".txt";

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain" );
            intent.putExtra(Intent.EXTRA_TITLE,fileName);

            startActivityForResult(intent, WRITE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ParcelFileDescriptor pfd;
    private FileOutputStream fileOutputStream;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            addText(uri);
        }
    }

    public void addText(Uri uri){
        try {
            pfd = this.getActivity().getContentResolver().openFileDescriptor(uri, "w"); //프래그먼트라 getActivity 붙여줌
            fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void putString(String st) throws IOException {
        if(fileOutputStream!=null) fileOutputStream.write(st.getBytes());
    }

    public void FinishRecord() throws IOException {
        Toast.makeText(getActivity(), "저장되었습니다.", Toast.LENGTH_LONG).show();
        fileOutputStream.close();
        pfd.close();

    }
     */

    //MediaStore 이용
    //TODO [MediaStore 파일 저장 실시]
    private void saveFile(String fileName, String fileData) {
        /*
        String deleteCheck = SharedPrefs.getString(getActivity(), "saveScopeContentTxt");
        if(deleteCheck != null && deleteCheck.length() > 0){ //TODO 이전에 저장된 파일이 있을 경우 지운다
            try {
                ContentResolver contentResolver = getActivity().getContentResolver();
                contentResolver.delete(
                        Uri.parse(SharedPrefs.getString(getActivity(), "saveScopeContentTxt")),
                        null,
                        null);
                Log.d("---","---");
                Log.d("//===========//","================================================");
                Log.d("","\n"+"[A_ScopeTxt > saveFile() 메소드 : 이전에 저장된 파일 삭제 실시]");
                Log.d("","\n"+"[콘텐츠 파일 경로 : "+String.valueOf(deleteCheck)+"]");
                Log.d("","\n"+"[절대 파일 경로 : "+SharedPrefs.getString(getActivity(), "saveScopeAbsoluteTxt")+"]");
                Log.d("//===========//","================================================");
                Log.d("---","---");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        */
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
    private void readFile(String path) {
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
            return;
        }

        //TODO [텍스트 파일 불러오기 실시]
        String absoluteUrl = path;
        String line = ""; //TODO [한줄씩 읽기]
        try {
            BufferedReader buf = new BufferedReader(new FileReader(absoluteUrl));
            while((line=buf.readLine())!=null){
                Log.d("---","---");
                Log.w("//===========//","================================================");
                Log.d("","\n"+"[A_ScopeTxt > readFile() 메소드 : MediaStore 파일 불러오기 성공]");
                Log.d("","\n"+"[절대 파일 경로 : "+String.valueOf(absoluteUrl)+"]");
                Log.d("","\n"+"[절대 파일 내용 : "+String.valueOf(line)+"]");
                Log.w("//===========//","================================================");
                Log.d("---","---");

                //TODO [UI 동작]
                //save_content_edit.setText("");
                //save_content_edit.append(line);
                //save_content_edit.append("\n");
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //TODO [현재 시간 알아오는 메소드]
    public static String getNowTime24() {
        long time = System.currentTimeMillis();
        //SimpleDateFormat dayTime = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMdd");
        String str = dayTime.format(new Date(time));
        return "TX"+str; //TODO [TX는 text 의미]
    }



}

