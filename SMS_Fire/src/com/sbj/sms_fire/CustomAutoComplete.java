package com.sbj.sms_fire;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class CustomAutoComplete extends AutoCompleteTextView{

	private String previous = "";
	  private String seperator = ",";

	  public CustomAutoComplete(Context paramContext)
	  {
	    super(paramContext);
	    setThreshold(0);
	  }

	  public CustomAutoComplete(Context paramContext, AttributeSet paramAttributeSet)
	  {
	    super(paramContext, paramAttributeSet);
	    setThreshold(0);
	  }

	  public CustomAutoComplete(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	  {
	    super(paramContext, paramAttributeSet, paramInt);
	    setThreshold(0);
	  }

	  public String getSeperator()
	  {
	    return this.seperator;
	  }

	  protected void performFiltering(CharSequence paramCharSequence, int paramInt)
	  {
	    String str1 = paramCharSequence.toString().trim();
	    this.previous = str1.substring(0, 1 + str1.lastIndexOf(getSeperator()));
	    String str2 = str1.substring(1 + str1.lastIndexOf(getSeperator()));
	    if (!TextUtils.isEmpty(str2))
	      super.performFiltering(str2, paramInt);
	  }

	  protected void replaceText(CharSequence paramCharSequence)
	  {
	    super.replaceText(this.previous + paramCharSequence + getSeperator());
	  }

	  public void setSeperator(String paramString)
	  {
	    this.seperator = paramString;
	  }
}
