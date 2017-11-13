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
    public static final int PRODUCTS_PER_PAGE = 10;

    private static ProductListRetriever instance = new ProductListRetriever();
    private RequestQueue queue = null;
    private ProductsListListener listener = null;

    public class Product {
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

            if ((mImageUrl == null && other.mImageUrl != null) || (mImageUrl != null && other.mImageUrl == null) || !other.mImageUrl.equals(mImageUrl)) {
                return false;
            }
            if ((mTitle == null && other.mTitle != null) || (mTitle != null && other.mTitle == null) || !other.mTitle.equals(mTitle)) {
                return false;
            }
            if ((mPrice == null && other.mPrice != null) || (mPrice != null && other.mPrice == null) || !other.mPrice.equals(mPrice)) {
                return false;
            }
            return true;
        }
    }

    ArrayList<Product> productsList = new ArrayList<>();
    int maxPage = 0;
    boolean endReached = false;

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
        void OnProductsListRetrievalFailed(int page);
    }

    //do the HTTP request asynchronously to get the products page
    private void loadProductsByPage(final int page) {
        if (page <= maxPage) {
            listener.OnProductsListRetrieved(0);
            return;
        }

        if (page > maxPage + 1 || endReached) {
            //TODO: add debug logs
            listener.OnProductsListRetrieved(0);
            return;
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, SERVER_URL + page, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() == 0) {
                            endReached = true;
                            if (listener != null) {
                                listener.OnProductsListRetrieved(response.length());
                            }
                            return;
                        }

                        if (listener != null) {
                            maxPage = page;
                            int count = 0;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = (JSONObject)response.get(i);
                                    Product p = new Product(
                                            obj.has("image") ? obj.getString("image") : null,
                                            obj.has("title") ? obj.getString("title") : null,
                                            obj.has("price") ? obj.getString("price") : null);

                                    if (productsList.contains(p)) {
                                        Log.d(TAG, "found duplicate: " + p.mTitle);
                                        continue;
                                    }
                                    productsList.add(p);
                                    count++;
                                } catch (JSONException e) {
                                    Log.e(TAG, "Error parsing product", e);
                                }
                            }
                            listener.OnProductsListRetrieved(count);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null) {
                            listener.OnProductsListRetrievalFailed(page);
                        }
                    }
                }
        );
        queue.add(request);
    }

    public void loadPage(int page) {
        loadProductsByPage(page);
    }

    public void loadNextPage() {
        loadProductsByPage(maxPage + 1);
    }

    public List<Product> getProductsList() {
        return productsList;
    }

    public void setListener(ProductsListListener l) {
        listener = l;
    }
}
