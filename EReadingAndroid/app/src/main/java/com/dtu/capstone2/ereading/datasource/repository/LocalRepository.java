package com.dtu.capstone2.ereading.datasource.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalRepository {
    private final static String E_READING_SHARED_PREFERENCE = "e_reading_shared_preference";
    private final static String KEY_TOKEN_USER = "key_token_user";
    private final static String KEY_EMAIL_USER = "key_email_user";
    private final static String KEY_NAME_LEVEL_USER = "key_name_level_user";

    private Context mContext;
    private SharedPreferences mShaPre;

    public LocalRepository(Context context) {
        mContext = context;
        mShaPre = context.getSharedPreferences(E_READING_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public void saveTokenUser(String token) {
        SharedPreferences.Editor editor = mShaPre.edit();
        editor.putString(KEY_TOKEN_USER, "Token " + token);
        editor.apply();
    }

    public String getTokenUser() {
        return mShaPre.getString(KEY_TOKEN_USER, "");
    }

    public void clearTokenUser() {
        mShaPre.edit().remove(KEY_TOKEN_USER).apply();
    }

    public void clearEmailUser() {
        mShaPre.edit().remove(KEY_EMAIL_USER).apply();
    }

    public void saveEmailUser(String email) {
        SharedPreferences.Editor editor = mShaPre.edit();
        editor.putString(KEY_EMAIL_USER, email);
        editor.apply();
    }

    public String getEmailUser() {
        return mShaPre.getString(KEY_EMAIL_USER, "");
    }

    public void saveNameLevelUser(String nameLevel) {
        SharedPreferences.Editor editor = mShaPre.edit();
        editor.putString(KEY_NAME_LEVEL_USER, nameLevel);
        editor.apply();
    }

    public String getNameLevelUser() {
        return mShaPre.getString(KEY_NAME_LEVEL_USER, "");
    }

    public Boolean isLogin() {
        return !getTokenUser().isEmpty();
    }

    public void clearNameLevelUser() {
        mShaPre.edit().remove(KEY_NAME_LEVEL_USER).apply();
    }
}
