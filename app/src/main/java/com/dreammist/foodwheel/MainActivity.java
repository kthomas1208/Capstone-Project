package com.dreammist.foodwheel;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreammist.foodwheel.provider.restaurant.RestaurantColumns;
import com.dreammist.foodwheel.provider.restaurant.RestaurantCursor;
import com.dreammist.foodwheel.provider.restaurant.RestaurantSelection;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int ACCESS_FINE_LOCATION_PERMISSION = 0;

    View mRestaurantLogo;
    View mRestaurantTitle;
    View mFindRestaurant;
    String mCoordinates;
    String mPlaceID;
    String mPhotoURI;

    private GoogleApiClient mGoogleApiClient;
    protected static final String PLACE_ID = "PLACE_ID";
    protected static final String PHOTO_URI = "PHOTO_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mRestaurantLogo = findViewById(R.id.restaurantImage);
        mRestaurantTitle = findViewById(R.id.restaurantTitle);
        mRestaurantLogo.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   // Start the details activity if you click on the logo
                                                   Intent intent = new Intent(MainActivity.this,
                                                           DetailActivity.class);

                                                   if (mPlaceID != null) intent.putExtra(PLACE_ID, mPlaceID);
                                                   if (mPhotoURI != null) intent.putExtra(PHOTO_URI, mPhotoURI);

                                                   ActivityOptions options =
                                                           ActivityOptions.makeSceneTransitionAnimation(
                                                                   MainActivity.this,
                                                                   Pair.create(mRestaurantLogo, "logo"),
                                                                   Pair.create(mRestaurantTitle, "title"));
                                                   startActivity(intent, options.toBundle());
                                               }
                                           }
        );

        mFindRestaurant = findViewById(R.id.findButton);
        mFindRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchRestaurantsTask restaurantTask = new FetchRestaurantsTask(MainActivity.this);
                restaurantTask.execute();
            }
        });

        initializeStetho();
    }

    private void initializeStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Check if location permission has been granted. If not, ask for it.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_PERMISSION);
            return;
        }
        else {
            getUserLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_PERMISSION: {
                Button findButton = (Button)findViewById(R.id.findButton);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findButton.setEnabled(true);
                    getUserLocation();

                } else {
                    findButton.setEnabled(false);
                }
                return;
            }
        }
    }

    /**
     * Get the user's location
     */
    public void getUserLocation() {
        try {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (lastLocation != null) {
                String lat = String.valueOf(lastLocation.getLatitude());
                String lng = String.valueOf(lastLocation.getLongitude());
                mCoordinates = lat.concat(",").concat(lng);
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public class FetchRestaurantsTask extends AsyncTask<Void, Void, Void> {
        Context mContext;

        public FetchRestaurantsTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if(mCoordinates == null) return null;

            //"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=" + API_KEY,

            final String PLACES_BASE_URL =
                    "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
            final String LOCATION_PARAM = "location";
            final String RADIUS_PARAM = "radius";
            final String TYPE_PARAM = "type";
            final String KEY_PARAM = "key";

            String API_KEY = BuildConfig.MyApiKey;
            String RADIUS = "500";
            String TYPE = "restaurant";

            try {
                Uri builtUri = Uri.parse(PLACES_BASE_URL).buildUpon()
                        .appendQueryParameter(LOCATION_PARAM, mCoordinates)
                        .appendQueryParameter(RADIUS_PARAM, RADIUS)
                        .appendQueryParameter(TYPE_PARAM, TYPE)
                        .appendQueryParameter(KEY_PARAM, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the Places API request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Add new line for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                getRestaurantDataFromJSON(buffer.toString());

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        /**
         * Parses through a JSON string with nearby restaurant data and creates JSON objects
         * @param jsonString a string with nearby restaurant data
         * @throws JSONException
         */
        private void getRestaurantDataFromJSON(String jsonString) throws JSONException{
            JSONObject response = new JSONObject(jsonString);
            JSONArray responseArray = response.getJSONArray("results");
            Vector<ContentValues> cvValues =
                    new Vector<>(responseArray.length());

            for(int i=0; i<responseArray.length(); i++) {
                ContentValues restaurantValues = new ContentValues();
                String restaurantName = responseArray.getJSONObject(i).getString("name");
                String placeID = responseArray.getJSONObject(i).getString("place_id");
                JSONArray photosArray = responseArray.getJSONObject(i).optJSONArray("photos");
                String photoRef = "";
                if(photosArray!=null)
                    photoRef = photosArray.getJSONObject(0).optString("photo_reference");
                float rating = (float)responseArray.getJSONObject(i).optDouble("rating");
                //String type = responseArray.getJSONObject(i).getString("type");
                JSONObject openingHoursObj = responseArray.getJSONObject(i).optJSONObject("opening_hours");
                boolean isOpen = false;
                if(openingHoursObj!=null)
                    isOpen = openingHoursObj.optBoolean("open_now");
                int priceLevel = responseArray.getJSONObject(i).optInt("price_level");

                JSONObject locationObject = responseArray.getJSONObject(i)
                        .getJSONObject("geometry").getJSONObject("location");
                String lat = locationObject.getString("lat");
                String lng = locationObject.getString("lng");
                String latLng = lat.concat(",").concat(lng);

                restaurantValues.put(RestaurantColumns.NAME,restaurantName);
                restaurantValues.put(RestaurantColumns.PLACE_ID,placeID);
                restaurantValues.put(RestaurantColumns.PHOTO_REF,photoRef);
                restaurantValues.put(RestaurantColumns.RATING,rating);
                //restaurantValues.put(RestaurantColumns.TYPE,type);
                restaurantValues.put(RestaurantColumns.IS_OPEN,isOpen);
                restaurantValues.put(RestaurantColumns.PRICE_LEVEL,priceLevel);
                restaurantValues.put(RestaurantColumns.LAT_LNG,latLng);

                cvValues.add(restaurantValues);
            }

            int inserted = 0;
            // add to database
            if ( cvValues.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cvValues.size()];
                cvValues.toArray(cvArray);
                inserted = getContentResolver().bulkInsert(RestaurantColumns.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchRestaurantsTask Complete. " + inserted + " Inserted");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            getRestaurantFromDB();
        }

        /**
         * Retrieves a random restaurant from the database and updates the view
         */
        private void getRestaurantFromDB() {
            // Get a random restaurant from the DB
            RestaurantSelection where = new RestaurantSelection();
            where.photoRefNot("null");
            RestaurantCursor restaurant = where.query(getContentResolver());
            Random rand = new Random();
            restaurant.moveToPosition(rand.nextInt(restaurant.getCount()));

            // Set the name of the restaurant for display
            ((TextView)mRestaurantTitle).setText(restaurant.getName());

            // Get a photo of the restaurant location
            final String PHOTOS_BASE_URL =
                    "https://maps.googleapis.com/maps/api/place/photo?";
            final String MAXWIDTH_PARAM = "maxwidth";
            final String PHOTOREF_PARAM = "photoreference";
            final String KEY_PARAM = "key";

            String API_KEY = BuildConfig.MyApiKey;
            String photoRef = restaurant.getPhotoRef();

            if(!photoRef.equals("null")) {
                Uri builtUri = Uri.parse(PHOTOS_BASE_URL).buildUpon()
                        .appendQueryParameter(MAXWIDTH_PARAM, "500")
                        .appendQueryParameter(PHOTOREF_PARAM, photoRef)
                        .appendQueryParameter(KEY_PARAM, API_KEY)
                        .build();

                mPhotoURI = builtUri.toString();

                Picasso.with(mContext).load(mPhotoURI).placeholder(R.drawable.placeholder)
                        .into((ImageView) mRestaurantLogo)
                ;
            }

            // Get the PlaceID
            mPlaceID = restaurant.getPlaceId();
            restaurant.close();
        }
    }
}
