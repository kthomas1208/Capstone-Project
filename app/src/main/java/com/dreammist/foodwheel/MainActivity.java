package com.dreammist.foodwheel;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dreammist.foodwheel.provider.restaurant.RestaurantColumns;
import com.dreammist.foodwheel.provider.restaurant.RestaurantCursor;
import com.dreammist.foodwheel.provider.restaurant.RestaurantSelection;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    View mRestaurantLogo;
    View mRestaurantTitle;
    View mLocationFinder;
    View mLocationEnter;
    View mFindRestaurant;
    String mCoordinates;
    String mPlaceID;
    String mPhotoURI;

    private GoogleApiClient mGoogleApiClient;
    protected final static int PLACE_PICKER_REQUEST = 9090;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Always delete data from db when launching app
        //getContentResolver().delete(RestaurantColumns.CONTENT_URI,null,null);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();

        mRestaurantLogo = findViewById(R.id.restaurantImage);
        mRestaurantTitle = findViewById(R.id.restaurantTitle);
        mLocationEnter = findViewById(R.id.locationEdit);
        mRestaurantLogo.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         // Start the details activity if you click on the logo
            Intent intent = new Intent(MainActivity.this,
                    DetailActivity.class);

            if (mPlaceID != null)  intent.putExtra("PLACE_ID", mPlaceID);
            if (mPhotoURI != null) intent.putExtra("PHOTO_URI", mPhotoURI);

            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(
                            MainActivity.this,
                            Pair.create(mRestaurantLogo,"logo"),
                            Pair.create(mRestaurantTitle,"title"));
            startActivity(intent,options.toBundle());
        }
    }
    );

    mLocationFinder = findViewById(R.id.locatorButton);
    mLocationFinder.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.v(LOG_TAG, "clicked");
            if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
                return;
            Log.v(LOG_TAG, "connected");
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult( builder.build( MainActivity.this), PLACE_PICKER_REQUEST );
            } catch ( GooglePlayServicesRepairableException e ) {
                Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
            } catch ( GooglePlayServicesNotAvailableException e ) {
                Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
            }
        }
    });

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

    private void initializeStetho(){
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
        super.onStart();
        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            displayPlace( PlacePicker.getPlace(this, data) );
        }
    }

    private void displayPlace(Place place) {
        LatLng latlng = place.getLatLng();
        mCoordinates = latlng.latitude + "," + latlng.longitude;
        Log.v(LOG_TAG,mCoordinates);

        if(mLocationEnter != null) {
            ((EditText)mLocationEnter).setText(mCoordinates);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

public class FetchRestaurantsTask extends AsyncTask<Void, Void, Void> {
    Context mContext;

    public FetchRestaurantsTask(Context context) {
        mContext = context;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        mCoordinates = "40.740812499999976,-73.69514453125";

        if(mCoordinates == null) return null;

        final RequestQueue mRequestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

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

        Uri builtUri = Uri.parse(PLACES_BASE_URL).buildUpon()
                .appendQueryParameter(LOCATION_PARAM, mCoordinates)
                .appendQueryParameter(RADIUS_PARAM, RADIUS)
                .appendQueryParameter(TYPE_PARAM, TYPE)
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .build();
        try {
            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG,"API Call: " + url.toString());

            /**
             * RETRIEVE RESTAURANT DATA FROM PLACES API
             */
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
                    url.toString(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v(LOG_TAG, "Received JSON response: " + response.toString());
                            try {
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

                                    Log.v(LOG_TAG, "restaurant name: " + restaurantName);
                                    Log.v(LOG_TAG, "LatLng from call: " + latLng);

                                    cvValues.add(restaurantValues);
                                    //getContentResolver().update(RestaurantColumns.CONTENT_URI,restaurantValues,null,null);
                                }

                                int inserted = 0;
                                // add to database
                                if ( cvValues.size() > 0 ) {
                                    ContentValues[] cvArray = new ContentValues[cvValues.size()];
                                    cvValues.toArray(cvArray);
                                    inserted = getContentResolver().bulkInsert(RestaurantColumns.CONTENT_URI, cvArray);
                                }

                                Log.v(LOG_TAG, "FetchRestaurantsTask Complete. " + inserted + " Inserted");

                                /**
                                 * RETRIEVE A RESTAURANT FROM THE DB
                                 */
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
                                Log.v(LOG_TAG, "PHOTO REF: " + photoRef);

                                if(!photoRef.equals("null")) {
                                    Uri builtUri = Uri.parse(PHOTOS_BASE_URL).buildUpon()
                                            .appendQueryParameter(MAXWIDTH_PARAM, "500")
                                            .appendQueryParameter(PHOTOREF_PARAM, photoRef)
                                            .appendQueryParameter(KEY_PARAM, API_KEY)
                                            .build();

                                    mPhotoURI = builtUri.toString();

                                    Log.v(LOG_TAG, mPhotoURI);
                                    Picasso.with(mContext).load(mPhotoURI).into((ImageView) mRestaurantLogo);
                                }

                                // Get the PlaceID
                                mPlaceID = restaurant.getPlaceId();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mRequestQueue.stop();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG, "Error getting JSON data: " + error.getMessage());
                    mRequestQueue.stop();
                }
            });

            mRequestQueue.add(jsonObjRequest);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.v(LOG_TAG, "ON POST EXECUTE ENTERED");
        /**
         * CURRENT ISSUE: this method is being triggered before the onResponse() of the
         * JsonObjectRequest. I haven't figured out how to lock the thread to wait for the
         * onResponse() before calling onPostExecute() so I moved all the code that was
         * originally in here into onResponse(). This gives us a huge, monolithic chunk of code that
         * isn't pretty, but it'll have to do for now.
         */
        }
    }
}
