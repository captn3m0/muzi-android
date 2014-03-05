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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class AlbumsFromArtistsFragment extends Fragment implements
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

	// Keys used in Hashmap
	String[] from = { TAG_ID, TAG_NAME };

	// Ids of views in listview_layout
	int[] to = { R.id.iv_in_li, R.id.tv_in_li };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.simple_grid_view, container,
				false);

		gv = (GridView) rootView.findViewById(R.id.gv);
//		gv.getRootView().setBackgroundColor(
//				getResources().getColor(R.color.Black));
//		gv.setCacheColorHint(Color.TRANSPARENT);
//		gv.setFastScrollEnabled(true);
		gv.setOnItemClickListener(AlbumsFromArtistsFragment.this);

		albumList = new ArrayList<HashMap<String, String>>();

		String type = getArguments().getString("search_type2");
		String album_id = getArguments().getString("search_id2");

		if (type != null) {
			root = GlobalVariables.api_root + type + "/albums.php?id="
					+ album_id;
			Log.i("request url", root);
			getActivity().setTitle(
					getActivity().getIntent().getStringExtra("search_title2"));
		} else {
			getActivity().finish();
			Toast.makeText(getActivity(),
					"Sorry, the request couldn't be executed",
					Toast.LENGTH_LONG).show();
		}

		task = new LoadSongs();
		task.execute();

		return rootView;
	}

	class LoadSongs extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
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
			getActivity().finish();
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

						// albumList.add(jsonObject.getString(TAG_NAME));

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_ID, jsonObject.getString(TAG_ID));
						map.put(TAG_NAME, jsonObject.getString(TAG_NAME));

						albumList.add(map);
						// albumIdList.add(jsonObject.getString(TAG_ID));
						// loadBitmap(jsonObject.getString(TAG_ID), ImageView
						// imageView);
						//
						// Bitmap bitmap = BitmapFactory
						// .decodeStream((InputStream) new URL(
						// GlobalVariables.pic_root
						// + jsonObject.getString(TAG_ID)
						// + ".jpg").getContent());

						// ivAlbumCover.setImageBitmap(bitmap);
						// songsPathList.add(jsonObject.getString(TAG_FILE));
						// creating new HashMap

						// Log.i((i + 1) + "", jsonObject.getString(TAG_FILE));
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
			if (albumList.size() == 0) {
				gv.setAdapter(null);
			} else {

				Log.i("Size of list", albumList.size() + "");
				GridAdapter adapter = new GridAdapter(getActivity(), albumList,
						R.layout.grid_cell, from, to);
				gv.setAdapter(adapter);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position, long id) {
		// TODO Auto-generated method stub

		Intent i = new Intent(getActivity(), SongsFromAlbums.class);

		i.putExtra("search_type1", "album");
		i.putExtra("search_id1", albumList.get(position).get(TAG_ID));
		i.putExtra("search_title1", albumList.get(position).get(TAG_NAME));
		startActivity(i);

	}
}