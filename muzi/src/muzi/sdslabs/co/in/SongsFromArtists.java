package muzi.sdslabs.co.in;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.Toast;

public class SongsFromArtists extends MyActivity implements OnItemClickListener {

	// url to make request
	// remember that its album & not albums
	private static String root;

	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_TITLE = "title";
	private static final String TAG_FILE = "file";

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<String> songsNameList, songsPathList;
	JSONObject jsonObject;
	LoadSongs task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.songs_from_artists, SongsFromArtists.this);
		super.onCreate(savedInstanceState);
		// if (!isNetworkAvailable()) {
		// finish();
		// Toast.makeText(SongsFromArtists.this,
		// "Please check your internet connection.", Toast.LENGTH_LONG)
		// .show();
		// }

		lv = (ListView) findViewById(R.id.lvSongsFromArtists);
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.Black));
		lv.setCacheColorHint(Color.TRANSPARENT);
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(SongsFromArtists.this);

		songsNameList = new ArrayList<String>();
		songsPathList = new ArrayList<String>();

		String type = getIntent().getStringExtra("search_type2");
		String album_id = getIntent().getStringExtra("search_id2");

		if (type != null) {
			root = GlobalVariables.api_root + type + "?id=" + album_id;
			Log.i("request url", root);
			this.setTitle(getIntent().getStringExtra("search_title2"));
		} else {
			SongsFromArtists.this.finish();
			Toast.makeText(SongsFromArtists.this,
					"Sorry, the request couldn't be executed",
					Toast.LENGTH_LONG).show();
		}

		task = new LoadSongs();
		task.execute();
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

	class LoadSongs extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SongsFromArtists.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					task.cancel(true);
				}
			});
			pDialog.show();
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			SongsFromArtists.this.finish();
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

				FilteredJSONArray = new JSONArray(test.getInternetData(root));
				Log.i("Array", FilteredJSONArray.toString());
				if (FilteredJSONArray != null) {
					// Getting Array of FilteredJSONArray
					// looping through All FilteredJSONArray
					Log.i("Working", "good");
					// require it later
					for (int i = 0; i < FilteredJSONArray.length(); i++) {

						if (isCancelled()) {
							return null;
						}

						jsonObject = FilteredJSONArray.getJSONObject(i);

						songsNameList.add((i + 1) + ". "
								+ jsonObject.getString(TAG_TITLE));
						songsPathList.add(jsonObject.getString(TAG_FILE));
						// creating new HashMap

						Log.i((i + 1) + "", jsonObject.getString(TAG_FILE));
					}
					// if php query doesn't give sorted results comment out
					// the
				}

			} catch (Exception e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
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
			if (songsNameList.size() == 0) {
				lv.setAdapter(null);
			} else {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SongsFromArtists.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, songsNameList);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(SongsFromArtists.this);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position, long id) {
		// TODO Auto-generated method stub
		playSong(songsNameList.get(position), songsPathList.get(position),
				SongsFromArtists.this);
	}
}