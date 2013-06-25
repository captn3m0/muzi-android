package muzi.sdslabs.co.in;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SongsFromAlbums extends SherlockListActivity implements
		OnItemClickListener {

	// url to make request
	// remember that its album & not albums
	private static String root;
	JSONObject jsonObject;

	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_TITLE = "title";
	private static int year;
	private static int firstId;

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<String> SongsList;
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
		//
		// if (!isNetworkAvailable()) {
		// finish();
		// Toast.makeText(SongsFromAlbums.this,
		// "Please check your internet connection.", Toast.LENGTH_LONG)
		// .show();
		// }

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		/*------------------------style list view-----------------*/
		{
			lv = getListView();
			lv.setFastScrollEnabled(true);
			lv.getRootView().setBackgroundColor(
					getResources().getColor(R.color.Black));
			getListView().setCacheColorHint(Color.TRANSPARENT);
			lv.setFastScrollEnabled(true);

			/*-------add header to list view-------*/
			View header = getLayoutInflater().inflate(
					R.layout.header_for_songs_from_album, null);
			lv.addHeaderView(header);
			ivAlbumCover = (ImageView) findViewById(R.id.ivAlbumCover);
			tvAlbumName = (TextView) findViewById(R.id.tvAlbumName);
			tvAlbumArtist = (TextView) findViewById(R.id.tvAlbumArtist);
			tvAlbumYear = (TextView) findViewById(R.id.tvAlbumYear);

			image_avail = false;
			SongsList = new ArrayList<String>();
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
			} else {
				SongsFromAlbums.this.finish();
				Toast.makeText(SongsFromAlbums.this,
						"Sorry, the request couldn't be executed",
						Toast.LENGTH_LONG).show();
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
			pDialog = new ProgressDialog(SongsFromAlbums.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
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
			GetMethodEx test = new GetMethodEx();
			try {
				jsonObject = new JSONObject(test.getInternetData(root));
				Log.i("Track url", jsonObject.toString());

				FilteredJSONArray = jsonObject.getJSONArray("tracks");
				Log.i("Array", FilteredJSONArray.toString());

				if (FilteredJSONArray != null) {
					firstId = FilteredJSONArray.getJSONObject(0).getInt("id");

					// Getting Array of FilteredJSONArray
					// looping through All FilteredJSONArray
					Log.i("Working", "good");
					// require it later
					for (int i = 0; i < FilteredJSONArray.length(); i++) {
						if (isCancelled()) {
							return null;
						}
						jsonObject = FilteredJSONArray.getJSONObject(i);

						SongsList.add((i + 1) + ". "
								+ jsonObject.getString(TAG_TITLE));
						// creating new HashMap
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
			// updating UI from Background Thread
			/**
			 * Updating parsed JSON data into ListView
			 * */

			if (SongsList.size() == 0) {
				lv.setAdapter(null);
			} else {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SongsFromAlbums.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, SongsList);
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
						GlobalVariables.album_pic_root + album_id + ".jpg")
						.getContent());

				image_avail = true;
			} catch (Exception e) {
				e.printStackTrace();
				image_avail = false;
				Log.i("Image Not returned", "");
			}

			GetMethodEx test = new GetMethodEx();

			if (SongsList.size() > 0) {
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
			// dismissing the dialog
			pDialog.dismiss();
			// has to be before setting up the adapter
			if (image_avail == true) {
				ivAlbumCover.setImageBitmap(bitmap);
				Log.i("Image available", "true");
			}

			if (year != 0) {

				tvAlbumYear.setText("RELEASED IN " + year);
				Toast.makeText(SongsFromAlbums.this,
						"Year of release: " + year, Toast.LENGTH_SHORT).show();
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

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stubString url = "http://........"; //
		// your URL here

		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		try {
			mediaPlayer
					.setDataSource("http://192.168.1.5/muzi/Adele___Set_Fire_To_The_Rain_Adele.mp3");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			mediaPlayer.prepare();
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} // might take long! (for buffering, etc)
		mediaPlayer.start();

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