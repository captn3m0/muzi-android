package muzi.sdslabs.co.in;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

public class MyActivity extends SherlockActivity {

	ImageButton ibNext, ibPrevious, ibCurrentList, ibShuffle, ibRepeat;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutResourceId());
		FooterForPlayerControls footer = new FooterForPlayerControls(
				getContext());
		footer = (FooterForPlayerControls) findViewById(R.id.footer);
		footer.initFooter();
		ibNext = (ImageButton) findViewById(R.id.ibNext);
		ibPrevious = (ImageButton) findViewById(R.id.ibPrevious);
		ibCurrentList = (ImageButton) findViewById(R.id.ibCurrentList);
		ibShuffle = (ImageButton) findViewById(R.id.ibShuffle);
		ibRepeat = (ImageButton) findViewById(R.id.ibRepeat);

		getSupportActionBar().setHomeButtonEnabled(true);
	}

	protected Context getContext() {
		return null;
	};

	public void footerClickControls(View v) {
		Toast.makeText(getContext(), v.getId(), Toast.LENGTH_SHORT).show();
	}

	public void footerPlayToggle(View view) {

		if (view.getId() == R.id.tbPlayPause) {
			boolean on = ((ToggleButton) view).isChecked();

			if (on) {
				Toast.makeText(getContext(), "Play button clicked",
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getContext(), LocalService.class);
				startService(i);
			} else {
				stopService(new Intent(this, LocalService.class));
			}
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent mainIntent = new Intent(getApplicationContext(),
					HomeScreen.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			/*--------Declaring stop player for debugging----------*/

			stopService(new Intent(this, LocalService.class));
			// works absolutely fine

			/*-------------------------------------------------------*/
			startActivity(mainIntent);
		}
		return true;
	}

	protected int getLayoutResourceId() {
		return 0;
	}
}