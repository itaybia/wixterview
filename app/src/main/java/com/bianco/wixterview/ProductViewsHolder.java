package com.bianco.wixterview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by bianco on 13/11/2017.
 */
public class ProductViewsHolder extends RecyclerView.ViewHolder {
    public ImageView mProductImage;
    public TextView mProductTitle;
    public TextView mProductPrice;

    public ProductViewsHolder(View itemView) {
        super(itemView);

        mProductImage = (ImageView) itemView.findViewById(R.id.productImage);
        mProductTitle = (TextView) itemView.findViewById(R.id.productTitle);
        mProductPrice = (TextView) itemView.findViewById(R.id.productPrice);
    }
}
