package com.bianco.wixterview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;


/**
 * Created by bianco on 13/11/2017.
 */

public class ProductsListAdapter extends RecyclerView.Adapter<ProductViewsHolder> {
    private static final String TAG = "ProductsListAdapter";

    WeakReference<Context> mContext;
    WeakReference<ProductsListAdapterListener> mListener;


    ProductsListAdapter(Context context, ProductsListAdapterListener listener) {
        mContext = new WeakReference<>(context);
        mListener = new WeakReference<>(listener);
    }

    public interface ProductsListAdapterListener {
        void OnLastReachedListener();
        void OnRowClicked(int row, TextView title, ImageView image);
    }

    @Override
    public ProductViewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, null);
        return new ProductViewsHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ProductViewsHolder holder, final int position) {
        if (mListener != null && mListener.get() != null && position == ProductListRetriever.getInstance(mContext != null ? mContext.get() : null).getProductsList().size() - 1) {
            mListener.get().OnLastReachedListener();
        }

        Product p = ProductListRetriever.getInstance(mContext != null ? mContext.get() : null).getProductsList().get(position);
        holder.mProductTitle.setText(p.mTitle == null ? "" : p.mTitle);
        holder.mProductPrice.setText(p.mPrice == null ? "" : p.mPrice);

        //try to load the image from the url into the holder's image view
        if (p.mImageUrl != null && mContext.get() != null) {
            Log.d(TAG, "loading product image for " + (p.mTitle == null ? "" : p.mTitle));
            try {
                Glide
                        .with(mContext.get())
                        .load(p.mImageUrl)
                        .into(holder.mProductImage);
            } catch (Exception e) {
                Log.e(TAG, "Error loading product image", e);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && mListener.get() != null) {
                    mListener.get().OnRowClicked(position, holder.mProductTitle, holder.mProductImage);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ProductListRetriever.getInstance(mContext != null ? mContext.get() : null).getProductsList().size();
    }
}
