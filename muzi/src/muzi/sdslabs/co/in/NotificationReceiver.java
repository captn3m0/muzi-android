package muzi.sdslabs.co.in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i("MusicService: DownloadReceiver", intent.getIntExtra("action", 0)
				+ "received");
		Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();

		ResultReceiver receiver = (ResultReceiver) intent
				.getParcelableExtra("RECEIVER");

		Bundle resultData = new Bundle();
		resultData.putInt("action", intent.getIntExtra("action", 0));
		receiver.send(MyActivity.NOTIFICATION_RECEIVER, resultData);

		// if (resultCode == MusicService.PLAY_PAUSE) {
		// // MyActivity.tempSongIndex = (MyActivity.currentSongIndex + 1)
		// // % MyActivity.nowPlayingPathsList.size();
		// // Intent i = new Intent(this, MusicService.class);
		// // startService(i);
		// }
	}
}