package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SearchResults extends SherlockActivity {

	// url to make request
	// remember that its album & not albums
	private static String url;
	TabHost th;

	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_NAME = "name";
	private static final String TAG_TITLE = "title";

	JSONArray FilteredJSONArray = null;
	ArrayList<String> arrayList1, arrayList2, arrayList3;
	ListView lv1, lv2, lv3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String text = getIntent().getStringExtra("search_query");
		url = GlobalVariables.api_root + "search/?search=" + text;
		this.setTitle("Search Results for " + text);
		Log.i("url", url);

		th = (TabHost) findViewById(R.id.tabhost);
		arrayList1 = new ArrayList<String>();
		arrayList2 = new ArrayList<String>();
		arrayList3 = new ArrayList<String>();
		lv1 = (ListView) findViewById(R.id.lvTab1);
		lv2 = (ListView) findViewById(R.id.lvTab2);
		lv3 = (ListView) findViewById(R.id.lvTab3);

		lv1.setFastScrollEnabled(true);
		lv2.setFastScrollEnabled(true);
		lv3.setFastScrollEnabled(true);

		th.setup();
		TabSpec specs = th.newTabSpec("tag1");
		specs.setContent(R.id.tab1);
		specs.setIndicator("Albums");
		th.addTab(specs);
		specs = th.newTabSpec("tag2");
		specs.setContent(R.id.tab2);
		specs.setIndicator("Artists");
		th.addTab(specs);
		specs = th.newTabSpec("tag3");
		specs.setContent(R.id.tab3);
		specs.setIndicator("Songs");
		th.addTab(specs);

		new LoadAllProducts().execute();
	}

	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SearchResults.this);
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
				Log.i("Trying", "done");

				JSONObject json = new JSONObject(test.getInternetData(url));

				Log.i("json", json.toString());

				JSONArray jsonArray1 = json.getJSONArray("albums");
				JSONArray jsonArray2 = json.getJSONArray("artists");

				Log.i("artist list", jsonArray2.toString());
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
				lv1.setAdapter(null);
				th.setCurrentTab(1);
			} else {
				Collections.sort(arrayList1);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SearchResults.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList1);

				lv1.setAdapter(adapter);
			}

			if (arrayList2.size() == 0) {
				lv2.setAdapter(null);
				if (th.getCurrentTab() == 1)
					th.setCurrentTab(2);
			} else {
				Collections.sort(arrayList2);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SearchResults.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList2);
				lv2.setAdapter(adapter);
			}

			if (arrayList3.size() == 0) {
				lv3.setAdapter(null);
				if (th.getCurrentTab() == 2) {
					// SearchResults.this.finish();
					Toast.makeText(SearchResults.this,
							"Your search query doesn't match any result",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Collections.sort(arrayList3);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SearchResults.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList3);
				lv3.setAdapter(adapter);
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("Nothing");
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent mainIntent = new Intent(getApplicationContext(),
					HomeScreen.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainIntent);
		}
		return true;
	}
}