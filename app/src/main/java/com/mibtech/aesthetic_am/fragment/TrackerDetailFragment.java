package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.adapter.ItemsAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.OrderTracker;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class TrackerDetailFragment extends Fragment {
    public static ProgressBar pBar;
    public static Button btnCancel, btnreorder;
    public static LinearLayout lyttracker;
    View root;
    OrderTracker order;
    TextView txtorderotp, tvItemTotal, tvDeliveryCharge, tvTotal, tvPromoCode, tvPCAmount, tvWallet, tvFinalTotal, tvDPercent, tvDAmount;
    TextView txtcanceldetail, txtotherdetails, txtorderid, txtorderdate;
    RecyclerView recyclerView;
    View l4;
    RelativeLayout relativeLyt;
    LinearLayout returnLyt, lytPromo, lytWallet, lytPriceDetail, lytotp;
    double totalAfterTax = 0.0;
    Activity activity;
    String id;
    Session session;
    HashMap<String, String> hashMap;
    LinearLayout lytMainTracker;
    private ShimmerFrameLayout mShimmerViewContainer;
    ScrollView scrollView;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tracker_detail, container, false);
        activity = getActivity();
        session = new Session(activity);

        pBar = root.findViewById(R.id.pBar);
        lytPriceDetail = root.findViewById(R.id.lytPriceDetail);
        lytPromo = root.findViewById(R.id.lytPromo);
        lytWallet = root.findViewById(R.id.lytWallet);
        tvItemTotal = root.findViewById(R.id.tvItemTotal);
        tvDeliveryCharge = root.findViewById(R.id.tvDeliveryCharge);
        tvDAmount = root.findViewById(R.id.tvDAmount);
        tvDPercent = root.findViewById(R.id.tvDPercent);
        tvTotal = root.findViewById(R.id.tvTotal);
        tvPromoCode = root.findViewById(R.id.tvPromoCode);
        tvPCAmount = root.findViewById(R.id.tvPCAmount);
        tvWallet = root.findViewById(R.id.tvWallet);
        tvFinalTotal = root.findViewById(R.id.tvFinalTotal);
        txtorderid = root.findViewById(R.id.txtorderid);
        txtorderdate = root.findViewById(R.id.txtorderdate);
        relativeLyt = root.findViewById(R.id.relativeLyt);
        txtotherdetails = root.findViewById(R.id.txtotherdetails);
        txtcanceldetail = root.findViewById(R.id.txtcanceldetail);
        lyttracker = root.findViewById(R.id.lyttracker);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        btnCancel = root.findViewById(R.id.btncancel);
        btnreorder = root.findViewById(R.id.btnreorder);
        l4 = root.findViewById(R.id.l4);
        returnLyt = root.findViewById(R.id.returnLyt);
        txtorderotp = root.findViewById(R.id.txtorderotp);
        lytotp = root.findViewById(R.id.lytotp);
        lytMainTracker = root.findViewById(R.id.lytMainTracker);
        scrollView = root.findViewById(R.id.scrollView);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        hashMap = new HashMap<>();

        id = getArguments().getString("id");
        if (id.equals("")) {
            order = (OrderTracker) getArguments().getSerializable("model");
            id = order.getOrder_id();
            SetData(order);
        } else {
            getOrderDetails(id);
        }


        setHasOptionsMenu(true);

        btnreorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.re_order))
                        .setMessage(getString(R.string.reorder_msg))
                        .setPositiveButton(getString(R.string.proceed), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (getContext() != null) {
                                    GetReOrderData();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Map<String, String> params = new HashMap<>();
                params.put(Constant.UPDATE_ORDER_STATUS, Constant.GetVal);
                params.put(Constant.ID, order.getOrder_id());
                params.put(Constant.STATUS, Constant.CANCELLED);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                // Setting Dialog Message
                alertDialog.setTitle(activity.getResources().getString(R.string.cancel_order));
                alertDialog.setMessage(activity.getResources().getString(R.string.cancel_msg));
                alertDialog.setCancelable(false);
                final AlertDialog alertDialog1 = alertDialog.create();

                // Setting OK Button
                alertDialog.setPositiveButton(activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (pBar != null)
                            pBar.setVisibility(View.VISIBLE);
                        ApiConfig.RequestToVolley(new VolleyCallback() {
                            @Override
                            public void onSuccess(boolean result, String response) {
                                // System.out.println("================= " + response);
                                if (result) {
                                    try {
                                        JSONObject object = new JSONObject(response);
                                        if (!object.getBoolean(Constant.ERROR)) {
                                            btnCancel.setVisibility(View.GONE);
                                            ApiConfig.getWalletBalance(activity, new Session(activity));
                                        }
                                        Toast.makeText(activity, object.getString(Constant.MESSAGE), Toast.LENGTH_LONG).show();
                                        if (pBar != null)
                                            pBar.setVisibility(View.GONE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, activity, Constant.ORDERPROCESS_URL, params, false);

                    }
                });
                alertDialog.setNegativeButton(activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog1.dismiss();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
        });

        return root;
    }

    public void GetReOrderData() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_REORDER_DATA, Constant.GetVal);
        params.put(Constant.ID, id);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONObject(Constant.DATA).getJSONArray(Constant.ITEMS);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            hashMap.put(jsonArray.getJSONObject(i).getString(Constant.PRODUCT_VARIANT_ID), jsonArray.getJSONObject(i).getString(Constant.QUANTITY));
                        }
                        ApiConfig.AddMultipleProductInCart(session, activity, hashMap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, params, false);
    }

    public void getOrderDetails(String id) {
        scrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ORDERS, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.ORDER_ID, id);

        //  System.out.println("=====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        if (!jsonObject1.getBoolean(Constant.ERROR)) {
                            JSONObject jsonObject = jsonObject1.getJSONArray(Constant.DATA).getJSONObject(0);

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

                            SetData(orderTracker);

                        } else {
                            scrollView.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mShimmerViewContainer.stopShimmer();
                        }
                    } catch (JSONException e) {
                        scrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, params, false);
    }

    @SuppressLint("SetTextI18n")
    public void SetData(OrderTracker order) {
        String[] date = order.getDate_added().split("\\s+");
        txtorderid.setText(order.getOrder_id());
        if (order.getOtp().equals("0")) {
            lytotp.setVisibility(View.GONE);
        } else {
            txtorderotp.setText(order.getOtp());
        }
        txtorderdate.setText(date[0]);
        txtotherdetails.setText(getString(R.string.name_1) + order.getUsername() + getString(R.string.mobile_no_1) + order.getMobile() + getString(R.string.address_1) + order.getAddress());
        totalAfterTax = (Double.parseDouble(order.getTotal()) + Double.parseDouble(order.getDelivery_charge()) + Double.parseDouble(order.getTax_amt()));
        tvItemTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat(order.getTotal()));
        tvDeliveryCharge.setText("+ " + session.getData(Constant.currency) + ApiConfig.StringFormat(order.getDelivery_charge()));
        tvDPercent.setText(getString(R.string.discount) + "(" + order.getdPercent() + "%) :");
        tvDAmount.setText("- " + session.getData(Constant.currency) + ApiConfig.StringFormat(order.getdAmount()));
        tvTotal.setText(session.getData(Constant.currency) + totalAfterTax);
        tvPCAmount.setText("- " + session.getData(Constant.currency) + ApiConfig.StringFormat(order.getPromoDiscount()));
        tvWallet.setText("- " + session.getData(Constant.currency) + ApiConfig.StringFormat(order.getWalletBalance()));
        tvFinalTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat(order.getFinal_total()));

        try {
            if (!order.getStatus().equalsIgnoreCase("delivered") && !order.getStatus().equalsIgnoreCase("cancelled") && !order.getStatus().equalsIgnoreCase("returned")) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
        try {
            if (order.getStatus().equalsIgnoreCase("cancelled") || order.getStatus().equalsIgnoreCase("awaiting_payment")) {
                lyttracker.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                if (order.getStatus().equalsIgnoreCase("awaiting_payment")) {
                    txtcanceldetail.setVisibility(View.GONE);
                } else {
                    txtcanceldetail.setVisibility(View.VISIBLE);
                    txtcanceldetail.setText(getString(R.string.canceled_on) + order.getStatusdate());

                }
                lytPriceDetail.setVisibility(View.GONE);
            } else {
                lytPriceDetail.setVisibility(View.VISIBLE);
                if (order.getStatus().equals("returned")) {
                    l4.setVisibility(View.VISIBLE);
                    returnLyt.setVisibility(View.VISIBLE);
                }
                lyttracker.setVisibility(View.VISIBLE);

                for (int i = 0; i < order.getItemsList().size(); i++) {
                    hashMap.put(order.getItemsList().get(i).getProduct_variant_id(), order.getItemsList().get(i).getQuantity());
                }

                for (int i = 0; i < order.getOrderStatusArrayList().size(); i++) {
                    int img = getResources().getIdentifier("img" + i, "id", activity.getPackageName());
                    int view = getResources().getIdentifier("l" + i, "id", activity.getPackageName());
                    int txt = getResources().getIdentifier("txt" + i, "id", activity.getPackageName());
                    int textview = getResources().getIdentifier("txt" + i + "" + i, "id", activity.getPackageName());


                    if (img != 0 && root.findViewById(img) != null) {
                        ImageView imageView = root.findViewById(img);
                        imageView.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    }

                    if (view != 0 && root.findViewById(view) != null) {
                        View view1 = root.findViewById(view);
                        view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }

                    if (txt != 0 && root.findViewById(txt) != null) {
                        TextView view1 = root.findViewById(txt);
                        view1.setTextColor(getResources().getColor(R.color.black));
                    }

                    if (textview != 0 && root.findViewById(textview) != null) {
                        TextView view1 = root.findViewById(textview);
                        String str = order.getOrderStatusArrayList().get(i).getStatusdate();
                        String[] splited = str.split("\\s+");
                        view1.setText(splited[0] + "\n" + splited[1]);
                    }
                }
            }

            scrollView.setVisibility(View.VISIBLE);
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmer();
        } catch (Exception e) {
            scrollView.setVisibility(View.VISIBLE);
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmer();
        }
        recyclerView.setAdapter(new ItemsAdapter(activity, order.getItemsList(), "detail"));
        relativeLyt.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.order_track_detail);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(true);
    }
}