package com.dreammist.foodwheel.provider.restaurant;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dreammist.foodwheel.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code restaurant} table.
 */
public class RestaurantCursor extends AbstractCursor implements RestaurantModel {
    public RestaurantCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(RestaurantColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Name of the restaurant
     * Can be {@code null}.
     */
    @Nullable
    public String getName() {
        String res = getStringOrNull(RestaurantColumns.NAME);
        return res;
    }

    /**
     * Textual ID that uniquely identifies a restaurant
     * Can be {@code null}.
     */
    @Nullable
    public String getPlaceId() {
        String res = getStringOrNull(RestaurantColumns.PLACE_ID);
        return res;
    }

    /**
     * Reference string for retrieving photos of the restaurant from Places API.
     * Can be {@code null}.
     */
    @Nullable
    public String getPhotoRef() {
        String res = getStringOrNull(RestaurantColumns.PHOTO_REF);
        return res;
    }

    /**
     * Rating of the restaurant from 0.0 to 5.0
     * Can be {@code null}.
     */
    @Nullable
    public Float getRating() {
        Float res = getFloatOrNull(RestaurantColumns.RATING);
        return res;
    }

    /**
     * The type of the restaurant as categorized by Google's Places API.
     * Can be {@code null}.
     */
    @Nullable
    public String getType() {
        String res = getStringOrNull(RestaurantColumns.TYPE);
        return res;
    }

    /**
     * True if restaurant is currently open
     * Can be {@code null}.
     */
    @Nullable
    public Boolean getIsOpen() {
        Boolean res = getBooleanOrNull(RestaurantColumns.IS_OPEN);
        return res;
    }

    /**
     * The price level of the restaurant, on a scale of 0 to 4.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getPriceLevel() {
        Integer res = getIntegerOrNull(RestaurantColumns.PRICE_LEVEL);
        return res;
    }
}
