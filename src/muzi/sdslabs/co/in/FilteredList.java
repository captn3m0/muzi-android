package muzi.sdslabs.co.in;

/*if writable cursor isn't available then pass this hashmap to database file & then parse
 * it or rather use its strings to put in array*/

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class FilteredList extends SherlockActivity implements
		OnItemClickListener {

	// url to make request
	// remember that its album & not albums
	private static String type;

	private ProgressDialog pDialog;
	boolean artist, album;

	// JSON keys
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_LANGUAGE = "language";

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<String> FilteredNamesList;

	ArrayList<HashMap<String, String>> FilteredArrayList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * if (!isNetworkAvailable()) { FilteredList.this.finish();
		 * Toast.makeText(FilteredList.this,
		 * "Please check your internet connection.", Toast.LENGTH_LONG) .show();
		 * }
		 */

		setContentView(R.layout.filtered_list_after_query);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		lv = (ListView) findViewById(R.id.lvFilteredList);
		lv.setFastScrollEnabled(true);
		FilteredNamesList = new ArrayList<String>();
		FilteredArrayList = new ArrayList<HashMap<String, String>>();
		String value1 = getIntent().getStringExtra("list_type");

		Log.i("value", value1);

		if (value1.equals("Albums")) {
			type = "album/";
			this.setTitle("Albums");
		} else if (value1.equals("Artists")) {
			type = "band/";
			this.setTitle("Artists");
		} else {
			FilteredList.this.finish();
			Toast.makeText(FilteredList.this,
					"Sorry, the request couldn't be executed",
					Toast.LENGTH_LONG).show();
		}

		new LoadAllProducts().execute();
	}

	/*--------------------To enable alphabetical scrollbar-----------------*/
	public void onClick(View v) {
		// Toast.makeText(FilteredJSONArrayAfterQuery.this, "Clicked",
		// Toast.LENGTH_SHORT).show();
		String firstLetter = (String) v.getTag();
		int index = 0;
		for (index = 0; index < FilteredNamesList.size(); index++) {
			char alpha = FilteredNamesList.get(index).charAt(0);
			if (Character.toLowerCase(alpha) == Character
					.toLowerCase(firstLetter.charAt(0))) {
				// index = stringlist.indexOf(alpha);
				break;
			}
		}

		lv.setSelectionFromTop(index, 0);
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FilteredList.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		void parseJsonAndReturnHashMap() {

			GetMethodEx test = new GetMethodEx();
			try {
				Log.i("url", GlobalVariables.api_root + type + "list.php");
				FilteredJSONArray = new JSONArray(
						test.getInternetData(GlobalVariables.api_root + type
								+ "list.php"));

				if (FilteredJSONArray != null) {
					// Getting Array of FilteredJSONArray
					// looping through All FilteredJSONArray
					// the code saves much more than required for now as
					// we may require it later
					for (int i = 0; i < FilteredJSONArray.length(); i++) {
						JSONObject c = FilteredJSONArray.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_ID);
						String name = c.getString(TAG_NAME);
						String language = c.getString(TAG_LANGUAGE);

						FilteredNamesList.add(name);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key =>
						// value
						map.put(TAG_ID, id);
						map.put(TAG_NAME, name);
						map.put(TAG_LANGUAGE, language);

						// adding HashList to ArrayList
						FilteredArrayList.add(map);

					}
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			// Creating JSON Parser instance
			// getting JSON string from URL

			parseJsonAndReturnHashMap();
			// if (type == "album/") {

			// // Use date here otherwise it'll update everytime
			// // but this is just temporary ;)
			// if (GlobalVariables.date_of_last_stored_album_db !=
			// Calendar.ZONE_OFFSET) {
			//
			// GlobalVariables.date_of_last_stored_album_db =
			// Calendar.ZONE_OFFSET;
			//
			// parseJsonAndReturnHashMap();
			// // call a method from database to add values to album db
			//
			// } else {
			// // return it from database
			//
			// }
			// } else {

			// // Use date here otherwise it'll update everytime
			// // but this is just temporary ;)
			// if (GlobalVariables.date_of_last_stored_artist_db !=
			// Calendar.ZONE_OFFSET) {
			//
			// GlobalVariables.date_of_last_stored_artist_db =
			// Calendar.ZONE_OFFSET;
			//
			// parseJsonAndReturnHashMap();
			// // call a method from Database to add values to artist db
			// } else {
			// // return it from database
			//
			// }
			// }
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
			if (FilteredArrayList.size() == 0) {
				lv.setAdapter(null);
			} else {
				ListAdapter adapter = new SimpleAdapter(FilteredList.this,
						FilteredArrayList, R.layout.list_item_with_one_tv,
						new String[] { TAG_NAME },
						new int[] { R.id.tv_in_list_item_with_one_tv });

				lv.setAdapter(adapter);
				lv.setOnItemClickListener(FilteredList.this);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		position -= 1;

		// For Albums
		if (type == "album/") {
			HashMap<String, String> map = new HashMap<String, String>();
			map = FilteredArrayList.get(position);
			Intent i = new Intent(FilteredList.this, SongsFromAlbums.class);

			i.putExtra("search_type1", type);
			i.putExtra("search_id1", map.get(TAG_ID));
			i.putExtra("search_title1", map.get(TAG_NAME));
			startActivity(i);

			// For Artists
		} else if (type == "band/") {
			HashMap<String, String> map = new HashMap<String, String>();
			map = FilteredArrayList.get(position);
			Intent i = new Intent(FilteredList.this, SongsFromArtists.class);

			i.putExtra("search_type2", type);
			i.putExtra("search_id2", map.get(TAG_ID));
			i.putExtra("search_title2", map.get(TAG_NAME));

			startActivity(i);
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