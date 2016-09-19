package com.dreammist.foodwheel.provider.restaurant;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dreammist.foodwheel.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code restaurant} table.
 */
public class RestaurantContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return RestaurantColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable RestaurantSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable RestaurantSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Name of the restaurant
     */
    public RestaurantContentValues putName(@Nullable String value) {
        mContentValues.put(RestaurantColumns.NAME, value);
        return this;
    }

    public RestaurantContentValues putNameNull() {
        mContentValues.putNull(RestaurantColumns.NAME);
        return this;
    }

    /**
     * Textual ID that uniquely identifies a restaurant
     */
    public RestaurantContentValues putPlaceId(@Nullable String value) {
        mContentValues.put(RestaurantColumns.PLACE_ID, value);
        return this;
    }

    public RestaurantContentValues putPlaceIdNull() {
        mContentValues.putNull(RestaurantColumns.PLACE_ID);
        return this;
    }

    /**
     * Reference string for retrieving photos of the restaurant from Places API.
     */
    public RestaurantContentValues putPhotoRef(@Nullable String value) {
        mContentValues.put(RestaurantColumns.PHOTO_REF, value);
        return this;
    }

    public RestaurantContentValues putPhotoRefNull() {
        mContentValues.putNull(RestaurantColumns.PHOTO_REF);
        return this;
    }

    /**
     * Rating of the restaurant from 0.0 to 5.0
     */
    public RestaurantContentValues putRating(@Nullable Float value) {
        mContentValues.put(RestaurantColumns.RATING, value);
        return this;
    }

    public RestaurantContentValues putRatingNull() {
        mContentValues.putNull(RestaurantColumns.RATING);
        return this;
    }

    /**
     * The type of the restaurant as categorized by Google's Places API.
     */
    public RestaurantContentValues putType(@Nullable String value) {
        mContentValues.put(RestaurantColumns.TYPE, value);
        return this;
    }

    public RestaurantContentValues putTypeNull() {
        mContentValues.putNull(RestaurantColumns.TYPE);
        return this;
    }

    /**
     * True if restaurant is currently open
     */
    public RestaurantContentValues putIsOpen(@Nullable Boolean value) {
        mContentValues.put(RestaurantColumns.IS_OPEN, value);
        return this;
    }

    public RestaurantContentValues putIsOpenNull() {
        mContentValues.putNull(RestaurantColumns.IS_OPEN);
        return this;
    }

    /**
     * The price level of the restaurant, on a scale of 0 to 4.
     */
    public RestaurantContentValues putPriceLevel(@Nullable Integer value) {
        mContentValues.put(RestaurantColumns.PRICE_LEVEL, value);
        return this;
    }

    public RestaurantContentValues putPriceLevelNull() {
        mContentValues.putNull(RestaurantColumns.PRICE_LEVEL);
        return this;
    }
}
