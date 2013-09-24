package muzi.sdslabs.co.in;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class HomeScreen extends SherlockListActivity implements
		OnItemClickListener {

	ListView lv;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Albums", "Artists", "Top Tracks", "Top Albums",
			"Language" };
	EditText etSearch;
	//Button Search;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lv = getListView();
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.Black));
		getListView().setCacheColorHint(Color.TRANSPARENT);
		lv.setFastScrollEnabled(true);

		lv.setOnItemClickListener(HomeScreen.this);

		View header = getLayoutInflater().inflate(
				R.layout.header_for_homescreen, null);
		lv.addHeaderView(header);
		etSearch = (EditText) findViewById(R.id.etSearchBoxHomeScreen);

		adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_with_one_tv,
				R.id.tv_in_list_item_with_one_tv, listItems);

		lv.setAdapter(adapter);
		
		

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
						InputMethodManager imm = (InputMethodManager)getSystemService(
							      Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
						Intent i1 = new Intent(HomeScreen.this,
								SearchResults.class);
						i1.putExtra("search_query", etSearch.getText()
								.toString());
						startActivity(i1);
					}
					handled = true;
				}
				return handled;
			}
		});
		
	}
	
//	public void startSearch (View view)
//	{
//		//this function starts the search when the image button is pressed
//		if (etSearch.getText().toString().length() < 3) {
//			Toast.makeText(HomeScreen.this,
//					"Enter minimum of 3 characters to search",
//					Toast.LENGTH_SHORT).show();
//		} else {
//			InputMethodManager imm = (InputMethodManager)getSystemService(
//				      Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
//			Intent i1 = new Intent(HomeScreen.this,
//					SearchResults.class);
//			i1.putExtra("search_query", etSearch.getText()
//					.toString());
//			startActivity(i1);
//			}
//	    	
//	}

	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("Settings");
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);

		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i = new Intent(HomeScreen.this, UserSettings.class);
    	startActivity(i);
        return true;
    }

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
		// TODO Auto-generated method stub

		Log.i("position", position + "");

		// Learnt it the hard way that position starts from 1 here
		// May be it's because of header otherwise the count starts from 0 in
		// general
		position -= 1;

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
			Intent i= new Intent(HomeScreen.this , TopAlbums.class);
			startActivity(i);
		} else if (position == 4) {
			Intent i = new Intent(HomeScreen.this , LangSettings.class);
			startActivity(i);
		}
		
		
		
		
	}
}