package com.zebra.scanner.inventory.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
	
	private static final String APP_SETTINGS = "settings";

	public static void addSetting(String key, String value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putString(key, value);
	    editor.commit();
	}

	public static void addSetting(String key, int value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putInt(key, value);
	    editor.commit();
	}
	
	public static void addSetting(String key, float value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putFloat(key, value);
	    editor.commit();
	}
	
	public static void addSetting(String key, boolean value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean(key, value);
	    editor.commit();
	}
	
	public static String getSetting(String key, String defValue, Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
	    return settings.getString(key, defValue);
	}
	
	public static int getSetting(String key, int defValue, Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
	    return settings.getInt(key, defValue);
	}
	
	public static float getSetting(String key, float defValue, Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
	    return settings.getFloat(key, defValue);
	}
	
	public static boolean getSetting(String key, boolean defValue, Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
	    return settings.getBoolean(key, defValue);
	}

	public static void removeAllSettings(Context context) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
	
	
}
