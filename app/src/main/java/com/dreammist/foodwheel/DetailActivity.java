package com.dreammist.foodwheel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.dreammist.foodwheel.provider.restaurant.RestaurantCursor;
import com.dreammist.foodwheel.provider.restaurant.RestaurantSelection;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String placeID = null;

        if (getIntent().hasExtra("PLACE_ID"))
            placeID = getIntent().getStringExtra("PLACE_ID");

        RestaurantSelection where = new RestaurantSelection();
        where.placeId(placeID);
        RestaurantCursor restaurant = where.query(getContentResolver());
        restaurant.moveToNext();
        String restaurantName = restaurant.getName();

        TextView restaurantTitleText = (TextView)findViewById(R.id.restaurantTitle);
        restaurantTitleText.setText(restaurantName);
    }
}
