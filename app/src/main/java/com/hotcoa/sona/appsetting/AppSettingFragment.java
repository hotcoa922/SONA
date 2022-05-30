package com.hotcoa.sona.appsetting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.BackpressListener;
import com.hotcoa.sona.main.BaseFragment;
import com.hotcoa.sona.main.MainActivity;

public class AppSettingFragment extends BaseFragment {


    private ViewGroup rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_app_setting, container, false);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchBtn = (Switch) rootView.findViewById(R.id.switch1);
        Button infoBtn = rootView.findViewById(R.id.info_button);
        Button reportBtn = rootView.findViewById(R.id.report_button);

        //진동 설정
        if(savedInstanceState == null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("vibrate_info", Context.MODE_PRIVATE);
            switchBtn.setChecked(prefs.getBoolean("vibrate", false));
        }
        switchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences prefs = getActivity().getSharedPreferences("vibrate_info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if(isChecked) {
                editor.putBoolean("vibrate", true);
                editor.apply();
                Toast.makeText(rootView.getContext(), "진동 ON", Toast.LENGTH_SHORT).show();
            }
            else {
                editor.putBoolean("vibrate", false);
                editor.apply();
                Toast.makeText(rootView.getContext(), "진동 OFF", Toast.LENGTH_SHORT).show();
            }
        });
        //버전 정보
        infoBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("version " + getAppVersionName()).setTitle("SONA 애플리케이션 버전 정보");
            builder.setIcon(R.drawable.ic_circle_info_solid);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        //개발자 문의
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("개발자 이메일: SONA@soongil.ac.kr\n이메일로 버그 제보 및 문의 바랍니다.").setTitle("개발자 문의");
                builder.setIcon(R.drawable.ic_flag_solid);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return rootView;
    }
    private String getAppVersionName(){
        PackageInfo packageInfo;
        try{
            PackageManager packageManager = getContext().getPackageManager();
            packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return null;
        }
        return packageInfo.versionName;
    }
}