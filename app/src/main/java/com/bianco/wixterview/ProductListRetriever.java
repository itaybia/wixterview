package com.bianco.wixterview;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by bianco on 13/11/2017.
 *
 * Holds products and retrieves from the server new product pages when requested
 */

public class ProductListRetriever {
    private static final String TAG = "ProductListRetriever";
    private static final String SERVER_URL = "https://stark-atoll-33661.herokuapp.com/products.php?page=";

    private static ProductListRetriever instance = new ProductListRetriever();
    private RequestQueue queue = null;
    private WeakReference<ProductsListListener> mListener = null;

    private ArrayList<Product> mProductsList = new ArrayList<>();           //TODO: maybe limit the array to X, and send notification to delete items from adapter if over the limit
    private ArrayList<Product> mFilteredProductsList = new ArrayList<>();
    private String mFilter = "";
    private int mMaxPage = 0;
    private boolean mEndReached = false;
    private boolean mIsLoading = false;

    class Product {
        String mImageUrl;
        String mTitle;
        String mPrice;

        Product(String image, String title, String price) {
            mImageUrl = image;
            mTitle = title;
            mPrice = price;
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

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private ProductListRetriever() {}

    /* Static 'instance' method */
    public static ProductListRetriever getInstance(Context context) {
        if (instance.queue == null && context != null) {
            instance.queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return instance;
    }

    //listener interface to be notified about new products page retrieval
    public interface ProductsListListener {
        void OnProductsListRetrieved(int numberOfNewProducts);
        void OnProductsListFiltered();
        void OnProductsListRetrievalFailed(int page);
    }

    //do the HTTP request asynchronously to get the products page
    private boolean loadProductsByPage(final int page) {
        Log.d(TAG, "loadProductsByPage: page " + page);
        if (page <= mMaxPage) {
            Log.d(TAG, "loadProductsByPage: page already loaded");
            return false;
        }

        if (page > mMaxPage + 1 || mEndReached || mIsLoading) {
            Log.d(TAG, "loadProductsByPage: isLoading=" + mIsLoading + ", endReached=" + mEndReached + ", maxPage=" + mMaxPage);
            return false;
        }

        mIsLoading = true;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, SERVER_URL + page, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "loadProductsByPage: onResponse with length=" + response.length());
                        mIsLoading = false;
                        if (response.length() == 0) {
                            mEndReached = true;
                            if (mListener != null && mListener.get() != null) {
                                mListener.get().OnProductsListRetrieved(0);
                            }
                            return;
                        }

                        mMaxPage = page;
                        int count = 0;
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject)response.get(i);
                                Product p = new Product(
                                        obj.has("image") ? obj.getString("image") : null,
                                        obj.has("title") ? obj.getString("title") : null,
                                        obj.has("price") ? obj.getString("price") : null);

                                if (mProductsList.contains(p)) {
                                    Log.d(TAG, "found duplicate: " + p.mTitle);
                                    continue;
                                }
                                mProductsList.add(p);

                                if (mFilter.isEmpty() || (p.mTitle != null && p.mTitle.toLowerCase(Locale.getDefault()).contains(mFilter))) {
                                    mFilteredProductsList.add(p);
                                    count++;
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing product", e);
                            }
                        }
                        if (mListener != null && mListener.get() != null) {
                            Log.d(TAG, "loadProductsByPage: calling listener with " + count + " items");
                            mListener.get().OnProductsListRetrieved(count);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "loadProductsByPage: onErrorResponse");
                        mIsLoading = false;
                        if (mListener != null && mListener.get() != null) {
                            Log.d(TAG, "loadProductsByPage: calling listener with error");
                            mListener.get().OnProductsListRetrievalFailed(page);
                        }
                    }
                }
        );
        queue.add(request);
        return true;
    }

    public boolean loadPage(int page) {
        return loadProductsByPage(page);
    }

    /**
     * load the next page from the server and into our list
     *
     * @return true if tried loading another page, false if already reached the last page
     */
    public boolean loadNextPage() {
        return loadProductsByPage(mMaxPage + 1);
    }

    public List<Product> getProductsList() {
        return mFilteredProductsList;
    }

    public void setListener(ProductsListListener l) {
        mListener = new WeakReference<>(l);
    }

    public void updateFilter(String f) {
        if (mFilter.equals(f)) {
            return;
        }
        Log.d(TAG, "updateFilter: new filter is: " + f.toLowerCase(Locale.getDefault()));
        mFilter = f.toLowerCase(Locale.getDefault());
        mFilteredProductsList.clear();

        for (Product p : mProductsList) {
            if (mFilter.isEmpty() || (p.mTitle != null && p.mTitle.toLowerCase(Locale.getDefault()).contains(mFilter))) {
                mFilteredProductsList.add(p);
            }
        }

        if (mListener != null && mListener.get() != null) {
            Log.d(TAG, "updateFilter: calling listener");
            mListener.get().OnProductsListFiltered();
        }
    }
}
