package muzi.sdslabs.co.in;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class HomeScreen extends Activity implements OnItemClickListener, OnClickListener {

	ListView lv;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Albums", "Artists", "Genre", "Top Tracks",
			"Top Albums", "Language" };
	char stringlist[] = new char[26];
	ImageButton ibSearch;
	EditText etSearch;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		lv = (ListView) findViewById(R.id.lvHomeScreen);
		lv.setOnItemClickListener(HomeScreen.this);
		ibSearch = (ImageButton)findViewById(R.id.ibSearchInHomeScreen);
		ibSearch.setOnClickListener(HomeScreen.this);
		etSearch = (EditText)findViewById(R.id.etSearchBoxHomeScreen);

		adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_with_one_tv,
				R.id.tv_in_list_item_with_one_tv, listItems);

		lv.setAdapter(adapter);

		for (int i = 0; i < 25; i++) {
			stringlist[i] = (char) (65 + i); // String.valueOf(Character.toChars(i))
		}
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
			try {
				Intent i = new Intent(HomeScreen.this,
						FilteredListAfterQuery.class);
				i.putExtra("filter_type", listItems[position]);
				//Log.i("extra", listItems[position]);
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ibSearchInHomeScreen){
			Intent i = new Intent(HomeScreen.this, SearchResults.class);
			i.putExtra("search_query", etSearch.getText().toString());
			startActivity(i);
		}
		
	}

}