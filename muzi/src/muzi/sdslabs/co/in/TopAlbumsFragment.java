package muzi.sdslabs.co.in;

/*if writable cursor isn't available then pass this hashmap to database file & then parse
 * it or rather use its strings to put in array*/

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

public class TopAlbumsFragment extends Fragment implements OnTouchListener {

	private ProgressDialog pDialog;
	boolean artist, album;

	// JSON keys
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "name";

	JSONArray FilteredJSONArray = null;
	ArrayList<HashMap<String, String>> FilteredArrayList;

	// Keys used in Hashmap
	String[] from = { TAG_ID, TAG_TITLE };

	// Ids of views in listview_layout
	int[] to = { R.id.iv_in_li, R.id.tv_in_li };

	/* To detect itemClick using touch gestures */
	boolean isTouch;
	float startXPosition = -1;
	float startYPosition = -1;
	float endXPosition = -10;
	float endYPosition = -10;

	public TopAlbumsFragment() {
		// Empty constructor required for fragment subclasses
	}

	GridView gv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.simple_grid_view, container,
				false);

		gv = (GridView) rootView.findViewById(R.id.gv);
		gv.setOnTouchListener(TopAlbumsFragment.this);

		FilteredArrayList = new ArrayList<HashMap<String, String>>();
		new LoadAlbums().execute();

		return rootView;
	}

	class LoadAlbums extends AsyncTask<String, String, String> {

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
			pDialog.show();
		}

		void parseJsonAndReturnHashMap() {

			InternetData test = new InternetData();
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
				gv.setAdapter(null);
			} else {
				Log.i("Size of list", FilteredArrayList.size() + "");

				if (getActivity() != null) {
					GridAdapter adapter = new GridAdapter(getActivity(),
							FilteredArrayList, R.layout.grid_cell, from, to);
					gv.setAdapter(adapter);
				}
			}
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent me) {
		// TODO Auto-generated method stub

		Log.i("TopTrackFragment:onPostExecute():setOnTouchListener",
				me.getAction() + "");

		Log.i("isTouch", isTouch + "");

		if (!isTouch) {
			startXPosition = -1;
			startYPosition = -1;
		}

		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			isTouch = true;
			startXPosition = me.getX();
			startYPosition = me.getY();
		} else if (me.getAction() == MotionEvent.ACTION_UP) {
			endXPosition = me.getX();
			endYPosition = me.getY();
			isTouch = false;
		}

		Log.i("startXPosition = ", startXPosition + "");
		Log.i("startYPosition = ", startYPosition + "");
		Log.i("endXPosition = ", endXPosition + "");
		Log.i("endYPosition = ", endYPosition + "");

		if ((Math.abs(startXPosition - endXPosition) <= 0.3)
				&& (Math.abs(startYPosition - endYPosition) <= 0.3)) {

			Toast.makeText(getActivity(), "Touch Detected", Toast.LENGTH_SHORT)
					.show();

			int position = gv.pointToPosition((int) startXPosition,
					(int) startYPosition);

			Log.i("result ------ onTouch", position + "\n");

//			for (int i = 0; i < FilteredJSONArray.length(); i++) {
//				String albumName = FilteredArrayList.get(position).get(
//						TAG_TITLE);
//				if (FilteredArrayList.contains(albumName)) {
					String id = FilteredArrayList.get(position).get(TAG_ID);
					Intent intent = new Intent(getActivity(),
							SongsFromAlbums.class);
					intent.putExtra("search_type1", "album");
					intent.putExtra("search_id1", id);
					intent.putExtra("search_title1",
							FilteredArrayList.get(position).get(TAG_TITLE));
					startActivity(intent);
			// }
			// }

			startXPosition = -1;
			startYPosition = -1;
			endXPosition = -10;
			endYPosition = -10;
		}
		return false;
	}
}
