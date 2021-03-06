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
        //?????? ??????
        //checkPermission();

        //?????? ??????
        toolbar = (Toolbar) findViewById(R.id.toolbar_x);
        setSupportActionBar(toolbar);

        //????????? ?????? ???????????? ?????? ????????? add
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_24);  //????????? ??????
        getSupportActionBar().setDisplayShowTitleEnabled(false);// ActionBar ????????? ?????????
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


        //????????? ?????? ??? ?????? ????????? ??? ???????????? ???
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

            drawerLayout.closeDrawer(GravityCompat.START);  //????????? ????????? ?????? ???

            return true;
        });

        mainmain = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_x, mainmain);
        transaction.commit();

        //???????????? ?????? ?????? ??????
        move_main_button = findViewById(R.id.home_bt);
        move_main_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               Intent intent = new Intent(getApplicationContext(), MainActivity.class); //?????? ??? MainFragment.class??? ????????? ??????
               startActivity(intent);
            }
        });
        setAndroidID();
        set_curDate_Today();
    }

    //ctrl+o ?????? ????????????????????? ?????? ??????
    //????????? ????????? ?????? ????????? ???
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
        //?????? ??????????????? ????????? 6.0???????????? ???????????? ????????????.
        //???????????????6.0 (????????????) ?????? ???????????? ?????? ???????????? ??????
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //?????? ?????? ????????? ????????????.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //?????? ?????????????????? ???????????? ?????? ?????????
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
                //???????????????
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"??? ?????? ???????????????",Toast.LENGTH_LONG).show();
                }
                else {
                    //????????? ???????????? ???????????? ???????????? ??? ??????
                    Toast.makeText(getApplicationContext(),"??? ?????? ???????????????",Toast.LENGTH_LONG).show();
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

        Log.d("????????? id", "Android_ID >>> "+ android_id);
        Log.d("check prefs", prefs.getString("android_id", ""));

        try{
            Log.d("????????? id PBKDF", " >>>>>>> " + LEA_Crypto.toHexString(LEA_Crypto.PBKDF(prefs.getString("android_id",""))));
        }
        catch (Exception e){
            Log.e("PBKDF ERROR", e.toString());
        }
    }
    // [?????? ?????? ???????????? ?????????]
    public String set_curDate_Today() {
        SharedPreferences prefs = this.getSharedPreferences("curDate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy??? M??? dd???");
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
            //????????? ?????? ????????? ???????????? ?????? ??????????????? ?????? ??????????????????
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
        Log.d("","?????????"+fragmentStack.size());
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