package com.hotcoa.sona.loading;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.hotcoa.sona.R;
import com.hotcoa.sona.calendar.CalendarFragment;
import com.hotcoa.sona.main.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {

    private PermissionSupport permission;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        checkPermission();
    }

    private void startLoading() {
        Log.d("per_log", "startloading 실행");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }

    //권한 확인
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void checkPermission() {
        permission = new PermissionSupport(this, this);
        if(!permission.checkPermission()) {
            Log.d("per_log", "checkPermission 실행");
            permission.requestPermission();
        }
    }

    //권한 설정에 대한 사용자 응답 처리
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!permission.permissionResult(requestCode, permissions, grantResults)) Log.d("per_log", "denied");
        else Log.d("per_log", "accepted");
        startLoading();
    }
}