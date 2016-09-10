package com.dreammist.foodwheel;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    View mRestaurantLogo;
    View mRestaurantTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRestaurantLogo = findViewById(R.id.restaurantImage);
        mRestaurantTitle = findViewById(R.id.restaurantTitle);
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
    }

}
