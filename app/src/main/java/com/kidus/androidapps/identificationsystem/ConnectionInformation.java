package com.kidus.androidapps.identificationsystem;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kidus on .
 */
public class ConnectionInformation {
    public static String remoteIp = null;
    public static String currentPremises = null;

    public static void changeSettings(Context context, String ip, String premises) {
        remoteIp = ip;
        currentPremises = premises;
        SharedPreferences.Editor editor = context.getSharedPreferences("CON_INFO", Context.MODE_PRIVATE).edit();
        editor.putString("IP", remoteIp);
        editor.putString("PREMISES", currentPremises);
        editor.commit();
    }

    public static void loadSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("CON_INFO", Context.MODE_PRIVATE);
        remoteIp = preferences.getString("IP", "");
        currentPremises = preferences.getString("PREMISES", "");
    }
}
