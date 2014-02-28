package muzi.sdslabs.co.in;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends MyActivity {

	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.main_activity, this);
		super.onCreate(savedInstanceState);
	}

	void playSong(String songName, String songPath, Context context) {
		Intent i = new Intent(context, MusicService.class);

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

		i.putExtra("RECEIVER", serviceActionReceiver);
		startService(i);
	}
}