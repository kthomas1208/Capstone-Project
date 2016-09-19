package com.dreammist.foodwheel.provider.restaurant;

import android.net.Uri;
import android.provider.BaseColumns;

import com.dreammist.foodwheel.provider.RestaurantProvider;
import com.dreammist.foodwheel.provider.restaurant.RestaurantColumns;

/**
 * A restaurant that serves food in a local area
 */
public class RestaurantColumns implements BaseColumns {
    public static final String TABLE_NAME = "restaurant";
    public static final Uri CONTENT_URI = Uri.parse(RestaurantProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Name of the restaurant
     */
    public static final String NAME = "name";

    /**
     * Textual ID that uniquely identifies a restaurant
     */
    public static final String PLACE_ID = "place_id";

    /**
     * Reference string for retrieving photos of the restaurant from Places API.
     */
    public static final String PHOTO_REF = "photo_ref";

    /**
     * Rating of the restaurant from 0.0 to 5.0
     */
    public static final String RATING = "rating";

    /**
     * The type of the restaurant as categorized by Google's Places API.
     */
    public static final String TYPE = "type";

    /**
     * True if restaurant is currently open
     */
    public static final String IS_OPEN = "is_open";

    /**
     * The price level of the restaurant, on a scale of 0 to 4.
     */
    public static final String PRICE_LEVEL = "price_level";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NAME,
            PLACE_ID,
            PHOTO_REF,
            RATING,
            TYPE,
            IS_OPEN,
            PRICE_LEVEL
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(NAME) || c.contains("." + NAME)) return true;
            if (c.equals(PLACE_ID) || c.contains("." + PLACE_ID)) return true;
            if (c.equals(PHOTO_REF) || c.contains("." + PHOTO_REF)) return true;
            if (c.equals(RATING) || c.contains("." + RATING)) return true;
            if (c.equals(TYPE) || c.contains("." + TYPE)) return true;
            if (c.equals(IS_OPEN) || c.contains("." + IS_OPEN)) return true;
            if (c.equals(PRICE_LEVEL) || c.contains("." + PRICE_LEVEL)) return true;
        }
        return false;
    }

}
