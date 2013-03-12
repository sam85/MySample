package com.sbj.sms_fire.dao;

import java.io.File;
import java.io.IOException;

import com.sbj.sms_fire.constant.ColumnConstants;
import com.sbj.sms_fire.constant.TableConstants;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = DataBaseHelper.class.getSimpleName();
	private static String DB_PATH = "/data/data/YOUR_PACKAGE/databases/";
	private static String DB_NAME = "SMS_Fire";
	private SQLiteDatabase mDataBase;
	private final Context mContext;
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "UpgradingDatabase, This will drop current database and will recreate it");
	}
}