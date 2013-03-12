package com.sbj.sms_fire.constant;

public enum TableConstants {
	
	TABLE_SmsFire("SMS_Fire", Constant.version);

	String tableName;
	int version;

	private TableConstants(String tableName, int version) {
		this.tableName = tableName;
		this.version = version;
	}

	public String getTableName() {
		return tableName;
	}

	public int getVersion() {
		return version;
	}
	
}	
