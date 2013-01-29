package com.DevExpressDemo.src;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ChartShowExample extends Activity {
	
	WebView webView1;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		webView1 = (WebView)findViewById(R.id.webView1);
		webView1.getSettings().setJavaScriptEnabled(true);
		
		String Type = getIntent().getStringExtra("Type");
		
		if(Type.equalsIgnoreCase("Chart"))
		webView1.loadUrl("file:///android_asset/www/chart.html");
		
		else if(Type.equalsIgnoreCase("Guage"))
			webView1.loadUrl("file:///android_asset/www/gauge.html");
		
		else if(Type.equalsIgnoreCase("Range"))
			webView1.loadUrl("file:///android_asset/www/rangeselect.html");
		
		else if(Type.equalsIgnoreCase("List"))
			webView1.loadUrl("file:///android_asset/www/list.html");
    }
}

