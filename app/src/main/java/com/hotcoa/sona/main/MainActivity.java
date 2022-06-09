package com.hotcoa.sona.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


import com.google.android.material.navigation.NavigationView;
import com.hotcoa.sona.R;
import com.hotcoa.sona.appsetting.AppSettingFragment;
import com.hotcoa.sona.calendar.CalendarFragment;
import com.hotcoa.sona.checkdiary.CheckDiaryFragment;
import com.hotcoa.sona.contents.ContentsFragment;
import com.hotcoa.sona.profile.ProfileEditFragment;
import com.hotcoa.sona.profile.ProfileFragment;
import com.hotcoa.sona.usergide.UserGuideFragment;
import com.hotcoa.sona.writediary.HashTagFragment;
import com.hotcoa.sona.writediary.WriteDiaryFragment;
import com.hotcoa.sona.leacrypto.LEA_Crypto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    public Stack <Fragment> fragmentStack = new Stack<>();

    Toolbar toolbar;
    AppSettingFragment appset;
    UserGuideFragment guide;
    CalendarFragment calendar;
    WriteDiaryFragment write;
    CheckDiaryFragment check;
    ContentsFragment contents;
    ProfileFragment profile;
    ProfileEditFragment profileedit;

    NavigationView navi;
    HashTagFragment hash;


    DrawerLayout drawerLayout;

    MainFragment mainmain;


    public BackpressListener backpressListener = null;

    ImageButton move_main_button;

    String[] permission_list = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public enum Direction{
        appsetGo, guideGo,calendarGo,writeGo, checkGo, mindcheckGo,contentsGo, profileGo, profileeditGo,  hashGo
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //권한 체크
        //checkPermission();

        //툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar_x);
        setSupportActionBar(toolbar);

        //사이드 메뉴 오픈하기 위한 아이콘 add
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_24);  //아이콘 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false);// ActionBar 앱이름 감추기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setTitle("SONA");

        navi = (NavigationView) findViewById(R.id.navi_x);
        appset = new AppSettingFragment();
        guide = new UserGuideFragment();
        calendar = new CalendarFragment();
        write = new WriteDiaryFragment();
        check = new CheckDiaryFragment();
        contents = new ContentsFragment();
        profile = new ProfileFragment();
        profileedit = new ProfileEditFragment();
        hash = new HashTagFragment();

        drawerLayout = findViewById(R.id.drawlayout_x);


        //네비창 열면 연 다음 눌렀을 때 처리하는 것
        navi.setNavigationItemSelectedListener(menuItem->{
            if(menuItem.getItemId() == R.id.appsetting_navi){
                onChangeFragment(Direction.appsetGo);
            }
            if(menuItem.getItemId() == R.id.calaendar_navi){
                onChangeFragment(Direction.calendarGo);
            }
            if(menuItem.getItemId() == R.id.guide_navi){
                onChangeFragment(Direction.guideGo);
            }

            if(menuItem.getItemId() == R.id.contentschuchu_navi){
                set_curDate_Today();
                onChangeFragment(Direction.contentsGo);
            }
            if(menuItem.getItemId() == R.id.profile_navi){
                onChangeFragment(Direction.profileGo);
            }

            drawerLayout.closeDrawer(GravityCompat.START);  //방향을 지정해 주는 것

            return true;
        });

        mainmain = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_x, mainmain);
        transaction.commit();

        //메인메뉴 이동 버튼 구현
        move_main_button = findViewById(R.id.home_bt);
        move_main_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               Intent intent = new Intent(getApplicationContext(), MainActivity.class); //이게 왜 MainFragment.class가 아닌지 의문
               startActivity(intent);
            }
        });
        setAndroidID();
        set_curDate_Today();
    }

    //ctrl+o 에서 오버라이드할꺼 검색 ㄱㄴ
    //누르면 사이드 메뉴 열리는 것
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //drawerLayout = findViewById(R.id.drawlayout_x);
        if(item.getItemId()==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        //안드로이드6.0 (마시멜로) 이후 버전부터 유저 권한설정 필요
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용됬다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"앱 권한 설정되있음",Toast.LENGTH_LONG).show();
                }
                else {
                    //권한을 하나라도 허용하지 않는다면 앱 종료
                    Toast.makeText(getApplicationContext(),"앱 권한 설정하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }*/
    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    public void setAndroidID(){
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        SharedPreferences prefs = this.getSharedPreferences("android_id", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("android_id", android_id);
        editor.apply();

        Log.d("휴대폰 id", "Android_ID >>> "+ android_id);
        Log.d("check prefs", prefs.getString("android_id", ""));

        try{
            Log.d("휴대폰 id PBKDF", " >>>>>>> " + LEA_Crypto.toHexString(LEA_Crypto.PBKDF(prefs.getString("android_id",""))));
        }
        catch (Exception e){
            Log.e("PBKDF ERROR", e.toString());
        }
    }
    // [현재 날짜 저장하는 메소드]
    public String set_curDate_Today() {
        SharedPreferences prefs = this.getSharedPreferences("curDate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy년 M월 dd일");
        String nowDayTime = dayTime.format(new Date(time));
        Log.d("now24", nowDayTime);

        editor.putString("curDate", nowDayTime);
        editor.apply();

        return nowDayTime;
    }
    private void pathSave() {
        SharedPreferences pathPrefs = this.getSharedPreferences("curDateTxtPath", Context.MODE_PRIVATE);
        SharedPreferences datePrefs = this.getSharedPreferences("curDate", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pathPrefs.edit();
        String curDate = datePrefs.getString("curDate","");
        String diary_path = "/storage/emulated/0/SONA/text/" + curDate + ".txt";
        editor.putString("curDateTxtPath", diary_path);
        editor.apply();
    }

    public void onChangeFragment(Direction d){
        switch(d){
            //여러분 이동 이벤트 관련해서 해당 케이스들로 전부 수정해주세요
            case appsetGo:
                changeFragment(appset);
                break;
            case guideGo:
                changeFragment(guide);
                break;
            case calendarGo:
                CalendarFragment new_calendarFragment = new CalendarFragment();
                changeFragment(new_calendarFragment);
                break;
            case writeGo:
                WriteDiaryFragment new_writeDiaryFragment = new WriteDiaryFragment();
                changeFragment(new_writeDiaryFragment);
                break;
            case checkGo:
                changeFragment(check);
                break;
            case contentsGo:
                changeFragment(contents);
                break;
            case profileGo:
                changeFragment(profile);
                break;
            case profileeditGo:
                changeFragment(profileedit);
                break;
            case hashGo:
                HashTagFragment new_hashtagFragment = new HashTagFragment();
                changeFragment(new_hashtagFragment);
                break;
            default:
                break;
        }
    }

    private void changeFragment(Fragment fragment){
        Log.d("","제발요"+fragmentStack.size());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_x,fragment).commit();
    }


    @Override
    public void onBackPressed() {
        if(backpressListener!=null){
            backpressListener.onBackpress();
            return;
        }
        super.onBackPressed();
    }


}