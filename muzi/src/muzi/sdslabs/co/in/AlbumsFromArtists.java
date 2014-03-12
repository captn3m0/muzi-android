package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class AlbumsFromArtists extends MyActivity implements
		OnItemClickListener {

	private static String root;
	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_NAME = "name";
	private static final String TAG_ID = "id";

	JSONArray FilteredJSONArray = null;
	GridView gv;
	ArrayList<HashMap<String, String>> albumList;// , albumIdList;
	JSONObject jsonObject;
	LoadSongs task;

	String[] from = { TAG_ID, TAG_NAME };
	int[] to = { R.id.iv_in_li, R.id.tv_in_li };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.albums_from_artists, AlbumsFromArtists.this);
		super.onCreate(savedInstanceState);

		gv = (GridView) findViewById(R.id.gv);
		gv.setOnItemClickListener(AlbumsFromArtists.this);

		albumList = new ArrayList<HashMap<String, String>>();

		String type = getIntent().getStringExtra("search_type2");
		String album_id = getIntent().getStringExtra("search_id2");

		if (type != null) {
			
			// get album_id
			root = GlobalVariables.api_root + type + "/albums.php?id="
					+ album_id;
			Log.i("request url", root);
			AlbumsFromArtists.this.setTitle(AlbumsFromArtists.this.getIntent()
					.getStringExtra("search_title2"));
		} else {
			
			// if no album id is received then quit the activity
			AlbumsFromArtists.this.finish();
			Toast.makeText(AlbumsFromArtists.this,
					"Sorry, the request couldn't be executed",
					Toast.LENGTH_LONG).show();
		}

		task = new LoadSongs();
		task.execute();
	}

	class LoadSongs extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AlbumsFromArtists.this);
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
			AlbumsFromArtists.this.finish();
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

				jsonObject = new JSONObject(test.getInternetData(root));
				FilteredJSONArray = new JSONArray(
						jsonObject.getString("albums"));

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

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_ID, jsonObject.getString(TAG_ID));
						map.put(TAG_NAME, jsonObject.getString(TAG_NAME));

						albumList.add(map);
					}
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
			if (albumList.size() == 0) {
				gv.setAdapter(null);
			} else {

				Log.i("Size of list", albumList.size() + "");
				GridAdapter adapter = new GridAdapter(AlbumsFromArtists.this,
						albumList, R.layout.grid_cell, from, to);
				gv.setAdapter(adapter);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position, long id) {
		// TODO Auto-generated method stub

		Intent i = new Intent(AlbumsFromArtists.this, SongsFromAlbums.class);
		i.putExtra("search_type1", "album");
		i.putExtra("search_id1", albumList.get(position).get(TAG_ID));
		i.putExtra("search_title1", albumList.get(position).get(TAG_NAME));
		startActivity(i);

	}
}