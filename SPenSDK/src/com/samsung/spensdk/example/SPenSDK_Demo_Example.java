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

import com.samsung.spensdk.example.eraseranimation.SPen_Example_EraserAnimation;
import com.samsung.spensdk.example.imageclip.SPen_Example_ImageClip;
import com.samsung.spensdk.example.sammeditor.SPen_Example_SAMMEditor;
import com.samsung.spensdk.example.scratcheffect.SPen_Example_ScratchEffect;
import com.samsung.spensdk.example.signature.SPen_Example_Signature;
import com.samsung.spensdk.example.spenevent.SPen_Example_SPenEvent;

public class SPenSDK_Demo_Example extends Activity {
	
//	Context mContext = null;

	private ListAdapter mListAdapter = null;
	private ListView mlistView = null;
	
	// The item of list
	private static final int 	SDK_EXAMPLE_SPENEVENT 		= 0;
	private static final int 	SDK_EXAMPLE_IMAGECLIP   	= 1;
	private static final int 	SDK_EXAMPLE_SCRATCHEFFECT 	= 2;		
	private static final int 	SDK_EXAMPLE_ERASERANIMATION = 3;
	private static final int 	SDK_EXAMPLE_SAMMEDITOR 		= 4;
	private static final int 	SDK_EXAMPLE_SIGNATURE 		= 5;
	private static final int 	TOTAL_LIST_NUM 				= 6;	
	
	
	private final String EXAMPLE_NAMES[] = {
			"Example : SPen Event",
			"Example : Image Clip",
			"Example : Scratch Effect",
			"Example : Eraser Animation",
			"Example : SAMM Editor",
			"Example : Signature"
	};
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.spensdk_demo);
		
		createUI();
	}
	
	private void createUI() {		
		TextView textTitle = (TextView)findViewById(R.id.title);
		textTitle.setText(SPenSDK_Demo.SECTION_SDK_EXAMPLES);
		textTitle.setTextColor(0xFFCCFFCC);
		
		mListAdapter = new ListAdapter(this);
		mlistView = (ListView)findViewById(R.id.demo_list);
		mlistView.setAdapter(mListAdapter);

		mlistView.setItemsCanFocus(false);
		mlistView.setTextFilterEnabled(true);
		mlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// S Pen SDK Demo programs
				if(position == SDK_EXAMPLE_SPENEVENT) {
					Intent intent = new Intent(SPenSDK_Demo_Example.this, SPen_Example_SPenEvent.class);				
					startActivity(intent);
				}
				else if(position == SDK_EXAMPLE_IMAGECLIP) {
					Intent intent = new Intent(SPenSDK_Demo_Example.this, SPen_Example_ImageClip.class);				
					startActivity(intent);
				}
				else if(position == SDK_EXAMPLE_SCRATCHEFFECT) {
					Intent intent = new Intent(SPenSDK_Demo_Example.this, SPen_Example_ScratchEffect.class);				
					startActivity(intent);
				}
				else if(position == SDK_EXAMPLE_ERASERANIMATION) {
					Intent intent = new Intent(SPenSDK_Demo_Example.this, SPen_Example_EraserAnimation.class);				
					startActivity(intent);
				}
				else if(position == SDK_EXAMPLE_SAMMEDITOR) {
					Intent intent = new Intent(SPenSDK_Demo_Example.this, SPen_Example_SAMMEditor.class);				
					startActivity(intent);
				}
				else if(position == SDK_EXAMPLE_SIGNATURE) {
					Intent intent = new Intent(SPenSDK_Demo_Example.this, SPen_Example_Signature.class);				
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
