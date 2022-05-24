package com.hotcoa.sona.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPrefs {

    //========== [사용 설명] ==========
    /** [String 데이터 저장]
     S_Preference.setString(getApplication(), "Key_Name", "Data_kwon"); //특정 데이터 저장한다
     S_Preference.getString(getApplication(), "Key_Name"); //저장된 특정 데이터 불러온다
     */

    /** [Int 데이터 저장]
     S_Preference.setInt(getApplication(), "Key_Age", 28); //특정 데이터 저장한다
     S_Preference.getInt(getApplication(), "Key_Age"); //저장된 특정 데이터 불러온다
    */

    /** [Boolean 데이터 저장]
     S_Preference.setBoolean(getApplication(), "Key_Sex", true); //특정 데이터 저장한다
     S_Preference.getBoolean(getApplication(), "Key_Sex"); //저장된 특정 데이터 불러온다
     */

    /** [특정 데이터 삭제]
     S_Preference.removeKey(getApplication(), "Key_Name"); //특정 데이터 삭제한다
     */

    /** [전체 데이터 삭제]
     S_Preference.clear(getApplication()); //전체 데이터 삭제한다
     */

    //========== [전역 변수 선언 ==========
    public static final String PREFERENCES_NAME = "rebuild_preference";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = 1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    //========== [프리퍼런스 생성] ==========
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //========== [String 값 저장] ==========
    /**
     * String 값 저장
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }

    //========== [String 값 호출] ==========
    /**
     * String 값 로드
     */
    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getInt(key, DEFAULT_VALUE_INT);
    }


}
