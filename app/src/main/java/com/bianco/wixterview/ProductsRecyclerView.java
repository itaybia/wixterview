package com.bianco.wixterview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bianco on 13/11/2017.
 */
public class ProductsRecyclerView extends RecyclerView {
    private int mMaxNumberOfChildrenInView = 0;

    public ProductsRecyclerView(Context context) {
        this(context, null);
    }

    public ProductsRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductsRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            mMaxNumberOfChildrenInView = getMeasuredHeight() % child.getMeasuredHeight() > 0 ? getMeasuredHeight() / child.getMeasuredHeight() + 1 : getMeasuredHeight() / child.getMeasuredHeight();
        }
    }

    public int getMaxNumberOfChildrenInView() {
        return mMaxNumberOfChildrenInView;
    }
}
