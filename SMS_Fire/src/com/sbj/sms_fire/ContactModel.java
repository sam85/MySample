package com.sbj.sms_fire;

public class ContactModel {
	
	 private String _ID;
	  private String contactName;
	  private String contactNumber;
	  private boolean selectFlag;

	  public String getContactID()
	  {
	    return this._ID;
	  }

	  public String getContactName()
	  {
	    return this.contactName;
	  }

	  public String getContactNumber()
	  {
	    return this.contactNumber;
	  }

	  public boolean isSelectFlag()
	  {
	    return this.selectFlag;
	  }

	  public void setContactID(String paramString)
	  {
	    this._ID = paramString;
	  }

	  public void setContactName(String paramString)
	  {
	    this.contactName = paramString;
	  }

	  public void setContactNumber(String paramString)
	  {
	    this.contactNumber = paramString;
	  }

	  public void setSelectFlag(boolean paramBoolean)
	  {
	    this.selectFlag = paramBoolean;
	  }
}
