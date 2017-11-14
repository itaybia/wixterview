package com.bianco.wixterview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by bianco on 14/11/2017.
 */
public class FullscreenItemActivity extends Activity {
    private static final String TAG = "FullscreenItemActivity";

    TextView mTitleView;
    TextView mPriceView;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_fullscreen);

        mTitleView = (TextView) findViewById(R.id.fullscreen_title);
        mPriceView = (TextView) findViewById(R.id.fullscreen_price);
        mImageView = (ImageView) findViewById(R.id.fullscreen_image);

        String imageUrl = getIntent().getStringExtra("image");
        String title = getIntent().getStringExtra("title");
        String price = getIntent().getStringExtra("price");

        mTitleView.setText(title);
        mPriceView.setText(price);

        if (imageUrl != null) {
            try {
                Glide
                        .with(this)
                        .load(imageUrl)
                        .into(mImageView);
            } catch (Exception e) {
                Log.e(TAG, "Error loading product image", e);
            }
        }

        //clicking the image should return us to the list of products
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
