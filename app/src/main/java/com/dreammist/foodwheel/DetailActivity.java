package com.dreammist.foodwheel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dreammist.foodwheel.provider.restaurant.RestaurantCursor;
import com.dreammist.foodwheel.provider.restaurant.RestaurantSelection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback{

    double mLat;
    double mLng;
    String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String placeID = null;
        String photoURI = null;

        if (getIntent().hasExtra(MainActivity.PLACE_ID))
            placeID = getIntent().getStringExtra(MainActivity.PLACE_ID);
        if (getIntent().hasExtra(MainActivity.PHOTO_URI))
            photoURI = getIntent().getStringExtra(MainActivity.PHOTO_URI);

        RestaurantSelection where = new RestaurantSelection();
        where.placeId(placeID);
        RestaurantCursor restaurant = where.query(getContentResolver());
        restaurant.moveToNext();
        mName = restaurant.getName();

        // Set Restaurant Title
        TextView restaurantTitleText = (TextView)findViewById(R.id.restaurantTitle);
        restaurantTitleText.setText(mName);

        // Set Restaurant Logo
        ImageView restaurantLogo = (ImageView)findViewById(R.id.restaurantImage);
        Picasso.with(this).load(photoURI).into(restaurantLogo);
        // if no image, use placeholder

        // Set rating
        float rating = 0;
        if(restaurant.getRating() != null) rating = restaurant.getRating();

        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setRating(rating);

        // Set type
        String type = restaurant.getType();
        TextView typeView = (TextView)findViewById(R.id.restaurantTypes);
        typeView.setText(type);

        // Set Open Status
        String isOpen = (restaurant.getIsOpen() ? getString(R.string.open_now) :
                getString(R.string.closed));
        TextView isOpenView = (TextView)findViewById(R.id.hoursText);
        isOpenView.setText(isOpen);

        // Set price
        int price = restaurant.getPriceLevel();
        String pricing;
        switch (price) {
            case 0:
            case 1: pricing = "$";
                    break;
            case 2: pricing = "$$";
                    break;
            case 3: pricing = "$$$";
                    break;
            case 4: pricing = "$$$$";
                    break;
            default: pricing = getString(R.string.no_pricing_data);
        }

        TextView pricingView = (TextView)findViewById(R.id.price);
        pricingView.setText(pricing);

        // Get location
        String latLngData = restaurant.getLatLng();
        String[] latLngString = latLngData.split(",");
        mLat = Double.parseDouble(latLngString[0]);
        mLng = Double.parseDouble(latLngString[1]);

        // Show the restaurant location on a map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng position = new LatLng(mLat, mLng);
        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(mName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,15));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);

    }
}
