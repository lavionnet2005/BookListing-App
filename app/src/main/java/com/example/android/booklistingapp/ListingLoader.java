package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by lkatta on 2/14/17.
 */

public class ListingLoader extends AsyncTaskLoader<List<String>>{

    String url;

    public ListingLoader(Context context, String requrl){
        super(context);
        url = requrl;

    }

    @Override
    public List<String> loadInBackground() {
        if(url ==null){
            return null;
        }

        return QueryUtils.fetchData(url);

    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
