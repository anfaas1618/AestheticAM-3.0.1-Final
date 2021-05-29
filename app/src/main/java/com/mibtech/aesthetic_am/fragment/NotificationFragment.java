package com.mibtech.aesthetic_am.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.DrawerActivity;
import com.mibtech.aesthetic_am.adapter.NotificationAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.Notification;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class NotificationFragment extends Fragment {
    View root;

    RecyclerView recyclerView;
    ArrayList<Notification> notifications;
    NotificationAdapter notificationAdapter;
    SwipeRefreshLayout swipeLayout;
    NestedScrollView scrollView;
    RelativeLayout tvAlert;
    int total = 0;
    LinearLayoutManager linearLayoutManager;
    Activity activity;
    int offset = 0;
    Session session;
    boolean isLoadMore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_notification, container, false);

        activity = getActivity();

        session = new Session(activity);

        recyclerView = root.findViewById(R.id.recyclerView);
        swipeLayout = root.findViewById(R.id.swipeLayout);
        tvAlert = root.findViewById(R.id.tvAlert);
        scrollView = root.findViewById(R.id.scrollView);
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        setHasOptionsMenu(true);


        if (ApiConfig.isConnected(activity)) {
            getNotificationData();
        }

        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (notifications != null) {
                    notifications = null;
                }
                offset = 0;
                getNotificationData();
                swipeLayout.setRefreshing(false);
            }
        });


        return root;
    }


    void getNotificationData() {
        notifications = new ArrayList<>();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_NOTIFICATIONS, Constant.GetVal);
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT + 10);

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

                            Gson g = new Gson();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                if (jsonObject1 != null) {
                                    Notification notification = g.fromJson(jsonObject1.toString(), Notification.class);
                                    notifications.add(notification);
                                } else {
                                    break;
                                }

                            }
                            if (offset == 0) {
                                notificationAdapter = new NotificationAdapter(activity, notifications);
                                notificationAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(notificationAdapter);
                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            if (notifications.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == notifications.size() - 1) {
                                                        //bottom of list!
                                                        notifications.add(null);
                                                        notificationAdapter.notifyItemInserted(notifications.size() - 1);
                                                        offset += Constant.LOAD_ITEM_LIMIT + 10;
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.GET_NOTIFICATIONS, Constant.GetVal);
                                                        params.put(Constant.OFFSET, "" + offset);
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT + 10);

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {

                                                                if (result) {
                                                                    try {
                                                                        JSONObject objectbject1 = new JSONObject(response);
                                                                        if (!objectbject1.getBoolean(Constant.ERROR)) {

                                                                            session.setData(Constant.TOTAL, objectbject1.getString(Constant.TOTAL));

                                                                            notifications.remove(notifications.size() - 1);
                                                                            notificationAdapter.notifyItemRemoved(notifications.size());

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                            Gson g = new Gson();


                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                                                                if (jsonObject1 != null) {
                                                                                    Notification notification = g.fromJson(jsonObject1.toString(), Notification.class);
                                                                                    notifications.add(notification);
                                                                                } else {
                                                                                    break;
                                                                                }
                                                                            }
                                                                            notificationAdapter.notifyDataSetChanged();
                                                                            notificationAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {

                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.GET_SECTION_URL, params, false);

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
                            tvAlert.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.GET_SECTION_URL, params, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.notifications);
        activity.invalidateOptionsMenu();
        Session.setCount(Constant.UNREAD_NOTIFICATION_COUNT, 0, getContext());
        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_notifications, Session.getCount(Constant.UNREAD_NOTIFICATION_COUNT, getContext()));
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
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }


}