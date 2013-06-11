package muzi.sdslabs.co.in;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeScreen extends Activity implements OnItemClickListener {

	ListView lv;
	TextView a, b, c, d;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Albums", "Artists", "Genre", "Top Tracks", "Top Albums",
			"Language" };
	char stringlist[] = new char[26];

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		lv = (ListView) findViewById(R.id.lvHomeScreen);
		lv.setOnItemClickListener(HomeScreen.this);

		adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_with_one_tv, R.id.tv_in_list_item_with_one_tv,
				listItems);

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
						Class.forName("muzi.sdslabs.co.in.FilteredListAfterQuery"));
				i.putExtra("filter_type", listItems[position]);
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}