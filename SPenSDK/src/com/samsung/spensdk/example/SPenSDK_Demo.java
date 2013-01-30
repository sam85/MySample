package com.samsung.spensdk.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SPenSDK_Demo extends Activity {

	private ListAdapter mListAdapter = null;
	private ListView mListView = null;
	
	// The item of list
	private static final int 	SDK_DEMO_EDITOR			= 0;
	private static final int 	SDK_DEMO_EXAMPLE		= 1;
	private static final int 	SDK_DEMO_GENERAL		= 2;
	private static final int 	TOTAL_LIST_NUM 	= 3;

	
	
	public static final String SECTION_EDITOR = "Editors";
	public static final String SECTION_SDK_EXAMPLES = "SDK Examples";
	public static final String SECTION_GENERAL_PURPOSE = "General Purpose";
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.spensdk_demo);
		
		createUI();
	}
	
	private void createUI() {
		
		TextView textTitle = (TextView)findViewById(R.id.title);
		textTitle.setText("S Pen SDK Demo");
		
		mListAdapter = new ListAdapter(this);
		mListView = (ListView)findViewById(R.id.demo_list);
		mListView.setAdapter(mListAdapter);

		mListView.setItemsCanFocus(false);
		mListView.setTextFilterEnabled(true);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// S Pen SDK Demo programs
				if(position == SDK_DEMO_EDITOR) {
					Intent intent = new Intent(SPenSDK_Demo.this, SPenSDK_Demo_Editor.class);				
					startActivity(intent);					
				}
				else if(position == SDK_DEMO_EXAMPLE) {
					Intent intent = new Intent(SPenSDK_Demo.this, SPenSDK_Demo_Example.class);				
					startActivity(intent);					
				}
				else if(position == SDK_DEMO_GENERAL) {
					Intent intent = new Intent(SPenSDK_Demo.this, SPenSDK_Demo_General.class);				
					startActivity(intent);					
				}
			}
		});
	}
	
	//=========================================
	// List Adapter : S Pen SDK Demo Programs   
	//=========================================
	public class ListAdapter extends BaseAdapter {

		public ListAdapter(Context context) {
		}    	

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.spensdk_demolist_item, parent, false);
			}
			// UI Item			
			TextView tvListItemText=  (TextView)convertView.findViewById(R.id.listitemText);
			tvListItemText.setTextColor(0xFFFFFFFF);

			//==================================
			// basic data display 
			//==================================
			if(position==0)
				tvListItemText.setText(SECTION_EDITOR);
			else if(position==1)
				tvListItemText.setText(SECTION_SDK_EXAMPLES);
			else if(position==2)
				tvListItemText.setText(SECTION_GENERAL_PURPOSE);
					
			return convertView; 
		}

		public void updateDisplay() {
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return TOTAL_LIST_NUM;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

}
