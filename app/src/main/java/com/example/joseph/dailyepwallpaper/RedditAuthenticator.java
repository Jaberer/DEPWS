package com.example.joseph.dailyepwallpaper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

public class RedditAuthenticator extends Activity {
    private static final String REDIRECT_URL = "http://josephzhong.me";
    private static final String LOG_TAG = RedditAuthenticator.class.getSimpleName();
    private static final String CLIENT_ID = "-FO9qCWgOM4GcQ";
    private String mRefreshToken = "PUT_TOKEN_HERE";
    RedditManager redditManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reddit_authenticator);


        redditManager = new RedditManager();
        String s = redditManager.getAuthorizationUrl().toExternalForm();
        Log.i(LOG_TAG, "authorization url: " + s);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.loadUrl(redditManager.getAuthorizationUrl().toExternalForm());
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.contains("code=")) {
                // We've detected the redirect URL
                new UserChallengeTask(redditManager.getHelper(), redditManager.getCredentials()).execute(url);
            }
            }
        });


    }

    private final class TokenRefreshTask extends AsyncTask<String, Void, OAuthData> {

        @Override
        protected OAuthData doInBackground(String... params) {

            if (!mRefreshToken.isEmpty()) {
                final Credentials credentials = Credentials.installedApp(CLIENT_ID, REDIRECT_URL);
                OAuthHelper oAuthHelper = redditManager.getRedditClient().getOAuthHelper();
                oAuthHelper.setRefreshToken(mRefreshToken);
                try {
                    OAuthData finalData = oAuthHelper.refreshToken(credentials);
                    redditManager.getRedditClient().authenticate(finalData);
                    if (redditManager.getRedditClient().isAuthenticated()) {
                        Log.v(LOG_TAG, "Authenticated");
                    }
                } catch (OAuthException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private final class UserChallengeTask extends AsyncTask<String, Void, OAuthData> {

        private OAuthHelper mOAuthHelper;

        private Credentials mCredentials;

        public UserChallengeTask(OAuthHelper oAuthHelper, Credentials credentials) {
            Log.v(LOG_TAG, "UserChallengeTask()");
            mOAuthHelper = oAuthHelper;
            mCredentials = credentials;
        }

        @Override
        protected OAuthData doInBackground(String... params) {
            Log.v(LOG_TAG, "doInBackground()");
            Log.v(LOG_TAG, "params[0]: " + params[0]);
            try {
                return mOAuthHelper.onUserChallenge(params[0], mCredentials);
            } catch (IllegalStateException | NetworkException | OAuthException e) {
                // Handle me gracefully
                Log.e(LOG_TAG, "OAuth failed");
                Log.e(LOG_TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(OAuthData oAuthData) {
            Log.v(LOG_TAG, "onPostExecute()");
            if (oAuthData != null) {
                redditManager.getRedditClient().authenticate(oAuthData);
                Log.v(LOG_TAG, "Reddit client authentication: " + redditManager.getRedditClient().isAuthenticated());
                Log.i(LOG_TAG, "Logged in");
                //TODO: Save refresh token:
                String refreshToken = redditManager.getRedditClient().getOAuthData().getRefreshToken();
                mRefreshToken = refreshToken;
                Log.v(LOG_TAG, "Refresh Token: " + refreshToken);
                mRefreshToken = refreshToken;
            } else {
                Log.e(LOG_TAG, "Passed in OAuthData was null");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reddit_authenticator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
