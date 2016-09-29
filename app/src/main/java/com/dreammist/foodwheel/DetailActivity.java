package com.dreammist.foodwheel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreammist.foodwheel.provider.restaurant.RestaurantCursor;
import com.dreammist.foodwheel.provider.restaurant.RestaurantSelection;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String placeID = null;
        String photoURI = null;

        if (getIntent().hasExtra("PLACE_ID"))
            placeID = getIntent().getStringExtra("PLACE_ID");
        if (getIntent().hasExtra("PHOTO_URI"))
            photoURI = getIntent().getStringExtra("PHOTO_URI");

        RestaurantSelection where = new RestaurantSelection();
        where.placeId(placeID);
        RestaurantCursor restaurant = where.query(getContentResolver());
        restaurant.moveToNext();
        String restaurantName = restaurant.getName();

        // Set Restaurant Title
        TextView restaurantTitleText = (TextView)findViewById(R.id.restaurantTitle);
        restaurantTitleText.setText(restaurantName);

        // Set Restaurant Logo
        ImageView restaurantLogo = (ImageView)findViewById(R.id.restaurantImage);
        Picasso.with(this).load(photoURI).into(restaurantLogo);
    }
}
