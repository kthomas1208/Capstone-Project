package com.dreammist.foodwheel;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

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
}
