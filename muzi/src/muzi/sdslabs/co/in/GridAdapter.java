package muzi.sdslabs.co.in;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GridAdapter extends SimpleAdapter {
	private Context mContext;
	ArrayList<HashMap<String, String>> eventsList;
	HashMap<String, String> map = new HashMap<String, String>();
	GridAdapter adapter = this;

	Bitmap mPlaceHolderBitmap;
	private static LruCache<String, Bitmap> mMemoryCache;
	int reqHeight = 60, reqWidth = 60;
	private static final String TAG_NAME = "name";
	private static final String TAG_IMAGE = "id";

	public void loadBitmap(String img, ImageView imageView) {

		final Bitmap bitmap = getBitmapFromMemCache(img);

		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else if (cancelPotentialWork(img, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(
					mContext.getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(img);
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public static boolean cancelPotentialWork(String data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final String bitmapData = bitmapWorkerTask.data;
			if (!bitmapData.equals(data)) {
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

	static class AsyncDrawable extends BitmapDrawable {
		private WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

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

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private String data = "0";

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {

			Log.i("TileAdapter: BitmapWorkerTask: doInBackground", params[0]);

			data = params[0];

			return decodeSampledBitmapFromResource(mContext.getResources(),
					data, reqWidth, reqHeight);
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

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			String img, int reqWidth, int reqHeight) {

		String url = GlobalVariables.pic_root + img + ".jpg";

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		Bitmap bitmap = null;

		try {

			Log.i("TileAdapter: decodeSam***", url);
			bitmap = BitmapFactory.decodeStream(
					(InputStream) new URL(url).getContent(), null, options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(res,
					R.drawable.default_album_cover, options);
		}
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		try {
			bitmap = BitmapFactory.decodeStream(
					(InputStream) new URL(url).getContent(), null, options);
			addBitmapToMemoryCache(img, bitmap);
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

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public GridAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {

		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mContext = context;
		this.eventsList = (ArrayList<HashMap<String, String>>) data;

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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View grid = super.getView(position, convertView, parent);
		if (grid == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			grid = mInflater.inflate(R.layout.grid_cell, parent, false);
			grid.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "Yo", Toast.LENGTH_LONG).show();
				}
			});
		}

		TextView tv = (TextView) grid.findViewById(R.id.tvTitle);
		ImageView iv = (ImageView) grid.findViewById(R.id.ivEvent);
		// final ImageButton ibOpt = (ImageButton) grid
		// .findViewById(R.id.ibGridPlay);

		Log.i("TileAdapter: getView", eventsList.get(position).get(TAG_NAME));
		// ibOpt.setOnClickListener(new OnClickListener() {

		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// int[] posXY = new int[2];
		// ibOpt.getLocationOnScreen(posXY);
		// // show_Dialog(mContext, posXY[0], posXY[1]);
		// }
		// });

		tv.setText(eventsList.get(position).get(TAG_NAME) + "");// .get(TAG_NAME));
		// iv.setImageResource(R.drawable.default_album_cover);

		String img = eventsList.get(position).get(TAG_IMAGE);

		Log.i("images", img);
		loadBitmap(img, iv);
		return grid;
	}
}