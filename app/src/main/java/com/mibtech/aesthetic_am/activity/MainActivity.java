package com.mibtech.aesthetic_am.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.fragment.AddressListFragment;
import com.mibtech.aesthetic_am.fragment.CartFragment;
import com.mibtech.aesthetic_am.fragment.CategoryFragment;
import com.mibtech.aesthetic_am.fragment.FavoriteFragment;
import com.mibtech.aesthetic_am.fragment.HomeFragment;
import com.mibtech.aesthetic_am.fragment.OrderPlacedFragment;
import com.mibtech.aesthetic_am.fragment.PaymentFragment;
import com.mibtech.aesthetic_am.fragment.ProductDetailFragment;
import com.mibtech.aesthetic_am.fragment.SearchFragment;
import com.mibtech.aesthetic_am.fragment.SubCategoryFragment;
import com.mibtech.aesthetic_am.fragment.TrackOrderFragment;
import com.mibtech.aesthetic_am.fragment.TrackerDetailFragment;
import com.mibtech.aesthetic_am.fragment.WalletTransactionFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.DatabaseHelper;
import com.mibtech.aesthetic_am.helper.Session;


public class MainActivity extends DrawerActivity implements OnMapReadyCallback, PaymentResultListener {

    static final String TAG = "MAIN ACTIVITY";
    public static Toolbar toolbar;
    public static SmoothBottomBar bottomNavigationView;
    public static Fragment active;
    public static FragmentManager fm = null;
    public static Fragment homeFragment, categoryFragment, favoriteFragment, trackOrderFragment;
    public static boolean homeClicked = false, categoryClicked = false, favoriteClicked = false, trackingClicked = false;
    public static Activity activity;
    public static Session session;
    boolean doubleBackToExitPressedOnce = false;
    Menu menu;
    DatabaseHelper databaseHelper;
    String from;
    CardView cardViewHamburger;
    TextView toolbarTitle;
    ImageView imageMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiConfig.transparentStatusAndNavigation(this);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open, R.string.drawer_close);
        drawer_layout.addDrawerListener(actionBarDrawerToggle);
        cardViewHamburger = findViewById(R.id.cardViewHamburger);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        imageMenu = findViewById(R.id.imageMenu);
        actionBarDrawerToggle.syncState();

        activity = MainActivity.this;
        session = new Session(activity);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        from = getIntent().getStringExtra(Constant.FROM);
        databaseHelper = new DatabaseHelper(activity);

        drawerToggle = new ActionBarDrawerToggle
                (
                        activity,
                        drawer_layout, toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close
                ) {
        };

        cardViewHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });

        if (session.isUserLoggedIn()) {
            ApiConfig.getCartItemCount(activity, session);
        } else {
            databaseHelper.getTotalItemOfCart(activity);
        }

//        setAppLocal("en"); //Change you language code here

        fm = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        categoryFragment = new CategoryFragment();
        favoriteFragment = new FavoriteFragment();
        trackOrderFragment = new TrackOrderFragment();

        if (from.equals("tracker")) {
            bottomNavigationView.setItemActiveIndex(3);
            active = trackOrderFragment;
            trackingClicked = true;
            homeClicked = false;
            favoriteClicked = false;
            categoryClicked = false;
            fm.beginTransaction().add(R.id.container, trackOrderFragment).commit();
        } else {
            Bundle bundle = new Bundle();
            bottomNavigationView.setItemActiveIndex(0);
            active = homeFragment;
            homeClicked = true;
            trackingClicked = false;
            favoriteClicked = false;
            categoryClicked = false;
            try {
                if (!getIntent().getStringExtra("json").isEmpty()) {
                    bundle.putString("json", getIntent().getStringExtra("json"));
                }
                homeFragment.setArguments(bundle);
                fm.beginTransaction().add(R.id.container, homeFragment).commit();
            } catch (Exception e) {
                fm.beginTransaction().add(R.id.container, homeFragment).commit();
            }
        }

        bottomNavigationView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {

                switch (i) {
                    case 0:
                        if (!homeClicked) {
                            fm.beginTransaction().add(R.id.container, homeFragment).show(homeFragment).hide(active).commit();
                            homeClicked = true;
                        } else {
                            fm.beginTransaction().show(homeFragment).hide(active).commit();
                        }
                        active = homeFragment;
                        break;
                    case 1:
                        if (!categoryClicked) {
                            fm.beginTransaction().add(R.id.container, categoryFragment).show(categoryFragment).hide(active).commit();
                            categoryClicked = true;
                        } else {
                            fm.beginTransaction().show(categoryFragment).hide(active).commit();
                        }
                        active = categoryFragment;
                        break;
                    case 2:
                        if (!favoriteClicked) {
                            fm.beginTransaction().add(R.id.container, favoriteFragment).show(favoriteFragment).hide(active).commit();
                            favoriteClicked = true;
                        } else {
                            fm.beginTransaction().show(favoriteFragment).hide(active).commit();
                        }
                        active = favoriteFragment;
                        break;

                    case 3:
                        if (session.isUserLoggedIn()) {
                            if (!trackingClicked) {
                                fm.beginTransaction().add(R.id.container, trackOrderFragment).show(trackOrderFragment).hide(active).commit();
                                trackingClicked = true;
                            } else {
                                fm.beginTransaction().show(trackOrderFragment).hide(active).commit();
                            }
                            active = trackOrderFragment;
                        } else {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            // Setting Dialog Message
                            alertDialog.setTitle(getString(R.string.login));
                            alertDialog.setMessage(getString(R.string.login_msg));
                            alertDialog.setCancelable(false);
                            final AlertDialog alertDialog1 = alertDialog.create();
                            // Setting OK Button
                            alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(activity, LoginActivity.class);
                                    i.putExtra(Constant.FROM, "tracker");
                                    activity.startActivity(i);
                                }
                            });
                            alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (active == homeFragment) {
                                        bottomNavigationView.setItemActiveIndex(0);
                                    }
                                    if (active == categoryFragment) {
                                        bottomNavigationView.setItemActiveIndex(1);
                                    }
                                    if (active == favoriteFragment) {
                                        bottomNavigationView.setItemActiveIndex(2);
                                    }
                                    fm.beginTransaction().show(active).commit();
                                    alertDialog1.dismiss();
                                }
                            });
                            alertDialog.show();
                        }
                        break;
                }
                return false;
            }
        });

        switch (from) {
            case "checkout":
                bottomNavigationView.setVisibility(View.GONE);
                ApiConfig.getCartItemCount(activity, session);
                Fragment fragment = new AddressListFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.FROM, "process");
                bundle.putDouble("total", Double.parseDouble(ApiConfig.StringFormat("" + Constant.FLOAT_TOTAL_AMOUNT)));
                fragment.setArguments(bundle);
                fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                break;
            case "share":
                Fragment fragment0 = new ProductDetailFragment();
                Bundle bundle0 = new Bundle();
                bundle0.putInt("vpos", getIntent().getIntExtra("vpos", 0));
                bundle0.putString("id", getIntent().getStringExtra("id"));
                bundle0.putString(Constant.FROM, "share");
                fragment0.setArguments(bundle0);
                fm.beginTransaction().add(R.id.container, fragment0).addToBackStack(null).commit();
                break;
            case "product":
                Fragment fragment1 = new ProductDetailFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putInt("vpos", getIntent().getIntExtra("vpos", 0));
                bundle1.putString("id", getIntent().getStringExtra("id"));
                bundle1.putString(Constant.FROM, "product");
                fragment1.setArguments(bundle1);
                fm.beginTransaction().add(R.id.container, fragment1).addToBackStack(null).commit();
                break;
            case "category":
                Fragment fragment2 = new SubCategoryFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString("id", getIntent().getStringExtra("id"));
                bundle2.putString("name", getIntent().getStringExtra("name"));
                bundle2.putString(Constant.FROM, "category");
                fragment2.setArguments(bundle2);
                fm.beginTransaction().add(R.id.container, fragment2).addToBackStack(null).commit();
                break;
            case "order":
                Fragment fragment3 = new TrackerDetailFragment();
                Bundle bundle3 = new Bundle();
                bundle3.putSerializable("model", "");
                bundle3.putString("id", getIntent().getStringExtra("id"));
                fragment3.setArguments(bundle3);
                fm.beginTransaction().add(R.id.container, fragment3).addToBackStack(null).commit();
                break;
            case "payment_success":
                fm.beginTransaction().add(R.id.container, new OrderPlacedFragment()).addToBackStack(null).commit();
                break;
            case "wallet":
                fm.beginTransaction().add(R.id.container, new WalletTransactionFragment()).addToBackStack(null).commit();
                break;
        }

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_transaction_history, Session.getCount(Constant.UNREAD_TRANSACTION_COUNT, getApplicationContext()));
                ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_wallet_history, Session.getCount(Constant.UNREAD_WALLET_COUNT, getApplicationContext()));
                ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_notifications, Session.getCount(Constant.UNREAD_NOTIFICATION_COUNT, getApplicationContext()));
                toolbar.setVisibility(View.VISIBLE);
                Fragment currentFragment = fm.findFragmentById(R.id.container);
                currentFragment.onResume();
            }
        });

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                if (!token.equals(session.getData(Constant.FCM_ID))) {
                    session.setData(Constant.FCM_ID, token);
                    Register_FCM(token);
                }
            }
        });
        GetProductsName();
    }

//    public void setAppLocal(String languageCode) {
//        Resources resources = getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        Configuration configuration = resources.getConfiguration();
//        configuration.setLocale(new Locale(languageCode.toLowerCase()));
//        resources.updateConfiguration(configuration, dm);
//    }

    public void Register_FCM(String token) {
        Map<String, String> params = new HashMap<>();
        if (session.isUserLoggedIn()) {
            params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        }
        params.put(Constant.FCM_ID, token);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        session.setData(Constant.FCM_ID, token);
                    }
                } catch (JSONException ignored) {

                }

            }
        }, activity, Constant.REGISTER_DEVICE_URL, params, false);
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(navigationView))
            drawer_layout.closeDrawers();
        else
            doubleBack();
    }

    public void doubleBack() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        if (fm.getBackStackEntryCount() == 0) {
            if (active != homeFragment) {

                this.doubleBackToExitPressedOnce = false;
                bottomNavigationView.setItemActiveIndex(0);
                homeClicked = true;
                fm.beginTransaction().hide(active).show(homeFragment).commit();
                active = homeFragment;
            } else {
                Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_cart:
                MainActivity.fm.beginTransaction().add(R.id.container, new CartFragment()).addToBackStack(null).commit();
                break;
            case R.id.toolbar_search:
                MainActivity.fm.beginTransaction().add(R.id.container, new SearchFragment()).addToBackStack(null).commit();
                break;
            case R.id.toolbar_logout:
                session.logoutUserConfirmation(activity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(true);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, activity));

        if (fm.getBackStackEntryCount() > 0) {

            drawerToggle.onDrawerClosed(drawer_layout);

            toolbarTitle.setText(Constant.TOOLBAR_TITLE);
            bottomNavigationView.setVisibility(View.GONE);
            imageMenu.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
            cardViewHamburger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fm.popBackStack();
                }
            });
            lockDrawer();

        } else {
            if (session.isUserLoggedIn()) {
                toolbarTitle.setText(getString(R.string.hi) + session.getData(Constant.NAME) + "!");
            } else {
                toolbarTitle.setText(getString(R.string.hi_user));
            }
            bottomNavigationView.setVisibility(View.VISIBLE);
            imageMenu.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
            cardViewHamburger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer_layout.openDrawer(GravityCompat.START);
                }
            });
            unLockDrawer();
        }

        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    public void lockDrawer() {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).requestDisallowInterceptTouchEvent(true);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
    }

    public void unLockDrawer() {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).requestDisallowInterceptTouchEvent(false);
        ((DrawerLayout) findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        LatLng latLng = new LatLng(Double.parseDouble(session.getCoordinates(Constant.LATITUDE)), Double.parseDouble(session.getCoordinates(Constant.LONGITUDE)));
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Current Location"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            if (WalletTransactionFragment.payFromWallet) {
                WalletTransactionFragment.payFromWallet = false;
                new WalletTransactionFragment().AddWalletBalance(activity, new Session(activity), WalletTransactionFragment.amount, WalletTransactionFragment.msg, razorpayPaymentID);
            } else {
                PaymentFragment.razorPayId = razorpayPaymentID;
                new PaymentFragment().PlaceOrder(MainActivity.this, PaymentFragment.paymentMethod, PaymentFragment.razorPayId, true, PaymentFragment.sendparams, Constant.SUCCESS);
            }
        } catch (Exception e) {
            Log.d(TAG, "onPaymentSuccess  ", e);
        }
    }

    public void GetProductsName() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ALL_PRODUCTS_NAME, Constant.GetVal);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        session.setData(Constant.GET_ALL_PRODUCTS_NAME, jsonObject.getString(Constant.DATA));
                    }
                } catch (JSONException ignored) {

                }

            }
        }, activity, Constant.GET_ALL_PRODUCTS_URL, params, false);
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(activity, getString(R.string.order_cancel), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "onPaymentError  ", e);
        }
    }

    @Override
    protected void onPause() {
        invalidateOptionsMenu();
        super.onPause();
    }

    @Override
    protected void onResume() {
        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_transaction_history, Session.getCount(Constant.UNREAD_TRANSACTION_COUNT, getApplicationContext()));
        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_wallet_history, Session.getCount(Constant.UNREAD_WALLET_COUNT, getApplicationContext()));
        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_notifications, Session.getCount(Constant.UNREAD_NOTIFICATION_COUNT, getApplicationContext()));
        super.onResume();
    }
}