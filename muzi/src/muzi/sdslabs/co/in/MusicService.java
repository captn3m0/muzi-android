package muzi.sdslabs.co.in;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service implements
		MediaPlayer.OnErrorListener {

	public final IBinder mBinder = new ServiceBinder();
	MediaPlayer mPlayer;
	private int length = 0;

	public MusicService() {
	}

	public class ServiceBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// mPlayer = new MediaPlayer();
		//
		// Uri uri = Uri.parse(GlobalVariables.music_root
		// + "Adele___Set_Fire_To_The_Rain_Adele.mp3");
		//
		// Log.i("uri in music service", uri.toString());

		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(GlobalVariables.music_root
					+ "Adele___Set_Fire_To_The_Rain_Adele.mp3");
			mPlayer.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i("Song won't play", "NO");
			e.printStackTrace();
		}
		mPlayer.setOnErrorListener(this);

		if (mPlayer != null) {
			mPlayer.setLooping(true);
			mPlayer.setVolume(100, 100);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mPlayer.start();
		return START_STICKY;
	}

	public void pauseMusic() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
			length = mPlayer.getCurrentPosition();
		}
	}

	public void resumeMusic() {
		if (mPlayer.isPlaying() == false) {
			mPlayer.seekTo(length);
			mPlayer.start();
		}
	}

	public void stopMusic() {
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPlayer != null) {
			try {
				mPlayer.stop();
				mPlayer.release();
			} finally {
				mPlayer = null;
			}
		}
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {

		Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
		if (mPlayer != null) {
			try {
				mPlayer.stop();
				mPlayer.release();
			} finally {
				mPlayer = null;
			}
		}
		return false;
	}
}