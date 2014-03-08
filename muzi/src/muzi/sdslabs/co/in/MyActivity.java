package muzi.sdslabs.co.in;

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
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

	public static boolean shouldShuffle = false;
	private static MusicService mServ;
	private ServiceConnection Scon;

	public static ArrayList<HashMap<String, String>> nowPlayingSongList;
	public static String TAG_NAME = "name";
	public static String TAG_PATH = "path";
	public static String TAG_IMAGEPATH = "id";

	// public static ArrayList<String> nowPlayingSongList = new
	// ArrayList<String>(),
	// nowPlayingSongList = new ArrayList<String>();
	public static int currentSongIndex = 0, tempSongIndex = 0;
	Context context;
	static ResultReceiver serviceActionReceiver;

	/* Navigation Drawer */
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	CharSequence mTitle, mDrawerTitle;

	Integer listItems[] = { R.drawable.muzi, R.drawable.artist,
			R.drawable.toptracks, R.drawable.topalbum, R.drawable.playlist,
			R.drawable.settings };

	String titles[] = { "Muzi", "Artists", "Top Tracks", "Top Albums",
			"Now Playing List", "Settings" };

	// Keys used in Hashmap
	String[] from = { "image" };

	// Ids of views in listview_layout
	int[] to = { R.id.ivTitleInDrawer };
	ArrayList<HashMap<String, String>> listImages;

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
				TextView tvSongTitle = (TextView) findViewById(R.id.tvSongTitleFooter);
				tvSongTitle.setText(nowPlayingSongList.get(tempSongIndex)
						.get(TAG_NAME));
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

				if (nowPlayingSongList.size() > 0) {
					tempSongIndex = (currentSongIndex + 1)
							% nowPlayingSongList.size();
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

				if (nowPlayingSongList.size() > 0) {
					tempSongIndex = (currentSongIndex - 1 + nowPlayingSongList
							.size()) % nowPlayingSongList.size();
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

		Log.i("MyActivity: onCreate", isNetworkAvailable(context)
				+ " = network availability");

		nowPlayingSongList = new ArrayList<HashMap<String, String>>();

		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerTitle = mTitle = getSupportActionBar().getTitle();
		listImages = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < listItems.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("image", listItems[i] + "");
			listImages.add(map);
		}

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new SimpleAdapter(this, listImages,
				R.layout.drawer_list_item, from, to));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.app_name, /* "open drawer" description for accessibility */
		R.string.abc_action_mode_done /*
									 * "close drawer" description for
									 * accessibility
									 */
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// This will decide which item to show when displayed first time on the
		// screen
		if (savedInstanceState == null) {
			// selectItem(4);
		}

		FooterForPlayerControls footer = new FooterForPlayerControls(context);
		footer = (FooterForPlayerControls) findViewById(R.id.footer);
		footer.initFooter();

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

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;

		Log.i("position", position + "");

		// Learnt it the hard way that position starts from 1 here
		// May be it's because of header otherwise the count starts from 0 in
		// general
		if (position == 1) {
			fragment = new Artist_or_Album_Fragment();
			Bundle args = new Bundle();
			args.putString("list_type", "band");
			fragment.setArguments(args);
		} else if (position == 2 || position == 0) {
			fragment = new TopTrackFragment();
		} else if (position == 3) {
			fragment = new TopAlbumsFragment();
		} else if (position == 4) {
			fragment = new NowPlayingListFragment();
		} else if (position == 5) {
			fragment = new UserSettingsFragment();
		}
		// } else if (position == 5) {
		// String email[] = { "contact+muzi@sdslabs.co.in" };
		// Intent EmailIntent = new Intent(android.content.Intent.ACTION_SEND);
		// EmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, email);
		// EmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
		// "Feedback for Muzi");
		// EmailIntent.setType("plain/Text");
		// EmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		// startActivity(EmailIntent);
		// }

		// Bundle args = new Bundle();
		// fragment.setArguments(args);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(titles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
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
				nowPlayingSongList.get(MyActivity.currentSongIndex).get(
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
						nowPlayingSongList.get(MyActivity.currentSongIndex)
								.get(TAG_NAME)).setAutoCancel(true);
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

	void playSong(String songName, String songPath, String imagePath,
			Context context) {
		Intent i = new Intent(context, MusicService.class);

		HashMap<String, String> songToBeSearched = new HashMap<String, String>();
		songToBeSearched.put(TAG_NAME, songName);

		if (!nowPlayingSongList.contains(songToBeSearched)) {

			HashMap<String, String> song = new HashMap<String, String>();
			song.put(TAG_NAME, songName);
			song.put(TAG_PATH, songPath);
			song.put(TAG_IMAGEPATH, imagePath);
			Log.i("Requested song", GlobalVariables.music_root + songPath);
			
			tempSongIndex = nowPlayingSongList.size();
			nowPlayingSongList.add(song);
		} else {
			tempSongIndex = nowPlayingSongList.indexOf(songToBeSearched);
		}

		for (int j = 0; j < nowPlayingSongList.size(); j++) {
			Log.i("song " + j, nowPlayingSongList.get(j).get(TAG_PATH));
		}

		i.putExtra("RECEIVER", serviceActionReceiver);
		startService(i);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

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

			if (nowPlayingSongList.size() > 0) {
				tempSongIndex = (currentSongIndex - 1 + nowPlayingSongList
						.size()) % nowPlayingSongList.size();
				startMusicService(context);
			}

		} else if (id == R.id.ibNextFooter) {

			if (nowPlayingSongList.size() > 0) {
				tempSongIndex = (currentSongIndex + 1)
						% nowPlayingSongList.size();
				startMusicService(context);
			}

		} else if (id == R.id.ibShuffle) {
			shouldShuffle = !shouldShuffle;
		}
	}
}