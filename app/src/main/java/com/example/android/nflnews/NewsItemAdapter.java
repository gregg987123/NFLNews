package com.example.android.nflnews;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    public static final String LOG_TAG = NewsItemAdapter.class.getName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param newsItems is a List of NewsItem objects to display in a list.
     */
    public NewsItemAdapter(Activity context, ArrayList<NewsItem> newsItems) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for multiple Views/TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, newsItems);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the NewsItem at the given position in the list of NewsItems
        NewsItem currentNewsItem = getItem(position);

        // Find the TextView with view ID article_title
        TextView titleView = (TextView) listItemView.findViewById(R.id.article_title);
        // Get magnitude and format the number
        String articleTitle = currentNewsItem.getTitle();
        // Display the magnitude of the current earthquake in that TextView
        titleView.setText(articleTitle);

        // Find the TextView with view ID article_byline
        TextView bylineView = (TextView) listItemView.findViewById(R.id.article_byline);
        String articleByline = currentNewsItem.getByline();
        // Display the location of the current earthquake in that TextView
        bylineView.setText(articleByline);

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDate = parseAndFormatDate(currentNewsItem.getPublicationDate());
        // Display the date of the current news item in that TextView
        dateView.setText(formattedDate);

        // Find ImageView
        ImageView image = listItemView.findViewById(R.id.article_image);

        if (currentNewsItem != null) {
            new ImageLoaderTask(image).execute(currentNewsItem.getImageUrl());
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * @param dateTime is the DateTime format from the JSON results
     * @return the parsed and formatted date string
     */
    private String parseAndFormatDate(String dateTime){
        String date = "";
        String monthAsNumber = dateTime.substring(5,7);
        String month = "";
        String day = dateTime.substring(8,10);
        String year = dateTime.substring(0,4);
        switch(monthAsNumber){
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
            default:
                month = "error in formatting date";
                break;
        }

        date = month + " " + day + ", " + year;

        return date;
    }
}
