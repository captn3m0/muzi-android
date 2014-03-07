package muzi.sdslabs.co.in;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends MyActivity {

	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.main_activity, this);
		super.onCreate(savedInstanceState);
	}

	void playSong(String songName, String songPath, Context context) {
		Intent i = new Intent(context, MusicService.class);

		if (!nowPlayingList.contains(songName)) {
			nowPlayingList.add(songName);

			Log.i("Requested song", GlobalVariables.music_root + songPath);
			nowPlayingPathsList.add(GlobalVariables.music_root + songPath);
			tempSongIndex = nowPlayingList.size() - 1;
		} else {
			tempSongIndex = nowPlayingList.indexOf(songName);
		}

		for (int j = 0; j < nowPlayingPathsList.size(); j++) {
			Log.i("song " + j, nowPlayingPathsList.get(j));
		}

		i.putExtra("RECEIVER", serviceActionReceiver);
		startService(i);
	}

	/** Code to add slider now playing screen **/
	/*
	 * 
	 * private static final String TAG = "DemoActivity"; public static final
	 * String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	 * 
	 * 
	 * 
	 * 
	 * @Override protected void onSaveInstanceState(Bundle outState) {
	 * super.onSaveInstanceState(outState);
	 * outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN,
	 * !getSupportActionBar().isShowing()); }
	 * 
	 * 
	 * 
	 * 
	 * SlidingUpPanelLayout layout = (SlidingUpPanelLayout)
	 * findViewById(R.id.sliding_layout);
	 * layout.setShadowDrawable(getResources().getDrawable(
	 * R.drawable.above_shadow)); layout.setAnchorPoint(0.3f);
	 * layout.setPanelSlideListener(new PanelSlideListener() {
	 * 
	 * @Override public void onPanelSlide(View panel, float slideOffset) {
	 * Log.i(TAG, "onPanelSlide, offset " + slideOffset); if (slideOffset < 0.2)
	 * { if (getSupportActionBar().isShowing()) { getSupportActionBar().hide();
	 * } } else { if (!getSupportActionBar().isShowing()) {
	 * getSupportActionBar().show(); } } }
	 * 
	 * @Override public void onPanelExpanded(View panel) { Log.i(TAG,
	 * "onPanelExpanded");
	 * 
	 * }
	 * 
	 * @Override public void onPanelCollapsed(View panel) { Log.i(TAG,
	 * "onPanelCollapsed");
	 * 
	 * }
	 * 
	 * @Override public void onPanelAnchored(View panel) { Log.i(TAG,
	 * "onPanelAnchored");
	 * 
	 * } });
	 * 
	 * boolean actionBarHidden = savedInstanceState != null ? savedInstanceState
	 * .getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false) : false; if
	 * (actionBarHidden) { getSupportActionBar().hide(); }
	 */

}