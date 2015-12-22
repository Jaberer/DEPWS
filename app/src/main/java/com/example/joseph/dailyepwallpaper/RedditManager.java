package com.example.joseph.dailyepwallpaper;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

import java.net.URL;

/**
 * Created by Joseph on 8/31/15.
 */
public class RedditManager
{
    private static final String CLIENT_ID = "-FO9qCWgOM4GcQ";
    private static final String CLIENT_SECRET = "msJmIwQF7glwXXR5YTYU4MY4Uk8";
    private String mRefreshToken = "PUT_TOKEN_HERE";

    /** User object */
    private UserAgent userAgent;

    /** Reddit Client to call API */
    private RedditClient redditClient;

    /** oauth helper that uses redditClient */
    private final OAuthHelper helper;

    /** Credentials of our app */
    private final Credentials credentials;

    /** OAuth Read/Write scope */
    private String[] scopes;

    // If this is true, then you will be able to refresh to access token
    boolean permanent;

    URL authorizationUrl;

    /**
     * Constructor Class
     */
    public RedditManager()
    {
        userAgent = UserAgent.of("android",
                "com.example.joseph.deailyepwallpaper",
                "0.1",
                "Jaberer");

        redditClient = new RedditClient(userAgent);
        helper = redditClient.getOAuthHelper();
        // This is Android, so our OAuth app should be an installed app.
        credentials = Credentials.installedApp(CLIENT_ID, "http://josephzhong.me");
        //credentials = Credentials.script("Jaberer", "Ellen123", CLIENT_ID, CLIENT_SECRET);


        permanent = true;

        // OAuth2 scopes to request. See https://www.reddit.com/dev/api/oauth for a full list
        scopes = new String[]{"identity", "read"};

        authorizationUrl = helper.getAuthorizationUrl(credentials, permanent, scopes);
    }

    /** Gets authorization url for the app */
    public URL getAuthorizationUrl()
    {
        return authorizationUrl;
    }

    public UserAgent getUserAgent()
    {
        return userAgent;
    }

    public OAuthHelper getHelper()
    {
        return helper;
    }

    public Credentials getCredentials()
    {
        return credentials;
    }

    public RedditClient getRedditClient()
    {
        return redditClient;
    }

    public void authenticate()
    {

    }
//
//    /**
//     * Void method to retrieve a random submission from a given subreddit
//     */
//    public Submission getSubmission(String subreddit)
//    {
//        /** return single s */
//        Submission s = null;
//        Submissions submissions = new Submissions(restClient, user);
//
//        int randomPostIndex = generateRandomNumber(100);
//        int randomSubmissionSort = generateRandomNumber(4);
//        switch(randomSubmissionSort)
//        {
//            case 0:
//                s = submissions.ofSubreddit(subreddit, SubmissionSort.HOT, -1, 100, null, null, true).get(randomPostIndex);
//                break;
//            case 1:
//                s = submissions.ofSubreddit(subreddit, SubmissionSort.NEW, -1, 100, null, null, true).get(randomPostIndex);
//                break;
//            case 2:
//                s = submissions.ofSubreddit(subreddit, SubmissionSort.RISING, -1, 100, null, null, true).get(randomPostIndex);
//                break;
//            case 3:
//                s = submissions.ofSubreddit(subreddit, SubmissionSort.CONTROVERSIAL, -1, 100, null, null, true).get(randomPostIndex);
//                break;
//            case 4:
//                s = submissions.ofSubreddit(subreddit, SubmissionSort.TOP, -1, 100, null, null, true).get(randomPostIndex);
//                break;
//        }
//
//        return s;
//    }

    /**
     * Method to return a url of an image from a specified subreddit
     * @param subreddit string of a subreddit ie. EarthPorn
     * @return url of an image
     */
//    public String getImageURL(String subreddit)
//    {
//        String url;
//        Submission submission;
//        submission = getSubmission(subreddit);
//
//        return url;
//    }

    /** generates a random integer with a specified maximum */
    private int generateRandomNumber(int max)
    {
        return (int) Math.floor(Math.random() * (max + 1));
    }
}
