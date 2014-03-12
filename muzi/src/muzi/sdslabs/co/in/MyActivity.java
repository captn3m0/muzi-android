package muzi.sdslabs.co.in;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/*
 * Caution: If you need a Context object within your Fragment, 
 * you can call getActivity(). However, be careful to call getActivity() only 
 * when the fragment is attached to an activity. When the fragment is not yet attached, 
 * or was detached during the end of its lifecycle, getActivity() will return null.
 * */

/**
 * BaseActivity which is extended by all activities The activity has an abstract
 * method called setMyContentView() which has to be called by all the activities
 * which extend MyActivity
 * 
 * @author shivam To do: Add the capability of ordering songs by dragging Note:
 *         Dynamically registered receiver works only when app is in foreground.
 *         So, it won't be called from notification.
 */

public class MyActivity extends ActionBarActivity implements OnClickListener {

	ImageButton ibNext, ibPrevious; // , ibShuffle, ibRepeat; //ibCurrentList,
	static ToggleButton tbPlayPause;
	public SeekBar sbSongTimer;
	int layout_id;
	public static final int PLAY_PAUSE = 103, NEXT = 104, PREVIOUS = 105,
			CLOSE = 106, MUSIC_READY = 107;
	private boolean mIsBound = false;
	private static boolean isApplicationVisible;
	final static int mId = 10;

	private static MusicService mServ;
	private ServiceConnection Scon;

	Context context;
	static ResultReceiver serviceActionReceiver;

	public static String TAG_NAME = "name";
	public static String TAG_PATH = "path";
	public static String TAG_IMAGEPATH = "id";

	Integer listItems[] = { R.drawable.muzi, R.drawable.toptracks,
			R.drawable.topalbum, R.drawable.playlist, R.drawable.settings };

	String titles[] = { "Muzi", "Top Tracks", "Top Albums", "Now Playing List",
			"Settings" };

	// Keys used in Hashmap
	String[] from = { "image" };

	// Ids of views in listview_layout
	int[] to = { R.id.ivTitleInDrawer };
	ArrayList<HashMap<String, String>> listImages;

	public static FooterForPlayerControls footer;

	public static ImageView ivAlbumFooter;
	public static TextView tvSongNameFooter;

	private class ServiceActionReceiver extends ResultReceiver {
		public ServiceActionReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if (resultCode == MUSIC_READY && !isApplicationVisible
					&& MusicService.mp != null && MusicService.mp.isPlaying()) {
				showNotification();
			}
			if (resultCode == MUSIC_READY && MusicService.mp != null) {
				setFooter();
				Log.e("MyActivity", "Should set footer");
			}
		}
	}

	public static class NotificationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.i("MusicService: DownloadReceiver",
					intent.getIntExtra("action", 0) + "received");
			Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();

			int action = intent.getIntExtra("action", 0);

			if (action == NEXT) {

				if (Globals.nowPlayingSongList.size() > 0) {
					Globals.tempSongIndex = (Globals.currentSongIndex + 1)
							% Globals.nowPlayingSongList.size();
					startMusicService(context);
				}
			} else if (action == PLAY_PAUSE) {
				Log.i("MyActivity: NotificationReceiver", "play pause clicked");
				// boolean on = tbPlayPause.isChecked();
				//
				// if (on) {
				// mServ.resumeMusic();
				// } else {
				mServ.pauseMusic();
				// }
			} else if (action == PREVIOUS) {

				if (Globals.nowPlayingSongList.size() > 0) {
					Globals.tempSongIndex = (Globals.currentSongIndex - 1 + Globals.nowPlayingSongList
							.size()) % Globals.nowPlayingSongList.size();
					startMusicService(context);
				}
			} else if (action == CLOSE) {
				// Has a little problem if close is pressed & application state
				// is restored from android cache
				// Then it force closes the app on pressing play/pause button
				mServ.stopMusic();
				cancelNotification(context);

			}
		}
	}

	private Handler mHandler = new Handler();

	public class MyAdapter extends SimpleAdapter {

		Context mContext;
		HashMap<String, String> map = new HashMap<String, String>();

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {

			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View row = super.getView(position, convertView, parent);
			if (row == null) {
				LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = mInflater.inflate(R.layout.drawer_list_item, parent,
						false);
			}
			ImageView iv = (ImageView) row.findViewById(R.id.ivTitleInDrawer);
			if (position == 0 || position == 1) {
				iv.setBackgroundColor(getResources().getColor(
						android.R.color.white));
			}
			iv.setImageResource(listItems[position]);
			return row;
		}
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			if (MusicService.mp != null && MusicService.mp.isPlaying()) {
				if (MusicService.mp.getDuration() > 0) {
					// sbSongTimer.incrementProgressBy(diff);
					int mCurrentPosition = (MusicService.mp
							.getCurrentPosition()) / 1000;
					sbSongTimer.setMax(MusicService.mp.getDuration() / 1000);
					sbSongTimer.setProgress(mCurrentPosition);
				}
			}
			mHandler.postDelayed(this, 1000);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(layout_id);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Log.i("MyActivity: onCreate", isNetworkAvailable(context)
				+ " = network availability");

		if (Globals.nowPlayingSongList == null) {
			Globals.nowPlayingSongList = new ArrayList<HashMap<String, String>>();

		}

		ivAlbumFooter = (ImageView) findViewById(R.id.ivAlbumArtFooter);
		tvSongNameFooter = (TextView) findViewById(R.id.tvSongTitleFooter);

		footer = new FooterForPlayerControls(context);
		footer = (FooterForPlayerControls) findViewById(R.id.footer);
		footer.initFooter();
		setFooter();

		ibNext = (ImageButton) findViewById(R.id.ibNextFooter);
		ibPrevious = (ImageButton) findViewById(R.id.ibPreviousFooter);
		// ibCurrentList = (ImageButton) findViewById(R.id.ibCurrentList);
		// ibShuffle = (ImageButton) findViewById(R.id.ibShuffle);
		// ibRepeat = (ImageButton) findViewById(R.id.ibRepeat);
		tbPlayPause = (ToggleButton) findViewById(R.id.tbPlayPauseFooter);
		sbSongTimer = (SeekBar) findViewById(R.id.sbSongTimerFooter);

		ibNext.setOnClickListener(MyActivity.this);
		ibPrevious.setOnClickListener(MyActivity.this);
		// ibCurrentList.setOnClickListener(MyActivity.this);
		// ibShuffle.setOnClickListener(MyActivity.this);
		// ibRepeat.setOnClickListener(MyActivity.this);

		// if (Globals.nowPlayingSongList.isEmpty()) {
		// footer.setVisibility(View.GONE);
		// }else{
		// footer.setVisibility(View.VISIBLE);
		// }

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
		serviceActionReceiver = new ServiceActionReceiver(new Handler());
		sbSongTimer.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//
				// Log.i("MyActivity: onSeekBarChangeListener: onProgressChanged",
				// "progress " + progress);

				if (MusicService.mp != null && fromUser) {
					MusicService.mp.seekTo(progress * 1000);
				}
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	private boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	@Override
	protected void onStart() {
		doBindService();
		super.onStart();
		cancelNotification(context);
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isApplicationVisible = true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isApplicationVisible = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		doUnbindService();
		// isApplicationVisible = false;

		if (!isApplicationVisible && MusicService.mp != null
				&& MusicService.mp.isPlaying()) {
			showNotification();
		}
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

	private void showNotification() {
		RemoteViews notiView = new RemoteViews(this.getPackageName(),
				R.layout.notification);

		// for next song
		Intent active = new Intent(this, NotificationReceiver.class);
		active.putExtra("action", NEXT);
		PendingIntent actionPendingIntent = PendingIntent.getBroadcast(this,
				NEXT, active, 0);
		notiView.setOnClickPendingIntent(R.id.nibNext, actionPendingIntent);

		// for previous song
		active = new Intent(this, NotificationReceiver.class);
		active.putExtra("action", PREVIOUS);
		actionPendingIntent = PendingIntent.getBroadcast(this, PREVIOUS,
				active, 0);
		notiView.setOnClickPendingIntent(R.id.nibPrevious, actionPendingIntent);

		// for play pause
		active = new Intent(this, NotificationReceiver.class);
		active.putExtra("action", PLAY_PAUSE);
		actionPendingIntent = PendingIntent.getBroadcast(this, PLAY_PAUSE,
				active, 0);
		notiView.setOnClickPendingIntent(R.id.nibPlayPause, actionPendingIntent);

		// to close the app
		active = new Intent(this, NotificationReceiver.class);
		active.putExtra("action", CLOSE);
		actionPendingIntent = PendingIntent
				.getBroadcast(this, CLOSE, active, 0);
		notiView.setOnClickPendingIntent(R.id.nibClose, actionPendingIntent);
		notiView.setTextViewText(
				R.id.ntvTitle,
				Globals.nowPlayingSongList.get(Globals.currentSongIndex).get(
						TAG_NAME));

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.muzi_icon)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.muzi_icon))
				// .setContentTitle("Muzi")
				.setContent(notiView)
				.setOngoing(true)
				.setContentTitle(
						Globals.nowPlayingSongList
								.get(Globals.currentSongIndex).get(TAG_NAME))
				.setAutoCancel(true);
		mBuilder.build().contentView = notiView;

		// shows big notification in Android > 3.0 (Honeycomb)
		mBuilder.setStyle(
				new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.default_album_cover)))
				.addAction(
						R.drawable.previous,
						"",
						PendingIntent.getBroadcast(this, PREVIOUS, (new Intent(
								this, NotificationReceiver.class)).putExtra(
								"action", PREVIOUS), 0))
				.addAction(
						R.drawable.play_button,
						"",
						PendingIntent.getBroadcast(this, PLAY_PAUSE,
								(new Intent(this, NotificationReceiver.class))
										.putExtra("action", PLAY_PAUSE), 0))
				.addAction(
						R.drawable.next,
						"",
						PendingIntent.getBroadcast(this, NEXT, (new Intent(
								this, NotificationReceiver.class)).putExtra(
								"action", NEXT), 0))

				// For some reason android shows only 3 actions, so close isn't
				// displayed
				.addAction(
						R.drawable.close_button,
						"",
						PendingIntent.getBroadcast(this, CLOSE, (new Intent(
								this, NotificationReceiver.class)).putExtra(
								"action", CLOSE), 0));
		/**
		 * Apparently Build.VERSION.RELEASE will have to be used 'cos it may not
		 * work on rooted devices. Should show bigger content according to
		 * documentation but apparently it doesn't make any difference
		 */
		// if (android.os.Build.VERSION.SDK_INT >=
		// android.os.Build.VERSION_CODES.JELLY_BEAN) {
		// mBuilder.build().bigContentView = notiView;
		// }

		// Creates an explicit intent for an Activity in your app

		Intent resultIntent = new Intent(this, MainActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);

		Log.i("MyActivity: showNotification",
				"" + stackBuilder.getIntentCount());

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_CANCEL_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(mId, mBuilder.build());
	}

	static void cancelNotification(Context ctx) {
		NotificationManager mNotificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(mId);
	}

	public void footerPlayToggle(View view) {

		int id = view.getId();

		if (id == R.id.tbPlayPauseFooter) {
			boolean on = ((ToggleButton) view).isChecked();

			if (on) {
				Log.i("Service", mServ + "");
				mServ.resumeMusic();
			} else {
				mServ.pauseMusic();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		MenuItem searchItem = menu.findItem(R.id.search);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub

		Log.e("Yes onSearchRequested() called", "true");

		return super.onSearchRequested();
	}

	void playSong(String songName, String songPath, String imagePath,
			Context context) {
		Intent i = new Intent(context, MusicService.class);

		HashMap<String, String> song = new HashMap<String, String>();
		song.put(TAG_NAME, songName);
		song.put(TAG_PATH, songPath);
		song.put(TAG_IMAGEPATH, imagePath);

		Log.e("Globals.nowPlayingSongList.contains(songToBeSearched)",
				Globals.nowPlayingSongList.contains(song) + "");

		if (!Globals.nowPlayingSongList.contains(song)) {

			Log.i("Requested song", GlobalVariables.music_root + songPath);

			Globals.tempSongIndex = Globals.nowPlayingSongList.size();
			Globals.nowPlayingSongList.add(song);
		} else {
			Globals.tempSongIndex = Globals.nowPlayingSongList.indexOf(song);
		}

		for (int j = 0; j < Globals.nowPlayingSongList.size(); j++) {
			Log.i("song " + j, Globals.nowPlayingSongList.get(j).get(TAG_PATH));
		}

		i.putExtra("RECEIVER", serviceActionReceiver);
		startService(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			Intent mainIntent = new Intent(getApplicationContext(),
					MainActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainIntent);
		}
		return true;
	}

	static void startMusicService(Context context) {
		Intent i = new Intent(context, MusicService.class);
		i.putExtra("RECEIVER", serviceActionReceiver);
		context.startService(i);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		int id = arg0.getId();
		if (id == R.id.ibPreviousFooter) {

			if (Globals.nowPlayingSongList.size() > 0) {
				Globals.tempSongIndex = (Globals.currentSongIndex - 1 + Globals.nowPlayingSongList
						.size()) % Globals.nowPlayingSongList.size();
				startMusicService(context);
			}

		} else if (id == R.id.ibNextFooter) {

			if (Globals.nowPlayingSongList.size() > 0) {
				Globals.tempSongIndex = (Globals.currentSongIndex + 1)
						% Globals.nowPlayingSongList.size();
				startMusicService(context);
			}

		} else if (id == R.id.ibShuffle) {
			Globals.shouldShuffle = !Globals.shouldShuffle;
		}
	}

	String urlFooterCover;

	public void setFooter() {

		if (MusicService.mp != null && MusicService.mp.isPlaying()
				&& Globals.nowPlayingSongList.size() > 0) {

			// footer.initFooter();
			Log.e("MyActivity", "should definitely set footer");

			tvSongNameFooter = (TextView) findViewById(R.id.tvSongTitleFooter);
			tvSongNameFooter.setText(Globals.nowPlayingSongList.get(
					Globals.tempSongIndex).get(TAG_NAME));

			String img = Globals.nowPlayingSongList.get(Globals.tempSongIndex)
					.get(TAG_IMAGEPATH);

			urlFooterCover = GlobalVariables.pic_root + img + ".jpg";

			LoadAlbumCoverInFooter async = new LoadAlbumCoverInFooter();
			async.execute();
			if (!Globals.nowPlayingSongList.isEmpty()) {
				footer.setVisibility(View.VISIBLE);
			}
		}
	}

	Bitmap bitmap = null;

	class LoadAlbumCoverInFooter extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {

			String url = urlFooterCover;

			// First decode with inJustDecodeBounds=true to check dimensions
			try {
				Log.i("TileAdapter: decodeSam***", url);
				bitmap = BitmapFactory.decodeStream(
						(InputStream) new URL(url).getContent(), null, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.default_album_cover, null);
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {

			Log.i("Album should print", "true");
			ivAlbumFooter = (ImageView) findViewById(R.id.ivAlbumArtFooter);
			ivAlbumFooter.setImageBitmap(bitmap);
		}
	}
}