package com.sbj.sms_fire.dao;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sbj.sms_fire.constant.ColumnConstants;
import com.sbj.sms_fire.constant.TableConstants;
import com.sbj.sms_fire.model.clsEntryModule;

public class DBAdapter {

	private static final String TAG = "DBAdapter";
	public static final String DATABASE_NAME = "SMS_Fire";

	private final Context context;
	private final DataBaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DataBaseHelper(context);
	}

	public DBAdapter createDatabase() throws SQLException {
		try {
			DBHelper.createDataBase();
		} catch (IOException mIOException) {
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public DBAdapter open() throws SQLException {
		try {
			final boolean isAlreadyExists = DBHelper.checkDataBase();
			// android.util.Log.i("TAG", "isAlreadyExists: " + isAlreadyExists);
			if (!isAlreadyExists) {
				DBHelper.openDataBase();
				DBHelper.close();
				db = DBHelper.getReadableDatabase();
			} else if (isAlreadyExists) {
				db = DBHelper.getReadableDatabase();
			}
		} catch (SQLException mSQLException) {
			Log.e(TAG, mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}

	public void close() {
		DBHelper.close();
	}

	/**
	 * Inserting Entry Data in Database
	 * 
	 * @param clsEntryModule
	 *            Object of Class
	 */
	public void InsertEntry(clsEntryModule objclsEntryModule) {
		ContentValues cv = new ContentValues();
		cv.put(ColumnConstants.COLUMN_varContacts.getColumnName(),
				objclsEntryModule.getContacts());
		cv.put(ColumnConstants.COLUMN_varEventName.getColumnName(),
				objclsEntryModule.getEventName());
		cv.put(ColumnConstants.COLUMN_varMessage.getColumnName(),
				objclsEntryModule.getMessage());
		cv.put(ColumnConstants.COLUMN_varIntervalTime.getColumnName(),
				objclsEntryModule.getIntervalTime());
		cv.put(ColumnConstants.COLUMN_varEntryDate.getColumnName(),
				objclsEntryModule.getEntryDate());
		db.insert(TableConstants.TABLE_SmsFire.getTableName(),
				ColumnConstants.COLUMN_intGlCode.getColumnName(), cv);
	}

}