package com.samsung.spensdk.example.tools;

import java.io.File;
import java.util.ArrayList;

import com.samsung.spensdk.example.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ToolAudioListView extends Activity {
	Context mContext = null;

	private Button BtnOK, BtnCancel;
	private ListView lView;		
	private ArrayList<String> userSoundList;
	private ListAdapter listAdapter = null;		
	private int m_nCurAudioFileIndex = -1;	
	private String strFilepath = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String m_strAudioFileName = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_sound_list);

		if(strFilepath == null)
			return;

		mContext = this;		
		m_nCurAudioFileIndex = -1;

		// load sound file list
		userSoundList = new ArrayList<String>();        
		audioFileListUp(strFilepath);

		listAdapter = new ListAdapter(this);
		lView = (ListView)findViewById(R.id.soundList);
		lView.setAdapter(listAdapter);

		lView.setItemsCanFocus(false);
		lView.setTextFilterEnabled(true);
		lView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				m_nCurAudioFileIndex = position;		
				m_strAudioFileName = userSoundList.get(m_nCurAudioFileIndex);				
				listAdapter.notifyDataSetChanged();
				listAdapter.updateDisplay();
			}
		});

		BtnOK = (Button) findViewById(R.id.btnOK);
		BtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(m_strAudioFileName != null)
				{
					getIntent().putExtra("BackgroundAudioFileName", m_strAudioFileName);						
					setResult(RESULT_OK, getIntent());
					finish();
				}
				else
				{
					Toast.makeText(mContext, "Select valid audiofile!!!!", Toast.LENGTH_SHORT).show();					
				}
			}
		});        
		BtnCancel = (Button) findViewById(R.id.btnCancel);
		BtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				finish();
			}
		});        


		// update button and listview
		if(userSoundList.size() <= 0)
		{
			BtnOK.setEnabled(false);			
			lView.setVisibility(View.GONE);
			findViewById(R.id.noItemListMsg).setVisibility(View.VISIBLE);
		}
		else
		{
			BtnOK.setEnabled(true);
			lView.setVisibility(View.VISIBLE);
			findViewById(R.id.noItemListMsg).setVisibility(View.GONE);
		}   
	}

	public void audioFileListUp(String filepath)
	{
		if(filepath == null)
			return;		

		File file = new File(filepath);
		File[] files = file.listFiles();
		if (files == null)
			return;

		for(int i = 0;i < files.length;i++)
		{	
			if(files[i].isDirectory())
			{
				strFilepath = files[i].getPath();
				audioFileListUp(strFilepath);
			}
			else
			{
				int nExtIndex = files[i].getName().lastIndexOf(".");				
				String strExt = files[i].getName().substring(nExtIndex + 1);
				// Supporting audio type
				if(strExt.compareToIgnoreCase("3gp")==0 
						||	strExt.compareToIgnoreCase("mp3")==0
						||	strExt.compareToIgnoreCase("wav")==0
						||	strExt.compareToIgnoreCase("amr")==0
						||	strExt.compareToIgnoreCase("wma")==0
						||	strExt.compareToIgnoreCase("m4a")==0
						||	strExt.compareToIgnoreCase("aac")==0
						||	strExt.compareToIgnoreCase("ogg")==0
						||	strExt.compareToIgnoreCase("mid")==0
						||	strExt.compareToIgnoreCase("3ga")==0)				
				{
					userSoundList.add(files[i].getPath());
				}
			}
		}
	}

	public class ListAdapter extends BaseAdapter{	
		//private Context mContext;

		public ListAdapter(Context context) {
			//mContext = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			if(convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.tool_sound_list_item, parent, false);
			}

			String strPath = userSoundList.get(position);
			int ntemp = strPath.lastIndexOf("/");			
			String strTitle = strPath.substring(ntemp+1); 
			TextView textText=  (TextView)convertView.findViewById(R.id.itemText);
			textText.setText(strTitle);
			if(position == m_nCurAudioFileIndex)
			{
				textText.setTextColor(0xFF00FF00);
			}
			else
			{
				textText.setTextColor(0xFFFFFFFF);
			}

			RadioButton radio = (RadioButton)convertView.findViewById(R.id.checkradio);
			if(position == m_nCurAudioFileIndex) radio.setChecked(true);
			else radio.setChecked(false);

			return convertView; 
		}	
		public void updateDisplay() {
			this.notifyDataSetChanged();
		}       
		@Override
		public int getCount() {
			if(userSoundList==null)
				return 0;
			return userSoundList.size();
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
