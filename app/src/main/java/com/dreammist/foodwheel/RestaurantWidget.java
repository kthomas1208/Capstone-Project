package com.dreammist.foodwheel;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName restaurantWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.restaurant_widget);
        restaurantWidget = new ComponentName(context,RestaurantWidget.class);

        // Set action for find button
        remoteViews.setOnClickPendingIntent(R.id.widget_button, getPendingSelfIntent(context,FIND_ACTION));

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

                // Launch a Detail Activity when you click the restaurant name
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra(MainActivity.PLACE_ID, restaurant.getPlaceId());
                detailIntent.putExtra(MainActivity.PHOTO_URI,
                        createPhotoURI(restaurant.getPhotoRef()));

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, detailIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.widget_restaurant_name, pendingIntent);

                appWidgetManager.updateAppWidget(restaurantWidget, remoteViews);
            }
            else {
                Toast.makeText(context, context.getString(R.string.widget_warning), Toast.LENGTH_SHORT).show();
            }
            restaurant.close();
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    /**
     * Creates a Photo URI for a image using a photo reference from the Places API
     * @param photoRef photo reference string from Places API for a specific location
     * @return a fully qualified URI that will return an image for a specific location
     */
    public String createPhotoURI(String photoRef) {
        // Get a photo of the restaurant location
        final String PHOTOS_BASE_URL =
                "https://maps.googleapis.com/maps/api/place/photo?";
        final String MAXWIDTH_PARAM = "maxwidth";
        final String PHOTOREF_PARAM = "photoreference";
        final String KEY_PARAM = "key";

        String API_KEY = BuildConfig.MyApiKey;

        if (!photoRef.equals("null")) {
            Uri builtUri = Uri.parse(PHOTOS_BASE_URL).buildUpon()
                    .appendQueryParameter(MAXWIDTH_PARAM, "500")
                    .appendQueryParameter(PHOTOREF_PARAM, photoRef)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .build();

            return builtUri.toString();
        }
        return "";
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

