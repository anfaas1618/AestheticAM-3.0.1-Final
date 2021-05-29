package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.adapter.ProductLoadMoreAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.PriceVariation;
import com.mibtech.aesthetic_am.model.Product;


public class SearchFragment extends Fragment {
    public static ArrayList<Product> productArrayList;
    public static ProductLoadMoreAdapter productAdapter;
    public ProgressBar progressBar;
    View root;
    RecyclerView recyclerView;
    TextView noResult, msg;
    Session session;
    Activity activity;
    EditText searchview;
    boolean isGrid = false;
    int resource;
    ListView listView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        activity = getActivity();


        setHasOptionsMenu(true);

        recyclerView = root.findViewById(R.id.recyclerView);
        productArrayList = new ArrayList<>();
        noResult = root.findViewById(R.id.noResult);
        msg = root.findViewById(R.id.msg);
        progressBar = root.findViewById(R.id.pBar);
        searchview = root.findViewById(R.id.searchview);
        listView = root.findViewById(R.id.listView);
        progressBar.setVisibility(View.GONE);
        session = new Session(getContext());

        Constant.CartValues = new HashMap<>();
        String[] productsName = session.getData(Constant.GET_ALL_PRODUCTS_NAME).split(",");
        searchview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(productsName)));
        listView.setAdapter(arrayAdapter);

        searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arrayAdapter.getFilter().filter(searchview.getText().toString().trim());
                if (searchview.getText().toString().trim().length() > 0) {
                    listView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    searchview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close, 0);
                } else {
                    listView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    searchview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SearchRequest(v.getText().toString().trim());
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchview.setText(arrayAdapter.getItem(position));
                SearchRequest(arrayAdapter.getItem(position));
            }
        });

        searchview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (searchview.getText().toString().trim().length() > 0) {
                        if (event.getRawX() >= (searchview.getRight() - searchview.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            searchview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
                            searchview.setText("");
                        }
                        return true;
                    }
                }
                return false;
            }
        });


        recyclerView = root.findViewById(R.id.recyclerView);

        if (session.getGrid("grid")) {
            resource = R.layout.lyt_item_grid;
            isGrid = true;
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));

        } else {
            resource = R.layout.lyt_item_list;
            isGrid = false;
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }

        return root;
    }

    public void SearchRequest(final String query) {  //json request for product search
        Map<String, String> params = new HashMap<>();
        params.put(Constant.TYPE, Constant.PRODUCT_SEARCH);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.SEARCH, query);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        productArrayList = new ArrayList<>();
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                                        JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                                        for (int j = 0; j < pricearray.length(); j++) {
                                            JSONObject obj = pricearray.getJSONObject(j);
                                            String discountpercent = "0";
                                            if (!obj.getString(Constant.DISCOUNTED_PRICE).equals("0")) {
                                                discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                                            }
                                            priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                                        }
                                        productArrayList.add(new Product(jsonObject.getString(Constant.TAX_PERCENT), jsonObject.getString(Constant.ROW_ORDER), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR)));
                                    } catch (JSONException e) {

                                    }
                                }
                            } catch (Exception e) {

                            }

                            productAdapter = new ProductLoadMoreAdapter(activity, productArrayList, resource, "search");
                            recyclerView.setAdapter(productAdapter);
                            noResult.setVisibility(View.GONE);
                            msg.setVisibility(View.GONE);
                            listView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            noResult.setVisibility(View.VISIBLE);
                            msg.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            productArrayList.clear();
                            recyclerView.setAdapter(new ProductLoadMoreAdapter(activity, productArrayList, resource, "search"));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.PRODUCT_SEARCH_URL, params, false);

    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.search);
        activity.invalidateOptionsMenu();
        searchview.requestFocus();
        showSoftKeyboard(searchview);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Constant.CartValues.size() > 0) {
            ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
        }
    }
}