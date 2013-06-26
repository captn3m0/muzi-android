package muzi.sdslabs.co.in;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class MyActivity extends SherlockActivity {

	ImageButton ibPlay, ibPause;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutResourceId());
		FooterForPlayerControls footer = new FooterForPlayerControls(
				getContext());
		footer = (FooterForPlayerControls) findViewById(R.id.footer);
		footer.initFooter();

		ibPlay = (ImageButton) findViewById(R.id.ibPlay);
		ibPause = (ImageButton) findViewById(R.id.ibPause);
	}

	protected Context getContext() {
		return null;
	};

	public void footerClickControls(View v) {
		if (v.getId() == R.id.ibPlay) {
			Toast.makeText(getContext(), "Play button clicked",
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getContext(), LocalService.class);
			startService(i);
		}
	}

	protected int getLayoutResourceId() {
		return 0;
	}
}
