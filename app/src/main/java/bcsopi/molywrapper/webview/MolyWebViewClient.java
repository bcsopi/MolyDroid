package bcsopi.molywrapper.webview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.KeyEvent;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import bcsopi.molywrapper.util.Logger;

/**
 * MolyWebViewClient.<br/>
 * Extends {@link android.webkit.WebViewClient}.<br/>
 * Used by {@link MolyWebView}.
 */
public class MolyWebViewClient extends WebViewClient {

    // Members
    private WebViewClientListener mListener = null;
    private boolean mAllowAnyUrl = false;

    /**
     * Set the listener for this WebViewClient.
     *
     * @param listener {@link WebViewClientListener}. It must be
     *                 in the Activity context.
     */
    public void setListener(WebViewClientListener listener) {
        mListener = listener;
    }

    /**
     * Destroy WebViewClient instance.
     */
    public void destroy() {
        mListener = null;
    }

    /**
     * Whether this WebViewClient should load any domain without
     * checking the internal domain list.
     *
     * @param allow {@link boolean}
     */
    public void setAllowAnyDomain(boolean allow) {
        mAllowAnyUrl = allow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedError(WebView view, int errorCod, String description, String failingUrl) {
        Logger.e(getClass().getSimpleName(), "WebView hiba betöltés közben:");
        Logger.e(getClass().getSimpleName(), "\tHibakód: " + errorCod);
        Logger.e(getClass().getSimpleName(), "\tLeírás: " + description);
        Logger.e(getClass().getSimpleName(), "\tHibás URL: " + failingUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebResourceResponse shouldInterceptRequest (final WebView view, String url) {
        // We are currently not intercepting any resources
        return super.shouldInterceptRequest(view, url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        // Allow the WebView to handle all KeyEvents for now
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logger.d(getClass().getSimpleName(), "shouldOverrideUrlLoading? " + url);

        // Do not override any type of loading if we can load any URL
        if (mAllowAnyUrl) {
            Logger.d(getClass().getSimpleName(), "The user is allowing us to open any URL in this WebView, let it load.");
            return false;
        }

        // Avoid NPEs when clicking on weird blank links
        if (url.equals("about:blank")) {
            Logger.d(getClass().getSimpleName(), "Blank page, let it load");
            return false;
        }

        // Get the URL's domain name
        String domain = Uri.parse(url).getHost();

        Logger.d(getClass().getSimpleName(), "Checking URL: " + url);
        Logger.d(getClass().getSimpleName(), "\tDomain: " + domain);

        if (domain != null) {
            // Let this WebView open the URL
            // TODO: Check the proper domain names that facebook uses or find another way
            if (domain.contains("moly")) {
                Logger.d(getClass().getSimpleName(), "This URL should be loaded internally. Let it load.");
                view.loadUrl(url);
                return false;
            }
        }

        // Otherwise, fire the listener to open the URL by
        // any app that can handle it
        Logger.d(getClass().getSimpleName(), "This URL should be loaded by a 3rd party. Override.");
        fireOpenExternalSiteListener(url);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        // Fire the callback
        fireOnPageStartedListener(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        // Fire the callback
        fireOnPageFinishedListener(url);
    }

    /**
     * Fire off the onPageLoadStarted callback.
     *
     * @param url {@link String} The url that just started loading.
     */
    private void fireOnPageStartedListener(String url) {
        if (mListener != null) {
            mListener.onPageLoadStarted(url);
        }
    }

    /**
     * Fire off the onPageLoadFinished callback.
     *
     * @param url {@link String} The url that just finished loading.
     */
    private void fireOnPageFinishedListener(String url) {
        if (mListener != null) {
            mListener.onPageLoadFinished(url);
        }
    }

    /**
     * Fire off the openExternalSite callback.
     * The listener {@link Activity} should just hand off this
     * url as an intent to make sure some other app can handle it.
     */
    private void fireOpenExternalSiteListener(String url) {
        if (mListener != null) {
            mListener.openExternalSite(url);
        }
    }

    /**
     * Listener interface used to fire callbacks for page load
     * started, finished as well as handing off opening an external url
     */
    public interface WebViewClientListener {

        /**
         * Notify the host activity that a page has started loading.
         *
         * @param url {@link String} The url that just started loading.
         */
        void onPageLoadStarted(String url);

        /**
         * Notify the host activity that a page has finished loading.
         *
         * @param url {@link String} The url that just finished loading.
         */
        void onPageLoadFinished(String url);

        /**
         * Notify the host activity that it should hand off this URL
         * as an intent to any application that can open it since it
         * won't be handled by us.
         *
         * @param url {@link String} The url to be loaded.
         */
        void openExternalSite(String url);

    }
}
