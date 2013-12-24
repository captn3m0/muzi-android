package muzi.sdslabs.co.in;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
	static ToggleButton tbPlayPause;
	public SeekBar sbSongTimer;
	int layout_id;

	private boolean mIsBound = false;
	public static boolean shouldShuffle = false;
	private MusicService mServ;
	private ServiceConnection Scon;
	public static ArrayList<String> nowPlayingList = new ArrayList<String>(),
			nowPlayingPathsList = new ArrayList<String>();
	public static int currentSongIndex = 0, tempSongIndex = 0;
	Context context;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			if (mServ.mp != null && mServ.mp.isPlaying()) {
				if (mServ.mp.getDuration() > 0) {
					// sbSongTimer.incrementProgressBy(diff);
					int mCurrentPosition = (mServ.mp.getCurrentPosition()) / 1000;
					sbSongTimer.setMax(mServ.mp.getDuration() / 1000);
					sbSongTimer.setProgress(mCurrentPosition);
				}
			}
			mHandler.postDelayed(this, 1000);
		}
	};

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
		tbPlayPause = (ToggleButton) findViewById(R.id.tbPlayPause);
		sbSongTimer = (SeekBar) findViewById(R.id.sbSongTimer);

		ibNext.setOnClickListener(MyActivity.this);
		ibPrevious.setOnClickListener(MyActivity.this);
		ibCurrentList.setOnClickListener(MyActivity.this);
		ibShuffle.setOnClickListener(MyActivity.this);
		ibRepeat.setOnClickListener(MyActivity.this);

		if (context.getClass().equals(NowPlayingList.class)) {
			ibCurrentList.setVisibility(View.GONE);
		}

		mRunnable.run();

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

		sbSongTimer.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//
				// Log.i("MyActivity: onSeekBarChangeListener: onProgressChanged",
				// "progress " + progress);

				if (mServ.mp != null && fromUser) {
					mServ.mp.seekTo(progress * 1000);
				}
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
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

		/** Set the icon to pause when music service is called **/

		startService(i);
	}

	public void footerPlayToggle(View view) {

		int id = view.getId();

		if (id == R.id.tbPlayPause) {
			boolean on = ((ToggleButton) view).isChecked();

			if (on) {
				Log.i("Service", mServ + "");
				mServ.resumeMusic();
			} else {
				mServ.pauseMusic();
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

			if (nowPlayingList.size() > 0) {
				tempSongIndex = (currentSongIndex - 1)
						% nowPlayingPathsList.size();
				Intent i = new Intent(context, MusicService.class);
				startService(i);
			}

		} else if (id == R.id.ibNext) {

			if (nowPlayingList.size() > 0) {
				tempSongIndex = (currentSongIndex + 1)
						% nowPlayingPathsList.size();
				Intent i = new Intent(context, MusicService.class);
				startService(i);
			}

		} else if (id == R.id.ibCurrentList) {
			Intent i = new Intent(context, NowPlayingList.class);
			startActivity(i);

		} else if (id == R.id.ibShuffle) {
			shouldShuffle = !shouldShuffle;
		}
	}
}