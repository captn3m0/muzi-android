package muzi.sdslabs.co.in;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class HomeScreen extends Activity implements OnItemClickListener {

	ListView lv;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Albums", "Artists", "Top Tracks", "Top Albums",
			"Language" };
	char stringlist[] = new char[26];
	EditText etSearch;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		lv = (ListView) findViewById(R.id.lvHomeScreen);
		lv.setOnItemClickListener(HomeScreen.this);
		etSearch = (EditText) findViewById(R.id.etSearchBoxHomeScreen);

		adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_with_one_tv,
				R.id.tv_in_list_item_with_one_tv, listItems);

		lv.setAdapter(adapter);

		for (int i = 0; i < 25; i++) {
			stringlist[i] = (char) (65 + i); // String.valueOf(Character.toChars(i))
		}

		etSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (etSearch.getText().toString().length() < 3) {
						Toast.makeText(HomeScreen.this,
								"Enter minimum of 3 characters to search",
								Toast.LENGTH_SHORT).show();
					} else {
						Intent i = new Intent(HomeScreen.this,
								SearchResults.class);
						i.putExtra("search_query", etSearch.getText()
								.toString());
						startActivity(i);
					}
					handled = true;
				}
				return handled;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
		// TODO Auto-generated method stub

		if (av.getId() == R.id.lvHomeScreen) {

			// if clicked on artist or album
			if (position == 1 || position == 0) {
				try {
					Intent i = new Intent(HomeScreen.this,
							FilteredListAfterQuery.class);
					i.putExtra("filter_type", listItems[position]);
					// Log.i("extra", listItems[position]);
					startActivity(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}