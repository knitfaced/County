package com.bobbinsmag;

import android.app.Activity;
import android.content.SharedPreferences;

public class UserPreferences {

	public static final String SHOW_CURRENT_ROW = "showCurrentRow";
	private static final String PREFS_NAME = "RowCountPrefs";
	
	private Activity activity;
	
	public UserPreferences(Activity activity) {
		this.activity = activity;
	}
	
	public boolean isShowCurrentRow() {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(SHOW_CURRENT_ROW, true);
	}
	public void setShowCurrentRow(boolean showCurrentRow) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(SHOW_CURRENT_ROW, showCurrentRow);
		editor.commit();
	}
}
