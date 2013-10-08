package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Radio extends MyActivity implements OnItemClickListener{
	
	String stationName[] = {"Radio Mirchi 98.5" , "Red FM 91.2"};
	String stationsUrl[] = {"abc" , "def"};
	ListView lv;
	
	//private static MediaPlayer mediaPlayer = new MediaPlayer();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.simple_list_view_with_footer , Radio.this);
		super.onCreate(savedInstanceState);
		
		
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		List<HashMap<String , String>> stations = new ArrayList<HashMap<String ,String>>();
		HashMap<String, String> hm = new HashMap<String,String>();
		
		lv = (ListView) findViewById(R.id.lvSimple);
		lv.setFastScrollEnabled(true);

		lv.setCacheColorHint(Color.TRANSPARENT);
		lv.setFastScrollEnabled(true);
	    
		for(int i=0 ; i<stationName.length ; i++ )
		{
			hm.put("name",stationName[i]);
			hm.put("url",stationsUrl[i]);
		//	hm.put("frequency" , frequency[i]);
			stations.add(hm);

			
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				Radio.this, R.layout.list_item_with_one_tv,
				R.id.tv_in_list_item_with_one_tv, stationName);

        
        // Setting the adapter to the listView
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
}
		}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
		
	}

	



