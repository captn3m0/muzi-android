package muzi.sdslabs.co.in;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RemoteViews;
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
	public static final int PLAY_PAUSE = 103, NEXT = 104, PREVIOUS = 105,
			NOTIFICATION_RECEIVER = 106, MUSIC_READY = 107;
	NotificationReceiver myReceiver;
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

		// Register BroadcastReceiver
		// to receive event from our service
		myReceiver = new NotificationReceiver();
		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(MyService.MY_ACTION);
		registerReceiver(myReceiver, intentFilter);
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

	private void showNotification() {
		final int mId = 10;

		RemoteViews notiView = new RemoteViews(this.getPackageName(),
				R.layout.notification);

		// for next song
		Intent active = new Intent(this, NotificationReceiver.class);
		active.putExtra("action", NEXT);
		active.putExtra("RECEIVER", new DownloadReceiver(new Handler()));

		PendingIntent actionPendingIntent = PendingIntent.getBroadcast(this,
				NEXT, active, 0);
		notiView.setOnClickPendingIntent(R.id.nibNext, actionPendingIntent);

		// for previous song
		new Intent(this, NotificationReceiver.class);
		active.putExtra("action", PREVIOUS);
		actionPendingIntent = PendingIntent.getBroadcast(this, PREVIOUS,
				active, 0);
		notiView.setOnClickPendingIntent(R.id.nibPrevious, actionPendingIntent);

		// for play pause
		new Intent(this, NotificationReceiver.class);
		active.putExtra("action", PLAY_PAUSE);
		actionPendingIntent = PendingIntent.getBroadcast(this, PLAY_PAUSE,
				active, 0);
		notiView.setOnClickPendingIntent(R.id.ntbPlayPause, actionPendingIntent);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon).setContentTitle("Muzi")
				.setContent(notiView).setOngoing(true);

		// .setAutoCancel(true) will be used later when notification will be
		// shown only when muzi isn't on screen

		mBuilder.build().contentView = notiView;

		// .setContentText(
		// MyActivity.nowPlayingList
		// .get(MyActivity.currentSongIndex));
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, NowPlayingList.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(HomeScreen.class);
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(mId, mBuilder.build());
	}

	public class DownloadReceiver extends ResultReceiver {
		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);

			if (resultCode == MUSIC_READY) {
				showNotification();
			} else if (resultCode == NOTIFICATION_RECEIVER) {
				int action = resultData.getInt("action");

				if (action == NEXT) {

					if (nowPlayingList.size() > 0) {
						tempSongIndex = (currentSongIndex + 1)
								% nowPlayingPathsList.size();
						startMusicService();
					}
				} else if (action == PLAY_PAUSE) {

					boolean on = tbPlayPause.isChecked();

					if (on) {
						Log.i("Service", mServ + "");
						mServ.resumeMusic();
					} else {
						mServ.pauseMusic();
					}
				} else if (action == PREVIOUS) {

					if (nowPlayingList.size() > 0) {
						tempSongIndex = (currentSongIndex - 1)
								% nowPlayingPathsList.size();
						startMusicService();
					}
				}
			}
		}
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

	void startMusicService() {
		Intent i = new Intent(context, MusicService.class);
		i.putExtra("RECEIVER", new DownloadReceiver(new Handler()));
		startService(i);
	}

	public class NotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.i("MusicService: DownloadReceiver",
					intent.getIntExtra("action", 0) + "received");
			Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();

			// ResultReceiver receiver = (ResultReceiver) intent
			// .getParcelableExtra("RECEIVER");
			//
			// Bundle resultData = new Bundle();
			// resultData.putInt("action", intent.getIntExtra("action", 0));
			// receiver.send(MyActivity.NOTIFICATION_RECEIVER, resultData);

			// if (resultCode == MusicService.PLAY_PAUSE) {
			// // MyActivity.tempSongIndex = (MyActivity.currentSongIndex + 1)
			// // % MyActivity.nowPlayingPathsList.size();
			// // Intent i = new Intent(this, MusicService.class);
			// // startService(i);
			// }
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		int id = arg0.getId();
		if (id == R.id.ibPrevious) {

			if (nowPlayingList.size() > 0) {
				tempSongIndex = (currentSongIndex - 1)
						% nowPlayingPathsList.size();
				startMusicService();
			}

		} else if (id == R.id.ibNext) {

			if (nowPlayingList.size() > 0) {
				tempSongIndex = (currentSongIndex + 1)
						% nowPlayingPathsList.size();
				startMusicService();
			}

		} else if (id == R.id.ibCurrentList) {
			Intent i = new Intent(context, NowPlayingList.class);
			startActivity(i);

		} else if (id == R.id.ibShuffle) {
			shouldShuffle = !shouldShuffle;
		}
	}
}