package com.bianco.wixterview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;


/**
 * Created by bianco on 13/11/2017.
 */

public class ProductsListAdapter extends RecyclerView.Adapter<ProductViewsHolder> implements ProductListRetriever.ProductsListListener {
    private static final String TAG = "ProductsListAdapter";

    WeakReference<Context> mContext;

    ProductsListAdapter(Context context) {
        mContext = new WeakReference<>(context);
        ProductListRetriever.getInstance().init(context);
        ProductListRetriever.getInstance().setListener(this);
        ProductListRetriever.getInstance().loadPage(1);
    }

    @Override
    public ProductViewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, null);
        ProductViewsHolder holder = new ProductViewsHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductViewsHolder holder, int position) {
        if (position == ProductListRetriever.getInstance().getProductsList().size() - 1) {
            ProductListRetriever.getInstance().loadNextPage();
            //TODO: add UI of loading
        }

        ProductListRetriever.Product p = ProductListRetriever.getInstance().getProductsList().get(position);
        holder.mProductTitle.setText(p.mTitle == null ? "" : p.mTitle);
        holder.mProductPrice.setText(p.mPrice == null ? "" : p.mPrice);

        if (p.mImageUrl != null && mContext.get() != null) {
            try {
                Glide
                        .with(mContext.get())
                        .load(p.mImageUrl)
                        .into(holder.mProductImage);
            } catch (Exception e) {
                Log.e(TAG, "Error loading product image", e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return ProductListRetriever.getInstance().getProductsList().size();
    }

    @Override
    public void OnProductsListRetrieved(int numberOfNewProducts) {
        Log.d(TAG, "OnProductsListRetrieved: " + numberOfNewProducts + ", List size=" + ProductListRetriever.getInstance().getProductsList().size());
        if (numberOfNewProducts > 0) {
            int insertionStart = ProductListRetriever.getInstance().getProductsList().size() - numberOfNewProducts;
            notifyItemRangeInserted(insertionStart, numberOfNewProducts);
        }
    }

    @Override
    public void OnProductsListFiltered() {
        notifyDataSetChanged();
        //TODO: remove UI of loading
    }

    @Override
    public void OnProductsListRetrievalFailed(int page) {
        //TODO: remove UI of loading
    }
}
