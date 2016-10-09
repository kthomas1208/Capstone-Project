package com.dreammist.foodwheel;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dreammist.foodwheel.provider.restaurant.RestaurantCursor;
import com.dreammist.foodwheel.provider.restaurant.RestaurantSelection;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class RestaurantWidget extends AppWidgetProvider {

    private static final String FIND_ACTION = "findRestaurantClicked";
    private static final String CLICK_RESTAURANT_ACTION = "restaurantNameClicked";

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

        // Set action for find button
        remoteViews.setOnClickPendingIntent(R.id.widget_button, getPendingSelfIntent(context,FIND_ACTION));

        // Set intent for pressing restaurant name
        //remoteViews.setOnClickPendingIntent(R.id.widget_restaurant_name, getPendingSelfIntent(context,CLICK_RESTAURANT_ACTION));
        Intent detailIntent = new Intent(context, MainActivity.class);
        // TODO: 10/9/16 Figure out how to get the CORRECT placeId data from the DB
//        RestaurantSelection where = new RestaurantSelection();
//        RestaurantCursor restaurant = where.query(context.getContentResolver());
//        restaurant.moveToNext();
//        detailIntent.putExtra(MainActivity.PLACE_ID, restaurant.getPlaceId());

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,detailIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.widget_restaurant_name, pendingIntent);


        appWidgetManager.updateAppWidget(restaurantWidget,remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (FIND_ACTION.equals(intent.getAction())) {
            // Find button was clicked, so get a random restaurant from the DB
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName restaurantWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.restaurant_widget);
            restaurantWidget = new ComponentName(context, RestaurantWidget.class);

            // Get a random restaurant from the DB
            RestaurantSelection where = new RestaurantSelection();
            where.photoRefNot("null");
            RestaurantCursor restaurant = where.query(context.getContentResolver());
            if(restaurant.getCount()>0) {
                Random rand = new Random();
                restaurant.moveToPosition(rand.nextInt(restaurant.getCount()));

                // Set the name of the restaurant for display
                remoteViews.setTextViewText(R.id.widget_restaurant_name, restaurant.getName());

                appWidgetManager.updateAppWidget(restaurantWidget, remoteViews);
            }
            else {
                Toast.makeText(context, context.getString(R.string.widget_warning),
                        Toast.LENGTH_SHORT).show();
            }
            restaurant.close();
        }
        if (CLICK_RESTAURANT_ACTION.equals(intent.getAction())) {
//            // Launch the Detail Activity with the restaurant data
//            Intent detailIntent = new Intent(context, DetailActivity.class);
//            RestaurantSelection where = new RestaurantSelection();
//            RestaurantCursor restaurant = where.query(context.getContentResolver());
//            if(restaurant.moveToNext()) {
//
//                detailIntent.putExtra(MainActivity.PLACE_ID, restaurant.getPlaceId());
//                if (!restaurant.getPhotoRef().isEmpty())
//                    detailIntent.putExtra(MainActivity.PHOTO_URI, restaurant.getPhotoRef());
//
//                context.startActivity(detailIntent);
//            }
//            else {
//                Toast.makeText(context, context.getString(R.string.widget_warning),
//                        Toast.LENGTH_SHORT).show();
//            }
        }

    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.restaurant_widget);
        ComponentName restaurantWidget = new ComponentName(context, RestaurantWidget.class);

        // Set the initial text on the widget
        CharSequence widgetText = context.getString(R.string.widget_init_text);
        views.setTextViewText(R.id.widget_restaurant_name, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(restaurantWidget, views);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

