package org.hse.android;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final static String PREFERENCE_FILE = "org.hse.android.file";
    private final static String AVATAR_PREFERENCE_KEY = "avatar";
    private final static String NAME_KEY = "user_name";

    private final SharedPreferences sharedPref;

    public PreferenceManager(Context context) {
        this.sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    public void saveAvatarPath(String path){
        saveValue(AVATAR_PREFERENCE_KEY, path);
    }

    public String getAvatarPath(){
        return getValue(AVATAR_PREFERENCE_KEY, null);
    }

    public void saveName(String name){
        saveValue(NAME_KEY, name);
    }

    public String getName(){
        return getValue(NAME_KEY, null);
    }

    private void saveValue(String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getValue(String key, String defaultValue){
        return sharedPref.getString(key, defaultValue);
    }
}
