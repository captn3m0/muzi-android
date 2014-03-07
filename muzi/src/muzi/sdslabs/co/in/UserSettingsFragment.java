package muzi.sdslabs.co.in;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserSettingsFragment extends Fragment implements
		OnItemClickListener {

	ListView lv;
	ArrayAdapter<String> adapter;
	String listItems[] = { "Language Settings", "Other Settings" };

	public UserSettingsFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.simple_list_view, container,
				false);

		 lv = (ListView) rootView.findViewById(R.id.lvSimple);//
		// getListView();
		  lv.getRootView().setBackgroundColor(
		  getResources().getColor(R.color.Black));
		  lv.setCacheColorHint(Color.TRANSPARENT);
		  lv.setFastScrollEnabled(true);
		
		  lv.setOnItemClickListener(UserSettingsFragment.this);

		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_with_one_tv,
				R.id.tv_in_list_item_with_one_tv, listItems);
		lv.setAdapter(adapter);

		return rootView;
	}

	//
	// @Override
	public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
		// // TODO Auto-generated method stub
		//
		try {
			Intent i = new Intent(getActivity(), LangSettings.class);
			startActivity(i);
			// UserSettings.this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}