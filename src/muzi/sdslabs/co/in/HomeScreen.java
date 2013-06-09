package muzi.sdslabs.co.in;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HomeScreen extends Activity implements OnItemClickListener {

	ListView lv;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Albums", "Artists", "Top Tracks", "Top Albums",
			"Language" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		lv = (ListView) findViewById(R.id.lvHomeScreen);

		lv.setOnItemClickListener(HomeScreen.this);

		adapter = new ArrayAdapter<String>(this,
				R.layout.tv_for_lv_home_screen, R.id.tvForLvHomeScreen, listItems);
		lv.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> v, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.lvHomeScreen) {
			Toast.makeText(HomeScreen.this, "Clicked", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
