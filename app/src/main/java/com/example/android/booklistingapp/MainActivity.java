package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<String>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int LOADER_ID = 1;
    private ArrayAdapter adapter;
    private static String searchTerm;
    private static String GOOGLE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private ArrayList<String> publishedLists = new ArrayList<>();
    private TextView mEmptyStateTextView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText search = (EditText) findViewById(R.id.search_bar);
        Button searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTerm = search.getText().toString();
                setContentView(R.layout.list_view);
                listView = (ListView) findViewById(R.id.list_view);
                mEmptyStateTextView = (TextView) findViewById(R.id.empty);

                adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, publishedLists);
                listView.setAdapter(adapter);

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(LOADER_ID, null, MainActivity.this);
                } else {
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    listView.setEmptyView(mEmptyStateTextView);
                    mEmptyStateTextView.setText(R.string.no_internet);
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new ListingLoader(this, GOOGLE_REQUEST_URL + searchTerm);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        adapter.clear();

        if (data != null && !data.isEmpty()) {
            publishedLists.addAll(data);
        } else {
            listView.setEmptyView(mEmptyStateTextView);
            mEmptyStateTextView.setText(R.string.no_results);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {

        adapter.clear();

    }

}
