package com.sprc.circlelauncher;

import android.os.Build;

public class Constants {

	public static final boolean SUPPORTS_FROYO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	public static final boolean SUPPORTS_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

	public static final String SETTINGS_PACKAGENAME = "com.android.settings";
	public static final String SETTINGS_APPDETAILS_CLASS = "com.android.settings.InstalledAppDetails";
	public static final String SETTINGS_EXTRA_KEY = SUPPORTS_FROYO ? "pkg" : "com.android.settings.ApplicationPkgName";

}
