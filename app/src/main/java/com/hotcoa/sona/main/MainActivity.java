package com.hotcoa.sona.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


import com.google.android.material.navigation.NavigationView;
import com.hotcoa.sona.R;
import com.hotcoa.sona.appsetting.AppSettingFragment;
import com.hotcoa.sona.calendar.CalendarFragment;
import com.hotcoa.sona.usergide.UserGuideFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppSettingFragment appset;
    UserGuideFragment guide;
    CalendarFragment calendar;
    NavigationView navi;

    DrawerLayout drawerLayout;

    MainFragment mainmain;

    ImageButton move_main_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        drawerLayout = findViewById(R.id.drawlayout_x);


        //네비창 열면 연 다음 눌렀을 때 처리하는 것
        navi.setNavigationItemSelectedListener(menuItem->{

            if(menuItem.getItemId() == R.id.appsetting_navi){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_x, appset).commit();
            }
            if(menuItem.getItemId() == R.id.calaendar_navi){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_x,calendar ).commit();
            }
            if(menuItem.getItemId() == R.id.guide_navi){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_x,guide ).commit();
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

        /*
        if(item.getItemId()==R.id.home_bt){
            Toast.makeText(this, "홈아이콘 클릭", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

}