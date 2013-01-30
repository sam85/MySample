package com.samsung.spensdk.example.tools;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

import com.samsung.spensdk.SCanvasView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SAMMFileListViewOld extends ListView {
	private Context mContext = null;
	private ArrayList<String> mList = new ArrayList<String>();
	private ArrayList<String> mFileList = new ArrayList<String>();
	private ArrayAdapter<String> mAdapter = null;
	
	private OnSAMMFileSelectedListener  mFileSelectedListener = null;
		
	private String mPath = "";
	private String [] mExts = null;
	private boolean mbSearchOnlySammFile = true;
	
	public SAMMFileListViewOld(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);		 
		init(context);
	}

	public SAMMFileListViewOld(Context context, AttributeSet attrs){
		super(context, attrs);		 
		init(context);
	}
	
	public SAMMFileListViewOld(Context context){
		super(context);		
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
		setOnItemClickListener(mOnItemClick);		
	}
	
	

	private boolean openFilePath(String path, boolean bSearchOnlySammFile){
		mFileList.clear();
		
		File folder = new File(path);
		
		if (!folder.exists())
		{
			// create directory
			if(!folder.mkdirs())
				return false;
		}
				
		File[] files = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				
				String fileExtension = getFileExtension(file);
				// Filtering as file extension
				if(mExts!=null){
					for(String ext : mExts){
						if(ext.equalsIgnoreCase(fileExtension))
							return true;
					}
					return false;
				}
				else{
					return true;
				}
			}
		});		
		
		if(files == null) 
			return false;
		
		for (int i=0; i<files.length; i++)
		{
			if(files[i].isDirectory())
				continue;
			else
			{	
				if(bSearchOnlySammFile){
					if(SCanvasView.isSAMMFile(path + files[i].getName()))
						mFileList.add(files[i].getName());
				}
				else{
					mFileList.add(files[i].getName());
				}
			}
		}
		
		Collections.sort(mFileList);
		
		return true;	
	}	
	
	
	public static String getFileExtension(File f){
		int idx = f.getName().lastIndexOf(".");
		if (idx == -1)
			return "";
	    else 
	    	return f.getName().substring(idx+1);
	}

	private void updateAdapter(){
		mList.clear();
		mList.addAll(mFileList);
		
		mAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mList);
		setAdapter(mAdapter);		
	}

	public void setFilePath(String path, String [] exts, boolean bSearchOnlySammFile){
		int length = path.length();
		if(length == 0)
			path = "/";
		else{
			String last = path.substring(length - 1, length);
			if(!last.matches("/"))
				path = path + '/';		
		}
		
		// set file extension
		if(exts==null){
			// Do not anything
		}
		else {
			setExts(exts);
		}
		
		// Update view 
		if(openFilePath(path, bSearchOnlySammFile))
		{
			mbSearchOnlySammFile = bSearchOnlySammFile;
			mPath = path;
			updateAdapter();
		}		
	}
	
	public void updateListView(){
		// Update view 
		if(openFilePath(mPath, mbSearchOnlySammFile))
		{
			updateAdapter();
		}
	}
	
	

	public String getPath(){
		return mPath;		
	}
	
	// Set the file extension (if exts is null, don't resister extension filter)
	public void setExts(String [] exts){
		if(exts==null)
			mExts = null;
		else
		{
		int nExtNum = exts.length;
		mExts = new String [nExtNum];
			System.arraycopy(exts, 0, mExts, 0, nExtNum);
		} 
	}

	public int getListCount(){
		return mList.size();
	}

	public boolean isEmpty(){
		return mList.isEmpty();
	}

	public void setOnFileSelectedListener(OnSAMMFileSelectedListener listener){
		mFileSelectedListener = listener;
	}

	public OnSAMMFileSelectedListener getOnFileSelectedListener(){
		return mFileSelectedListener;
	}

	private AdapterView.OnItemClickListener mOnItemClick = new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int p, long id) {
			String fileName = getItemAtPosition(p).toString();
			if(mFileSelectedListener != null)
				mFileSelectedListener.onSelected(mPath, fileName);		
		}
	};

	public interface OnSAMMFileSelectedListener {
		public void onSelected(String path, String fileName);
	}
}

