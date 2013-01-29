package com.DevExpressDemo.src;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DevExpressDemo extends Activity {
	
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);
		
		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DevExpressDemo.this, ChartShowExample.class);
				i.putExtra("Type", "Chart");
				startActivity(i);
				
			}
		});
		
		button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DevExpressDemo.this, ChartShowExample.class);
				i.putExtra("Type", "Guage");
				startActivity(i);
			}
		});
		
		button3 = (Button)findViewById(R.id.button3);
		button3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DevExpressDemo.this, ChartShowExample.class);
				i.putExtra("Type", "Range");
				startActivity(i);
			}
		});
		
		button4 = (Button)findViewById(R.id.button4);
		button4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DevExpressDemo.this, ChartShowExample.class);
				i.putExtra("Type", "List");
				startActivity(i);
			}
		});
		
		
    }
}