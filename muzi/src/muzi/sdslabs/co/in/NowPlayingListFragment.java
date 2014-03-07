package muzi.sdslabs.co.in;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NowPlayingListFragment extends Fragment implements OnItemClickListener {

	boolean artist, album;

	ListView lv;
	ArrayList<String> listItems;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.simple_list_view, container,
				false);
		lv = (ListView) rootView.findViewById(R.id.lvSimple);
		lv.setFastScrollEnabled(true);
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.Black));
		lv.setCacheColorHint(Color.TRANSPARENT);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_with_one_tv,
				R.id.tv_in_list_item_with_one_tv, MyActivity.nowPlayingList);

		Log.i("Size of now playing list in NOWPLAYINGLIST",
				MyActivity.nowPlayingList.size() + "");
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(NowPlayingListFragment.this);

		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position,
			long arg3) {
		((MainActivity) getActivity()).playSong(
				MyActivity.nowPlayingList.get(position),
				MyActivity.nowPlayingPathsList.get(position), getActivity());
	}
}