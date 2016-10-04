package com.dreammist.foodwheel.provider.restaurant;

import com.dreammist.foodwheel.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A restaurant that serves food in a local area
 */
public interface RestaurantModel extends BaseModel {

    /**
     * Name of the restaurant
     * Can be {@code null}.
     */
    @Nullable
    String getName();

    /**
     * Textual ID that uniquely identifies a restaurant
     * Cannot be {@code null}.
     */
    @NonNull
    String getPlaceId();

    /**
     * Reference string for retrieving photos of the restaurant from Places API.
     * Can be {@code null}.
     */
    @Nullable
    String getPhotoRef();

    /**
     * Rating of the restaurant from 0.0 to 5.0
     * Can be {@code null}.
     */
    @Nullable
    Float getRating();

    /**
     * The type of the restaurant as categorized by Google's Places API.
     * Can be {@code null}.
     */
    @Nullable
    String getType();

    /**
     * True if restaurant is currently open
     * Can be {@code null}.
     */
    @Nullable
    Boolean getIsOpen();

    /**
     * The price level of the restaurant, on a scale of 0 to 4.
     * Can be {@code null}.
     */
    @Nullable
    Integer getPriceLevel();

    /**
     * The lat/lng coordinates of the restaurant delimited by a comma.
     * Can be {@code null}.
     */
    @Nullable
    String getLatLng();
}
