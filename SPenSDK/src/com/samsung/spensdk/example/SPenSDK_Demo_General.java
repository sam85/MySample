package com.samsung.spensdk.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.samsung.spensdk.example.imagefilter.SPen_Example_ImageFilter;
import com.samsung.spensdk.example.spenevent_general.SPen_Example_SPenEventGeneral;
import com.samsung.spensdk.example.spenhover_general.SPen_Example_SPenHoverPointerGeneral;
import com.samsung.spensdk.example.spenhover_general.SPen_Example_SPenHoverPopupGeneral;

public class SPenSDK_Demo_General extends Activity {
	
	private ListAdapter mListAdapter = null;
	private ListView mListView = null;
	
	// The item of list
	private static final int 	SDK_GENERAL_IMAGEFILTER = 0;
	private static final int 	SDK_GENERAL_SPENEVENT 	= 1;	
	private static final int 	SDK_GENERAL_HOVER_POINTER 	= 2;
	private static final int 	SDK_GENERAL_HOVER_POPUPWINDOW = 3;
	

	private static final int 	TOTAL_LIST_NUM 	= 4;	

	private final String EXAMPLE_NAMES[] = {
			"General : Image Filter",
			"General : SPen Event",
			"General : SPen Hover Pointer",
			"General : SPen Hover PopUp Window",
	};
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.spensdk_demo);
		
		createUI();
	}
	
	private void createUI() {
		TextView textTitle = (TextView)findViewById(R.id.title);
		textTitle.setText(SPenSDK_Demo.SECTION_GENERAL_PURPOSE);
		textTitle.setTextColor(0xFFFFFFCC);
		
		mListAdapter = new ListAdapter(this);
		mListView = (ListView)findViewById(R.id.demo_list);
		mListView.setAdapter(mListAdapter);

		mListView.setItemsCanFocus(false);
		mListView.setTextFilterEnabled(true);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// S Pen SDK Demo programs
				if(position == SDK_GENERAL_IMAGEFILTER) {
					Intent intent = new Intent(SPenSDK_Demo_General.this, SPen_Example_ImageFilter.class);				
					startActivity(intent);
				}
				else if(position == SDK_GENERAL_SPENEVENT) {
					Intent intent = new Intent(SPenSDK_Demo_General.this, SPen_Example_SPenEventGeneral.class);				
					startActivity(intent);
				}
				else if(position == SDK_GENERAL_HOVER_POINTER) {
					Intent intent = new Intent(SPenSDK_Demo_General.this, SPen_Example_SPenHoverPointerGeneral.class);				
					startActivity(intent);
				}
				else if(position == SDK_GENERAL_HOVER_POPUPWINDOW) {
					Intent intent = new Intent(SPenSDK_Demo_General.this, SPen_Example_SPenHoverPopupGeneral.class);				
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
			if(position < TOTAL_LIST_NUM){
				tvListItemText.setText(EXAMPLE_NAMES[position]);
					
			}
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
