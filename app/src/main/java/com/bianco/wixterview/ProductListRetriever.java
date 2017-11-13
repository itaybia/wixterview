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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianco on 13/11/2017.
 */

public class ProductListRetriever {
    private static final String TAG = "ProductListRetriever";
    private static final String SERVER_URL = "https://stark-atoll-33661.herokuapp.com/products.php?page=";

    private static ProductListRetriever instance = new ProductListRetriever();
    private RequestQueue queue = null;
    private ProductsListListener mListener = null;

    private ArrayList<Product> mProductsList = new ArrayList<>();           //TODO: maybe limit the array to X, and send notification to delete items from adapter if over the limit
    private ArrayList<Product> mFilteredProductsList = new ArrayList<>();
    private String mFilter = "";
    private int mMaxPage = 0;
    private boolean mEndReached = false;

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
    public static ProductListRetriever getInstance( ) {
        return instance;
    }

    //the ProductListRetriever should be initialized once to get the request queue running
    public void init(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    //listener interface to be notified about new products page retrieval
    public interface ProductsListListener {
        void OnProductsListRetrieved(int numberOfNewProducts);
        void OnProductsListFiltered();
        void OnProductsListRetrievalFailed(int page);
    }

    //do the HTTP request asynchronously to get the products page
    private void loadProductsByPage(final int page) {
        if (page <= mMaxPage) {
            mListener.OnProductsListRetrieved(0);
            return;
        }

        if (page > mMaxPage + 1 || mEndReached) {
            //TODO: add debug logs
            mListener.OnProductsListRetrieved(0);
            return;
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, SERVER_URL + page, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() == 0) {
                            mEndReached = true;
                            if (mListener != null) {
                                mListener.OnProductsListRetrieved(0);
                            }
                            return;
                        }

                        if (mListener != null) {
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

                                    if (mFilter.isEmpty() || (p.mTitle != null && p.mTitle.toLowerCase().contains(mFilter))) {
                                        mFilteredProductsList.add(p);
                                        count++;
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "Error parsing product", e);
                                }
                            }
                            mListener.OnProductsListRetrieved(count);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mListener != null) {
                            mListener.OnProductsListRetrievalFailed(page);
                        }
                    }
                }
        );
        queue.add(request);
    }

    public void loadPage(int page) {
        loadProductsByPage(page);
    }

    /**
     * load the next page from the server and into our list
     *
     * @return true if tried loading another page, false if already reached the last page
     */
    public boolean loadNextPage() {
        if (mEndReached) {
            return false;
        }
        loadProductsByPage(mMaxPage + 1);
        return true;
    }

    public List<Product> getProductsList() {
        return mFilteredProductsList;
    }

    public void setListener(ProductsListListener l) {
        mListener = l;
    }

    public void updateFilter(String f) {
        if (mFilter.equals(f)) {
            return;
        }
        mFilter = f.toLowerCase();
        mFilteredProductsList.clear();

        for (Product p : mProductsList) {
            if (mFilter.isEmpty() || (p.mTitle != null && p.mTitle.toLowerCase().contains(mFilter))) {
                mFilteredProductsList.add(p);
            }
        }
        mListener.OnProductsListFiltered();
    }
}
