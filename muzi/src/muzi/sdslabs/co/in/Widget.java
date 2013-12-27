package muzi.sdslabs.co.in;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {

	// This class is called every time the app updates
	int awID;
	RemoteViews v;
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		v = new RemoteViews(context.getPackageName(), R.layout.notification);
		Log.i("WIDGET", "running");
		// v.setTextViewText(R.id.tvWidget, quote);
		appWidgetManager.updateAppWidget(awID, v);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		// Toast.makeText(context, "See you soon !!",
		// Toast.LENGTH_SHORT).show();
	}
}