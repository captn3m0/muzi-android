package muzi.sdslabs.co.in;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.GridView;

public class NowPlayingListFragment extends Fragment {

	boolean artist, album;

	GridView gv;
	private static final String TAG_TRACKID = "trackid";
	private static final String TAG_NAME = "name";
	private static final String TAG_ID = "id";

	String[] from = { TAG_ID, TAG_NAME };
	int[] to = { R.id.ivEvent, R.id.tvTitle };

	/* To detect itemClick using touch gestures */
	boolean isTouch;

	float startXPosition = -1;
	float startYPosition = -1;
	float endXPosition = -10;
	float endYPosition = -10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.simple_grid_view, container,
				false);
		gv = (GridView) rootView.findViewById(R.id.gv);

		GridAdapter adapter = new GridAdapter(getActivity(),
				MyActivity.nowPlayingSongList, R.layout.grid_cell, from, to);
		gv.setAdapter(adapter);

		gv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent me) {

				// Log.i("TopTrackFragment:onPostExecute():setOnTouchListener",
				// me.getAction() + "");
				//
				// Log.i("isTouch", isTouch + "");

				if (!isTouch) {
					startXPosition = -1;
					startYPosition = -1;
				}

				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					isTouch = true;
					startXPosition = me.getX();
					startYPosition = me.getY();
				} else if (me.getAction() == MotionEvent.ACTION_UP) {
					endXPosition = me.getX();
					endYPosition = me.getY();
					isTouch = false;
				}

				Log.i("startXPosition = ", startXPosition + "");
				Log.i("startYPosition = ", startYPosition + "");
				Log.i("endXPosition = ", endXPosition + "");
				Log.i("endYPosition = ", endYPosition + "");

				if ((Math.abs(startXPosition - endXPosition) <= 0.3)
						&& (Math.abs(startYPosition - endYPosition) <= 0.3)) {
					int position = gv.pointToPosition((int) startXPosition,
							(int) startYPosition);

					Log.i("result ------ onTouch", position + "\n");

					((MainActivity) getActivity()).playSong(
							MyActivity.nowPlayingSongList.get(position).get(
									MyActivity.TAG_NAME),
							MyActivity.nowPlayingSongList.get(position).get(
									MyActivity.TAG_PATH),
							MyActivity.nowPlayingSongList.get(position).get(
									MyActivity.TAG_IMAGEPATH), getActivity());

					startXPosition = -1;
					startYPosition = -1;
					endXPosition = -10;
					endYPosition = -10;
				}
				return false;
			}
		});

		return rootView;
	}
}