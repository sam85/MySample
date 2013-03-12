package com.sbj.sms_fire.constant;


public enum ColumnConstants {
	
	// Login
	COLUMN_intGlCode("intGlCode" , Constant.version),
	COLUMN_varEventName("varEventName" , Constant.version),
	COLUMN_varContacts("varContacts" , Constant.version),
	COLUMN_varContactsWithName("varContactsWithName" , Constant.version),
	COLUMN_varMessage("varMessage" , Constant.version),
	COLUMN_varIntervalTime("varIntervalTime" , Constant.version),
	COLUMN_varEntryDate("varEntryDate" , Constant.version);
	
		
	String coulmnName;
	int version;

	private ColumnConstants(String coulmnName, int version) {
		this.coulmnName = coulmnName;
		this.version = version;
	}

	public String getColumnName() {
		return coulmnName;
	}

	public int getVersion() {
		return version;
	}
}
