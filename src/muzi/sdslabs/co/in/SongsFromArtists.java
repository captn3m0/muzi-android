package muzi.sdslabs.co.in;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SongsFromArtists extends Activity implements OnItemClickListener {

	// url to make request
	// remember that its album & not albums
	private static String root;

	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_TITLE = "title";

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<String> SongsList;
	JSONObject jsonObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filtered_list_after_query);
		lv = (ListView) findViewById(R.id.lvFilteredList);
		lv.setFastScrollEnabled(true);
		SongsList = new ArrayList<String>();
		String value = getIntent().getStringExtra("search_id");

		if (value != null) {
			root = GlobalVariables.api_root + value;
			Log.i("request url", root);
		} else {
			finish();
			Toast.makeText(SongsFromArtists.this,
					"Sorry, the request couldn't be executed",
					Toast.LENGTH_LONG).show();
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
			pDialog = new ProgressDialog(SongsFromArtists.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
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

				FilteredJSONArray = new JSONArray(test.getInternetData(root));
				Log.i("Array", FilteredJSONArray.toString());
				if (FilteredJSONArray != null) {
					// Getting Array of FilteredJSONArray
					// looping through All FilteredJSONArray
					Log.i("Working", "good");
					// require it later
					for (int i = 0; i < FilteredJSONArray.length(); i++) {
						jsonObject = FilteredJSONArray.getJSONObject(i);

						SongsList.add((i + 1) + ". "
								+ jsonObject.getString(TAG_TITLE));
						// creating new HashMap

						Log.i((i + 1) + "", jsonObject.getString(TAG_TITLE));
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
			if (SongsList.size() == 0) {
				lv.setAdapter(null);
			} else {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SongsFromArtists.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, SongsList);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(SongsFromArtists.this);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub

	}
}