package muzi.sdslabs.co.in;

import java.util.ArrayList;

import muzi.sdslabs.co.in.MusicService.ServiceBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

/**
 * @author shivam To do: Add the capability of ordering songs by dragging
 */

public class MyActivity extends SherlockActivity implements OnClickListener {

	ImageButton ibNext, ibPrevious, ibCurrentList, ibShuffle, ibRepeat;
	int layout_id;

	private boolean mIsBound = false;
	private MusicService mServ;
	private ServiceConnection Scon;
	public static ArrayList<String> nowPlayingList, nowPlayingPathsList;
	public static int currentSongIndex = 0, tempSongIndex = 0;
	Context context;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout_id);

		FooterForPlayerControls footer = new FooterForPlayerControls(context);

		footer = (FooterForPlayerControls) findViewById(R.id.footer);
		footer.initFooter();

		ibNext = (ImageButton) findViewById(R.id.ibNext);
		ibPrevious = (ImageButton) findViewById(R.id.ibPrevious);
		ibCurrentList = (ImageButton) findViewById(R.id.ibCurrentList);
		ibShuffle = (ImageButton) findViewById(R.id.ibShuffle);
		ibRepeat = (ImageButton) findViewById(R.id.ibRepeat);

		ibNext.setOnClickListener(MyActivity.this);
		ibPrevious.setOnClickListener(MyActivity.this);
		ibCurrentList.setOnClickListener(MyActivity.this);
		ibShuffle.setOnClickListener(MyActivity.this);
		ibRepeat.setOnClickListener(MyActivity.this);

		getSupportActionBar().setDisplayShowHomeEnabled(true);

		// should be initialized in first activity only
		nowPlayingList = new ArrayList<String>();
		nowPlayingPathsList = new ArrayList<String>();

		// to check if these lists get initialized before every activity
		Log.i("Size of nowPlayingList", nowPlayingList.size() + "");

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
	}

	protected void onStart() {
		super.onStart();
		doBindService();
	};

	@Override
	protected void onStop() {
		super.onStop();
		doUnbindService();
	};

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

	public void setMyContentView(int layout_id, Context c) {
		this.layout_id = layout_id;
		this.context = c;
	}

	public void footerClickControls(View v) {
		Toast.makeText(context, v.getId(), Toast.LENGTH_SHORT).show();
	}

	void playSong(String songName, String songPath, Context context) {
		Intent i = new Intent(this, MusicService.class);

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

		startService(i);
	}

	public void footerPlayToggle(View view) {

		int id = view.getId();

		if (id == R.id.tbPlayPause) {
			boolean on = ((ToggleButton) view).isChecked();

			if (on) {
				Log.i("Service", mServ + "");
				mServ.pauseMusic();
			} else {
				mServ.resumeMusic();
				// stopService(new Intent(this, LocalService.class));
			}
		}
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		int id = arg0.getId();
		if (id == R.id.ibPrevious) {
			if (currentSongIndex != 0) {
				currentSongIndex--;
				Intent i = new Intent(context, MusicService.class);
				i.setData(Uri.parse(nowPlayingPathsList.get(currentSongIndex)));
				i.putExtra("song_path",
						nowPlayingPathsList.get(currentSongIndex));
				startService(i);
			}
		} else if (id == R.id.ibNext) {
			if (currentSongIndex < nowPlayingPathsList.size() - 1) {
				currentSongIndex++;
				Intent i = new Intent(context, MusicService.class);
				i.setData(Uri.parse(nowPlayingPathsList.get(currentSongIndex)));
				i.putExtra("song_path",
						nowPlayingPathsList.get(currentSongIndex));
				startService(i);
			}
		}
	}
}