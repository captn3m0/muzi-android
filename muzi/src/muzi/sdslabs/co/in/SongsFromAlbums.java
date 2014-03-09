package muzi.sdslabs.co.in;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SongsFromAlbums extends Activity implements OnItemClickListener {

	// url to make request
	// remember that its album & not albums
	private static String root;
	JSONObject jsonObject;

	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_TITLE = "title";
	private static final String TAG_ID = "id";

	private static int year;
	private static int firstId;

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<String> songsNameList;
	ArrayList<Integer> songsIdList;
	ImageView ivAlbumCover;
	TextView tvAlbumName, tvAlbumArtist, tvAlbumYear;
	Drawable image;
	Boolean image_avail;
	String album_id;
	Bitmap bitmap;
	String album_artist;
	LoadAlbumCover task2;
	LoadSongs task1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.songs_from_albums);
		/*------------------------style list view-----------------*/
		{
			lv = (ListView) findViewById(R.id.lvSongsFromAlbums);
			lv.setFastScrollEnabled(true);
			ivAlbumCover = (ImageView) findViewById(R.id.ivAlbumCover);
			tvAlbumName = (TextView) findViewById(R.id.tvAlbumName);
			tvAlbumArtist = (TextView) findViewById(R.id.tvAlbumArtist);
			tvAlbumYear = (TextView) findViewById(R.id.tvAlbumYear);

			image_avail = false;
			songsNameList = new ArrayList<String>();
			songsIdList = new ArrayList<Integer>();
			album_artist = null;
		}

		/*------------------get query value to search for-----------------*/
		{
			String type = getIntent().getStringExtra("search_type1");
			album_id = getIntent().getStringExtra("search_id1");

			if (type != null) {
				root = GlobalVariables.api_root + type + "?id=" + album_id;
				Log.i("request url", root);
				this.setTitle(getIntent().getStringExtra("search_title1"));
				tvAlbumName
						.setText(getIntent().getStringExtra("search_title1"));
			}
		}

		/*----------------------call async method---------------------*/
		// want to execute these tasks in parallel in different threads
		// but it may give errors if header content is loaded after the list
		// items
		task1 = new LoadSongs();
		task2 = new LoadAlbumCover();
		task1.execute();
		task2.execute();
	}

	class LoadSongs extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SongsFromAlbums.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					task1.cancel(true);
					task2.cancel(true);
				}
			});
			pDialog.show();
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			SongsFromAlbums.this.finish();
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
				Log.i("Root", root);
				jsonObject = new JSONObject(test.getInternetData(root));
				Log.i("Album URL", jsonObject.toString());

				FilteredJSONArray = jsonObject.getJSONArray("tracks");
				Log.i("JSON Array", FilteredJSONArray.toString());

				if (FilteredJSONArray != null) {
					firstId = FilteredJSONArray.getJSONObject(0).getInt("id");

					// Getting Array of FilteredJSONArray
					// looping through All FilteredJSONArray
					// require it later
					for (int i = 0; i < FilteredJSONArray.length(); i++) {
						if (isCancelled()) {
							return null;
						}
						jsonObject = FilteredJSONArray.getJSONObject(i);

						if (!jsonObject.getString(TAG_TITLE).trim().equals("")) {
							songsNameList.add(jsonObject.getString(TAG_TITLE));
							songsIdList.add(jsonObject.getInt(TAG_ID));
						}
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
			// dismiss the dialog because apparently these two async tasks are
			// going parallely
			// & obviously songs load later
			pDialog.dismiss();

			// updating UI from Background Thread
			/**
			 * Updating parsed JSON data into ListView
			 * */

			if (songsNameList.size() == 0) {
				lv.setAdapter(null);
				Toast.makeText(SongsFromAlbums.this, "Album is empty.",
						Toast.LENGTH_SHORT).show();
				SongsFromAlbums.this.finish();
			} else {
				Log.i("Songs list size", songsNameList.size() + "");
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SongsFromAlbums.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, songsNameList);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(SongsFromAlbums.this);
			}
		}
	}

	class LoadAlbumCover extends AsyncTask<String, String, String> {

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
			SongsFromAlbums.this.finish();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			try {
				// Log.i("Image url", "http://localhost/muzi/images/pics/"
				// + album_id + ".jpg");
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						GlobalVariables.pic_root + album_id + ".jpg")
						.getContent());

				image_avail = true;
			} catch (Exception e) {
				e.printStackTrace();
				image_avail = false;
				Log.i("Image", "false");
			}

			InternetData test = new InternetData();

			if (songsNameList.size() > 0) {
				try {
					jsonObject = new JSONObject(
							test.getInternetData(GlobalVariables.api_root
									+ "/track/?id=" + firstId));
					year = jsonObject.getInt("year");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					jsonObject = new JSONObject(test.getInternetData(root));
					album_artist = jsonObject.getString("band");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// has to be before setting up the adapter
			if (image_avail == true) {
				ivAlbumCover.setImageBitmap(bitmap);
				Log.i("Image available", "true");
			}

			if (year != 0) {
				tvAlbumYear.setText("RELEASED IN " + year);
			} else {
				tvAlbumYear.setText("");
			}

			if (album_artist != null) {
				tvAlbumArtist.setText("BY " + album_artist);
			} else {
				tvAlbumArtist.setText("");
			}
		}
	}

	String requestedTrackURL, songPath, songName;

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method

		final int pos = position;
		// // Let's see which one to implement setData or setExtra
		// // Get song path by requesting the url below
		requestedTrackURL = GlobalVariables.api_root + "track/?id="
				+ songsIdList.get(pos);

		Log.i("Requested track url", requestedTrackURL);

		/** Parse the string to remove the number before it **/
		songName = songsNameList.get(pos);
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
			SongsFromAlbums.this.finish();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			InternetData test = new InternetData();

			try {
				jsonObject = new JSONObject(
						test.getInternetData(requestedTrackURL));
				songPath = jsonObject.getString("file");

				Log.i("Song Path", songPath);
				// playSong(songName, songPath, jsonObject.getString("albumId"),
				// SongsFromAlbums.this);

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
			Toast.makeText(SongsFromAlbums.this, songName, Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void stopPlayer(View v) {
		stopService(new Intent(this, MusicService.class));
	}
}