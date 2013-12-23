package muzi.sdslabs.co.in;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;

/*if writable cursor isn't available then pass this hashmap to database file & then parse
 * it or rather use its strings to put in array*/

public class FilteredList extends MyActivity implements OnItemClickListener {

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
	ArrayList<String> FilteredNamesList, FilteredIdList, langList;
	SharedPreferences pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.filtered_list, this);
		super.onCreate(savedInstanceState);

		/*
		 * if (!isNetworkAvailable()) { FilteredList.this.finish();
		 * Toast.makeText(FilteredList.this,
		 * "Please check your internet connection.", Toast.LENGTH_LONG) .show();
		 * }
		 */

		lv = (ListView) findViewById(R.id.lvFilteredList);
		lv.setFastScrollEnabled(true);
		FilteredNamesList = new ArrayList<String>();
		FilteredIdList = new ArrayList<String>();
		String value1 = getIntent().getStringExtra("list_type");

		Log.i("value", value1);

		if (value1.equals("Albums")) {
			type = "album";
			this.setTitle("Albums");
		} else if (value1.equals("Artists")) {
			type = "band";
			this.setTitle("Artists");
		} else {
			FilteredList.this.finish();
			Toast.makeText(FilteredList.this,
					"Sorry, the request couldn't be executed",
					Toast.LENGTH_LONG).show();
		}

		langList = new ArrayList<String>();
		pref = getApplicationContext().getSharedPreferences("Lang Pref", 0);

		if (pref.getBoolean("English", true))
			langList.add("English");
		if (pref.getBoolean("Hindi", true))
			langList.add("Hindi");
		if (pref.getBoolean("Tamil", true))
			langList.add("Tamil");

		for (int i = 0; i < langList.size(); i++) {
			Log.i("pref list", langList.get(i));
		}

		new LoadAllProducts().execute();
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
			pDialog.setCancelable(true);
			pDialog.show();
		}

		void parseJson(String string) {

			Log.i("JSON data", string);

			try {
				FilteredJSONArray = new JSONArray(string);

				if (FilteredJSONArray != null) {
					// Getting Array of FilteredJSONArray
					// looping through All FilteredJSONArray
					// the code saves much more than required for now as
					// we may require it later
					for (int i = 0; i < FilteredJSONArray.length(); i++) {
						JSONObject c = FilteredJSONArray.getJSONObject(i);

						// Storing each json item in variable
						if (langList.contains(c.getString(TAG_LANGUAGE))) {
							// Storing each json item in variable
							FilteredIdList.add(c.getString(TAG_ID));
							FilteredNamesList.add(c.getString(TAG_NAME));
						}
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
			Log.i("background processing", " ");
			listFileNames();

			String string = "";
			if (fileList() == null
					|| !Arrays.asList(fileList()).contains(type + getDate())) {
				Log.i("", "deleted");
				deleteFileList();

				InternetData test = new InternetData();
				try {
					Log.i("url", GlobalVariables.api_root + type + "/list.php");

					string = test.getInternetData(GlobalVariables.api_root
							+ type + "/list.php");

					Log.i("Filtered List", string);

					if (string != null && !string.equals("")) {

						Log.i("Filtered List", " is writing in cache.");
						FileOutputStream fos = openFileOutput(type + getDate(),
								Context.MODE_PRIVATE);
						fos.write(string.getBytes());
						fos.close();
					} else {
						FilteredList.this.finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				// return it from database

				Log.i("Coming form local storage", "true");
				try {
					FileInputStream fis = openFileInput(type + getDate());
					StringBuffer fileContent = new StringBuffer("");
					byte[] buffer = new byte[1024];

					while (fis.read(buffer) != -1) {
						fileContent.append(new String(buffer));
					}

					fis.close();
					string = fileContent.toString();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			parseJson(string);
			return null;
		}

		private void deleteFileList() {
			// TODO Auto-generated method stub

			int i = 0;

			if (fileList().length > 0
					&& (fileList()[0].equals("album" + getDate()) || fileList()[0]
							.equals("artist" + getDate()))) {
				i = 1;
			}
			while (fileList().length > i) {
				Log.i("deleted", fileList()[i]);
				getApplicationContext().deleteFile(fileList()[i]);
			}
		}

		private void listFileNames() {
			// TODO Auto-generated method stub
			int i = 0;
			while (i < fileList().length) {
				Log.i("listing", fileList()[i]);
				i++;
			}
		}

		private String getDate() {
			Calendar c = Calendar.getInstance();
			return " " + c.get(Calendar.DAY_OF_MONTH) + "_"
					+ c.get(Calendar.MONTH) + "_" + c.get(Calendar.YEAR);
			// return null;
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
			if (FilteredNamesList.size() == 0) {
				lv.setAdapter(null);
			} else {

				lv.setAdapter(new MyIndexerAdapter<String>(FilteredList.this,
						android.R.layout.simple_list_item_1, FilteredNamesList));
				lv.setOnItemClickListener(FilteredList.this);
			}
		}

	}

	class MyIndexerAdapter<T> extends ArrayAdapter<T> implements SectionIndexer {

		ArrayList<String> myElements;
		HashMap<String, Integer> alphaIndexer;

		String[] sections;

		public MyIndexerAdapter(Context context, int textViewResourceId,
				List<T> objects) {
			super(context, textViewResourceId, objects);
			myElements = (ArrayList<String>) objects;
			// here is the tricky stuff
			alphaIndexer = new HashMap<String, Integer>();
			// in this hashmap we will store here the positions for
			// the sections

			int size = FilteredNamesList.size();
			for (int i = size - 1; i >= 0; i--) {
				String element = FilteredNamesList.get(i);
				alphaIndexer.put(element.substring(0, 1), i);
				// We store the first letter of the word, and its index.
				// The Hashmap will replace the value for identical keys are
				// putted in
			}

			// now we have an hashmap containing for each first-letter
			// sections(key), the index(value) in where this sections begins

			// We've now to build the sections (letters to be displayed)
			// array, it must contain the keys, and must be
			// ordered alphabetically

			Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
			// cannot be sorted...

			Iterator<String> it = keys.iterator();
			ArrayList<String> keyList = new ArrayList<String>(); // list can be
			// sorted

			while (it.hasNext()) {
				String key = it.next();
				keyList.add(key);
			}

			Collections.sort(keyList);

			sections = new String[keyList.size()]; // simple conversion to an
			// array of object
			keyList.toArray(sections);
		}

		@Override
		public int getPositionForSection(int section) {
			// Log.v("getPositionForSection", ""+section);
			String letter = sections[section];
			return alphaIndexer.get(letter);
		}

		@Override
		public int getSectionForPosition(int position) {
			// It's never called (right?)
			Log.i("getSectionForPosition", "called");
			return 0;
		}

		@Override
		public Object[] getSections() {
			// to string will be called each object, to display the letter
			return sections;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Log.i("position", "" + position);

		// For Albums
		if (type == "album") {
			Intent i = new Intent(FilteredList.this, SongsFromAlbums.class);

			i.putExtra("search_type1", type);
			i.putExtra("search_id1", FilteredIdList.get(position));
			i.putExtra("search_title1", FilteredNamesList.get(position));
			startActivity(i);

			// For Artists
		} else if (type == "band") {
			Intent i = new Intent(FilteredList.this, AlbumsFromArtists.class);

			i.putExtra("search_type2", type);
			i.putExtra("search_id2", FilteredIdList.get(position));
			i.putExtra("search_title2", FilteredNamesList.get(position));

			startActivity(i);
		}
	}
}