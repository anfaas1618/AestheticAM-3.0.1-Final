package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.LoginActivity;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.adapter.CartAdapter;
import com.mibtech.aesthetic_am.adapter.OfflineCartAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.DatabaseHelper;
import com.mibtech.aesthetic_am.helper.OfflineSwipeToDeleteCallback;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.SwipeToDeleteCallback;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.Cart;
import com.mibtech.aesthetic_am.model.OfflineCart;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CartFragment extends Fragment {
    public static LinearLayout lytempty;
    public static RelativeLayout lytTotal;
    public static ArrayList<Cart> carts;
    public static ArrayList<OfflineCart> offlineCarts;
    public static HashMap<String, String> values;
    public static boolean isSoldOut = false;
    static TextView txttotalamount, txttotalitems, tvConfirmOrder;
    static CartAdapter cartAdapter;
    static OfflineCartAdapter offlineCartAdapter;
    static Activity activity;
    static Session session;
    static JSONObject objectbject;
    View root;
    RecyclerView cartrecycleview;
    NestedScrollView scrollView;
    double total;
    Button btnShowNow;
    DatabaseHelper databaseHelper;
    private ShimmerFrameLayout mShimmerViewContainer;

    @SuppressLint("SetTextI18n")
    public static void SetData() {
        txttotalamount.setText(session.getData(Constant.currency) + ApiConfig.StringFormat(String.valueOf(Constant.FLOAT_TOTAL_AMOUNT)));
        txttotalitems.setText(Constant.TOTAL_CART_ITEM + " Items");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cart, container, false);

        values = new HashMap<>();
        activity = getActivity();
        session = new Session(getActivity());
        lytTotal = root.findViewById(R.id.lytTotal);
        lytempty = root.findViewById(R.id.lytempty);
        btnShowNow = root.findViewById(R.id.btnShowNow);
        txttotalamount = root.findViewById(R.id.txttotalamount);
        txttotalitems = root.findViewById(R.id.txttotalitems);
        scrollView = root.findViewById(R.id.scrollView);
        cartrecycleview = root.findViewById(R.id.cartrecycleview);
        tvConfirmOrder = root.findViewById(R.id.tvConfirmOrder);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        databaseHelper = new DatabaseHelper(activity);

        ApiConfig.GetSettings(activity);

        setHasOptionsMenu(true);

        Constant.FLOAT_TOTAL_AMOUNT = 0.00;

        carts = new ArrayList<>();
        cartrecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (ApiConfig.isConnected(getActivity())) {
            if (session.isUserLoggedIn()) {
                GetSettings(getActivity());
            } else {
                GetOfflineCart();
            }
        }

        tvConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiConfig.isConnected(requireActivity())) {
                    if (!isSoldOut) {
                        if (Constant.SETTING_MINIMUM_ORDER_AMOUNT <= Constant.FLOAT_TOTAL_AMOUNT) {
                            if (session.isUserLoggedIn()) {
                                if (values.size() > 0) {
                                    ApiConfig.AddMultipleProductInCart(session, getActivity(), values);
                                }

                                AddressListFragment.selectedAddress = "";
                                Fragment fragment = new AddressListFragment();
                                final Bundle bundle = new Bundle();
                                bundle.putString(Constant.FROM, "process");
                                bundle.putDouble("total", Constant.FLOAT_TOTAL_AMOUNT);
                                fragment.setArguments(bundle);
                                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                            } else {
                                startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("fromto", "checkout").putExtra("total", Constant.FLOAT_TOTAL_AMOUNT).putExtra(Constant.FROM, "checkout"));
                            }
                        } else {
                            Toast.makeText(activity, getString(R.string.msg_minimum_order_amount) + session.getData(Constant.currency) + ApiConfig.StringFormat(""+Constant.SETTING_MINIMUM_ORDER_AMOUNT), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.msg_sold_out), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnShowNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fm.popBackStack();
            }
        });

        return root;
    }

    private void GetOfflineCart() {
        cartrecycleview.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        if (databaseHelper.getTotalItemOfCart(activity) >= 1) {
            offlineCarts = new ArrayList<>();
            offlineCartAdapter = null;
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_CART_OFFLINE, Constant.GetVal);
            params.put(Constant.VARIANT_IDs, databaseHelper.getCartList().toString().replace("[", "").replace("]", "").replace("\"", ""));

            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                session.setData(Constant.TOTAL, jsonObject.getString(Constant.TOTAL));

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                                Gson g = new Gson();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    OfflineCart cart = g.fromJson(jsonObject1.toString(), OfflineCart.class);
                                    offlineCarts.add(cart);
                                }
                                offlineCartAdapter = new OfflineCartAdapter(getContext(), getActivity(), offlineCarts);
                                offlineCartAdapter.setHasStableIds(true);
                                cartrecycleview.setAdapter(offlineCartAdapter);
                                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new OfflineSwipeToDeleteCallback(offlineCartAdapter, activity));
                                itemTouchHelper.attachToRecyclerView(cartrecycleview);
                                lytTotal.setVisibility(View.VISIBLE);
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                cartrecycleview.setVisibility(View.VISIBLE);
                            } else {
                                cartrecycleview.setVisibility(View.GONE);
                                lytempty.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            cartrecycleview.setVisibility(View.VISIBLE);

                        }
                    }
                }
            }, getActivity(), Constant.GET_OFFLINE_CART_URL, params, false);
        } else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            cartrecycleview.setVisibility(View.VISIBLE);
            cartrecycleview.setVisibility(View.GONE);
            lytempty.setVisibility(View.VISIBLE);
        }
    }

    public void GetSettings(final Activity activity) {
        Session session = new Session(activity);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_TIMEZONE, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = objectbject.getJSONObject(Constant.SETTINGS);

                            session.setData(Constant.minimum_version_required, object.getString(Constant.minimum_version_required));
                            session.setData(Constant.is_version_system_on, object.getString(Constant.is_version_system_on));

                            session.setData(Constant.currency, object.getString(Constant.currency));

                            session.setData(Constant.min_order_amount, object.getString(Constant.min_order_amount));
                            session.setData(Constant.max_cart_items_count, object.getString(Constant.max_cart_items_count));
                            session.setData(Constant.area_wise_delivery_charge, object.getString(Constant.area_wise_delivery_charge));

                            getCartData();
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    private void getCartData() {
        cartrecycleview.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson g = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    try {
                                        Cart cart = g.fromJson(jsonObject1.toString(), Cart.class);

                                        float price;
                                        int qty = Integer.parseInt(cart.getQty());
                                        String taxPercentage = cart.getItems().get(0).getTax_percentage();

                                        if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                                            price = ((Float.parseFloat(cart.getItems().get(0).getPrice()) + ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
                                        } else {
                                            price = ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                                        }
                                        Constant.FLOAT_TOTAL_AMOUNT += (price * qty);
                                        carts.add(cart);
                                    } catch (Exception e) {

                                    }
                                } else {
                                    break;
                                }
                            }
                            cartAdapter = new CartAdapter(getContext(), getActivity(), carts);
                            cartAdapter.setHasStableIds(true);
                            cartrecycleview.setAdapter(cartAdapter);
                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(cartAdapter, activity));
                            itemTouchHelper.attachToRecyclerView(cartrecycleview);
                            lytTotal.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            cartrecycleview.setVisibility(View.VISIBLE);
                            total = Double.parseDouble(objectbject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));
                            Constant.TOTAL_CART_ITEM = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            SetData();
                        } else {
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            cartrecycleview.setVisibility(View.VISIBLE);
                            lytempty.setVisibility(View.VISIBLE);
                            lytTotal.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        cartrecycleview.setVisibility(View.VISIBLE);

                    }
                }
            }
        }, getActivity(), Constant.CART_URL, params, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (values.size() > 0) {
            ApiConfig.AddMultipleProductInCart(session, getActivity(), values);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.cart);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}