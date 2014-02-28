package muzi.sdslabs.co.in;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class HomeScreen extends MyActivity implements OnItemClickListener {

	ListView lv;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Albums", "Artists", "Top Tracks", "Top Albums",
			"Language", "Feedback" };
	EditText etSearch;

	// Button Search;

	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.activity_main, this);
		super.onCreate(savedInstanceState);
//		lv = (ListView) findViewById(R.id.lvHome);
//		lv.setCacheColorHint(Color.TRANSPARENT);
//		lv.setFastScrollEnabled(true);
//
//		lv.setOnItemClickListener(HomeScreen.this);
//
//		etSearch = (EditText) findViewById(R.id.etSearchBoxHomeScreen);
//
//		adapter = new ArrayAdapter<String>(this,
//				R.layout.list_item_with_one_tv,
//				R.id.tv_in_list_item_with_one_tv, listItems);
//
//		lv.setAdapter(adapter);
//
//		etSearch.setOnEditorActionListener(new OnEditorActionListener() {
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				boolean handled = false;
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//					if (etSearch.getText().toString().length() < 3) {
//						Toast.makeText(HomeScreen.this,
//								"Enter at least 3 characters to search",
//								Toast.LENGTH_SHORT).show();
//					} else {
//						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//						imm.hideSoftInputFromWindow(etSearch.getWindowToken(),
//								0);
//						Intent i1 = new Intent(HomeScreen.this,
//								SearchResults.class);
//						i1.putExtra("search_query", etSearch.getText()
//								.toString());
//						startActivity(i1);
//					}
//					handled = true;
//				}
//				return handled;
//			}
//		});

	}

	// public void startSearch (View view)
	// {
	// //this function starts the search when the image button is pressed
	// if (etSearch.getText().toString().length() < 3) {
	// Toast.makeText(HomeScreen.this,
	// "Enter minimum of 3 characters to search",
	// Toast.LENGTH_SHORT).show();
	// } else {
	// InputMethodManager imm = (InputMethodManager)getSystemService(
	// Context.INPUT_METHOD_SERVICE);
	// imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
	// Intent i1 = new Intent(HomeScreen.this,
	// SearchResults.class);
	// i1.putExtra("search_query", etSearch.getText()
	// .toString());
	// startActivity(i1);
	// }
	//
	// }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.main, menu);
//
//		MenuItem searchItem = menu.findItem(R.id.search);
//
//		// Associate searchable configuration with the SearchView
//		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//		SearchView searchView = (SearchView) MenuItemCompat
//				.getActionView(searchItem);
//		searchView.setSearchableInfo(searchManager
//				.getSearchableInfo(getComponentName()));
//		return true;
//	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
		// TODO Auto-generated method stub

		Log.i("position", position + "");

		// Learnt it the hard way that position starts from 1 here
		// May be it's because of header otherwise the count starts from 0 in
		// general
		// if clicked on artist or album
		if (position == 0 || position == 1) {
			try {
				Intent i = new Intent(HomeScreen.this, FilteredList.class);
				i.putExtra("list_type", listItems[position]);
				// Log.i("extra", listItems[position]);
				startActivity(i);
				// HomeScreen.this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (position == 2) {
			Intent i = new Intent(HomeScreen.this, TopTracks.class);
			startActivity(i);
		} else if (position == 3) {
			Intent i = new Intent(HomeScreen.this, TopAlbums.class);
			startActivity(i);
		} else if (position == 4) {
			Intent i = new Intent(HomeScreen.this, LangSettings.class);
			startActivity(i);
		} else if (position == 5) {
			String email[] = { "contact+muzi@sdslabs.co.in" };
			Intent EmailIntent = new Intent(android.content.Intent.ACTION_SEND);
			EmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, email);
			EmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Feedback for Muzi");
			EmailIntent.setType("plain/Text");
			EmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			startActivity(EmailIntent);
		}
	}
}