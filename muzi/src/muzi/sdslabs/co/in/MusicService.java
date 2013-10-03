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
	MediaPlayer mp;
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

		mp = new MediaPlayer();
		mp.setOnErrorListener(this);

		if (mp != null) {
			mp.setLooping(true);
			mp.setVolume(100, 100);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		playMusic();
		return START_STICKY;
	}

	void playMusic() {
		try {
			if (mp.isPlaying()) {
				mp.stop();
				mp.reset();
			}
			String song = MyActivity.nowPlayingPathsList.get(
					MyActivity.currentSongIndex).replaceAll(" ", "%20");

			mp.setDataSource(song);
			Log.i("Final song path", song);
			mp.prepareAsync();
			mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pauseMusic() {
		if (mp.isPlaying()) {
			mp.pause();
			length = mp.getCurrentPosition();
		}
	}

	public void resumeMusic() {
		if (mp.isPlaying() == false) {
			mp.seekTo(length);
			mp.start();
		}
	}

	public void stopMusic() {
		mp.stop();
		mp.release();
		mp = null;
	}

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

	public boolean onError(MediaPlayer mp, int what, int extra) {

		Toast.makeText(this,
				"Music player failed. Couldn't find the requested song.",
				Toast.LENGTH_SHORT).show();
		
		MyActivity.nowPlayingPathsList.remove(MyActivity.nowPlayingPathsList
				.size() - 1);
		MyActivity.nowPlayingList.remove(MyActivity.nowPlayingList.size() - 1);
		MyActivity.currentSongIndex = MyActivity.nowPlayingList.size() - 1;

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
}