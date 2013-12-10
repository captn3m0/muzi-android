package muzi.sdslabs.co.in;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AlbumsFromArtists extends MyActivity implements
		OnItemClickListener {

	Bitmap mPlaceHolderBitmap;
	// url to make request
	// remember that its album & not albums
	private static String root;

	private static LruCache<String, Bitmap> mMemoryCache;
	private ProgressDialog pDialog;

	// JSON keys
	private static final String TAG_NAME = "name";
	private static final String TAG_ID = "id";

	JSONArray FilteredJSONArray = null;
	ListView lv;
	ArrayList<HashMap<String, String>> albumList;// , albumIdList;
	JSONObject jsonObject;
	LoadSongs task;

	int reqHeight = 60, reqWidth = 60;

	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			return decodeSampledBitmapFromResource(getResources(), data,
					reqWidth, reqHeight);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null
						&& bitmap != null) {
					imageView.setImageBitmap(bitmap);
				} else {
					imageView.setImageResource(R.drawable.default_album_cover);
				}
			}
		}
	}

	/**
	 * Doubt: Should the default drawable be cached at different places for
	 * different ids
	 */

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int id,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		Bitmap bitmap = null;

		try {
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					GlobalVariables.pic_root + id + ".jpg").getContent(), null,
					options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (bitmap == null) {
			BitmapFactory.decodeResource(res, R.drawable.default_album_cover,
					options);
		}
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		try {
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					GlobalVariables.pic_root + id + ".jpg").getContent(), null,
					options);
			addBitmapToMemoryCache(String.valueOf(id), bitmap);
			return bitmap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return BitmapFactory.decodeResource(res,
					R.drawable.default_album_cover, options);
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public void loadBitmap(int id, ImageView imageView) {

		final String imageKey = String.valueOf(id);

		final Bitmap bitmap = getBitmapFromMemCache(imageKey);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else if (cancelPotentialWork(id, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(
					getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(id);
		}
	}

	public static boolean cancelPotentialWork(int data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final int bitmapData = bitmapWorkerTask.data;
			if (bitmapData != data) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	// Keys used in Hashmap
	String[] from = { TAG_ID, TAG_NAME };

	// Ids of views in listview_layout
	int[] to = { R.id.iv_in_li, R.id.tv_in_li };

	public class MyAdapter extends SimpleAdapter {

		Context mContext;
		HashMap<String, String> map = new HashMap<String, String>();

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {

			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View row = super.getView(position, convertView, parent);
			if (row == null) {
				LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = mInflater.inflate(R.layout.li_with_one_iv_and_one_tv,
						parent, false);
			}

			TextView tv = (TextView) row.findViewById(R.id.tv_in_li);
			ImageView iv = (ImageView) row.findViewById(R.id.iv_in_li);

			Log.i("Albums From Artists: MyAdapter: getView",
					albumList.get(position).get(TAG_NAME));

			// TextView rw2 = (TextView)findViewById(R.id.row2);
			tv.setText(albumList.get(position).get(TAG_NAME) + "");// .get(TAG_NAME));
			loadBitmap(Integer.parseInt(albumList.get(position).get(TAG_ID)),
					iv);
			return row;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setMyContentView(R.layout.simple_list_view_with_footer,
				AlbumsFromArtists.this);
		super.onCreate(savedInstanceState);

		lv = (ListView) findViewById(R.id.lvSimple);
		lv.getRootView().setBackgroundColor(
				getResources().getColor(R.color.Black));
		lv.setCacheColorHint(Color.TRANSPARENT);
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(AlbumsFromArtists.this);

		albumList = new ArrayList<HashMap<String, String>>();

		String type = getIntent().getStringExtra("search_type2");
		String album_id = getIntent().getStringExtra("search_id2");

		if (type != null) {
			root = GlobalVariables.api_root + type + "/albums.php?id="
					+ album_id;
			Log.i("request url", root);
			this.setTitle(getIntent().getStringExtra("search_title2"));
		} else {
			AlbumsFromArtists.this.finish();
			Toast.makeText(AlbumsFromArtists.this,
					"Sorry, the request couldn't be executed",
					Toast.LENGTH_LONG).show();
		}

		task = new LoadSongs();
		task.execute();

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
	}

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	class LoadSongs extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AlbumsFromArtists.this);
			pDialog.setMessage("Loading content. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					task.cancel(true);
				}
			});
			pDialog.show();
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			AlbumsFromArtists.this.finish();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			// Creating JSON Parser instance
			// getting JSON string from URL
			InternetData test = new InternetData();
			try {

				jsonObject = new JSONObject(test.getInternetData(root));
				FilteredJSONArray = new JSONArray(
						jsonObject.getString("albums"));

				Log.i("Array", FilteredJSONArray.toString());

				if (FilteredJSONArray != null) {
					// Getting Array of FilteredJSONArray
					// looping through All FilteredJSONArray
					Log.i("Working", "good");
					// require it later
					for (int i = 0; i < FilteredJSONArray.length(); i++) {

						if (isCancelled()) {
							return null;
						}

						jsonObject = FilteredJSONArray.getJSONObject(i);

						// albumList.add(jsonObject.getString(TAG_NAME));

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_ID, jsonObject.getString(TAG_ID));
						map.put(TAG_NAME, jsonObject.getString(TAG_NAME));

						albumList.add(map);
						// albumIdList.add(jsonObject.getString(TAG_ID));
						// loadBitmap(jsonObject.getString(TAG_ID), ImageView
						// imageView);
						//
						// Bitmap bitmap = BitmapFactory
						// .decodeStream((InputStream) new URL(
						// GlobalVariables.pic_root
						// + jsonObject.getString(TAG_ID)
						// + ".jpg").getContent());

						// ivAlbumCover.setImageBitmap(bitmap);
						// songsPathList.add(jsonObject.getString(TAG_FILE));
						// creating new HashMap

						// Log.i((i + 1) + "", jsonObject.getString(TAG_FILE));
					}
					// if php query doesn't give sorted results comment out
					// the
				}

			} catch (Exception e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			/**
			 * Updating parsed JSON data into ListView
			 * */
			if (albumList.size() == 0) {
				lv.setAdapter(null);
			} else {

				Log.i("Size of list", albumList.size() + "");
				MyAdapter adapter = new MyAdapter(AlbumsFromArtists.this,
						albumList, R.layout.li_with_one_iv_and_one_tv, from, to);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(AlbumsFromArtists.this);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position, long id) {
		// TODO Auto-generated method stub

		Intent i = new Intent(AlbumsFromArtists.this, SongsFromAlbums.class);

		i.putExtra("search_type1", "album");
		i.putExtra("search_id1", albumList.get(position).get(TAG_ID));
		i.putExtra("search_title1", albumList.get(position).get(TAG_NAME));
		startActivity(i);

	}
}