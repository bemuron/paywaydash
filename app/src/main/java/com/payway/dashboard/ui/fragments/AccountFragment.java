package com.payway.dashboard.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.payway.dashboard.R;


public class AccountFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener{
    private static final String LOG = AccountFragment.class.getSimpleName();
    private OnSettingsItemClickListener clickListener;
    private Preference myPref, viewProfileListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_main, rootKey);

        // app theme change listener
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_app_theme)));

        // get the preference keys
        findPreference(getString(R.string.key_send_feedback)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.key_view_profile)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.key_share_mypro)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.key_privacy_policy)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.key_logout)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_app_theme)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.key_about_mypro)).setOnPreferenceClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsItemClickListener) {
            clickListener = (OnSettingsItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsItemClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Account");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(LOG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //mCategoriesViewModel.start();
        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Account");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                if (newValue.toString().equals("System")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }else if(newValue.toString().equals("Light")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else if(newValue.toString().equals("Dark")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : listPreference.getEntries()[2]);

            }else if (preference instanceof SwitchPreference) {
                if (preference.getKey().equals("notifications_push_key")) {
                    // update the switch to user set value
                    boolean test = (boolean) newValue;
                    if (test) {
                        preference.setSummary("Enabled");
                    } else {
                        preference.setSummary("Disabled");
                    }
                }else if (preference.getKey().equals("notifications_email_key")) {
                    // update the switch to user set value
                    boolean test = (boolean) newValue;
                    if (test) {
                        preference.setSummary("Enabled");
                    } else {
                        preference.setSummary("Disabled");
                    }
                }

            } else {
                preference.setSummary(stringValue);
            }
            preference.setSummary(stringValue);
            return true;
        }
    };

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG,e.getMessage());
            //e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact_us@fixappug.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from FixApp user");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
//        if (intent.resolveActivity(context.getPackageManager()) != null) {
//            context.startActivity(intent);
//        }
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {

        if (preference.getKey().equals(getString(R.string.key_send_feedback))){
            sendFeedback(getActivity());
            return true;
        }else if (preference.getKey().equals(getString(R.string.key_view_profile))){
            clickListener.onSettingsItemClick( getString(R.string.key_view_profile));
            return true;
        }else if (preference.getKey().equals(getString(R.string.key_share_mypro))){
            clickListener.onSettingsItemClick( getString(R.string.key_share_mypro));
            return true;
        }else if (preference.getKey().equals(getString(R.string.key_privacy_policy))){
            clickListener.onSettingsItemClick( getString(R.string.key_privacy_policy));
            return true;
        }else if (preference.getKey().equals(getString(R.string.key_logout))){
            clickListener.onSettingsItemClick( getString(R.string.key_logout));
            return true;
        }else if (preference.getKey().equals(getString(R.string.pref_app_theme))){
            clickListener.onSettingsItemClick( getString(R.string.pref_app_theme));
            return true;
        }else if (preference.getKey().equals(getString(R.string.key_about_mypro))){
            clickListener.onSettingsItemClick( getString(R.string.key_about_mypro));
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            /*if (newValue.toString().equals("System")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }else if(newValue.toString().equals("Light")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else if(newValue.toString().equals("Dark")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }*/

            // Set the summary to reflect the new value.
            /*preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : listPreference.getEntries()[2]);*/

        }else if (preference instanceof SwitchPreference) {
            if (preference.getKey().equals("notifications_push_key")) {
                // update the switch to user set value
                boolean test = (boolean) newValue;
                if (test) {
                    preference.setSummary("Enabled");
                } else {
                    preference.setSummary("Disabled");
                }
            }else if (preference.getKey().equals("notifications_email_key")) {
                // update the switch to user set value
                boolean test = (boolean) newValue;
                if (test) {
                    preference.setSummary("Enabled");
                } else {
                    preference.setSummary("Disabled");
                }
            }

        } else {
            preference.setSummary(stringValue);
        }
        preference.setSummary(stringValue);
        return true;
    }

    public interface OnSettingsItemClickListener {
        void onSettingsItemClick(String clickedItem);
    }

}