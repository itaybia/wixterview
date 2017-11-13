package com.bianco.wixterview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by bianco on 13/11/2017.
 */

public class ProductsRecyclerView extends RecyclerView {
    public ProductsRecyclerView(Context context) {
        this(context, null);
    }

    public ProductsRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductsRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}
