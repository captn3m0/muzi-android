package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Home screen activity where all the basic elements like top tracks, albums etc
 * are shown
 */
public class MainActivity extends MyActivity {

	/* Navigation Drawer */
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	CharSequence mTitle, mDrawerTitle;

	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.main_activity, this);
		super.onCreate(savedInstanceState);

		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerTitle = mTitle = getSupportActionBar().getTitle();
		listImages = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < listItems.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("image", listItems[i] + "");
			listImages.add(map);
		}

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new SimpleAdapter(this, listImages,
				R.layout.drawer_list_item, from, to));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.app_name, /* "open drawer" description for accessibility */
		R.string.abc_action_mode_done /*
									 * "close drawer" description for
									 * accessibility
									 */
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// This will decide which item to show when displayed first time on the
		// screen
		if (savedInstanceState == null) {
			selectItem(0);
			getSupportActionBar().setLogo(R.drawable.menu);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;

		Log.i("position", position + "");

		// Learnt it the hard way that position starts from 1 here
		// May be it's because of header otherwise the count starts from 0 in
		// // general
		// if (position == 1) {
		// fragment = new Artist_or_Album_Fragment();
		// Bundle args = new Bundle();
		// args.putString("list_type", "band");
		// fragment.setArguments(args);
		// } else
		if (position == 1 || position == 0) {
			fragment = new TopTrackFragment();
		} else if (position == 2) {
			fragment = new TopAlbumsFragment();
		} else if (position == 3) {
			fragment = new NowPlayingListFragment();
		} else if (position == 4) {
			fragment = new UserSettingsFragment();
		}
		// } else if (position == 5) {
		// String email[] = { "contact+muzi@sdslabs.co.in" };
		// Intent EmailIntent = new Intent(android.content.Intent.ACTION_SEND);
		// EmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, email);
		// EmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
		// "Feedback for Muzi");
		// EmailIntent.setType("plain/Text");
		// EmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		// startActivity(EmailIntent);
		// }

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(titles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Log.e("Search selected", "hehe");
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Code to add slider now playing screen **/
	/*
	 * 
	 * private static final String TAG = "DemoActivity"; public static final
	 * String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	 * 
	 * 
	 * 
	 * 
	 * @Override protected void onSaveInstanceState(Bundle outState) {
	 * super.onSaveInstanceState(outState);
	 * outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN,
	 * !getSupportActionBar().isShowing()); }
	 * 
	 * 
	 * 
	 * 
	 * SlidingUpPanelLayout layout = (SlidingUpPanelLayout)
	 * findViewById(R.id.sliding_layout);
	 * layout.setShadowDrawable(getResources().getDrawable(
	 * R.drawable.above_shadow)); layout.setAnchorPoint(0.3f);
	 * layout.setPanelSlideListener(new PanelSlideListener() {
	 * 
	 * @Override public void onPanelSlide(View panel, float slideOffset) {
	 * Log.i(TAG, "onPanelSlide, offset " + slideOffset); if (slideOffset < 0.2)
	 * { if (getSupportActionBar().isShowing()) { getSupportActionBar().hide();
	 * } } else { if (!getSupportActionBar().isShowing()) {
	 * getSupportActionBar().show(); } } }
	 * 
	 * @Override public void onPanelExpanded(View panel) { Log.i(TAG,
	 * "onPanelExpanded");
	 * 
	 * }
	 * 
	 * @Override public void onPanelCollapsed(View panel) { Log.i(TAG,
	 * "onPanelCollapsed");
	 * 
	 * }
	 * 
	 * @Override public void onPanelAnchored(View panel) { Log.i(TAG,
	 * "onPanelAnchored");
	 * 
	 * } });
	 * 
	 * boolean actionBarHidden = savedInstanceState != null ? savedInstanceState
	 * .getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false) : false; if
	 * (actionBarHidden) { getSupportActionBar().hide(); }
	 */

}