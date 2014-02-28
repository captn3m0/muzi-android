package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class SearchResults extends MyActivity implements OnItemClickListener {

	// url to make request
	// remember that its album & not albums
	private static String url;
	TabHost th;

	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_NAME = "name";
	private static final String TAG_TITLE = "title";
	private static final String TAG_ID = "id";

	JSONArray FilteredJSONArray = null;
	HashMap<String, String> hashMap1, hashMap2, hashMap3;
	ArrayList<String> arrayList1, arrayList2, arrayList3;

	ListView lv1, lv2, lv3;

	String requestedTrackURL, songName;
	String query;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.search_results, SearchResults.this);
		super.onCreate(savedInstanceState);

		try {
			query = getIntent().getStringExtra("search_query");
		} catch (Exception e) {
			e.printStackTrace();
		}

		handleIntent(getIntent());

		url = GlobalVariables.api_root + "search/?search=" + query;
		this.setTitle("Search Results for " + query);
		Log.i("url", url);

		th = (TabHost) findViewById(R.id.tabhost);
		hashMap1 = new HashMap<String, String>();
		hashMap2 = new HashMap<String, String>();
		hashMap3 = new HashMap<String, String>();

		lv1 = (ListView) findViewById(R.id.lvTab1);
		lv2 = (ListView) findViewById(R.id.lvTab2);
		lv3 = (ListView) findViewById(R.id.lvTab3);

		lv1.setFastScrollEnabled(true);
		lv2.setFastScrollEnabled(true);
		lv3.setFastScrollEnabled(true);

		lv1.setOnItemClickListener(this);
		lv2.setOnItemClickListener(this);
		lv3.setOnItemClickListener(this);

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

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

			query = intent.getStringExtra(SearchManager.QUERY);

			Log.i("SerachResults: handleIntent()", query + "yes! it works");
			// use the query to search your data somehow
		}
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
			InternetData test = new InternetData();
			try {
				Log.i("Trying", "done");

				JSONObject json = new JSONObject(test.getInternetData(url));

				Log.i("json", json.toString());

				JSONArray jsonArray1 = json.getJSONArray("albums");
				JSONArray jsonArray2 = json.getJSONArray("artists");

				Log.i("Search Results", "Artists: " + jsonArray2.toString());
				JSONArray jsonArray3 = json.getJSONArray("tracks");
				Log.i("Search Results", "Tracks: " + jsonArray3.toString());

				int len = jsonArray1.length() + jsonArray2.length()
						+ jsonArray3.length();

				for (int i = 0; i < jsonArray1.length(); i++) {
					JSONObject c = jsonArray1.getJSONObject(i);
					String name = c.getString(TAG_NAME);
					hashMap1.put(name, c.getString(TAG_ID));
				}
				this.publishProgress((jsonArray1.length() / len) + "");

				for (int i = 0; i < jsonArray2.length(); i++) {
					JSONObject c = jsonArray2.getJSONObject(i);
					String name = c.getString(TAG_NAME);
					hashMap2.put(name, c.getString(TAG_ID));
				}

				this.publishProgress(((jsonArray1.length() + jsonArray2
						.length()) / len) + "");

				for (int i = 0; i < jsonArray3.length(); i++) {
					JSONObject c = jsonArray3.getJSONObject(i);
					String title = c.getString(TAG_TITLE);
					hashMap3.put(title, c.getString(TAG_ID));
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
			if (hashMap1.size() == 0) {
				lv1.setAdapter(null);
				th.setCurrentTab(1);
			} else {

				arrayList1 = new ArrayList<String>(new TreeSet<String>(
						hashMap1.keySet()));
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SearchResults.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList1);
				lv1.setAdapter(adapter);
			}

			if (hashMap2.size() == 0) {
				lv2.setAdapter(null);
				if (th.getCurrentTab() == 1)
					th.setCurrentTab(2);
			} else {
				arrayList2 = new ArrayList<String>(new TreeSet<String>(
						hashMap2.keySet()));
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SearchResults.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList2);
				lv2.setAdapter(adapter);
			}

			if (hashMap3.size() == 0) {
				lv3.setAdapter(null);
				if (th.getCurrentTab() == 2) {
					// SearchResults.this.finish();
					Toast.makeText(SearchResults.this,
							"Your search query doesn't match any result",
							Toast.LENGTH_LONG).show();
				}
			} else {
				arrayList3 = new ArrayList<String>(new TreeSet<String>(
						hashMap3.keySet()));
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SearchResults.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, arrayList3);
				lv3.setAdapter(adapter);
			}
		}
	}

	public void stopPlayer(View v) {
		stopService(new Intent(this, MusicService.class));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Log.i("Search Results", "list item position clicked" + position);

		if (arg0.getId() == R.id.lvTab1) {
			Intent i = new Intent(SearchResults.this, SongsFromAlbums.class);
			i.putExtra("search_type1", "album");
			i.putExtra("search_id1", hashMap1.get(arrayList1.get(position)));
			i.putExtra("search_title1", arrayList1.get(position));
			startActivity(i);

		} else if (arg0.getId() == R.id.lvTab2) {

			Intent i = new Intent(SearchResults.this, AlbumsFromArtists.class);
			i.putExtra("search_type2", "band");
			i.putExtra("search_id2", hashMap2.get(arrayList2.get(position)));
			i.putExtra("search_title2", arrayList2.get(position));
			startActivity(i);

		} else if (arg0.getId() == R.id.lvTab3) {
			Log.i("Search Results: onItemClick()", "Tab 3 clicked");
			requestedTrackURL = GlobalVariables.api_root + "track/?id="
					+ hashMap3.get(arrayList3.get(position));
			songName = arrayList3.get(position);

			new PlaySong().execute();

		}
	}

	class PlaySong extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			SearchResults.this.finish();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			InternetData test = new InternetData();

			try {
				JSONObject jsonObject = new JSONObject(
						test.getInternetData(requestedTrackURL));
				String songPath = jsonObject.getString("file");

				Log.i("Song Path", songPath);
				playSong(songName, songPath, SearchResults.this);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Toast.makeText(SearchResults.this, songName, Toast.LENGTH_SHORT)
					.show();
		}
	}
}