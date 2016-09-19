package com.dreammist.foodwheel.provider.restaurant;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.dreammist.foodwheel.provider.base.AbstractSelection;

/**
 * Selection for the {@code restaurant} table.
 */
public class RestaurantSelection extends AbstractSelection<RestaurantSelection> {
    @Override
    protected Uri baseUri() {
        return RestaurantColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code RestaurantCursor} object, which is positioned before the first entry, or null.
     */
    public RestaurantCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new RestaurantCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public RestaurantCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code RestaurantCursor} object, which is positioned before the first entry, or null.
     */
    public RestaurantCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new RestaurantCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public RestaurantCursor query(Context context) {
        return query(context, null);
    }


    public RestaurantSelection id(long... value) {
        addEquals("restaurant." + RestaurantColumns._ID, toObjectArray(value));
        return this;
    }

    public RestaurantSelection idNot(long... value) {
        addNotEquals("restaurant." + RestaurantColumns._ID, toObjectArray(value));
        return this;
    }

    public RestaurantSelection orderById(boolean desc) {
        orderBy("restaurant." + RestaurantColumns._ID, desc);
        return this;
    }

    public RestaurantSelection orderById() {
        return orderById(false);
    }

    public RestaurantSelection name(String... value) {
        addEquals(RestaurantColumns.NAME, value);
        return this;
    }

    public RestaurantSelection nameNot(String... value) {
        addNotEquals(RestaurantColumns.NAME, value);
        return this;
    }

    public RestaurantSelection nameLike(String... value) {
        addLike(RestaurantColumns.NAME, value);
        return this;
    }

    public RestaurantSelection nameContains(String... value) {
        addContains(RestaurantColumns.NAME, value);
        return this;
    }

    public RestaurantSelection nameStartsWith(String... value) {
        addStartsWith(RestaurantColumns.NAME, value);
        return this;
    }

    public RestaurantSelection nameEndsWith(String... value) {
        addEndsWith(RestaurantColumns.NAME, value);
        return this;
    }

    public RestaurantSelection orderByName(boolean desc) {
        orderBy(RestaurantColumns.NAME, desc);
        return this;
    }

    public RestaurantSelection orderByName() {
        orderBy(RestaurantColumns.NAME, false);
        return this;
    }

    public RestaurantSelection placeId(String... value) {
        addEquals(RestaurantColumns.PLACE_ID, value);
        return this;
    }

    public RestaurantSelection placeIdNot(String... value) {
        addNotEquals(RestaurantColumns.PLACE_ID, value);
        return this;
    }

    public RestaurantSelection placeIdLike(String... value) {
        addLike(RestaurantColumns.PLACE_ID, value);
        return this;
    }

    public RestaurantSelection placeIdContains(String... value) {
        addContains(RestaurantColumns.PLACE_ID, value);
        return this;
    }

    public RestaurantSelection placeIdStartsWith(String... value) {
        addStartsWith(RestaurantColumns.PLACE_ID, value);
        return this;
    }

    public RestaurantSelection placeIdEndsWith(String... value) {
        addEndsWith(RestaurantColumns.PLACE_ID, value);
        return this;
    }

    public RestaurantSelection orderByPlaceId(boolean desc) {
        orderBy(RestaurantColumns.PLACE_ID, desc);
        return this;
    }

    public RestaurantSelection orderByPlaceId() {
        orderBy(RestaurantColumns.PLACE_ID, false);
        return this;
    }

    public RestaurantSelection photoRef(String... value) {
        addEquals(RestaurantColumns.PHOTO_REF, value);
        return this;
    }

    public RestaurantSelection photoRefNot(String... value) {
        addNotEquals(RestaurantColumns.PHOTO_REF, value);
        return this;
    }

    public RestaurantSelection photoRefLike(String... value) {
        addLike(RestaurantColumns.PHOTO_REF, value);
        return this;
    }

    public RestaurantSelection photoRefContains(String... value) {
        addContains(RestaurantColumns.PHOTO_REF, value);
        return this;
    }

    public RestaurantSelection photoRefStartsWith(String... value) {
        addStartsWith(RestaurantColumns.PHOTO_REF, value);
        return this;
    }

    public RestaurantSelection photoRefEndsWith(String... value) {
        addEndsWith(RestaurantColumns.PHOTO_REF, value);
        return this;
    }

    public RestaurantSelection orderByPhotoRef(boolean desc) {
        orderBy(RestaurantColumns.PHOTO_REF, desc);
        return this;
    }

    public RestaurantSelection orderByPhotoRef() {
        orderBy(RestaurantColumns.PHOTO_REF, false);
        return this;
    }

    public RestaurantSelection rating(Float... value) {
        addEquals(RestaurantColumns.RATING, value);
        return this;
    }

    public RestaurantSelection ratingNot(Float... value) {
        addNotEquals(RestaurantColumns.RATING, value);
        return this;
    }

    public RestaurantSelection ratingGt(float value) {
        addGreaterThan(RestaurantColumns.RATING, value);
        return this;
    }

    public RestaurantSelection ratingGtEq(float value) {
        addGreaterThanOrEquals(RestaurantColumns.RATING, value);
        return this;
    }

    public RestaurantSelection ratingLt(float value) {
        addLessThan(RestaurantColumns.RATING, value);
        return this;
    }

    public RestaurantSelection ratingLtEq(float value) {
        addLessThanOrEquals(RestaurantColumns.RATING, value);
        return this;
    }

    public RestaurantSelection orderByRating(boolean desc) {
        orderBy(RestaurantColumns.RATING, desc);
        return this;
    }

    public RestaurantSelection orderByRating() {
        orderBy(RestaurantColumns.RATING, false);
        return this;
    }

    public RestaurantSelection type(String... value) {
        addEquals(RestaurantColumns.TYPE, value);
        return this;
    }

    public RestaurantSelection typeNot(String... value) {
        addNotEquals(RestaurantColumns.TYPE, value);
        return this;
    }

    public RestaurantSelection typeLike(String... value) {
        addLike(RestaurantColumns.TYPE, value);
        return this;
    }

    public RestaurantSelection typeContains(String... value) {
        addContains(RestaurantColumns.TYPE, value);
        return this;
    }

    public RestaurantSelection typeStartsWith(String... value) {
        addStartsWith(RestaurantColumns.TYPE, value);
        return this;
    }

    public RestaurantSelection typeEndsWith(String... value) {
        addEndsWith(RestaurantColumns.TYPE, value);
        return this;
    }

    public RestaurantSelection orderByType(boolean desc) {
        orderBy(RestaurantColumns.TYPE, desc);
        return this;
    }

    public RestaurantSelection orderByType() {
        orderBy(RestaurantColumns.TYPE, false);
        return this;
    }

    public RestaurantSelection isOpen(Boolean value) {
        addEquals(RestaurantColumns.IS_OPEN, toObjectArray(value));
        return this;
    }

    public RestaurantSelection orderByIsOpen(boolean desc) {
        orderBy(RestaurantColumns.IS_OPEN, desc);
        return this;
    }

    public RestaurantSelection orderByIsOpen() {
        orderBy(RestaurantColumns.IS_OPEN, false);
        return this;
    }

    public RestaurantSelection priceLevel(Integer... value) {
        addEquals(RestaurantColumns.PRICE_LEVEL, value);
        return this;
    }

    public RestaurantSelection priceLevelNot(Integer... value) {
        addNotEquals(RestaurantColumns.PRICE_LEVEL, value);
        return this;
    }

    public RestaurantSelection priceLevelGt(int value) {
        addGreaterThan(RestaurantColumns.PRICE_LEVEL, value);
        return this;
    }

    public RestaurantSelection priceLevelGtEq(int value) {
        addGreaterThanOrEquals(RestaurantColumns.PRICE_LEVEL, value);
        return this;
    }

    public RestaurantSelection priceLevelLt(int value) {
        addLessThan(RestaurantColumns.PRICE_LEVEL, value);
        return this;
    }

    public RestaurantSelection priceLevelLtEq(int value) {
        addLessThanOrEquals(RestaurantColumns.PRICE_LEVEL, value);
        return this;
    }

    public RestaurantSelection orderByPriceLevel(boolean desc) {
        orderBy(RestaurantColumns.PRICE_LEVEL, desc);
        return this;
    }

    public RestaurantSelection orderByPriceLevel() {
        orderBy(RestaurantColumns.PRICE_LEVEL, false);
        return this;
    }
}
