package muzi.sdslabs.co.in;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

/*
 * Caution: If you need a Context object within your Fragment, 
 * you can call getActivity(). However, be careful to call getActivity() only 
 * when the fragment is attached to an activity. When the fragment is not yet attached, 
 * or was detached during the end of its lifecycle, getActivity() will return null.
 * */

public class MyActivity extends SherlockActivity {

	ImageButton ibNext, ibPrevious, ibCurrentList, ibShuffle, ibRepeat;

	private boolean mIsBound = false;
	private MusicService mServ;
	private ServiceConnection Scon;
	static ArrayList<String> nowPlayingList, nowPlayingPathsList;
	static int currentSongIndex;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutResourceId());
		FooterForPlayerControls footer = new FooterForPlayerControls(
				getContext());
		footer = (FooterForPlayerControls) findViewById(R.id.footer);
		footer.initFooter();
		ibNext = (ImageButton) findViewById(R.id.ibNext);
		ibPrevious = (ImageButton) findViewById(R.id.ibPrevious);
		ibCurrentList = (ImageButton) findViewById(R.id.ibCurrentList);
		ibShuffle = (ImageButton) findViewById(R.id.ibShuffle);
		ibRepeat = (ImageButton) findViewById(R.id.ibRepeat);

		getSupportActionBar().setHomeButtonEnabled(true);

		// should be initialized in first activity only
		nowPlayingList = new ArrayList<String>();
		nowPlayingPathsList = new ArrayList<String>();
		currentSongIndex = 0;

		Scon = new ServiceConnection() {

			public void onServiceConnected(ComponentName name, IBinder binder) {
				MusicService ms = new MusicService();
				MusicService.ServiceBinder sv = ms.new ServiceBinder();
				mServ = sv.getService();
			}

			public void onServiceDisconnected(ComponentName name) {
				mServ = null;
			}
		};

		doBindService();
	}

	void doBindService() {
		bindService(new Intent(this, MusicService.class), Scon,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(Scon);
			mIsBound = false;
		}
	}

	protected Context getContext() {
		return null;
	};

	public void footerClickControls(View v) {
		Toast.makeText(getContext(), v.getId(), Toast.LENGTH_SHORT).show();
	}

	public void footerPlayToggle(View view) {

		int id = view.getId();
		if (id == R.id.tbPlayPause) {
			boolean on = ((ToggleButton) view).isChecked();

			if (on) {
				mServ.resumeMusic();
			} else {
				mServ.pauseMusic();
				// stopService(new Intent(this, LocalService.class));
			}
		} else if (id == R.id.ibPrevious) {

			if (currentSongIndex != 0) {
				currentSongIndex--;
				Intent i = new Intent(getContext(), MusicService.class);
				i.setData(Uri.parse(nowPlayingPathsList.get(currentSongIndex)));
				i.putExtra("song_path",
						nowPlayingPathsList.get(currentSongIndex));
				startService(i);

			}
		} else if (id == R.id.ibNext) {
			if (currentSongIndex < nowPlayingPathsList.size() - 1) {
				currentSongIndex++;
				Intent i = new Intent(getContext(), MusicService.class);
				i.setData(Uri.parse(nowPlayingPathsList.get(currentSongIndex)));
				i.putExtra("song_path",
						nowPlayingPathsList.get(currentSongIndex));
				startService(i);

			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnbindService();
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

	protected int getLayoutResourceId() {
		return 0;
	}
}