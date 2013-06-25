package muzi.sdslabs.co.in;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class LangSettings extends Activity {

	SharedPreferences pref;
	int PRIVATE_MODE;
	Editor editor;
	ToggleButton tbEng, tbHindi, tbTamil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lang_settings);
		PRIVATE_MODE = 0;
		pref = getApplicationContext().getSharedPreferences("Lang Pref",
				PRIVATE_MODE);
		tbEng = (ToggleButton)findViewById(R.id.tbEnglish);
		tbHindi = (ToggleButton)findViewById(R.id.tbHindi);
		tbTamil = (ToggleButton)findViewById(R.id.tbTamil);
		tbEng.setChecked(pref.getBoolean("English", true));
		tbHindi.setChecked(pref.getBoolean("Hindi", true));
		tbTamil.setChecked(pref.getBoolean("Tamil", true));
		
	}

	public void onToggleClicked(View view) {
		// Is the toggle on?
		if (view.getId() == R.id.tbEnglish) {
			boolean on = ((ToggleButton) view).isChecked();

			if (on) {
				Toast.makeText(getApplicationContext(), "English on",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "English off",
						Toast.LENGTH_SHORT).show();
			}
		}
		if (view.getId() == R.id.tbHindi) {
			boolean on = ((ToggleButton) view).isChecked();

			if (on) {
				Toast.makeText(getApplicationContext(), "Hindi on",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Hindi off",
						Toast.LENGTH_SHORT).show();
			}
		}
		if (view.getId() == R.id.tbTamil) {
			boolean on = ((ToggleButton) view).isChecked();

			if (on) {
				Toast.makeText(getApplicationContext(), "Tamil on",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Tamil off",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		editor = pref.edit();
		editor.putBoolean("English", tbEng.isChecked());
		editor.putBoolean("Hindi", tbHindi.isChecked());
		editor.putBoolean("Tamil", tbTamil.isChecked());
		editor.commit();
		Log.i("Eng", pref.getBoolean("English", true) + "");
		Log.i("Hindi", pref.getBoolean("Hindi", true) + "");
		Log.i("Tamil", pref.getBoolean("Tamil", true) + "");
		LangSettings.this.finish();
	}
}
