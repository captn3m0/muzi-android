package muzi.sdslabs.co.in;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Widget extends AppWidgetProvider {

	// This class is called every time the app updates
	public static int awID = 100;
	RemoteViews notiView;

	/**
	 * Order of calls: Enabled-Received-update-receive when u pick up a widget
	 * receive-delete when u delete a widget
	 */

	/**
	 * It gives errors when muzi is not actually running on screen. SO THIS HAS
	 * TO BE CHECKED.
	 */

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i("WIDGET", "running");
		// v.setTextViewText(R.id.tvWidget, quote);

		notiView = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		notiView.setTextViewText(R.id.ntvTitle, "HAHA HEHE HOHO");
		appWidgetManager.updateAppWidget(awID, notiView);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		notiView = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		notiView.setTextViewText(R.id.ntvTitle, "HAHA HEHE HOHO");
		appWidgetManager.updateAppWidget(awID, notiView);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i("MusicService: Widget", intent.getIntExtra("action", 0)
				+ "received");
		Toast.makeText(context, intent.getIntExtra("action", 0) + " received",
				Toast.LENGTH_SHORT).show();

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		notiView = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		notiView.setTextViewText(R.id.ntvTitle, "HAHA HEHE HOHO");
		appWidgetManager.updateAppWidget(awID, notiView);

	}
}