package muzi.sdslabs.co.in;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FooterForPlayerControls extends RelativeLayout {
	public static final String TAG = FooterForPlayerControls.class
			.getSimpleName();

	ImageButton ibPlay, ibPause;
	Context c;

	public FooterForPlayerControls(Context context) {
		super(context);
		c = context;
	}

	public FooterForPlayerControls(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
	}

	public FooterForPlayerControls(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		c = context;
	}

	public void initFooter() {
		inflateFooter();
	}

	private void inflateFooter() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.footer_for_player_controls, this);
	}

}