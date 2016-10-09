package com.dreammist.foodwheel;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dreammist.foodwheel.provider.restaurant.RestaurantCursor;
import com.dreammist.foodwheel.provider.restaurant.RestaurantSelection;

import java.util.Random;

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

        // Find button was clicked, so get a random restaurant from the DB
        if (FIND_ACTION.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName restaurantWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.restaurant_widget);
            restaurantWidget = new ComponentName(context, RestaurantWidget.class);

            // Get a random restaurant from the DB
            RestaurantSelection where = new RestaurantSelection();
            where.photoRefNot("null");
            RestaurantCursor restaurant = where.query(context.getContentResolver());
            Random rand = new Random();
            restaurant.moveToPosition(rand.nextInt(restaurant.getCount()));

            // Set the name of the restaurant for display
            remoteViews.setTextViewText(R.id.widget_restaurant_name, restaurant.getName());

            appWidgetManager.updateAppWidget(restaurantWidget, remoteViews);
            restaurant.close();
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

