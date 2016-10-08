package com.dreammist.foodwheel;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class RestaurantWidget extends AppWidgetProvider {

    private static final String FIND_ACTION = "findRestaurantClicked";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        String widgetText = "McDowell's";
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.restaurant_widget);
        //views.setTextViewText(R.id.widget_restaurant_name, widgetText);




        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        RemoteViews remoteViews;
        ComponentName restaurantWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.restaurant_widget);
        restaurantWidget = new ComponentName(context,RestaurantWidget.class);

        Intent intent = new Intent(context, this.getClass());
        intent.setAction(FIND_ACTION);

        remoteViews.setOnClickPendingIntent(R.id.widget_button, PendingIntent.getBroadcast(context,0,intent,0));
        appWidgetManager.updateAppWidget(restaurantWidget,remoteViews);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (FIND_ACTION.equals(intent.getAction())) {

            Toast.makeText(context, "Hi.", Toast.LENGTH_SHORT).show();

//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//
//            RemoteViews remoteViews;
//            ComponentName watchWidget;
//
//            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//            watchWidget = new ComponentName(context, Widget.class);
//
//            remoteViews.setTextViewText(R.id.sync_button, "TESTING");
//
//            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
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

