package com.bianco.wixterview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity{

    RecyclerView mRecyclerView;
    ProductsListAdapter mAdapter;
    EditText filterView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filterView = (EditText) findViewById(R.id.productsFilter);
        filterView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                ProductListRetriever.getInstance().updateFilter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.productsRecyclerView);
        mAdapter = new ProductsListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
    }
}
