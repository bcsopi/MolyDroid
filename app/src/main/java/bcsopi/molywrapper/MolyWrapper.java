package bcsopi.molywrapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bcsopi.molywrapper.activity.BaseMolyWebViewActivity;
import bcsopi.molywrapper.preferences.MolyPreferences;
import bcsopi.molywrapper.util.Logger;

/**
 * Facebook web wrapper activity.
 */
public class MolyWrapper extends BaseMolyWebViewActivity {

    private String felhNev;

    // Constant
    private final static String LOG_TAG = "MolyWrapper";
    private final static int MENU_DRAWER_GRAVITY = GravityCompat.END;
    protected final static int DELAY_RESTORE_STATE = (60 * 1000) * 30;

    // Members
    private DrawerLayout mDrawerLayout = null;
    private RelativeLayout mWebViewContainer = null;
    private String mDomain = INIT_URL;
    private String mDomainToUse = INIT_URL;

    // Preferences stuff
    private SharedPreferences mSharedPreferences = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityCreated() {
        Logger.d(LOG_TAG, "onActivityCreated()");

        // Set the content view layout
        setContentView(R.layout.main_layout);

        // Keep a reference of the DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_main);
        mWebViewContainer = (RelativeLayout) findViewById(R.id.webview_container);

        // Set the click listener interface for the buttons
        setOnClickListeners();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/olvasmanylista");
                break;
            case 2:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/ertekelesek");
                break;
            case 3:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/magankonyvtar");
                break;
            /*case 4:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/olvasmanylista");
                break;*/
            case 5:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/varolista");
                break;
            case 6:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/kivansaglista");
                break;
            case 7:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/kolcsonkeresek");
                break;
            case 8:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/karcok");
                break;
            case 9:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/idezetek");
                break;
            case 10:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/alkotoertekelesek");
                break;
            case 11:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/polcok");
                break;
            case 12:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/esemenyek");
                break;
            case 13:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/kihivasok");
                break;
            case 14:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/jelentkezes-listakra");
                break;
            case 15:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/szavazasok");
                break;
            case 16:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/eszlelesek");
                break;
            case 17:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/blogbejegyzesek");
                break;
            case 18:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/zona-tagsagok");
                break;
            case 19:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/jelentkezes-utazokonyvekre");
                break;
            case 20:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/csoportos-ajanlasok");
                break;
            case 21:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/aktivitasok");
                break;
            case 22:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/kedvencelesek");
                break;
            case 23:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/hozzaszolasok");
                break;
            case 24:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/konyvjelzok");
                break;
            case 25:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/ajandekba");
                break;
            case 26:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/widget");
                break;
            case 27:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/elbiralasra-varo-konyvek");
                break;
            case 28:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/varakozo-hibajavitasok");
                break;
            case 29:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/varakozo-hibajelzesek");
                break;
            case 30:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/bekuldott-hirek");
                break;
            case 31:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/megrendelesek");
                break;
            case 32:
                loadNewPage("http://moly.hu/konyvek");
                break;
            case 33:
                loadNewPage("http://moly.hu/sorozatok");
                break;
            case 34:
                loadNewPage("http://moly.hu/zonak");
                break;
            case 35:
                loadNewPage("http://moly.hu/tagok");
                break;
            case 36:
                loadNewPage("http://moly.hu/karcok");
                break;
            case 37:
                loadNewPage("http://moly.hu/kihivasok");
                break;
            case 38:
                loadNewPage("http://moly.hu/szavazasok");
                break;
            case 39:
                loadNewPage("http://moly.hu/ertekelesek");
                break;
            case 40:
                loadNewPage("http://moly.hu/alkotoertekelesek");
                break;
            case 41:
                loadNewPage("http://moly.hu/eszlelesek");
                break;
            case 42:
                loadNewPage("http://moly.hu/blogok");
                break;
            case 43:
                loadNewPage("http://moly.hu/alkotok");
                break;
            case 44:
                loadNewPage("http://moly.hu/kiadok");
                break;
            case 45:
                loadNewPage("http://moly.hu/cimkek");
                break;
            case 46:
                loadNewPage("http://moly.hu/enciklopedia");
                break;
            case 47:
                loadNewPage("http://moly.hu/idezetek");
                break;
            case 48:
                loadNewPage("http://moly.hu/polcok");
                break;
            case 49:
                loadNewPage("http://moly.hu/listak");
                break;
            case 50:
                loadNewPage("http://moly.hu/esemenyek");
                break;
            case 51:
                loadNewPage("http://moly.hu/hirek");
                break;
            case 52:
                loadNewPage("http://moly.hu/magankonyvtar");
                break;
            case 53:
                loadNewPage("http://moly.hu/utazokonyvek");
                break;
            case 54:
                loadNewPage("http://moly.hu/friss/tagok");
                break;
            case 55:
                loadNewPage("http://moly.hu/friss/konyvek");
                break;
            case 56:
                loadNewPage("http://moly.hu/friss/zonak");
                break;
            case 57:
                loadNewPage("http://moly.hu/friss?type=voted");
                break;
            case 58:
                loadNewPage("http://moly.hu/friss?type=RecommendationVote");
                break;
            case 59:
                loadNewPage("http://moly.hu/friss?type=Entry");
                break;
            case 60:
                loadNewPage("http://moly.hu/friss?type=SuggestionVote");
                break;
            case 61:
                loadNewPage("http://moly.hu/friss?type=Review");
                break;
            case 62:
                loadNewPage("http://moly.hu/friss?type=Follow");
                break;
            case 63:
                loadNewPage("http://moly.hu/friss?type=Citation");
                break;
            case 64:
                loadNewPage("http://moly.hu/friss?type=Statement");
                break;
            case 65:
                loadNewPage("http://moly.hu/friss?type=Campaign");
                break;
            case 66:
                loadNewPage("http://moly.hu/friss?type=BadgeOwnership");
                break;
            case 67:
                loadNewPage("http://moly.hu/friss?type=Wish");
                break;
            case 68:
                loadNewPage("http://moly.hu/friss?type=List");
                break;
            case 69:
                loadNewPage("http://moly.hu/friss?type=Post");
                break;
            case 70:
                loadNewPage("http://moly.hu/friss?type=Reading");
                break;
            case 71:
                loadNewPage("http://moly.hu/friss?type=Wait");
                break;
            case 72:
                loadNewPage("http://moly.hu/friss?type=Zone");
                break;
            case 73:
                loadNewPage("http://moly.hu/friss/korabbi-frissek");
                break;
            case 74:
                loadNewPage("http://moly.hu/tagok/"+felhNev+"/konyvjelzok");
                break;
            case 75:
                loadNewPage("http://moly.hu/esemenyek/uj");
                break;
            case 76:
                loadNewPage("http://moly.hu/eszlelesek/uj");
                break;
            case 77:
                loadNewPage("http://moly.hu/hirek/uj");
                break;
            case 78:
                loadNewPage("http://moly.hu/idezetek/uj");
                break;
            case 79:
                loadNewPage("http://moly.hu/kihivasok/uj");
                break;
            case 80:
                loadNewPage("http://moly.hu/konyvek/uj");
                break;
            case 81:
                loadNewPage("http://moly.hu/listak/uj");
                break;
            case 82:
                loadNewPage("http://moly.hu/polcok/uj");
                break;
            case 83:
                loadNewPage("http://moly.hu/szavazasok/uj");
                break;
            case 84:
                loadNewPage("http://moly.hu/zonak/uj");
                break;
            case 85:
                loadNewPage("http://moly.hu/utazokonyvek/uj");
                break;
            case 86:
                loadNewPage("http://moly.hu/uzenetek/uj");
                break;
            case 87:
                loadNewPage("http://moly.hu/uzenetfriss");
                break;
            /*case 88:
                loadNewPage("http://moly.hu/uzenetek");
                break;*/
            case 89:
                loadNewPage("http://moly.hu/uzenetek/csillagozott");
                break;
            case 90:
                loadNewPage("http://moly.hu/uzenetek/sajat");
                break;
            case 91:
                loadNewPage("http://moly.hu/uzenetek/osszes");
                break;
        }
        closeMenuDrawer();
        return super.onContextItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onWebViewInit(Bundle savedInstanceState) {
        Logger.d(LOG_TAG, "onWebViewInit()");

        // Load the application's preferences
        loadPreferences();

        // Get the Intent data in case we need to load a specific URL
        Intent intent = getIntent();

        // Get a subject and text and check if this is a link trying to be shared
        String sharedSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        String sharedUrl = intent.getStringExtra(Intent.EXTRA_TEXT);

        // If we have a valid URL that was shared to us, open the sharer
        /*if (sharedUrl != null) {
            if (!sharedUrl.equals("")) {

                // Check if the URL being shared is a proper web URL
                if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {
                    // if it's not, let's see if it includes a URL in it (prefixed with a message)
                    int startUrlIndex = sharedUrl.indexOf("http:");
                    if (startUrlIndex > 0) {
                        // Seems like it's prefixed with a message, let's trim the start and get the URL only
                        sharedUrl = sharedUrl.substring(startUrlIndex);
                    }
                }

                String formattedSharedUrl = String.format(mDomain + URL_PAGE_SHARE_LINKS,
                        sharedUrl, sharedSubject);
                Logger.d(LOG_TAG, "Loading the sharer page...");
                loadNewPage(Uri.parse(formattedSharedUrl).toString());
                return;
            }
        }*/

        // Open the proper URL in case the user clicked on a link that brought us here
        if (intent.getData() != null) {
            Logger.d(LOG_TAG, "Loading a specific Facebook URL a user " +
                    "clicked on somewhere else");
            loadNewPage(intent.getData().toString());
            return;
        }

        boolean loadInitialPage = true;

        if (savedInstanceState != null) {
            long savedStateTime = savedInstanceState.getLong(KEY_SAVE_STATE_TIME, -1);
            if (savedStateTime > 0) {
                long timeDiff = System.currentTimeMillis() - savedStateTime;
                if ((mWebView != null) && (timeDiff < DELAY_RESTORE_STATE)) {
                    // Restore the state of the WebView using the saved instance state
                    Logger.d(LOG_TAG, "Restoring the WebView state");
                    restoreWebView(savedInstanceState);
                    loadInitialPage = false;
                }
            }
        }

        if (loadInitialPage) {
            // Load the URL depending on the type of device or preference
            Logger.d(LOG_TAG, "Loading the initial URL");
            loadNewPage(mDomainToUse);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResumeActivity() {
        Logger.d(LOG_TAG, "onResumeActivity()");

        // This will allow us to check and see if the domain to be used changed
        String previousDomainUsed = mDomainToUse;

        // Reload the preferences in case the user changed something critical
        loadPreferences();

        // If the domain changes, reload the page with the new domain
        if (!mDomainToUse.equalsIgnoreCase(previousDomainUsed)) {
            loadNewPage(mDomainToUse);
        }
    }

    /**
     * Sets the click listener on all the buttons in the activity
     */
    private void setOnClickListeners() {
        // Create a new listener
        MenuDrawerButtonListener buttonsListener = new MenuDrawerButtonListener();

        // Set this listener to all the buttons
        findViewById(R.id.menu_drawer_right).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_item_tetejere).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_item_frissites).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_item_cimlap).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_item_friss).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_item_uzenetek).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_item_uj).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_item_info).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_megosztas).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_beallitasok).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_nevjegy).setOnClickListener(buttonsListener);
        findViewById(R.id.menu_kilepes).setOnClickListener(buttonsListener);
        findViewById(R.id.iv_cimlap_more).setOnClickListener(buttonsListener);
        findViewById(R.id.iv_title_more).setOnClickListener(buttonsListener);
        findViewById(R.id.iv_friss_more).setOnClickListener(buttonsListener);
        findViewById(R.id.iv_uj_more).setOnClickListener(buttonsListener);
        findViewById(R.id.iv_uzenetek_more).setOnClickListener(buttonsListener);
        if (felhNev!="") {
            findViewById(R.id.menu_title).setOnClickListener(buttonsListener);
        }
    }

    /**
     * Used to open the menu drawer
     */
    private void openMenuDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(MENU_DRAWER_GRAVITY);
        }
    }

    /**
     * Used to close the menu drawer
     */
    public void closeMenuDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(MENU_DRAWER_GRAVITY);
        }
    }

    /**
     * Check to see if the menu drawer is currently open
     *
     * @return {@link boolean} true if the menu drawer is open,
     *         false if closed.
     */
    private boolean isMenuDrawerOpen() {
        if (mDrawerLayout != null) {
            return mDrawerLayout.isDrawerOpen(MENU_DRAWER_GRAVITY);
        } else {
            return false;
        }
    }

    /**
     * Used to toggle the menu drawer
     */
    private void toggleMenuDrawer() {
        if (isMenuDrawerOpen()) {
            closeMenuDrawer();
        } else {
            openMenuDrawer();
        }
    }

    /**
     * Set the preferences for this activity by using the
     * {@link PreferenceManager} to load the Default shared preferences.<br />
     * Most preferences will be automatically set for the {@link bcsopi.molywrapper.webview.MolyWebView}.
     */
    private void loadPreferences() {

        if (mSharedPreferences == null) {
            // Get the default shared preferences instance
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }

        // Get the URL to load, check-in and proxy settings
        boolean anyDomain = mSharedPreferences.getBoolean(MolyPreferences.OPEN_LINKS_INSIDE, false);
        boolean allowCheckins = mSharedPreferences.getBoolean(MolyPreferences.ALLOW_CHECKINS, false);
        boolean blockImages = mSharedPreferences.getBoolean(MolyPreferences.BLOCK_IMAGES, false);
        boolean showZoom = mSharedPreferences.getBoolean(MolyPreferences.KEY_SHOW_ZOOM, false);
        boolean enableProxy = mSharedPreferences.getBoolean(MolyPreferences.KEY_PROXY_ENABLED, false);
        String proxyHost = mSharedPreferences.getString(MolyPreferences.KEY_PROXY_HOST, null);
        String proxyPort = mSharedPreferences.getString(MolyPreferences.KEY_PROXY_PORT, null);
        felhNev = mSharedPreferences.getString(MolyPreferences.KEY_FELHNEV, "");

        //Set title to username
        TextView lol = (TextView) findViewById(R.id.tv_title);
        if (felhNev!="") {
            lol.setText(felhNev);
        } else {
            lol.setText("nincs megadva");
        }

        // Set the flags for loading URLs, allowing geolocation and loading network images
        setAllowCheckins(allowCheckins);
        setAllowAnyDomain(anyDomain);
        setBlockImages(blockImages);
        mWebSettings.setDisplayZoomControls(showZoom);

        if (enableProxy && !TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort)) {
            int proxyPortInt = -1;
            try {
                proxyPortInt = Integer.parseInt(proxyPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setProxy(proxyHost, proxyPortInt);
        }

        // Whether the site should be loaded as the mobile or desktop version
        String mode = mSharedPreferences.getString(MolyPreferences.SITE_MODE,
                MolyPreferences.SITE_MODE_AUTO);

        // TODO: time to fix this mess
        // Force or detect the site mode to load
        if (mode.equalsIgnoreCase(MolyPreferences.SITE_MODE_MOBILE)) {
            // Force the webview config to mobile
            setupFacebookWebViewConfig(true, true, false, false, false);
        } else if (mode.equalsIgnoreCase(MolyPreferences.SITE_MODE_DESKTOP)) {
            // Force the webview config to desktop mode
            setupFacebookWebViewConfig(true, false, false, false, false);
        } else if (mode.equalsIgnoreCase(MolyPreferences.SITE_MODE_BASIC)) {
            // Force the webview to load the Basic HTML Mobile site
            setupFacebookWebViewConfig(true, true, true, false, false);
        } else {
            // Do not force, allow us to auto-detect what mode to use
            setupFacebookWebViewConfig(false, true, false, false, false);
        }
        String kezdolap = mSharedPreferences.getString(MolyPreferences.KEY_KEZDOLAP,MolyPreferences.KEZDOLAP_CIMLAP);
        if (kezdolap.equalsIgnoreCase(MolyPreferences.KEZDOLAP_PROFIL)) {
            mDomainToUse = "http://moly.hu/" +felhNev;
        } else if (kezdolap.equalsIgnoreCase(MolyPreferences.KEZDOLAP_FRISS)) {
            mDomainToUse = "http://moly.hu/" + URL_PAGE_FRISS;
        } else if (kezdolap.equalsIgnoreCase(MolyPreferences.KEZDOLAP_FRISS_KONYVEK)) {
            mDomainToUse = "http://moly.hu/friss/konyvek";
        } else if (kezdolap.equalsIgnoreCase(MolyPreferences.KEZDOLAP_FRISS_TAGOK)) {
            mDomainToUse = "http://moly.hu/friss/tagok";
        } else if (kezdolap.equalsIgnoreCase(MolyPreferences.KEZDOLAP_FRISS_ZONAK)) {
            mDomainToUse = "http://moly.hu/friss/zonak";
        } else if (kezdolap.equalsIgnoreCase(MolyPreferences.KEZDOLAP_UZENETEK)) {
            mDomainToUse = "http://moly.hu/" + URL_PAGE_UZENETEK;
        } else if (kezdolap.equalsIgnoreCase(MolyPreferences.KEZDOLAP_UJ)) {
            mDomainToUse = "http://moly.hu/" + URL_PAGE_UJ;
        } else if (kezdolap.equalsIgnoreCase(MolyPreferences.KEZDOLAP_SZERENCSESUTI)) {
            mDomainToUse = "http://moly.hu/szerencsesuti";
        }


        // If we haven't shown the new menu drawer to the user, auto open it
        if (!mSharedPreferences.getBoolean(MolyPreferences.MENU_DRAWER_SHOWED_OPENED, false)) {
            openMenuDrawer();

            // Make sure we don't auto-open the menu ever again
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(MolyPreferences.MENU_DRAWER_SHOWED_OPENED, true);
            editor.apply();
        }

    }

    /**
     * Configure this {@link bcsopi.molywrapper.webview.MolyWebView}
     * with the appropriate preferences depending on the device configuration.<br />
     * Use the 'force' flag to force the configuration to either mobile or desktop.
     *
     * @param force  {@link boolean}
     *               whether to force the configuration or not,
     *               if false the 'mobile' flag will be ignored
     * @param mobile {@link boolean}
     *               whether to use the mobile or desktop site.
     * @param facebookZero {@link boolean}
     *               whether or not to use Facebook Zero
     */
    // TODO: time to fix this mess
    private void setupFacebookWebViewConfig(boolean force, boolean mobile, boolean facebookBasic,
                                            boolean facebookZero, boolean facebookOnion) {
        setUserAgent(force, mobile, facebookBasic);
    }

    /**
     * Check whether this device is a phone or a tablet.
     *
     * @return {@link boolean} whether this device is a phone
     *         or a tablet
     */
    private boolean isDeviceTablet() {
        boolean isTablet;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // There were no tablet devices before Honeycomb, assume is a phone
            isTablet = false;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR2) {
            // Honeycomb only allowed tablets, always assume it's a tablet
            isTablet = true;
        } else {
            // If the device's screen width is higher than 720dp, it's a tablet,
            // otherwise, it's, at least, a phone (could be a phablet, or small tablet)
            Configuration config = getResources().getConfiguration();
            isTablet = config.smallestScreenWidthDp >= 720;
        }

        return isTablet;
    }

    /**
     * Menu drawer button listener interface
     */
    private class MenuDrawerButtonListener implements View.OnClickListener {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_title:
                    loadNewPage(mDomain + "/" + felhNev);
                    break;
                case R.id.menu_item_tetejere:
                    jumpToTop();
                    break;
                case R.id.menu_item_frissites:
                    refreshCurrentPage();
                    break;
                case R.id.menu_item_cimlap:
                    loadNewPage(mDomain);
                    break;
                case R.id.menu_item_friss:
                    loadNewPage(mDomain + URL_PAGE_FRISS);
                    break;
                case R.id.menu_item_uzenetek:
                    loadNewPage(mDomain + URL_PAGE_UZENETEK);
                    break;
                case R.id.menu_item_uj:
                    loadNewPage(mDomain + URL_PAGE_UJ);
                    break;
                case R.id.menu_item_info:
                    loadNewPage(mDomain + "/info");
                    break;
                case R.id.menu_megosztas:
                    shareCurrentPage();
                    break;
                case R.id.menu_beallitasok:
                    startActivity(new Intent(MolyWrapper.this, MolyPreferences.class));
                    break;
                case R.id.menu_nevjegy:
                    showAboutAlert();
                    break;
                case R.id.menu_kilepes:
                    mWebViewContainer.removeView(mWebView);
                    destroyWebView();
                    finish();
                    break;
                case R.id.iv_title_more:
                    openContextMenu(findViewById(R.id.menu_title));
                    break;
                case R.id.iv_cimlap_more:
                    openContextMenu(findViewById(R.id.menu_item_cimlap));
                    break;
                case R.id.iv_friss_more:
                    openContextMenu(findViewById(R.id.menu_item_friss));
                    break;
                case R.id.iv_uj_more:
                    openContextMenu(findViewById(R.id.menu_item_uj));
                    break;
                case R.id.iv_uzenetek_more:
                    openContextMenu(findViewById(R.id.menu_item_uzenetek));
                    break;
            }
            closeMenuDrawer();
        }
    }

    /**
     * Show an alert dialog with the information about the application.
     */
    private void showAboutAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.menu_nevjegy));
        alertDialog.setMessage(getString(R.string.txt_about));
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                getString(R.string.lbl_dialog_close),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Don't do anything, simply close the dialog
                    }
                });
        alertDialog.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_MENU:
                toggleMenuDrawer();
                return true;
            case KeyEvent.KEYCODE_BACK:
                // If the back button is pressed while the drawer
                // is open try to close it
                if (isMenuDrawerOpen()) {
                    closeMenuDrawer();
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

}
