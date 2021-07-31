package com.b18060412.superdiary;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileManager {
    public static String getName(Context context) {
        SharedPreferences settings = context.getSharedPreferences("profileName", Context.MODE_PRIVATE);
        return settings.getString("name", "你的名字？");
    }
    public static void setName(Context context, String yourNameIs) {
        SharedPreferences settings = context.getSharedPreferences("profileName", Context.MODE_PRIVATE);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString("name", yourNameIs);
        PE.commit();
    }
}
