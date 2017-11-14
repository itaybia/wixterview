package com.bianco.wixterview;

import java.util.Locale;

/**
 * Created by bianco on 15/11/2017.
 */
class Product {
    String mImageUrl;
    String mTitle;
    String mPrice;

    Product(String image, String title, String price) {
        mImageUrl = image;
        mTitle = title;

        if (price != null && !price.isEmpty()) {
            float f = Float.parseFloat(price);
            mPrice = String.format(Locale.getDefault(), "$%.2f", f);
        } else {
            mPrice = "";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Product other = (Product)obj;

        return  ((mImageUrl == null && other.mImageUrl == null) || (mImageUrl != null && other.mImageUrl != null && other.mImageUrl.equals(mImageUrl))) &&
                ((mTitle == null && other.mTitle == null) || (mTitle != null && other.mTitle != null && other.mTitle.equals(mTitle))) &&
                ((mPrice == null && other.mPrice == null) || (mPrice != null && other.mPrice != null && other.mPrice.equals(mPrice)));
    }
}
