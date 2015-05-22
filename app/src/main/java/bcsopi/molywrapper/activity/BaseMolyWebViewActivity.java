package bcsopi.molywrapper.activity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import java.util.List;
import java.util.Map;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import bcsopi.molywrapper.R;
import bcsopi.molywrapper.util.WebViewProxyUtil;
import bcsopi.molywrapper.webview.MolyWebChromeClient;
import bcsopi.molywrapper.webview.MolyWebView;
import bcsopi.molywrapper.webview.MolyWebViewClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import bcsopi.molywrapper.util.Logger;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Base activity that uses a {@link MolyWebView} to load the Facebook
 * site in different formats. Here we can implement all the boilerplate code
 * that has to do with loading the activity as well as lifecycle events.
 * <p/>
 * See {@link #onActivityCreated()}
 * See {@link #onWebViewInit(android.os.Bundle)}
 * See {@link #onResumeActivity()}
 */
public abstract class BaseMolyWebViewActivity extends Activity implements
        MolyWebViewClient.WebViewClientListener,
        MolyWebChromeClient.WebChromeClientListener {

    // Constants
    private final static String LOG_TAG = "MolyWrapper";
    protected final static int RESULT_CODE_FILE_UPLOAD = 1001;
    protected final static int RESULT_CODE_FILE_UPLOAD_LOLLIPOP = 2001;
    protected static final String KEY_SAVE_STATE_TIME = "_instance_save_state_time";
    private static final int ID_CONTEXT_MENU_SAVE_IMAGE = 2981279;
    protected final static String INIT_URL = "http://moly.hu";
    protected final static String URL_PAGE_FRISS = "/friss";
    protected final static String URL_PAGE_UJ = "/vezerlo";
    protected final static String URL_PAGE_UZENETEK = "/uzenetek";


    // URL for Sharing Links
    // u = url & t = title
    //protected final static String URL_PAGE_SHARE_LINKS = "/sharer.php?u=%s&t=%s";

    // Desktop user agent (Google Chrome's user agent from a MacBook running 10.9.1
    protected static final String USER_AGENT_DESKTOP = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36";
    // Mobile user agent (Mobile user agent from a Google Nexus S running Android 2.3.3
    protected static final String USER_AGENT_MOBILE_OLD = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-gb; " +
            "Nexus S Build/GRI20) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    // Mobile user agent (Mobile user agent from a Google Nexus 5 running Android 4.4.2
    protected static final String USER_AGENT_MOBILE = "Mozilla/5.0 (Linux; Android 5.0; Nexus 5 Build/LRX21O) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile Safari/537.36";
    // Firefox for Android user agent, it brings up a basic version of the site. Halfway between touch site and zero site.
    protected static final String USER_AGENT_BASIC = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";

    // Members
    protected ConnectivityManager mConnectivityManager = null;
    protected CookieSyncManager mCookieSyncManager = null;
    protected MolyWebView mWebView = null;
    protected ProgressBar mProgressBar = null;
    protected WebSettings mWebSettings = null;
    protected ValueCallback<Uri> mUploadMessage = null;
    protected ValueCallback<Uri[]> mUploadMessageLollipop = null;
    private boolean mCreatingActivity = true;
    private String mPendingImageUrlToSave = null;

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> menuCollection;
    ExpandableListView expListView;


    /**
     * BroadcastReceiver to handle ConnectivityManager.CONNECTIVITY_ACTION intent action.
     */
    private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // Set the cache mode depending on connection type and availability
            updateCacheMode();
        }
    };

    /**
     * Called when the Activity is created. Make sure the content view
     * is set here.
     */
    protected abstract void onActivityCreated();

    /**
     * Called when we are ready to start restoring or loading
     * data in the {@link MolyWebView}
     *
     * @param savedInstanceState {@link Bundle}
     */
    protected abstract void onWebViewInit(Bundle savedInstanceState);

    /**
     * Called anything the activity is resumed. Could be used to
     * reload any type of preference.
     */
    protected abstract void onResumeActivity();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the activity and set the layout
        onActivityCreated();

        mConnectivityManager = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);

        mWebView = (MolyWebView) findViewById(R.id.webview);
        mWebView.setCustomContentView((FrameLayout) findViewById(R.id.fullscreen_custom_content));
        mWebView.setWebChromeClientListener(this);
        mWebView.setWebViewClientListener(this);
        mWebSettings = mWebView.getWebSettings();

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Set the database path for this WebView so that
        // HTML5 Storage API works properly
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setDatabaseEnabled(true);

        // Create a CookieSyncManager instance and keep a reference of it
        mCookieSyncManager = CookieSyncManager.createInstance(this);

        registerForContextMenu(mWebView);
        registerForContextMenu(findViewById(R.id.menu_item_cimlap));
        registerForContextMenu(findViewById(R.id.menu_title));
        registerForContextMenu(findViewById(R.id.menu_item_friss));
        registerForContextMenu(findViewById(R.id.menu_item_uj));
        registerForContextMenu(findViewById(R.id.menu_item_uzenetek));


        // Have the activity open the proper URL
        onWebViewInit(savedInstanceState);
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        // Pass lifecycle events to the WebView
        mWebView.onResume();

        // Start synchronizing the CookieSyncManager
        mCookieSyncManager.startSync();

        // Set the cache mode depending on connection type and availability
        updateCacheMode();

        // Register a Connectivity action receiver so that we can update the cache mode accordingly
        registerReceiver(mConnectivityReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Horrible lifecycle hack
        if (mCreatingActivity) {
            mCreatingActivity = false;
            return;
        }

        // Resume this activity properly, reload preferences, etc.
        onResumeActivity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {

        // Un-register the connectivity changed receiver
        unregisterReceiver(mConnectivityReceiver);

        if (mWebView != null) {
            // Pass lifecycle events to the WebView
            mWebView.onPause();
        }

        if (mCookieSyncManager != null) {
            // Stop synchronizing the CookieSyncManager
            mCookieSyncManager.stopSync();
        }

        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Save the current time to the state bundle
        outState.putLong(KEY_SAVE_STATE_TIME, System.currentTimeMillis());

        // Save the state of the WebView as a Bundle to the Instance State
        mWebView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // Handle orientation configuration changes
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        switch (view.getId()) {
            case R.id.webview:
                WebView.HitTestResult result = mWebView.getHitTestResult();
                switch (result.getType()) {
                    case WebView.HitTestResult.IMAGE_TYPE:
                        showLongPressedImageMenu(menu, result.getExtra());
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                        showLongPressedLinkMenu(menu, result.getExtra());
                        break;
                }
                break;
            case R.id.menu_item_cimlap:
                menu.add(1,32,1,"Könyvek");
                menu.add(1,33,2,"Sorozatok");
                menu.add(1,34,3,"Zónák");
                menu.add(1,35,4,"Tagok");
                menu.add(1,36,5,"Karcok");
                menu.add(1,37,6,"Kihívások");
                menu.add(1,38,7,"Szavazások");
                menu.add(1,39,8,"Értékelések");
                menu.add(1,40,9,"Alkotóértékelések");
                menu.add(1,41,10,"Észlelések");
                menu.add(1,42,11,"Blogok");
                menu.add(1,43,12,"Alkotók");
                menu.add(1,44,13,"Kiadók");
                menu.add(1,45,14,"Cimkék");
                menu.add(1,46,15,"Enciklopédia");
                menu.add(1,47,16,"Idézetek");
                menu.add(1,48,17,"Polcok");
                menu.add(1,49,18,"Listák");
                menu.add(1,50,19,"Események");
                menu.add(1,51,20,"Hírek");
                menu.add(1,52,21,"Piactér");
                menu.add(1,53,22,"Utazókönyvek");
                break;
            case R.id.menu_title:
                menu.add(2,1,1,"Olvasmánylista");
                menu.add(2,2,2,"Értékelések");
                menu.add(2,3,3,"Könyvek");
                //menu.add(2,4,4,"Könyvek");
                menu.add(2,5,5,"Várólista");
                menu.add(2,6,6,"Kívánságlista");
                menu.add(2,7,7,"Kölcsönkérések");
                menu.add(2,8,8,"Karcok");
                menu.add(2,9,9,"Idézetek");
                menu.add(2,10,10,"Alkotóértékelések");
                menu.add(2,11,11,"Polcok");
                menu.add(2,12,12,"Események");
                menu.add(2,13,13,"Kihívások");
                menu.add(2,14,14,"Listák");
                menu.add(2,15,15,"Szavazások");
                menu.add(2,16,16,"Észlelések");
                menu.add(2,17,17,"Blogbejegyzések");
                menu.add(2,18,18,"Zónák");
                menu.add(2,19,19,"Utazókönyvek");
                menu.add(2,20,20,"Ajánlások");
                menu.add(2,21,21,"Összes aktivitás");
                menu.add(2,22,22,"Kedvencelések");
                menu.add(2,23,23,"Hozzászólások");
                menu.add(2,24,24,"Könyvjelzők");
                menu.add(2,25,25,"Ajándékba szánod");
                menu.add(2,26,26,"Beágyazókód bloghoz");
                menu.add(2,27,27,"Elbírálásra váró könyvek");
                menu.add(2,28,28,"Várakozó hibajavítások");
                menu.add(2,29,29,"Várakozó hibajelzések");
                menu.add(2,30,30,"Beküldött hírek");
                menu.add(2,31,31,"Megrendelések");
                break;
            case R.id.menu_item_friss:
                menu.add(3,54,0,"Tagok szerinti bontás");
                menu.add(3,55,0,"Könyvek szerinti bontás");
                menu.add(3,56,0,"Zónák szerinti bontás");
                menu.add(3,57,0,"Kedvencek");
                menu.add(3,73,0,"Korábbi frissek");
                menu.add(3,74,0,"Könyvjelzők");
                //elválasztó
                menu.add(3,58,0,"Ajánlások");
                menu.add(3,59,0,"Blogok");
                menu.add(3,60,0,"Csoportos ajánlások");
                menu.add(3,61,0,"Értékelések");
                menu.add(3,62,0,"Figyelések");
                menu.add(3,63,0,"Idézetek");
                menu.add(3,64,0,"Karcok");
                menu.add(3,65,0,"Kihívások");
                menu.add(3,66,0,"Kitüntetések");
                menu.add(3,67,0,"Kívánságlista-elemek");
                menu.add(3,68,0,"Listák");
                menu.add(3,69,0,"Molyblog");
                menu.add(3,70,0,"Olvasások");
                menu.add(3,71,0,"Várólistaelemek");
                menu.add(3,72,0,"Zónák");
                break;
            case R.id.menu_item_uj:
                menu.add(4,75,0,"Új esemény");
                menu.add(4,76,0,"Új észlelés");
                menu.add(4,77,0,"Új hír");
                menu.add(4,78,0,"Új idézet");
                menu.add(4,79,0,"Új kihívás");
                menu.add(4,80,0,"Új könyv");
                menu.add(4,81,0,"Új lista");
                menu.add(4,82,0,"Új polc");
                menu.add(4,83,0,"Új szavazás");
                menu.add(4,84,0,"Új zóna");
                menu.add(4,85,0,"Új utazókönyv");
                menu.add(4,86,0,"Új üzenet");
                break;
            case R.id.menu_item_uzenetek:
                menu.add(5,86,0,"Új üzenet");
                menu.add(5,87,0,"Friss üzenetek");
                menu.add(5,89,0,"Csillagozott üzenetek");
                menu.add(5,90,0,"Saját üzenetek");
                menu.add(5,91,0,"Összes üzenet");
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_CONTEXT_MENU_SAVE_IMAGE:
                saveImageToDisk(mPendingImageUrlToSave);
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Set a proxy for the {@link bcsopi.molywrapper.webview.MolyWebView}
     *
     * @param host {@link String}
     * @param port {@link int}
     */
    protected final void setProxy(String host, int port) {
        if (mWebView != null && !TextUtils.isEmpty(host) && port > 0) {
            WebViewProxyUtil.setProxy(getApplicationContext(), mWebView, host, port);
        }
    }

    /**
     * Restore the state of the {@link MolyWebView}
     *
     * @param inState {@link Bundle}
     */
    protected void restoreWebView(Bundle inState) {
        if (mWebView != null) {
            mWebView.restoreState(inState);
        }
    }

    /**
     * Set the browser user agent to be used. If the user agent should be forced,
     * make sure the 'force' param is set to true, otherwise the devices' default
     * user agent will be used.
     *
     * @param force  {@link boolean}
     *               true if we should force a custom user agent, false if not.
     *               Note, if this flag is false the default user agent will be
     *               used while disregarding the mobile {@link boolean} parameter
     * @param mobile {@link boolean}
     *               true if we should use a custom user agent for mobile devices,
     *               false if not.
     */
    protected void setUserAgent(boolean force, boolean mobile, boolean facebookBasic) {
        if (force && mobile && !facebookBasic) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mWebSettings.setUserAgentString(USER_AGENT_MOBILE_OLD);
            } else {
                mWebSettings.setUserAgentString(USER_AGENT_MOBILE);
            }
        } else if (force && !mobile && !facebookBasic) {
            mWebSettings.setUserAgentString(USER_AGENT_DESKTOP);
        } else if (force && mobile && facebookBasic) {
            mWebSettings.setUserAgentString(USER_AGENT_BASIC);
        } else {
            mWebSettings.setUserAgentString(null);
        }
    }

    /**
     * Used to load a new URL in the {@link MolyWebView}
     *
     * @param url {@link String}
     */
    protected void loadNewPage(String url) {
        if (mWebView != null) {
            //String htmlText = "<html><head><style type='text/css'>#top_banner,#ad,div[class*='commercial'] {display:none}</style></head></html>";
            //mWebView.loadData(htmlText, "text/html", "UTF-8");
            mWebView.loadUrl(url);
        }
    }

    /**
     * Method used to allow the user to refresh the current page
     */
    protected void refreshCurrentPage() {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    /**
     * Method used to allow the user to jump to the top of the webview
     */
    protected void jumpToTop() {
        loadNewPage("javascript:window.scrollTo(0,0);");
    }

    /**
     * Used to change the geolocation flag.
     *
     * @param allow {@link boolean} true if the use of
     *              geolocation is allowed, false if not
     */
    protected void setAllowCheckins(boolean allow) {
        if (mWebView != null) {
            mWebView.setAllowGeolocation(allow);
        }
    }

    /**
     * Used to change to change the behaviour of the {@link MolyWebView}<br/>
     * By default, this {@link MolyWebView} will only open URLs in which the
     * host is facebook.com, any other links should be sent to the default browser.<br/>
     * However, if the user wants to open the link inside this same webview, he could,
     * so in that case, make sure this flag is set to true.
     *
     * @param allow {@link boolean} true if any domain could be opened
     *              on this webview, false if only facebook domains
     *              are allowed.
     */
    protected void setAllowAnyDomain(boolean allow) {
        if (mWebView != null) {
            mWebView.setAllowAnyDomain(allow);
        }
    }

    /**
     * Used to block network requests of images in the {@link WebView}.
     * <p/>
     * See {@link WebSettings#setBlockNetworkImage(boolean)}
     *
     * @param blockImages {@link boolean}
     */
    protected void setBlockImages(boolean blockImages) {
        if (mWebSettings != null) {
            mWebSettings.setBlockNetworkImage(blockImages);
        }
    }

    /**
     * Allows us to share the page that's currently opened
     * using the ACTION_SEND share intent.
     */
    protected void shareCurrentPage() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, R.string.share_action_subject);
        i.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
        startActivity(Intent.createChooser(i, getString(R.string.share_action)));
    }

    /**
     * Show a context menu to allow the user to perform actions specifically related to the link they just long pressed
     * on.
     *
     * @param menu
     *         {@link ContextMenu}
     * @param url
     *         {@link String}
     */
    private void showLongPressedLinkMenu(ContextMenu menu, String url) {
        // TODO: needs to be implemented, add ability to open site with external browser
    }

    /**
     * Show a context menu to allow the user to perform actions specifically related to the image they just long pressed
     * on.
     *
     * @param menu
     *         {@link ContextMenu}
     * @param imageUrl
     *         {@link String}
     */
    private void showLongPressedImageMenu(ContextMenu menu, String imageUrl) {
        mPendingImageUrlToSave = imageUrl;
        menu.add(0, ID_CONTEXT_MENU_SAVE_IMAGE, 0, getString(R.string.lbl_save_image));
    }

    /**
     * This is to be used in case we want to force kill the activity.
     * Might not be necessary, but it's here in case we'd like to use it.
     */
    protected void destroyWebView() {
        if (mWebView != null) {
            mWebView.removeAllViews();

            /** Free memory and destroy WebView */
            mWebView.freeMemory();
            mWebView.destroy();
            mWebView = null;
        }
    }

    /**
     * Check whether this device has internet connection or not.
     *
     * @return {@link boolean}
     */
    private boolean checkNetworkConnection() {
        try {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected();
            }
            return false;
        } catch (SecurityException e) {
            // Catch the Security Exception in case the user revokes the ACCESS_NETWORK_STATE permission
            e.printStackTrace();
            // Let's assume the device has internet access
            return true;
        }
    }

    /**
     * Update the cache mode depending on the network connection state of the device.
     */
    private void updateCacheMode() {
        if (checkNetworkConnection()) {
            Logger.d(LOG_TAG, "Setting cache mode to: LOAD_DEFAULT");
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            Logger.d(LOG_TAG, "Setting cache mode to: LOAD_CACHE_ONLY");
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case RESULT_CODE_FILE_UPLOAD:
                if (null == mUploadMessage) {
                    return;
                }
                Uri result = intent == null || resultCode != RESULT_OK ? null
                        : intent.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
                break;
            case RESULT_CODE_FILE_UPLOAD_LOLLIPOP:
                if (null == mUploadMessageLollipop) {
                    return;
                }
                mUploadMessageLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,
                        intent));
                mUploadMessageLollipop = null;
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProgressChanged(WebView view, int progress) {
        Logger.d(LOG_TAG, "onProgressChanged(), progress: " + progress);

        // Posts current progress to the ProgressBar
        mProgressBar.setProgress(progress);

        // Hide the progress bar as soon as it goes over 85%
        if (progress >= 85) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private AlertDialog mLocationAlertDialog = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void showGeolocationDisabledAlert() {
        mLocationAlertDialog = new AlertDialog.Builder(this).create();
        mLocationAlertDialog.setTitle(getString(R.string.lbl_dialog_alert));
        mLocationAlertDialog.setMessage(getString(R.string.txt_checkins_disables));
        mLocationAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                getString(R.string.lbl_dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Don't do anything here, simply close the dialog
                    }
                });
        mLocationAlertDialog.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideGeolocationAlert() {
        if ((mLocationAlertDialog != null) && mLocationAlertDialog.isShowing()) {
            mLocationAlertDialog.dismiss();
            mLocationAlertDialog = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        Logger.d(LOG_TAG, "openFileChooser()");
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(
                Intent.createChooser(i,
                        getString(R.string.upload_file_choose)),
                RESULT_CODE_FILE_UPLOAD);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NewApi")
    @Override
    public boolean openFileChooser(ValueCallback<Uri[]> filePathCallback,
                                   WebChromeClient.FileChooserParams fileChooserParams) {
        try {
            Logger.d(LOG_TAG, "openFileChooser()");
            mUploadMessageLollipop = filePathCallback;
            startActivityForResult(
                    Intent.createChooser(fileChooserParams.createIntent(),
                            getString(R.string.upload_file_choose)),
                    RESULT_CODE_FILE_UPLOAD_LOLLIPOP);
            return true;
        } catch (ActivityNotFoundException e) {
            mUploadMessageLollipop = null;
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageLoadStarted(String url) {
        Logger.d(LOG_TAG, "onPageLoadStarted() -- url: " + url);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageLoadFinished(String url) {
        Logger.d(LOG_TAG, "onPageLoadFinished() -- url: " + url);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openExternalSite(String url) {
        Logger.d(LOG_TAG, "openExternalSite() -- url: " + url);

        // This link is not for a page on my site, launch another Activity
        // that handles this URL
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Override the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                // Check to see if there's history to go back to
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Save the image on the specified URL to disk
     *
     * @param imageUrl
     *         {@link String}
     */
    private void saveImageToDisk(String imageUrl) {
        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl).into(saveImageTarget);
        }
    }

    private Target saveImageTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            (new SaveImageTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            Toast.makeText(BaseMolyWebViewActivity.this, getString(R.string.txt_save_image_failed),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            // Not implemented
        }
    };

    private class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Bitmap... images) {
            Bitmap bitmap = images[0];
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File imageFile = new File(directory, System.currentTimeMillis() + ".jpg");
            try {
                if (imageFile.createNewFile()) {
                    FileOutputStream ostream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                    ostream.close();

                    // Ping the media scanner
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(BaseMolyWebViewActivity.this, getString(R.string.txt_save_image_success),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(BaseMolyWebViewActivity.this, getString(R.string.txt_save_image_failed),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
