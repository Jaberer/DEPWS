package com.example.joseph.dailyepwallpaper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;


public class MainActivity extends Activity {

    private static final String REDIRECT_URL = "http://josephzhong.me";
    private static final String LOG_TAG = RedditAuthenticator.class.getSimpleName();
    private static final String CLIENT_ID = "-FO9qCWgOM4GcQ";
    private String mRefreshToken = "PUT_TOKEN_HERE";

    RedditManager redditManager;

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        redditManager = new RedditManager();

//        rm.authenticate();
        webView = (WebView) findViewById(R.id.webview);
        // Load the authorization URL into the browser
        webView.loadUrl(redditManager.getAuthorizationUrl().toExternalForm());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("code=")) {
                    // We've detected the redirect URL
                    new UserChallengeTask(redditManager.getHelper(), redditManager.getCredentials()).execute(url);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                //TODO: Save refresh token:
                String refreshToken = redditManager.getRedditClient().getOAuthData().getRefreshToken();
                mRefreshToken = refreshToken;
                Log.v(LOG_TAG, "Refresh Token: " + refreshToken);

                /*
                Intent intent = new Intent(MainActivity.this, ImageLoader.class);
                startActivity(intent);
                */
                webView.setVisibility(View.GONE);
            } else {
                Log.e(LOG_TAG, "Passed in OAuthData was null");
            }
        }
    }
}
