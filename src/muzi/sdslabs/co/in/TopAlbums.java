package muzi.sdslabs.co.in;



/*if writable cursor isn't available then pass this hashmap to database file & then parse
 * it or rather use its strings to put in array*/

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TopAlbums extends SherlockListActivity implements OnItemClickListener {

	private ProgressDialog pDialog;
	boolean artist, album;

	// JSON keys
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "name";

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<String> FilteredNamesList;

	ArrayList<HashMap<String, String>> FilteredArrayList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * if (!isNetworkAvailable()) { TopTracks.this.finish();
		 * Toast.makeText(TopTracks.this,
		 * "Please check your internet connection.", Toast.LENGTH_LONG) .show();
		 * }
		 */
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.Black));
		getListView().setCacheColorHint(Color.TRANSPARENT);
		lv.setFastScrollEnabled(true);
		
		FilteredNamesList = new ArrayList<String>();
		FilteredArrayList = new ArrayList<HashMap<String, String>>();
		new LoadAllProducts().execute();
	}

	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(TopAlbums.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		void parseJsonAndReturnHashMap() {

			GetMethodEx test = new GetMethodEx();
			try {
				Log.i("url", GlobalVariables.api_root + "album/top.php");
				FilteredJSONArray = new JSONArray(
						test.getInternetData(GlobalVariables.api_root
								+ "album/top.php"));
				Log.i("returned", FilteredJSONArray.toString());

				if (FilteredJSONArray != null) {
					for (int i = 0; i < FilteredJSONArray.length(); i++) {
						JSONObject c = FilteredJSONArray.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_ID);
						String title = c.getString(TAG_TITLE);

						FilteredNamesList.add(title);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key =>
						// value
						map.put(TAG_ID, id);
						map.put(TAG_TITLE, title);

						// adding HashList to ArrayList
						FilteredArrayList.add(map);

					}
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		protected String doInBackground(String... args) {
			parseJsonAndReturnHashMap();
			return null;
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			if (FilteredArrayList.size() == 0) {
				lv.setAdapter(null);
			} else {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						TopAlbums.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, FilteredNamesList);

				lv.setAdapter(adapter);
				lv.setOnItemClickListener(TopAlbums.this);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		
		    for (int i = 0; i < FilteredJSONArray.length(); i++)
			{
		    	String albumName = FilteredNamesList.get(position).toString();
		    	if(FilteredArrayList.contains(albumName))
		    	{
		    		String id=FilteredArrayList.get(i).get(TAG_ID);
		    		Intent intent = new Intent(TopAlbums.this, SongsFromAlbums.class);
		    		intent.putExtra("search_type1", "album");
		    		intent.putExtra("search_id1", id);
		    		intent.putExtra("search_title1", FilteredNamesList.get(position));
		    		startActivity(intent);
		    	}
			}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
