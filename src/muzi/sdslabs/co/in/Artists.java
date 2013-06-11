package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Artists extends Activity {

	// url to make request
	// remember that its band & not artists
	private static String url = "http://192.168.1.5/muzi/ajax/band/list.php";

	// JSON Node names
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_LANGUAGE = "language";

	// artists JSONArray
	JSONArray artists = null;
	ListView lv;
	ArrayList<String> arrayList_artists;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artists);
		lv = (ListView) findViewById(R.id.lvArtists);
		lv.setFastScrollEnabled(true);
		// Hashmap for ListView
		arrayList_artists = new ArrayList<String>();
		ArrayList<HashMap<String, String>> ArtistList = new ArrayList<HashMap<String, String>>();

		// Creating JSON Parser instance
		// getting JSON string from URL
		GetMethodEx test = new GetMethodEx();
		try {
			Log.i("Trying", "done");
			try {
				// json = new JSONObject();
				artists = new JSONArray(test.getInternetData(url));
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			} finally {
				if (artists != null) {
					// Getting Array of artists
					// looping through All artists
					// the code saves much more than required for now as we may
					// require it later
					for (int i = 0; i < artists.length(); i++) {
						JSONObject c = artists.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_ID);
						String name = c.getString(TAG_NAME);
						String language = c.getString(TAG_LANGUAGE);

						arrayList_artists.add(name);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_ID, id);
						map.put(TAG_NAME, name);
						map.put(TAG_LANGUAGE, language);

						Log.i((i + 1) + "", name);

						// adding HashList to ArrayList
						ArtistList.add(map);
					}
					// if php query doesn't give sorted results comment out the
					// Collections.sort(arrayList_artists);
				}
				Log.i("Success", "Successful in taking JSON Object");

			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (ArtistList.isEmpty()) {
				lv.setAdapter(null);
			} else {
				ListAdapter adapter = new SimpleAdapter(Artists.this,
						ArtistList, R.layout.list_item_with_one_tv,
						new String[] { TAG_NAME },
						new int[] { R.id.tv_in_list_item_with_one_tv });

				lv.setAdapter(adapter);
			}
		}
	}

	public void onClick(View v) {
		Toast.makeText(Artists.this, "Clicked", Toast.LENGTH_SHORT).show();
		String firstLetter = (String) v.getTag();
		int index = 0;
		for (index = 0; index < arrayList_artists.size(); index++) {
			char alpha = arrayList_artists.get(index).charAt(0);
			if (Character.toLowerCase(alpha) == Character
					.toLowerCase(firstLetter.charAt(0))) {
				// index = stringlist.indexOf(alpha);
				break;
			}
		}

		lv.setSelectionFromTop(index, 0);
	}
}