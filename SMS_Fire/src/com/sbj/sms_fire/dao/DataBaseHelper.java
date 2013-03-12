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
	private static final String TABLE_CREATE_SMS = "CREATE TABLE "
			+ TableConstants.TABLE_SmsFire.getTableName() + " ("
			+ ColumnConstants.COLUMN_intGlCode.getColumnName()
			+ " Integer Primary Key,"
			+ ColumnConstants.COLUMN_varEventName.getColumnName() + " Text ,"
			+ ColumnConstants.COLUMN_varContacts.getColumnName() + " Text ,"
			+ ColumnConstants.COLUMN_varMessage.getColumnName() + " Text ,"
			+ ColumnConstants.COLUMN_varIntervalTime.getColumnName() + " Text ,"
			+ ColumnConstants.COLUMN_varEntryDate.getColumnName() + " Text)";

	
	
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		this.mContext = context;
	}

	public void createDataBase() throws IOException {
		boolean mDataBaseExist = checkDataBase();
		if (!mDataBaseExist) {
			this.getReadableDatabase();
		}
	}

	public boolean checkDataBase() {
		SQLiteDatabase mCheckDataBase = null;
		try {
			String mPath = DB_PATH + DB_NAME;
			File pathFile = new File(mPath);
			if(pathFile.exists()) {
				mCheckDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			}
		} catch (SQLiteException mSQLiteException) {
			Log.e(TAG, "DatabaseNotFound " + mSQLiteException.toString());
		}

		if (mCheckDataBase != null) {
			mCheckDataBase.close();
		}
		return mCheckDataBase != null;
	}
	
	public boolean openDataBase() throws SQLException {
		String mPath = DB_PATH + DB_NAME;
		mDataBase = SQLiteDatabase.openDatabase(mPath, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return mDataBase != null;
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_SMS);
		Log.i(TAG, "Database Created.");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "UpgradingDatabase, This will drop current database and will recreate it");
	}
}