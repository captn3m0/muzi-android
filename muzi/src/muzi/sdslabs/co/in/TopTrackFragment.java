package muzi.sdslabs.co.in;

/*if writable cursor isn't available then pass this hashmap to database file & then parse
 * it or rather use its strings to put in array*/

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
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

public class TopTrackFragment extends Fragment {

	private ProgressDialog pDialog;
	boolean artist, album;

	// JSON keys
	private static final String TAG_TRACKID = "trackid";
	private static final String TAG_NAME = "name";
	private static final String TAG_ID = "id";

	JSONArray FilteredJSONArray = null;
	GridView gv;
	String[] from = { TAG_ID, TAG_NAME };
	int[] to = { R.id.ivEvent, R.id.tvTitle };
	ArrayList<HashMap<String, String>> FilteredArrayList;

	/* To detect itemClick using touch gestures */
	boolean isTouch;

	float startXPosition = -1;
	float startYPosition = -1;
	float endXPosition = -10;
	float endYPosition = -10;

	public TopTrackFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.simple_grid_view, container,
				false);
		gv = (GridView) rootView.findViewById(R.id.gv);
		FilteredArrayList = new ArrayList<HashMap<String, String>>();
		new LoadAllProducts().execute();

		getActivity().setTitle("Top Tracks");
		return rootView;
	}

	class LoadAllProducts extends AsyncTask<String, String, String> {

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

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key =>
						// value
						map.put(TAG_TRACKID, id);
						map.put(TAG_NAME, c.getString("title"));
						map.put(TAG_ID, c.getString("albumId"));

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

				if (getActivity() != null) {

					GridAdapter adapter = new GridAdapter(getActivity(),
							FilteredArrayList, R.layout.grid_cell, from, to);
					gv.setAdapter(adapter);

					gv.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent me) {

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
								int position = gv.pointToPosition(
										(int) startXPosition,
										(int) startYPosition);

								Log.i("result ------ onTouch", position + "\n");

								requestedTrackURL = GlobalVariables.api_root
										+ "/track/?id="
										+ FilteredArrayList.get(position).get(
												TAG_TRACKID);
								Log.i("Requested track id", requestedTrackURL);
								songName = FilteredArrayList.get(position).get(
										TAG_NAME);
								new PlaySong().execute();

								startXPosition = -1;
								startYPosition = -1;
								endXPosition = -10;
								endYPosition = -10;
							}
							return false;
						}
					});
				}
			}
		}
	}

	String requestedTrackURL, songName, songPath;

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
			getActivity().finish();
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

				((MainActivity) getActivity()).playSong(songName, songPath,
						jsonObject.getString("albumId"), getActivity());

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
			Toast.makeText(getActivity(), songName, Toast.LENGTH_SHORT).show();
		}
	}

}