package muzi.sdslabs.co.in;

/*if writable cursor isn't available then pass this hashmap to database file & then parse
 * it or rather use its strings to put in array*/

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NowPlayingList extends MyActivity implements OnItemClickListener {

	boolean artist, album;

	ListView lv;
	ArrayList<String> listItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setMyContentView(R.layout.simple_list_view_with_footer,
				NowPlayingList.this);
		
		super.onCreate(savedInstanceState);

		lv = (ListView) findViewById(R.id.lvSimple);
		lv.setFastScrollEnabled(true);
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.Black));
		lv.setCacheColorHint(Color.TRANSPARENT);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				NowPlayingList.this, R.layout.list_item_with_one_tv,
				R.id.tv_in_list_item_with_one_tv, MyActivity.nowPlayingList);

		Log.i("Size of now playing list in NOWPLAYINGLIST",
				MyActivity.nowPlayingList.size() + "");
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(NowPlayingList.this);
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
	}
}