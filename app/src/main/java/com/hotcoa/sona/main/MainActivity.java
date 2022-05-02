package com.hotcoa.sona.main;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_x);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        navi = (NavigationView) findViewById(R.id.navi_x);
        appset = new AppSettingFragment();
        guide = new UserGuideFragment();
        calendar = new CalendarFragment();



        navi.setNavigationItemSelectedListener(menuItem->{
            if(menuItem.getItemId() == R.id.appsetting_navi){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_x, appset).commit();
            }

            if(menuItem.getItemId() == R.id.guide_navi){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_x,guide ).commit();
            }


            return true;
        });
    }
}