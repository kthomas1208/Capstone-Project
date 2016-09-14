package com.dreammist.foodwheel;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    View mRestaurantLogo;
    View mRestaurantTitle;
    View mLocationFinder;
    View mLocationEnter;

    private GoogleApiClient mGoogleApiClient;
    protected final static int PLACE_PICKER_REQUEST = 9090;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                                         Intent intent = new Intent(MainActivity.this,
                                                 DetailActivity.class);

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

        FetchRestaurantsTask restaurantTask = new FetchRestaurantsTask();
        restaurantTask.execute();

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
        String address = place.getAddress().toString();
        if(mLocationEnter != null) {
            ((EditText)mLocationEnter).setText(address);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class FetchRestaurantsTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            final RequestQueue mRequestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);

            // Start the queue
            mRequestQueue.start();

            String API_KEY = BuildConfig.MyApiKey;

            JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
                    "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=" + API_KEY,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v(LOG_TAG, "Received JSON response: " + response.toString());
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

            return null;
        }
    }
}
