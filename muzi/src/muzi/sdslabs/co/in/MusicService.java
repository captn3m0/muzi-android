package muzi.sdslabs.co.in;

import java.net.URLEncoder;
import java.util.Random;

import muzi.sdslabs.co.in.MyActivity.NotificationReceiver;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * 
 * @author shivam - Music doesn't stop when you call someone.
 * 
 */

public class MusicService extends Service implements
		MediaPlayer.OnErrorListener, OnCompletionListener {

	public final IBinder mBinder = new ServiceBinder();
	public static MediaPlayer mp;
	public int length = 0;
	public static final int PLAY_PAUSE = 103, NEXT = 104, PREVIOUS = 105;

	/** Constructor for the class **/
	public MusicService() {
	}

	/** To bind the service. Used in activities later. **/
	public class ServiceBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	/**
	 * First method which is called when MusicService is created i.e. any song
	 * is played. Creates a new object mp of class MediaPlayer() to be later
	 * used to play music. Sets completion & error listener to the object.
	 **/
	@Override
	public void onCreate() {
		super.onCreate();

		mp = new MediaPlayer();
		mp.setOnErrorListener(this);
		mp.setOnCompletionListener(this);

		if (mp != null) {
			mp.setLooping(true);
			mp.setVolume(100, 100);
		}
	}

	/**
	 * Called after onCreate method in life cycle of Music Service. Calls
	 * playMusic() method which basically plays the appropriate song from now
	 * playing list.
	 **/
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		playMusic();
		return START_STICKY;
	}

	/**
	 * tempSongIndex gives the song which has been added recently by the user if
	 * this index is valid then currSongIndex is assigned this value otherwise
	 * currSong is continued
	 */

	void playMusic() {

		Log.i("Current song index", MyActivity.currentSongIndex + "");
		Log.i("Temp song index", MyActivity.tempSongIndex + "");

		if (MyActivity.nowPlayingList.size() > 0) {

			try {
				if (mp.isPlaying()) {
					mp.stop();
				}

				mp.reset();
				String song = MyActivity.nowPlayingPathsList
						.get(MyActivity.tempSongIndex);

				String[] songArray = song.split("/");
				song = songArray[0];
				for (int i = 1; i < songArray.length; i++) {
					songArray[i] = URLEncoder.encode(songArray[i], "UTF-8")
							.replace("+", "%20");
					song += "/" + songArray[i];
				}

				Log.i("Final song path", song);
				mp.setDataSource(song);
				mp.prepareAsync();
				mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.start();
						MyActivity.currentSongIndex = MyActivity.tempSongIndex;
					}
				});

				MyActivity.tbPlayPause.setChecked(true);
//				showNotification();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"There is nothing to play.", Toast.LENGTH_SHORT).show();
		}
	}

	private void showNotification() {
		final int mId = 10;

		RemoteViews notiView = new RemoteViews(this.getPackageName(),
				R.layout.notification);

		// for next song
		Intent active = new Intent(this, NotificationReceiver.class);
		active.putExtra("action", NEXT);
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
		notiView.setOnClickPendingIntent(R.id.nibPlayPause, actionPendingIntent);

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

	/**
	 * This method checks if mp is playing any song & if it is so, then it
	 * pauses & records the current position for later use.
	 */
	public void pauseMusic() {
		if (mp.isPlaying()) {
			mp.pause();
			length = mp.getCurrentPosition();
		}
	}

	/**
	 * This method checks if mp is playing & if it's not so, then it plays the
	 * current song starting from length saved in pauseMusic
	 */
	public void resumeMusic() {

		if (mp.isPlaying() == false) {
			mp.seekTo(length);
			mp.start();
		}
	}

	/**
	 * This method is called to stop playing songs & to tell media player to
	 * release all the objects it has acquired
	 */
	public void stopMusic() {
		mp.stop();
		mp.release();
		mp = null;
	}

	/**
	 * This method is called when Music Service is destroyed. It basically tells
	 * mp to free/release all the resources.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mp != null) {
			try {
				mp.stop();
				mp.release();
			} finally {
				mp = null;
				Log.i("MusicService destroyed", "true");
			}
		}
	}

	/**
	 * If tempSong throws any error then this method removes the song
	 * corresponding to the tempSongIndex. Then tempSongIndex is assigned the
	 * value of currentSongIndex & song which was playing before the latest
	 * click is played again.
	 */
	public boolean onError(MediaPlayer mp, int what, int extra) {

		Log.i("Music Service", "Size of now playing list is "
				+ MyActivity.nowPlayingList.size());

		MyActivity.tbPlayPause.setChecked(false);

		if (MyActivity.nowPlayingList.size() == 0) {
			return true;
		}

		Toast.makeText(
				this,
				MyActivity.nowPlayingList.get(MyActivity.tempSongIndex)
						+ " couldn't be loaded.", Toast.LENGTH_SHORT).show();

		MyActivity.nowPlayingPathsList.remove(MyActivity.tempSongIndex);
		MyActivity.nowPlayingList.remove(MyActivity.tempSongIndex);
		MyActivity.tempSongIndex = MyActivity.currentSongIndex;

		if (mp != null) {
			mp.stop();
			mp.reset();
			playMusic();
		}

		if (mp == null) {
			mp = new MediaPlayer();
			Log.i("MusicService reinitialized", "true");

			if (mp != null) {
				mp.setLooping(true);
				mp.setVolume(100, 100);
			}
		}
		return false;
	}

	/**
	 * When a song completes then this method defines which song has to be
	 * played next according to shouldShuffle variable
	 */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub

		mp.stop();
		MyActivity.tbPlayPause.setChecked(false);

		if (MyActivity.nowPlayingList.size() > 0) {
			mp.reset();
			if (!MyActivity.shouldShuffle) {
				MyActivity.tempSongIndex = (MyActivity.currentSongIndex + 1)
						% MyActivity.nowPlayingList.size();
				playMusic();
			} else {
				Random randGenerator = new Random();
				MyActivity.tempSongIndex = randGenerator
						.nextInt(MyActivity.nowPlayingList.size());
				playMusic();
			}
		}
	}
}