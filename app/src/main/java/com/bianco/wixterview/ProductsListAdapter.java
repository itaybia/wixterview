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

public class ProductsListAdapter extends RecyclerView.Adapter<ProductViewsHolder> {
    private static final String TAG = "ProductsListAdapter";

    WeakReference<Context> mContext;
    LastReachedListener mListener;


    ProductsListAdapter(Context context, LastReachedListener listener) {
        mContext = new WeakReference<>(context);
        mListener = listener;
    }

    public interface LastReachedListener {
        void OnLastReachedListener();
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
            mListener.OnLastReachedListener();
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
}
