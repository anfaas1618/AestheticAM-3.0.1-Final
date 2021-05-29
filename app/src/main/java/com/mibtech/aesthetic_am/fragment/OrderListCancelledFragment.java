package com.mibtech.aesthetic_am.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.adapter.TrackerAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.OrderTracker;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class OrderListCancelledFragment extends Fragment {
    RecyclerView recyclerView;
    TextView nodata;
    Session session;
    Activity activity;
    View root;
    ArrayList<OrderTracker> orderTrackerArrayList;
    TrackerAdapter trackerAdapter;
    private int offset = 0;
    private int total = 0;
    private NestedScrollView scrollView;
    SwipeRefreshLayout swipeLayout;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_order_list, container, false);

        activity = getActivity();
        session = new Session(activity);
        recyclerView = root.findViewById(R.id.recyclerView);
        scrollView = root.findViewById(R.id.scrollView);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        nodata = root.findViewById(R.id.nodata);
        setHasOptionsMenu(true);

        swipeLayout = root.findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                swipeLayout.setRefreshing(false);
                getAllOrders();
            }
        });

        getAllOrders();

        return root;
    }

    void getAllOrders() {
        recyclerView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        orderTrackerArrayList = new ArrayList<>();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ORDERS, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.STATUS, Constant.CANCELLED);
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String laststatusname = null, laststatusdate = null;
                                        JSONArray statusarray = jsonObject.getJSONArray("status");
                                        ArrayList<OrderTracker> statusarraylist = new ArrayList<>();

                                        for (int k = 0; k < statusarray.length(); k++) {
                                            JSONArray sarray = statusarray.getJSONArray(k);
                                            String sname = sarray.getString(0);
                                            String sdate = sarray.getString(1);

                                            statusarraylist.add(new OrderTracker(sname, sdate));
                                            laststatusname = sname;
                                            laststatusdate = sdate;
                                        }

                                        ArrayList<OrderTracker> itemList = new ArrayList<>();
                                        JSONArray itemsarray = jsonObject.getJSONArray("items");

                                        for (int j = 0; j < itemsarray.length(); j++) {

                                            JSONObject itemobj = itemsarray.getJSONObject(j);

                                            JSONArray statusarray1 = itemobj.getJSONArray("status");
                                            ArrayList<OrderTracker> statusList = new ArrayList<>();

                                            for (int k = 0; k < statusarray1.length(); k++) {
                                                JSONArray sarray = statusarray1.getJSONArray(k);
                                                String sname = sarray.getString(0);
                                                String sdate = sarray.getString(1);
                                                statusList.add(new OrderTracker(sname, sdate));
                                            }
                                            itemList.add(new OrderTracker(itemobj.getString(Constant.ID),
                                                    itemobj.getString(Constant.ORDER_ID),
                                                    itemobj.getString(Constant.PRODUCT_VARIANT_ID),
                                                    itemobj.getString(Constant.QUANTITY),
                                                    itemobj.getString(Constant.PRICE),
                                                    itemobj.getString(Constant.DISCOUNT),
                                                    itemobj.getString(Constant.SUB_TOTAL),
                                                    itemobj.getString(Constant.DELIVER_BY),
                                                    itemobj.getString(Constant.NAME),
                                                    itemobj.getString(Constant.IMAGE),
                                                    itemobj.getString(Constant.MEASUREMENT),
                                                    itemobj.getString(Constant.UNIT),
                                                    jsonObject.getString(Constant.PAYMENT_METHOD),
                                                    itemobj.getString(Constant.ACTIVE_STATUS),
                                                    itemobj.getString(Constant.DATE_ADDED),
                                                    statusList,
                                                    itemobj.getString(Constant.RETURN_STATUS),
                                                    itemobj.getString(Constant.CANCELLABLE_STATUS),
                                                    itemobj.getString(Constant.TILL_STATUS),
                                                    itemobj.getString(Constant.DISCOUNTED_PRICE),
                                                    itemobj.getString(Constant.TAX_PERCENT)));
                                        }
                                        OrderTracker orderTracker = new OrderTracker(
                                                jsonObject.getString(Constant.OTP),
                                                jsonObject.getString(Constant.USER_ID),
                                                jsonObject.getString(Constant.ID),
                                                jsonObject.getString(Constant.DATE_ADDED),
                                                laststatusname, laststatusdate,
                                                statusarraylist,
                                                jsonObject.getString(Constant.MOBILE),
                                                jsonObject.getString(Constant.DELIVERY_CHARGE),
                                                jsonObject.getString(Constant.PAYMENT_METHOD),
                                                jsonObject.getString(Constant.ADDRESS),
                                                jsonObject.getString(Constant.TOTAL),
                                                jsonObject.getString(Constant.FINAL_TOTAL),
                                                jsonObject.getString(Constant.TAX_AMOUNT),
                                                jsonObject.getString(Constant.TAX_PERCENT),
                                                jsonObject.getString(Constant.KEY_WALLET_BALANCE),
                                                jsonObject.getString(Constant.PROMO_CODE),
                                                jsonObject.getString(Constant.PROMO_DISCOUNT),
                                                jsonObject.getString(Constant.DISCOUNT),
                                                jsonObject.getString(Constant.DISCOUNT_AMT),
                                                jsonObject.getString(Constant.USER_NAME), itemList,
                                                jsonObject.getString(Constant.ACTIVE_STATUS));
                                        orderTrackerArrayList.add(orderTracker);
                                    }
                                } else {
                                    break;
                                }

                            }
                            if (offset == 0) {
                                trackerAdapter = new TrackerAdapter(getContext(), activity, orderTrackerArrayList);
                                recyclerView.setAdapter(trackerAdapter);
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    private boolean isLoadMore;

                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (orderTrackerArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == orderTrackerArrayList.size() - 1) {
                                                        //bottom of list!
                                                        orderTrackerArrayList.add(null);
                                                        trackerAdapter.notifyItemInserted(orderTrackerArrayList.size() - 1);

                                                        offset += Constant.LOAD_ITEM_LIMIT;
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.GET_ORDERS, Constant.GetVal);
                                                        params.put(Constant.USER_ID, session.getData(Constant.ID));
                                                        params.put(Constant.STATUS, Constant.CANCELLED);
                                                        params.put(Constant.OFFSET, "" + offset);
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {

                                                                if (result) {
                                                                    try {
                                                                        // System.out.println("====product  " + response);
                                                                        JSONObject objectbject1 = new JSONObject(response);
                                                                        if (!objectbject1.getBoolean(Constant.ERROR)) {

                                                                            session.setData(Constant.TOTAL, objectbject1.getString(Constant.TOTAL));

                                                                            orderTrackerArrayList.remove(orderTrackerArrayList.size() - 1);
                                                                            trackerAdapter.notifyItemRemoved(orderTrackerArrayList.size());

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                            Gson g = new Gson();


                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                                                                if (jsonObject1 != null) {
                                                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                                                    String laststatusname = null, laststatusdate = null;
                                                                                    JSONArray statusarray = jsonObject.getJSONArray("status");
                                                                                    ArrayList<OrderTracker> statusarraylist = new ArrayList<>();

                                                                                    for (int k = 0; k < statusarray.length(); k++) {
                                                                                        JSONArray sarray = statusarray.getJSONArray(k);
                                                                                        String sname = sarray.getString(0);
                                                                                        String sdate = sarray.getString(1);

                                                                                        statusarraylist.add(new OrderTracker(sname, sdate));
                                                                                        laststatusname = sname;
                                                                                        laststatusdate = sdate;
                                                                                    }

                                                                                    ArrayList<OrderTracker> itemList = new ArrayList<>();
                                                                                    JSONArray itemsarray = jsonObject.getJSONArray("items");

                                                                                    for (int j = 0; j < itemsarray.length(); j++) {

                                                                                        JSONObject itemobj = itemsarray.getJSONObject(j);

                                                                                        JSONArray statusarray1 = itemobj.getJSONArray("status");
                                                                                        ArrayList<OrderTracker> statusList = new ArrayList<>();

                                                                                        for (int k = 0; k < statusarray1.length(); k++) {
                                                                                            JSONArray sarray = statusarray1.getJSONArray(k);
                                                                                            String sname = sarray.getString(0);
                                                                                            String sdate = sarray.getString(1);
                                                                                            statusList.add(new OrderTracker(sname, sdate));
                                                                                        }
                                                                                        itemList.add(new OrderTracker(itemobj.getString(Constant.ID),
                                                                                                itemobj.getString(Constant.ORDER_ID),
                                                                                                itemobj.getString(Constant.PRODUCT_VARIANT_ID),
                                                                                                itemobj.getString(Constant.QUANTITY),
                                                                                                itemobj.getString(Constant.PRICE),
                                                                                                itemobj.getString(Constant.DISCOUNT),
                                                                                                itemobj.getString(Constant.SUB_TOTAL),
                                                                                                itemobj.getString(Constant.DELIVER_BY),
                                                                                                itemobj.getString(Constant.NAME),
                                                                                                itemobj.getString(Constant.IMAGE),
                                                                                                itemobj.getString(Constant.MEASUREMENT),
                                                                                                itemobj.getString(Constant.UNIT),
                                                                                                jsonObject.getString(Constant.PAYMENT_METHOD),
                                                                                                itemobj.getString(Constant.ACTIVE_STATUS),
                                                                                                itemobj.getString(Constant.DATE_ADDED),
                                                                                                statusList,
                                                                                                itemobj.getString(Constant.RETURN_STATUS),
                                                                                                itemobj.getString(Constant.CANCELLABLE_STATUS),
                                                                                                itemobj.getString(Constant.TILL_STATUS),
                                                                                                itemobj.getString(Constant.DISCOUNTED_PRICE),
                                                                                                itemobj.getString(Constant.TAX_PERCENT)));
                                                                                    }
                                                                                    OrderTracker orderTracker = new OrderTracker(
                                                                                            jsonObject.getString(Constant.OTP),
                                                                                            jsonObject.getString(Constant.USER_ID),
                                                                                            jsonObject.getString(Constant.ID),
                                                                                            jsonObject.getString(Constant.DATE_ADDED),
                                                                                            laststatusname, laststatusdate,
                                                                                            statusarraylist,
                                                                                            jsonObject.getString(Constant.MOBILE),
                                                                                            jsonObject.getString(Constant.DELIVERY_CHARGE),
                                                                                            jsonObject.getString(Constant.PAYMENT_METHOD),
                                                                                            jsonObject.getString(Constant.ADDRESS),
                                                                                            jsonObject.getString(Constant.TOTAL),
                                                                                            jsonObject.getString(Constant.FINAL_TOTAL),
                                                                                            jsonObject.getString(Constant.TAX_AMOUNT),
                                                                                            jsonObject.getString(Constant.TAX_PERCENT),
                                                                                            jsonObject.getString(Constant.KEY_WALLET_BALANCE),
                                                                                            jsonObject.getString(Constant.PROMO_CODE),
                                                                                            jsonObject.getString(Constant.PROMO_DISCOUNT),
                                                                                            jsonObject.getString(Constant.DISCOUNT),
                                                                                            jsonObject.getString(Constant.DISCOUNT_AMT),
                                                                                            jsonObject.getString(Constant.USER_NAME), itemList,
                                                                                            jsonObject.getString(Constant.ACTIVE_STATUS));
                                                                                    orderTrackerArrayList.add(orderTracker);
                                                                                } else {
                                                                                    break;
                                                                                }

                                                                            }
                                                                            trackerAdapter.notifyDataSetChanged();
                                                                            trackerAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        mShimmerViewContainer.stopShimmer();
                                                                        mShimmerViewContainer.setVisibility(View.GONE);
                                                                        recyclerView.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.ORDERPROCESS_URL, params, false);

                                                    }
                                                    isLoadMore = true;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodata.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                }
            }
        }, activity, Constant.ORDERPROCESS_URL, params, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {

        }
    }
}