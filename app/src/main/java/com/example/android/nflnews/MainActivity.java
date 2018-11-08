package com.example.android.nflnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<NewsItem>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for NewsItem data from the Guardian dataset
     */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis" +
                    ".com/search?";

    /**
     * Constant value for the NewsItem loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWSITEM_LOADER_ID = 1;

    /**
     * Adapter for the list of NewsItems
     */
    private NewsItemAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate() triggered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView newsItemListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of NewsItems as input
        mAdapter = new NewsItemAdapter(this, new ArrayList<NewsItem>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsItemListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsItemListView.setEmptyView(mEmptyStateTextView);

        //set onItemClickListener then set up implicit intent to direct user to the correct url
        // when item is clicked.
        newsItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //https://stackoverflow.com/questions/21636269/implicit-intent-to-view-url
                //https://www.concretepage.com/android/android-implicit-intent-example-open-url-in-browser-make-phone-call
                //https://stackoverflow.com/questions/3487389/convert-string-to-uri

                // Find the current NewsItem that was clicked on
                NewsItem currentNewsItem = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsItemUri = Uri.parse(currentNewsItem.getNewsItemUrl());

                // Create a new intent to view the newsItem URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsItemUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context
                .CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //If there is a network connection, fetch data
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.i(LOG_TAG, "calling initLoader() triggered");
            loaderManager.initLoader(NEWSITEM_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_connection);
        }
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader() triggered");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the
        // default value for this preference.
        String sportFilter = sharedPrefs.getString(
                getString(R.string.settings_sport_filter_key),
                getString(R.string.settings_sport_filter_default));

        String countryOriginFilter = sharedPrefs.getString(
                getString(R.string.settings_country_origin_key),
                getString(R.string.settings_country_origin_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `section=sport`
        uriBuilder.appendQueryParameter("section", "sport");
        uriBuilder.appendQueryParameter("tag", sportFilter);

        //if countryOriginFilter == "all", we do not want to add this filter
        if (!countryOriginFilter.equals(getString(R.string.settings_country_origin_all_value)
        )) {
            uriBuilder.appendQueryParameter("production-office", countryOriginFilter);
        }

        uriBuilder.appendQueryParameter("show-fields", "byline,thumbnail");
        uriBuilder.appendQueryParameter("page-size", "20");
        uriBuilder.appendQueryParameter("api-key", "e3b12aa9-122a-43a4-b4ed-7a512f85c89b");

        Log.i(LOG_TAG, "URL Search = " + uriBuilder.toString());
        return new NewsItemLoader(MainActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        // Set empty state text to display "No results found."
        mEmptyStateTextView.setText(R.string.no_results_found);

        // Set the visibility of the loading spinner to gone
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        Log.i(LOG_TAG, "onLoadFinished() triggered");
        // Clear the adapter of previous NewsItem data
        mAdapter.clear();

        // If there is a valid list of {@link NewsItem}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsItems != null && !newsItems.isEmpty()) {
            mAdapter.addAll(newsItems);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        Log.i(LOG_TAG, "onLoadReset() triggered");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    // This method initializes the contents of the Activity's options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // This method is called whenever an item in the options menu is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}