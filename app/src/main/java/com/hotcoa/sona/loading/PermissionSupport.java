package com.hotcoa.sona.loading;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionSupport {
    private final Context context;
    private final Activity activity;

    private final String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SET_ALARM,
            Manifest.permission.VIBRATE

    };
    private List<String> permissionList;

    private final int MULTIPLE_PERMISSIONS = 1023;

    public PermissionSupport(Activity _activity, Context _context){
        this.activity = _activity;
        this.context = _context;
    }

    //배열로 선언한 권한 중 허용되지 않은 권한 있는지 체크
    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<>();

        for(String pm : permissions){
            result = ContextCompat.checkSelfPermission(context, pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }
        return permissionList.isEmpty();
    }

    //배열로 선언한 권한에 대해 사용자에게 허용 요청
    public void requestPermission(){
        ActivityCompat.requestPermissions(activity, (String[]) permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
    }

    //요청한 권한에 대한 결과값 판단 및 처리
    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == MULTIPLE_PERMISSIONS && (grantResults.length >0)) {
            for (int grantResult : grantResults) {
                if (grantResult == -1) return false;
            }
        }
        return true;
    }

}
