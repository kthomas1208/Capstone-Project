package com.dreammist.foodwheel;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView image = (ImageView)findViewById(R.id.restaurantImage);
        image.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         Intent intent = new Intent(MainActivity.this,
                                                 DetailActivity.class);
                                         ActivityOptions options =
                                                 ActivityOptions.makeSceneTransitionAnimation(
                                                         MainActivity.this,
                                                         Pair.create(view,"logo"));
                                         startActivity(intent,options.toBundle());
                                     }
                                 }
        );
    }

}
