package com.sbj.sms_fire.model;

public class clsEntryModule {

	int intGlCode;
	String EventName;
	String Contacts;
	String Message;
	String IntervalTime;
	String EntryDate;

	public int getIntGlCode() {
		return intGlCode;
	}

	public clsEntryModule(String  EventNam ,String contacts, String message,
			String intervalTime, String entryDate) {
		super();	
		EventName = EventNam;
		Contacts = contacts;
		Message = message;
		IntervalTime = intervalTime;
		EntryDate = entryDate;
	}

	public void setIntGlCode(int intGlCode) {
		this.intGlCode = intGlCode;
	}

	public String getEventName() {
		return EventName;
	}

	public void setEventName(String Eventame) {
		EventName = Eventame;
	}

	
	public String getContacts() {
		return Contacts;
	}

	public void setContacts(String contacts) {
		Contacts = contacts;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getIntervalTime() {
		return IntervalTime;
	}

	public void setIntervalTime(String intervalTime) {
		IntervalTime = intervalTime;
	}

	public String getEntryDate() {
		return EntryDate;
	}

	public void setEntryDate(String entryDate) {
		EntryDate = entryDate;
	}

}
