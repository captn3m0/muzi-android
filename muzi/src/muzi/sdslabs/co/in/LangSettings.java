package muzi.sdslabs.co.in;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ToggleButton;


// To add language setting preferences
public class LangSettings extends MyActivity {

	SharedPreferences pref;
	int PRIVATE_MODE;
	Editor editor;
	ToggleButton tbEng, tbHindi, tbTamil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		setMyContentView(R.layout.lang_settings, LangSettings.this);
		super.onCreate(savedInstanceState);
		PRIVATE_MODE = 0;
		pref = getApplicationContext().getSharedPreferences("Lang Pref",
				PRIVATE_MODE);
		tbEng = (ToggleButton) findViewById(R.id.tbEnglish);
		tbHindi = (ToggleButton) findViewById(R.id.tbHindi);
		tbTamil = (ToggleButton) findViewById(R.id.tbTamil);
		tbEng.setChecked(pref.getBoolean("English", true));
		tbHindi.setChecked(pref.getBoolean("Hindi", true));
		tbTamil.setChecked(pref.getBoolean("Tamil", true));

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
