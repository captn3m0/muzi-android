package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;


public class SearchResultsWithTab extends SherlockFragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private static String url;
	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_NAME = "name";
	private static final String TAG_TITLE = "title";

	JSONArray FilteredJSONArray = null;
	static ArrayList<String> arrayList1, arrayList2, arrayList3;
	static ArrayAdapter<String> adapter1, adapter2, adapter3;
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results_with_tab);

		String text = getIntent().getStringExtra("search_query");
		url = GlobalVariables.api_root + "search/?search=" + text;
		this.setTitle("Search Results for " + text);
		Log.i("url", url);

		arrayList1 = new ArrayList<String>();
		arrayList2 = new ArrayList<String>();
		arrayList3 = new ArrayList<String>();
		// Set up the action bar.
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.vpSearchResults);

		mViewPager.setBackgroundColor(getResources().getColor(R.color.Black));
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {

						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		new LoadAllProducts().execute();
	}

	// Solves the issue of unresponsive tabs when orientation changes are
	// self-handled

	// private void recreateTabs() {
	// if (mTabScrollView == null) {
	// return;
	// }
	// ArrayList<TabImpl> tabs = new ArrayList<TabImpl>(mTabs);
	// int tabPosition = getSelectedNavigationIndex();
	// cleanupTabs();
	// mTabScrollView.onDetachedFromWindow();
	// mTabScrollView = null;
	// ensureTabsExist();
	// for (ActionBar.Tab tab : tabs) {
	// addTab(tab, false);
	// }
	// if (tabPosition != INVALID_POSITION) {
	// selectTab(tabs.get(tabPosition));
	// }
	//
	// }

	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SearchResultsWithTab.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			int progress = (int) Double.parseDouble(values[0]);
			pDialog.setProgress(progress);
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			// Creating JSON Parser instance
			// getting JSON string from URL
			GetMethodEx test = new GetMethodEx();
			try {

				JSONObject json = new JSONObject(test.getInternetData(url));

				JSONArray jsonArray1 = json.getJSONArray("albums");
				JSONArray jsonArray2 = json.getJSONArray("artists");
				JSONArray jsonArray3 = json.getJSONArray("tracks");

				int len = jsonArray1.length() + jsonArray2.length()
						+ jsonArray3.length();

				for (int i = 0; i < jsonArray1.length(); i++) {
					JSONObject c = jsonArray1.getJSONObject(i);
					String name = c.getString(TAG_NAME);
					arrayList1.add(name);
				}
				this.publishProgress((jsonArray1.length() / len) + "");

				for (int i = 0; i < jsonArray2.length(); i++) {
					JSONObject c = jsonArray2.getJSONObject(i);
					String name = c.getString(TAG_NAME);
					arrayList2.add(name);
				}

				this.publishProgress(((jsonArray1.length() + jsonArray2
						.length()) / len) + "");

				for (int i = 0; i < jsonArray3.length(); i++) {
					JSONObject c = jsonArray3.getJSONObject(i);
					String title = c.getString(TAG_TITLE);
					arrayList3.add(title);
				}
				// if php query doesn't give sorted results comment out
				// the
			} catch (Exception e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
			this.publishProgress(100 + "");

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			/**
			 * Updating parsed JSON data into ListView
			 * */
			if (arrayList1.size() == 0) {
				adapter1 = null;
			} else {
				Collections.sort(arrayList1);
				adapter1 = new ArrayAdapter<String>(SearchResultsWithTab.this,
						R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList1);

			}

			if (arrayList2.size() == 0) {
				adapter2 = null;
			} else {
				Collections.sort(arrayList2);
				adapter2 = new ArrayAdapter<String>(SearchResultsWithTab.this,
						R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList2);
			}

			if (arrayList3.size() == 0) {
				adapter3 = null;
			} else {

				// to filter the list to remove duplicate items
				// Set<String> hashsetList = new HashSet<String>(arrayList3);
				// arrayList3 = new ArrayList<String>(hashsetList);
				Collections.sort(arrayList3);
				adapter3 = new ArrayAdapter<String>(SearchResultsWithTab.this,
						R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList3);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a CustomListViewFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new CustomListViewFragment();
			Bundle args = new Bundle();
			args.putInt(CustomListViewFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int p) {
			if (p == 0)
				return "Albums";
			if (p == 1)
				return "Artists";
			if (p == 2)
				return "Songs";

			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class CustomListViewFragment extends android.support.v4.app.Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public CustomListViewFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			int tabPosition = getArguments().getInt(ARG_SECTION_NUMBER);
			Log.i("..", "..");
			Log.i("..", "..");
			Log.i("..", "..");
			Log.i("..", "..");
			// for (int i = 0; i < lv1.getAdapter().getCount(); i++)
			// Log.i("list = ", lv1.getAdapter().getItem(i).toString());

			ListView lv = new ListView(getActivity());
			lv.setCacheColorHint(Color.TRANSPARENT);
			lv.setFastScrollEnabled(true);

			if (tabPosition == 1) {
				lv.setAdapter(adapter1);
				return lv;
			} else if (tabPosition == 2) {

				lv.setAdapter(adapter2);
				return lv;
			} else if (tabPosition == 3) {
				lv.setAdapter(adapter3);
				return lv;
			}
			return null;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

		// String title = tab.getText().toString();
		Log.i("Tab", "clicked");
		tab.select();
		actionBar.setSelectedNavigationItem(tab.getPosition());
		actionBar.selectTab(tab);

		Fragment fragment = new CustomListViewFragment();
		Bundle args = new Bundle();
		args.putInt(CustomListViewFragment.ARG_SECTION_NUMBER,
				tab.getPosition() + 1);
		fragment.setArguments(args);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.vpSearchResults, fragment).commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
}
