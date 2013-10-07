package muzi.sdslabs.co.in;

/*if writable cursor isn't available then pass this hashmap to database file & then parse
 * it or rather use its strings to put in array*/

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TopTracks extends MyActivity implements OnItemClickListener {

	private ProgressDialog pDialog;
	boolean artist, album;

	// JSON keys
	private static final String TAG_TRACKID = "trackid";
	private static final String TAG_TITLE = "title";

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<String> FilteredNamesList;

	ArrayList<HashMap<String, String>> FilteredArrayList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.simple_list_view_with_footer, TopTracks.this);
		super.onCreate(savedInstanceState);

		/*
		 * if (!isNetworkAvailable()) { TopTracks.this.finish();
		 * Toast.makeText(TopTracks.this,
		 * "Please check your Internet connection.", Toast.LENGTH_LONG) .show();
		 * }
		 */
		lv = (ListView) findViewById(R.id.lvSimple);
		lv.setFastScrollEnabled(true);
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.Black));
		lv.setCacheColorHint(Color.TRANSPARENT);
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
			pDialog = new ProgressDialog(TopTracks.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		void parseJsonAndReturnHashMap() {

			InternetData test = new InternetData();
			try {
				Log.i("url", GlobalVariables.api_root + "track/top.php");
				FilteredJSONArray = new JSONArray(
						test.getInternetData(GlobalVariables.api_root
								+ "track/top.php"));
				Log.i("returned", FilteredJSONArray.toString());

				if (FilteredJSONArray != null) {
					for (int i = 0; i < FilteredJSONArray.length(); i++) {
						JSONObject c = FilteredJSONArray.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_TRACKID);
						String title = c.getString(TAG_TITLE);

						FilteredNamesList.add(title);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key =>
						// value
						map.put(TAG_TRACKID, id);
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
						TopTracks.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, FilteredNamesList);

				lv.setAdapter(adapter);
				lv.setOnItemClickListener(TopTracks.this);
			}
		}
	}

	String requestedTrackURL, songName, songPath;

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub

		requestedTrackURL = GlobalVariables.api_root + "/track/?id="
				+ FilteredArrayList.get(position).get(TAG_TRACKID);
		Log.i("Requested track id", requestedTrackURL);
		songName = FilteredArrayList.get(position).get(TAG_TITLE);
		new PlaySong().execute();

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
			TopTracks.this.finish();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			InternetData test = new InternetData();

			try {
				JSONObject jsonObject = new JSONObject(
						test.getInternetData(requestedTrackURL));
				songPath = jsonObject.getString("file");

				Log.i("Song Path", songPath);
				playSong(songName, songPath, TopTracks.this);

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
			Toast.makeText(TopTracks.this, songName, Toast.LENGTH_SHORT).show();
		}
	}
}