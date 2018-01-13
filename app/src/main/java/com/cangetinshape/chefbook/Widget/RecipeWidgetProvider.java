package com.cangetinshape.chefbook.Widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.cangetinshape.chefbook.MainActivity;
import com.cangetinshape.chefbook.R;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects
            // to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            Intent intent = new Intent(context, GridWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // Add a random integer to stop the Intent being ignored. This is
            // necessary for some API levels due to intent caching
            intent.putExtra("Random", Math.random() * 1000);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            rv.setRemoteAdapter(R.id.widget_grid_view, intent);
            rv.setEmptyView(R.id.widget_grid_view, R.id.empty_view);

            Intent clickItent = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickItent)
                    .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_grid_view, clickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_grid_view);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, RecipeWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if(action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, RecipeWidgetProvider.class);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.appwidget_image);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(cn);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

