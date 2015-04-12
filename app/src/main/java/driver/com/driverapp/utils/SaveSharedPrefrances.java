package driver.com.driverapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by parviz on 4/12/15.
 */
public class SaveSharedPrefrances {
    static final String PREF_PHONE_NUMBER = "1234567";
    static final String PREF_PASSWORD = "password";

    static SharedPreferences getSharedPreferences(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setNumber(Context context,String number)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_PHONE_NUMBER,number);
        editor.commit();
    }

    public static void setPassword(Context context,String password)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_PASSWORD,password);
        editor.commit();
    }

    public static String getNumber(Context context)
    {
        return  getSharedPreferences(context).getString(PREF_PHONE_NUMBER,"");
    }

    public static String getPassword(Context context)
    {
        return  getSharedPreferences(context).getString(PREF_PASSWORD,"");
    }

    public static void clearData(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }
}
