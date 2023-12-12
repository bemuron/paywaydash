package com.payway.dashboard.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.payway.dashboard.R;
import com.payway.dashboard.helpers.SessionManager;
import com.payway.dashboard.ui.fragments.AccountFragment;
import com.payway.dashboard.ui.fragments.HomeFragment;
import com.payway.dashboard.ui.fragments.TransactionsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AccountFragment.OnSettingsItemClickListener {
    private static final String TAG_FRAGMENT_HOME = "tag_frag_home";

    private static final String TAG_FRAGMENT_TRXS = "tag_frag_transactions";

    private static final String TAG_FRAGMENT_ACCOUNT = "tag_frag_account";
    private List<Fragment> fragments = new ArrayList<>(2);
    private BottomNavigationView navView;
    private static MainActivity mainActivity;
    private SharedPreferences prefs;
    private SessionManager session;
    private static boolean isAppThemeChange;
    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainActivity = this;

        // session manager
        session = new SessionManager(getApplicationContext());

        /*if (defaultTheme != null) {
            if (defaultTheme.equals("System")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }else if(defaultTheme.equals("Light")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else if(defaultTheme.equals("Dark")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }*/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        navView = findViewById(R.id.nav_view);
        setUpNavigationContent(navView);
        buildFragmentList();

        if (isAppThemeChange){
            currentFragment = 2;
            switchFragment(2, TAG_FRAGMENT_ACCOUNT);
            isAppThemeChange = false;
        }else{
            //set the 0th Fragment to be displayed by default
            currentFragment = 0;
            switchFragment(0, TAG_FRAGMENT_HOME);
        }
    }

    public static MainActivity getActivityInstance(){
        return mainActivity;
    }

    private void setUpNavigationContent(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_home) {
                            switchFragment(0, TAG_FRAGMENT_HOME);
                            return true;
                        } else if (itemId == R.id.navigation_transactions) {
                            switchFragment(1, TAG_FRAGMENT_TRXS);
                            return true;
                        } /*else if (itemId == R.id.navigation_account) {
                            switchFragment(2, TAG_FRAGMENT_ACCOUNT);
                            return true;
                        }*/
                        return false;
                    }
                });
    }

    //save the current fragment position of the bottom nav
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFragPos", currentFragment);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragment = savedInstanceState.getInt("currentFragPos");
        switch (currentFragment){
            case 0:
                navView.setSelectedItemId(R.id.navigation_home);
                switchFragment(0,TAG_FRAGMENT_HOME);
                break;
            case 1:
                navView.setSelectedItemId(R.id.navigation_transactions);
                switchFragment(1,TAG_FRAGMENT_TRXS);
                break;
            /*case 2:
                navView.setSelectedItemId(R.id.navigation_account);
                switchFragment(2,TAG_FRAGMENT_ACCOUNT);
                break;*/
        }
    }

    //method to switch correctly between the bottom navigation fragments
    private void switchFragment(int pos, String tag) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentHomeFrame, fragments.get(pos), tag)
                .commit();
    }

    //method to build list of fragments
    private void buildFragmentList() {
        HomeFragment homeFragment = buildHomeFragment();

        TransactionsFragment transactionsFragment = buildTransactionsFragment();

        AccountFragment accountFragment = buildAccountFragment();


        fragments.add(homeFragment);
        fragments.add(transactionsFragment);
        fragments.add(accountFragment);
    }

    private HomeFragment buildHomeFragment() {
        HomeFragment fragment;
        fragment = HomeFragment.newInstance();

        return fragment;
    }

    //build the fragment
    private TransactionsFragment buildTransactionsFragment() {
        TransactionsFragment fragment;
        fragment = TransactionsFragment.newInstance("Transactions");

        return fragment;
    }

    //build the fragment
    private AccountFragment buildAccountFragment() {

        return new AccountFragment();
    }

    //call back from Account fragment
    @Override
    public void onSettingsItemClick(String clickedItem) {
        Intent intent;
        if (  clickedItem.equals(getString(R.string.pref_app_theme))){
            isAppThemeChange = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(navView.getSelectedItemId () != R.id.navigation_home){
            navView.setSelectedItemId(R.id.navigation_home);
        } else {
            super.onBackPressed();
        }
    }

}