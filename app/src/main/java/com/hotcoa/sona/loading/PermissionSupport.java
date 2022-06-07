package com.hotcoa.sona.loading;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.R)
public class PermissionSupport {
    private final Context context;
    private final Activity activity;

    private final String[] permissions = {
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
    };
    private List<String> permissionList;

    private final int MULTIPLE_PERMISSIONS = 1023;

    public PermissionSupport(Activity _activity, Context _context){
        this.activity = _activity;
        this.context = _context;
    }

    private void checkFilePermission() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) && !isFileGranted()){
            // [안드로이드 R 버전 이상 파일 접근 권한 필요]
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private boolean isFileGranted() {
        boolean granted = false; // 권한 부여 상태값 저장
        try {
            // [파일 접근 권한이 허용 된 경우]
            if (Environment.isExternalStorageManager()){
                granted = true;
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return granted;
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
        checkFilePermission();
        ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[0]), MULTIPLE_PERMISSIONS);
    }

    //요청한 권한에 대한 결과값 판단 및 처리
    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == MULTIPLE_PERMISSIONS && (grantResults.length >0)) {
            if(isFileGranted()) grantResults[0] = PackageManager.PERMISSION_GRANTED;
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] == -1) {
                    Log.d("per_log", permissions[i] + ": " + "denied");
                    return false;
                }
            }
            makeSaveFolder();
        }
        return true;
    }

    private void makeSaveFolder() {
        String state = Environment.getExternalStorageState();
        String directoryName = "SONA";
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("dirdir", "Mount Complete");
            Log.d("dirdir", Environment.getExternalStorageDirectory().toString());

            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.d("dirdir", "root: " + root);
            File sona = new File(root + "/" + directoryName);
            File txt = new File(root + "/" + directoryName + "/text");
            File img = new File(root + "/" + directoryName + "/image");
            File screenshot = new File(root + "/" + directoryName + "/screenshot");
            makeFolder(sona);
            makeFolder(txt);
            makeFolder(img);
            makeFolder(screenshot);
        }
    }
    private void makeFolder(File file) {
        try{
            if(!file.exists()) {
                if(file.mkdir()) {
                    Log.d("dirdir", "success");
                }
                else Log.d("dirdir", "fail");
            }
            else Log.d("dirdir", "already presented");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
