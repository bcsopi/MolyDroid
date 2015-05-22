package bcsopi.molywrapper.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;

import bcsopi.molywrapper.R;

/**
 * Preferences activity
 *
 * TODO: use fragments-based preferences activity
 */
public class MolyPreferences extends PreferenceActivity {

    // Custom preferences
    public final static String MENU_DRAWER_SHOWED_OPENED = "drawer_shown_opened";

    // Shared preference keys
    public final static String ALLOW_CHECKINS = "prefs_allow_checkins";
    public final static String OPEN_LINKS_INSIDE = "prefs_open_links_inside";
    public final static String BLOCK_IMAGES = "prefs_block_images";
    public final static String KEY_SHOW_ZOOM = "prefs_show_zoom";
    public final static String KEY_PROXY_ENABLED = "prefs_enable_proxy";
    public final static String KEY_PROXY_HOST = "prefs_proxy_host";
    public final static String KEY_PROXY_PORT = "prefs_proxy_port";
    public final static String KEY_FELHNEV = "prefs_felhnev";
    public final static String SITE_MODE = "prefs_mobile_site";
    public final static String KEY_KEZDOLAP = "prefs_kezdolap";
    public final static String SITE_MODE_AUTO = "auto";
    public final static String SITE_MODE_BASIC = "basic";
    public final static String SITE_MODE_MOBILE = "mobile";
    public final static String SITE_MODE_DESKTOP = "desktop";
    public final static String KEZDOLAP_CIMLAP = "cimlap";
    public final static String KEZDOLAP_PROFIL = "profil";
    public final static String KEZDOLAP_FRISS = "friss";
    public final static String KEZDOLAP_FRISS_TAGOK = "tagok";
    public final static String KEZDOLAP_FRISS_KONYVEK = "konyvek";
    public final static String KEZDOLAP_FRISS_ZONAK = "zonak";
    public final static String KEZDOLAP_UZENETEK = "uzenetek";
    public final static String KEZDOLAP_UJ = "uj";
    public final static String KEZDOLAP_SZERENCSESUTI = "szsuti";


    // Preferences
    private EditTextPreference mPrefProxyHost = null;
    private EditTextPreference mPrefProxyPort = null;
    private EditTextPreference mPrefFelhnev = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.main_preferences);

        mPrefProxyHost = (EditTextPreference) findPreference(KEY_PROXY_HOST);
        mPrefProxyHost.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newProxyHostValue = (String) newValue;
                mPrefProxyHost.setSummary(newProxyHostValue);
                return true;
            }
        });

        mPrefProxyPort = (EditTextPreference) findPreference(KEY_PROXY_PORT);
        mPrefProxyPort.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newProxyPortValue = (String) newValue;
                mPrefProxyPort.setSummary(newProxyPortValue);
                return true;
            }
        });

        mPrefFelhnev = (EditTextPreference) findPreference(KEY_FELHNEV);
        mPrefFelhnev.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newFelhnev = (String) newValue;
                mPrefFelhnev.setSummary(newFelhnev);
                return true;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (mPrefProxyHost != null) {
            mPrefProxyHost.setSummary(mPrefProxyHost.getText());
        }
        if (mPrefProxyPort != null) {
            mPrefProxyPort.setSummary(mPrefProxyPort.getText());
        }
        if (mPrefFelhnev != null) {
            mPrefFelhnev.setSummary(mPrefFelhnev.getText());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
