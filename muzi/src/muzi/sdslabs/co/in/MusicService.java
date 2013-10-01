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

		// mp = new MediaPlayer();
		//
		// Uri uri = Uri.parse(GlobalVariables.music_root
		// + "Adele___Set_Fire_To_The_Rain_Adele.mp3");
		//
		// Log.i("uri in music service", uri.toString());

		mp = new MediaPlayer();
		try {
			String song = GlobalVariables.music_root
					+ "English/John Williams/1987 - Empire of The Sun/01 - Suo Gan.mp3";
			song = song.replaceAll(" ", "%20");
			mp.setDataSource(song);
			Log.i("song path", song);
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
		
//		mp = new MediaPlayer();
//		try {
//			mp.setDataSource(GlobalVariables.music_root
//					+ "English/John Williams/1987 - Empire of The Sun/01 - Suo Gan.mp3");
//			mp.prepare();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			Log.i("Song won't play", "NO");
//			e.printStackTrace();
//		}
//		mp.setOnErrorListener(this);

		if (mp != null) {
			mp.setLooping(true);
			mp.setVolume(100, 100);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mp.start();
		return START_STICKY;
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
			}
		}
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {

		Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
		if (mp != null) {
			try {
				mp.stop();
				mp.release();
			} finally {
				mp = null;
			}
		}
		return false;
	}
}