package com.dreammist.foodwheel.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.dreammist.foodwheel.BuildConfig;
import com.dreammist.foodwheel.provider.restaurant.RestaurantColumns;

public class RestaurantSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = RestaurantSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 1;
    private static RestaurantSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final RestaurantSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_RESTAURANT = "CREATE TABLE IF NOT EXISTS "
            + RestaurantColumns.TABLE_NAME + " ( "
            + RestaurantColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RestaurantColumns.NAME + " TEXT, "
            + RestaurantColumns.PLACE_ID + " TEXT, "
            + RestaurantColumns.PHOTO_REF + " TEXT, "
            + RestaurantColumns.RATING + " REAL, "
            + RestaurantColumns.TYPE + " TEXT, "
            + RestaurantColumns.IS_OPEN + " INTEGER, "
            + RestaurantColumns.PRICE_LEVEL + " INTEGER, "
            + "UNIQUE (place_id) ON CONFLICT REPLACE "
            + ");";

    // @formatter:on

    public static RestaurantSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static RestaurantSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static RestaurantSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new RestaurantSQLiteOpenHelper(context);
    }

    private RestaurantSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new RestaurantSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static RestaurantSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new RestaurantSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private RestaurantSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new RestaurantSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_RESTAURANT);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
