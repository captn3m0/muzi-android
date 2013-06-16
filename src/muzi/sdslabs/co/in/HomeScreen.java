package muzi.sdslabs.co.in;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
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

public class HomeScreen extends ListActivity implements OnItemClickListener {

	ListView lv;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Albums", "Artists", "Top Tracks", "Top Albums",
			"Language" };
	EditText etSearch;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lv = getListView();
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.homeGrey));
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
						Intent i1 = new Intent(HomeScreen.this,
								SearchResults.class);
						i1.putExtra(
								GlobalVariables.HomeScreen_to_SearchResults,
								etSearch.getText().toString());
						startActivity(i1);
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

		// Learnt it the hard way that position starts from 1 here
		position -= 1;

		// if clicked on artist or album
		if (position == 0 || position == 1) {
			try {
				Intent i = new Intent(HomeScreen.this, FilteredList.class);
				i.putExtra(GlobalVariables.HomeScreen_to_FilteredList,
						listItems[position]);
				// Log.i("extra", listItems[position]);
				startActivity(i);
				// HomeScreen.this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}else if(position == 2){
			Intent i = new Intent(HomeScreen.this, TopTracks.class);
			startActivity(i);
		}
	}
}