package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.adapter.CategoryAdapter;
import com.mibtech.aesthetic_am.adapter.OfferAdapter;
import com.mibtech.aesthetic_am.adapter.SectionAdapter;
import com.mibtech.aesthetic_am.adapter.SliderAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.Category;
import com.mibtech.aesthetic_am.model.Slider;


public class HomeFragment extends Fragment {

    public Session session;
    public static ArrayList<Category> categoryArrayList, sectionList;
    ArrayList<Slider> sliderArrayList;
    Activity activity;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeLayout;
    View root;
    int timerDelay = 0, timerWaiting = 0;
    EditText searchview;
    RecyclerView categoryRecyclerView, sectionView, offerView;
    ViewPager mPager;
    LinearLayout mMarkersLayout;
    int size;
    Timer swipeTimer;
    Handler handler;
    Runnable Update;
    int currentPage = 0;
    LinearLayout lytCategory, lytSearchview;
    Menu menu;
    TextView tvMore;
    boolean searchVisible = false;
    private ArrayList<String> offerList;
    private ShimmerFrameLayout mShimmerViewContainer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        session = new Session(getContext());
        activity = getActivity();

        timerDelay = 3000;
        timerWaiting = 3000;
        setHasOptionsMenu(true);

        swipeLayout = root.findViewById(R.id.swipeLayout);

        categoryRecyclerView = root.findViewById(R.id.categoryrecycleview);

        sectionView = root.findViewById(R.id.sectionView);
        sectionView.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionView.setNestedScrollingEnabled(false);

        offerView = root.findViewById(R.id.offerView);
        offerView.setLayoutManager(new LinearLayoutManager(getContext()));
        offerView.setNestedScrollingEnabled(false);

        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        mMarkersLayout = root.findViewById(R.id.layout_markers);
        lytCategory = root.findViewById(R.id.lytCategory);
        lytSearchview = root.findViewById(R.id.lytSearchview);
        tvMore = root.findViewById(R.id.tvMore);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);

        searchview = root.findViewById(R.id.searchview);

        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Rect scrollBounds = new Rect();
                    nestedScrollView.getHitRect(scrollBounds);
                    if (!lytSearchview.getLocalVisibleRect(scrollBounds) || scrollBounds.height() < lytSearchview.getHeight()) {
                        searchVisible = true;
                        menu.findItem(R.id.toolbar_search).setVisible(true);
                    } else {
                        searchVisible = false;
                        menu.findItem(R.id.toolbar_search).setVisible(false);
                    }
                    activity.invalidateOptionsMenu();
                }
            });
        }

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.categoryClicked) {
                    MainActivity.fm.beginTransaction().add(R.id.container, MainActivity.categoryFragment).show(MainActivity.categoryFragment).hide(MainActivity.active).commit();
                    MainActivity.categoryClicked = true;
                } else {
                    MainActivity.fm.beginTransaction().show(MainActivity.categoryFragment).hide(MainActivity.active).commit();
                }
                MainActivity.bottomNavigationView.setItemActiveIndex(1);
                MainActivity.active = MainActivity.categoryFragment;
            }
        });

        searchview.setOnTouchListener((View v, MotionEvent event) -> {
            MainActivity.fm.beginTransaction().add(R.id.container, new SearchFragment()).addToBackStack(null).commit();
            return false;
        });

        lytSearchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fm.beginTransaction().add(R.id.container, new SearchFragment()).addToBackStack(null).commit();
            }
        });

        mPager = root.findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, getContext());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        categoryArrayList = new ArrayList<>();

        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeTimer != null) {
                    swipeTimer.cancel();
                }
                if (ApiConfig.isConnected(getActivity())) {
                    ApiConfig.getWalletBalance(activity, session);
                    if (new Session(activity).isUserLoggedIn()) {
                        ApiConfig.getWalletBalance(activity, new Session(activity));
                    }
                    GetHomeData();
                }
                swipeLayout.setRefreshing(false);
            }
        });

        if (ApiConfig.isConnected(getActivity())) {
            try {
                if (!getArguments().getString("json").isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(getArguments().getString("json"));
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            offerList = new ArrayList<>();
                            sliderArrayList = new ArrayList<>();
                            categoryArrayList = new ArrayList<>();
                            sectionList = new ArrayList<>();

                            offerList.clear();
                            sliderArrayList.clear();
                            categoryArrayList.clear();
                            sectionList.clear();

                            GetOfferImage(jsonObject.getJSONArray(Constant.OFFER_IMAGES));
                            GetCategory(jsonObject);
                            SectionProductRequest(jsonObject.getJSONArray(Constant.SECTIONS));
                            GetSlider(jsonObject.getJSONArray(Constant.SLIDER_IMAGES));
                        } else {
                            nestedScrollView.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mShimmerViewContainer.stopShimmer();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    GetHomeData();
                }
                if (new Session(activity).isUserLoggedIn()) {
                    ApiConfig.getWalletBalance(activity, new Session(activity));
                }
            } catch (Exception e) {
                GetHomeData();
            }
        } else {
            nestedScrollView.setVisibility(View.VISIBLE);
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmer();
        }

        return root;
    }

    public void GetHomeData() {
        nestedScrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        if (session.isUserLoggedIn()) {
            params.put(Constant.USER_ID, session.getData(Constant.ID));
        }
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            offerList = new ArrayList<>();
                            sliderArrayList = new ArrayList<>();
                            categoryArrayList = new ArrayList<>();
                            sectionList = new ArrayList<>();

                            offerList.clear();
                            sliderArrayList.clear();
                            categoryArrayList.clear();
                            sectionList.clear();

                            GetOfferImage(jsonObject.getJSONArray(Constant.OFFER_IMAGES));
                            GetCategory(jsonObject);
                            SectionProductRequest(jsonObject.getJSONArray(Constant.SECTIONS));
                            GetSlider(jsonObject.getJSONArray(Constant.SLIDER_IMAGES));
                        } else {
                            nestedScrollView.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mShimmerViewContainer.stopShimmer();
                        }
                    } catch (JSONException e) {
                        nestedScrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();

                    }
                }
            }
        }, getActivity(), Constant.GET_ALL_DATA_URL, params, false);
    }

    public void GetOfferImage(JSONArray jsonArray) {
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    offerList.add(object.getString(Constant.IMAGE));
                }
                offerView.setAdapter(new OfferAdapter(offerList, R.layout.offer_lyt));
            }
        } catch (JSONException e) {

        }

    }

    void GetCategory(JSONObject object) {
        try {
            int visible_count = 0;
            int column_count = 0;

            JSONArray jsonArray = object.getJSONArray(Constant.CATEGORIES);

            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Category category = new Gson().fromJson(jsonObject.toString(), Category.class);
                    categoryArrayList.add(category);
                }

                if (!object.getString("style").equals("")) {
                    if (object.getString("style").equals("style_1")) {
                        visible_count = Integer.parseInt(object.getString("visible_count"));
                        column_count = Integer.parseInt(object.getString("column_count"));
                        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), column_count));
                        categoryRecyclerView.setAdapter(new CategoryAdapter(getContext(), getActivity(), categoryArrayList, R.layout.lyt_category_grid, "home", visible_count));
                    } else if (object.getString("style").equals("style_2")) {
                        visible_count = Integer.parseInt(object.getString("visible_count"));
                        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        categoryRecyclerView.setAdapter(new CategoryAdapter(getContext(), getActivity(), categoryArrayList, R.layout.lyt_category_list, "home", visible_count));
                    }
                } else {
                    categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    categoryRecyclerView.setAdapter(new CategoryAdapter(getContext(), getActivity(), categoryArrayList, R.layout.lyt_category_list, "home", 6));
                }
            } else {
                lytCategory.setVisibility(View.GONE);
            }
        } catch (JSONException e) {

        }
    }

    public void SectionProductRequest(JSONArray jsonArray) {  //json request for product search
        try {
            for (int j = 0; j < jsonArray.length(); j++) {
                Category section = new Category();
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                section.setName(jsonObject.getString(Constant.TITLE));
                section.setId(jsonObject.getString(Constant.ID));
                section.setStyle(jsonObject.getString(Constant.SECTION_STYLE));
                section.setSubtitle(jsonObject.getString(Constant.SHORT_DESC));
                JSONArray productArray = jsonObject.getJSONArray(Constant.PRODUCTS);
                section.setProductList(ApiConfig.GetProductList(productArray));
                sectionList.add(section);
            }
            sectionView.setVisibility(View.VISIBLE);
            SectionAdapter sectionAdapter = new SectionAdapter(getContext(), getActivity(), sectionList);
            sectionView.setAdapter(sectionAdapter);

        } catch (JSONException e) {

        }

    }

    void GetSlider(JSONArray jsonArray) {
        try {
            size = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                sliderArrayList.add(new Slider(jsonObject.getString(Constant.TYPE), jsonObject.getString(Constant.TYPE_ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.IMAGE)));
            }
            mPager.setAdapter(new SliderAdapter(sliderArrayList, getActivity(), R.layout.lyt_slider, "home"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, getContext());
            handler = new Handler();
            Update = () -> {
                if (currentPage == size) {
                    currentPage = 0;
                }
                try {
                    mPager.setCurrentItem(currentPage++, true);
                } catch (Exception e) {

                }
            };
            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, timerDelay, timerWaiting);

        } catch (JSONException e) {

        }
        nestedScrollView.setVisibility(View.VISIBLE);
        mShimmerViewContainer.setVisibility(View.GONE);
        mShimmerViewContainer.stopShimmer();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.invalidateOptionsMenu();
        ApiConfig.GetSettings(activity);
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        this.menu = menu;
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(searchVisible);
    }

}