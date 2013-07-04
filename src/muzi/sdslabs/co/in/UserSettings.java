package muzi.sdslabs.co.in;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

public class UserSettings extends SherlockListActivity implements
		OnItemClickListener {

	ListView lv;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Language Settings", "Other Settings" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lv = getListView();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.Black));
		getListView().setCacheColorHint(Color.TRANSPARENT);
		lv.setFastScrollEnabled(true);

		lv.setOnItemClickListener(UserSettings.this);

		adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_with_one_tv,
				R.id.tv_in_list_item_with_one_tv, listItems);

		lv.setAdapter(adapter);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent mainIntent = new Intent(getApplicationContext(),
					HomeScreen.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainIntent);
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
		// TODO Auto-generated method stub

		try {
			Intent i = new Intent(UserSettings.this, LangSettings.class);
			startActivity(i);
			// UserSettings.this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}