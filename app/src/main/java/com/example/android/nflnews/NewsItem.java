package com.example.android.nflnews;

public class NewsItem {
    private String mTitle;
    private String mPublicationDate;
    private String mByline;
    private String mUrl;
    private String mImageUrl;

    /**
     * Constructs a new {@link NewsItem} object.
     *
     * @param title is the title of the news article
     * @param publicationDate is the web publication date of the article as a string in Datetime
     *                        JSON format
     * @param byline is the byline (author) of the article
     * @param url is the website address for more details on the earthquake
     *
     */
    public NewsItem(String title, String publicationDate, String byline, String url, String
            imageUrl){
        mTitle = title;
        mPublicationDate = publicationDate;
        mByline = byline;
        mUrl = url;
        mImageUrl = imageUrl;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getPublicationDate(){
        return mPublicationDate;
    }

    public String getByline(){
        return mByline;
    }

    public String getNewsItemUrl(){
        return mUrl;
    }

    public String getImageUrl(){
        return mImageUrl;
    }
}
