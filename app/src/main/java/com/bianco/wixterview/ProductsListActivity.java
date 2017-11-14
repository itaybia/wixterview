package com.bianco.wixterview;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProductsListActivity extends Activity implements ProductListRetriever.ProductsListListener, ProductsListAdapter.ProductsListAdapterListener {
    private static final String TAG = "ProductsListActivity";

    ProductsRecyclerView mRecyclerView;
    ProductsListAdapter mAdapter;
    EditText mFilterView;
    ProgressBar mProductsLoadingProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProductsLoadingProgressView = (ProgressBar) findViewById(R.id.productsLoadingProgress);

        mFilterView = (EditText) findViewById(R.id.productsFilter);
        mFilterView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                ProductListRetriever.getInstance(ProductsListActivity.this).updateFilter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        mRecyclerView = (ProductsRecyclerView) findViewById(R.id.productsRecyclerView);
        mAdapter = new ProductsListAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.requestFocus();

        ProductListRetriever.getInstance(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        ProductListRetriever.getInstance(this).setListener(this);
        if (ProductListRetriever.getInstance(this).loadPage(1)) {
            mProductsLoadingProgressView.setVisibility(View.VISIBLE);   //TODO: check why sometimes seems like it loads a lot at first
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ProductListRetriever.getInstance(this).setListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ProductListRetriever.getInstance(this).updateFilter(mFilterView.getText().toString());
    }

    @Override
    public void OnProductsListRetrieved(int numberOfNewProducts) {
        Log.d(TAG, "OnProductsListRetrieved: " + numberOfNewProducts + ", List size=" + ProductListRetriever.getInstance(this).getProductsList().size());
        if (numberOfNewProducts > 0) {
            int insertionStart = ProductListRetriever.getInstance(this).getProductsList().size() - numberOfNewProducts;
            mAdapter.notifyItemRangeInserted(insertionStart, numberOfNewProducts);
        }
        if (mRecyclerView != null && mRecyclerView.getMaxNumberOfChildrenInView() > mAdapter.getItemCount() && ProductListRetriever.getInstance(this).loadNextPage()) {
            mProductsLoadingProgressView.setVisibility(View.VISIBLE);
        } else {
            mProductsLoadingProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnProductsListFiltered() {
        mAdapter.notifyDataSetChanged();
        if (mRecyclerView != null && mRecyclerView.getMaxNumberOfChildrenInView() > mAdapter.getItemCount() && ProductListRetriever.getInstance(this).loadNextPage()) {
            mProductsLoadingProgressView.setVisibility(View.VISIBLE);
        } else {
            mProductsLoadingProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnProductsListRetrievalFailed(int page) {
        mProductsLoadingProgressView.setVisibility(View.GONE);
    }

    @Override
    public void OnLastReachedListener() {
        if (ProductListRetriever.getInstance(this).loadNextPage()) {
            mProductsLoadingProgressView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnRowClicked(int row, TextView title, ImageView image) {
        Pair<View, String> p = Pair.create((View) image, "image");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, p);
        Intent intent = new Intent(this, FullscreenItemActivity.class);
        intent.putExtra("image", ProductListRetriever.getInstance(this).getProductsList().get(row).mImageUrl);
        intent.putExtra("title", ProductListRetriever.getInstance(this).getProductsList().get(row).mTitle);
        intent.putExtra("price", ProductListRetriever.getInstance(this).getProductsList().get(row).mPrice);
        startActivity(intent, options.toBundle());
    }
}
