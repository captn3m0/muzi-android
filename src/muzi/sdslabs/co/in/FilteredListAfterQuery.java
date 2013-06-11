package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FilteredListAfterQuery extends Activity {

	// url to make request
	// remember that its album & not albums
	private static String url;

	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_LANGUAGE = "language";

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<String> FilteredNamesList;

	ArrayList<HashMap<String, String>> FilteredArrayList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filtered_list_after_query);
		lv = (ListView) findViewById(R.id.lvFilteredList);
		lv.setFastScrollEnabled(true);
		FilteredNamesList = new ArrayList<String>();
		FilteredArrayList = new ArrayList<HashMap<String, String>>();
		String value1 = getIntent().getStringExtra("filter_type");

		if (value1.equals("Artists")) {
			url = GlobalVariables.api_root + "album/list.php";
		} else if (value1.equals("Artists")) {
			url = GlobalVariables.api_root + "band/list.php";
		} else if (value1.equals("Genre")) {
			url = GlobalVariables.api_root + "genre/list.php";
		}

		new LoadAllProducts().execute();
	}

	public void onClick(View v) {
		// Toast.makeText(FilteredJSONArrayAfterQuery.this, "Clicked",
		// Toast.LENGTH_SHORT).show();
		String firstLetter = (String) v.getTag();
		int index = 0;
		for (index = 0; index < FilteredNamesList.size(); index++) {
			char alpha = FilteredNamesList.get(index).charAt(0);
			if (Character.toLowerCase(alpha) == Character
					.toLowerCase(firstLetter.charAt(0))) {
				// index = stringlist.indexOf(alpha);
				break;
			}
		}

		lv.setSelectionFromTop(index, 0);
	}

	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FilteredListAfterQuery.this);
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
				Log.i("Trying", "done");
				try {
					FilteredJSONArray = new JSONArray(test.getInternetData(url));
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				} finally {
					if (FilteredJSONArray != null) {
						// Getting Array of FilteredJSONArray
						// looping through All FilteredJSONArray
						// the code saves much more than required for now as we
						// may
						Log.i("Working", "good");
						// require it later
						for (int i = 0; i < FilteredJSONArray.length(); i++) {
							JSONObject c = FilteredJSONArray.getJSONObject(i);

							// Storing each json item in variable
							String id = c.getString(TAG_ID);
							String name = c.getString(TAG_NAME);
							String language = c.getString(TAG_LANGUAGE);

							FilteredNamesList.add(name);
							// creating new HashMap
							HashMap<String, String> map = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							map.put(TAG_ID, id);
							map.put(TAG_NAME, name);
							map.put(TAG_LANGUAGE, language);

							//Log.i((i + 1) + "", name);

							// adding HashList to ArrayList
							FilteredArrayList.add(map);
						}
						// if php query doesn't give sorted results comment out
						// the
					}

				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {

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
			if (FilteredArrayList.size() == 0) {
				lv.setAdapter(null);
			} else {
				ListAdapter adapter = new SimpleAdapter(
						FilteredListAfterQuery.this, FilteredArrayList,
						R.layout.list_item_with_one_tv,
						new String[] { TAG_NAME },
						new int[] { R.id.tv_in_list_item_with_one_tv });

				lv.setAdapter(adapter);
			}
		}

	}
}