package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Adapter.ImageSliderAdapter;

public class DashBord extends AppCompatActivity {

    private int[] images = {R.drawable.slide_one, R.drawable.slider_two};
    private ViewPager2 viewPager;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_bord);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, images);
        viewPager.setAdapter(adapter);

        // Start auto-slide
        startAutoSlide();
    }

    private void startAutoSlide() {
        Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int nextPosition = (viewPager.getCurrentItem() + 1) % images.length;
                viewPager.setCurrentItem(nextPosition, true);
                sliderHandler.postDelayed(this, 3000); // Change image every 3 seconds
            }
        };

        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    public void NavigateToSchemePage(View view) {

        Intent i = new Intent(DashBord.this,SchemeActivity.class);
        startActivity(i);
    }
}