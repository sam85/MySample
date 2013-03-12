package com.sbj.sms_fire;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.sbj.sms_fire.dao.DBAdapter;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TestFragment;

public class MainActivity extends FragmentActivity {

	DBAdapter db;
	private static final String[] CONTENT = new String[] { "Add Entry",
			"Entry List" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_testlist_pageindicate);
		CreateDatabase();
		SetFragment();		
	}

	private void SetFragment() {

		FragmentPagerAdapter adapter = new SMSDetailsAdapter(
				getSupportFragmentManager());

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setCurrentItem(0);

		// We set this on the indicator, NOT the pager
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	private void CreateDatabase() {
		try {
			db = new DBAdapter(this);
			db.createDatabase();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	class SMSDetailsAdapter extends FragmentPagerAdapter {
		public SMSDetailsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// Log.e("getItem ", " : " + position);
			return TestFragment.newInstance(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// Log.e("getPageTitle ", " : " + position);
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

}
