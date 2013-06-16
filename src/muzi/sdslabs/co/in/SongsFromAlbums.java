package muzi.sdslabs.co.in;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

public class SongsFromAlbums extends ListActivity implements
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
	TextView tvAlbumDetails;
	Drawable image;
	Boolean image_avail;
	String album_id;
	Bitmap bitmap;

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

		/*------------------------style list view-----------------*/
		{
			lv = getListView();
			lv.setFastScrollEnabled(true);
			lv.getRootView().setBackgroundColor(
					getResources().getColor(R.color.homeGrey));
			getListView().setCacheColorHint(Color.TRANSPARENT);
			lv.setFastScrollEnabled(true);

			/*-------add header to list view-------*/
			View header = getLayoutInflater().inflate(
					R.layout.header_for_songs_from_album, null);
			lv.addHeaderView(header);
			ivAlbumCover = (ImageView) findViewById(R.id.ivAlbumCover);
			tvAlbumDetails = (TextView) findViewById(R.id.tvAlbumDetails);

			image_avail = false;
			SongsList = new ArrayList<String>();
		}

		/*------------------get query value to search for-----------------*/
		{
			String type = getIntent().getStringExtra("search_type");
			album_id = getIntent().getStringExtra("search_id");

			if (type != null) {
				root = GlobalVariables.api_root + type + "?id=" + album_id;
				Log.i("request url", root);
			} else {
				SongsFromAlbums.this.finish();
				Toast.makeText(SongsFromAlbums.this,
						"Sorry, the request couldn't be executed",
						Toast.LENGTH_LONG).show();
			}
		}

		/*----------------------call async method---------------------*/
		new LoadAllProducts().execute();
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
			pDialog = new ProgressDialog(SongsFromAlbums.this);
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
				jsonObject = new JSONObject(test.getInternetData(root));
				FilteredJSONArray = jsonObject.getJSONArray("tracks");
				Log.i("Array", FilteredJSONArray.toString());

				if (FilteredJSONArray != null) {
					firstId = FilteredJSONArray.getJSONObject(0).getInt("id");

					// Getting Array of FilteredJSONArray
					// looping through All FilteredJSONArray
					Log.i("Working", "good");
					// require it later
					for (int i = 0; i < FilteredJSONArray.length(); i++) {
						jsonObject = FilteredJSONArray.getJSONObject(i);

						SongsList.add((i + 1) + ". "
								+ jsonObject.getString(TAG_TITLE));
						// creating new HashMap
					}
				}

			} catch (Exception e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			} finally {
				if (SongsList.size() > 0) {
					try {
						jsonObject = new JSONObject(
								test.getInternetData(GlobalVariables.api_root
										+ "/track/?id=" + firstId));
						Log.i("Track url", jsonObject.toString());
						year = jsonObject.getInt("year");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			try {
				Log.i("Image url", "http://localhost/muzi/images/pics/"
						+ album_id + ".jpg");
				// InputStream is = (InputStream) new URL(
				// GlobalVariables.album_pic_root + album_id + ".jpg")
				// .getContent();
				// image = Drawable.createFromStream(is, "src name");

				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						GlobalVariables.album_pic_root + album_id + ".jpg")
						.getContent());

				image_avail = true;
			} catch (Exception e) {
				e.printStackTrace();
				image_avail = false;
				Log.i("Image Not returned", "");

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

			// has to be before setting up the adapter
			if (image_avail == true) {
				ivAlbumCover.setImageBitmap(bitmap);
				Log.i("Image available", "");
			}

			if (SongsList.size() == 0) {
				lv.setAdapter(null);
			} else {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SongsFromAlbums.this, R.layout.list_item_with_one_tv,
						R.id.tv_in_list_item_with_one_tv, SongsList);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(SongsFromAlbums.this);
			}

			if (year != 0) {
				Toast.makeText(SongsFromAlbums.this,
						"Year of release: " + year, Toast.LENGTH_SHORT).show();
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stubString url = "http://........"; //
		// your URL here
		/*
		 * MediaPlayer mediaPlayer = new MediaPlayer();
		 * mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); try {
		 * mediaPlayer .setDataSource(
		 * "http://192.168.1.3/muzi/Adele___Set_Fire_To_The_Rain_Adele.mp3"); }
		 * catch (IllegalArgumentException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (SecurityException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (IllegalStateException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 * 
		 * try { mediaPlayer.prepare(); } catch (IllegalStateException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } // might take long! (for buffering, etc)
		 * mediaPlayer.start();
		 */

	}
}