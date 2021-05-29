package com.mibtech.aesthetic_am.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.LoginActivity;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.mibtech.aesthetic_am.activity.MainActivity.active;
import static com.mibtech.aesthetic_am.activity.MainActivity.bottomNavigationView;
import static com.mibtech.aesthetic_am.activity.MainActivity.fm;
import static com.mibtech.aesthetic_am.activity.MainActivity.homeClicked;
import static com.mibtech.aesthetic_am.activity.MainActivity.homeFragment;


public class TrackOrderFragment extends Fragment {
    View root;
    LinearLayout lytempty, lytdata;
    Session session;
    String[] tabs;
    TabLayout tabLayout;
    ViewPager viewPager;
    TrackOrderFragment.ViewPagerAdapter adapter;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_track_order, container, false);

        activity = getActivity();

        session = new Session(activity);
        tabs = new String[]{getString(R.string.all), getString(R.string.in_process1), getString(R.string.shipped1), getString(R.string.delivered1), getString(R.string.cancelled1), getString(R.string.returned1)};
        lytempty = root.findViewById(R.id.lytempty);
        lytdata = root.findViewById(R.id.lytdata);
        viewPager = root.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = root.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        if (session.isUserLoggedIn()) {
            if (ApiConfig.isConnected(activity)) {
                ApiConfig.getWalletBalance(activity, new Session(activity));
                setupViewPager(viewPager);
            }
        } else {
            startActivity(new Intent(activity, LoginActivity.class).putExtra(Constant.FROM, "tracker"));
        }

        root.findViewById(R.id.btnorder).setOnClickListener(view -> {
            fm.beginTransaction().show(homeFragment).hide(active).commit();
            bottomNavigationView.setItemActiveIndex(0);
            homeClicked = true;
        });

        return root;
    }

    void setupViewPager(ViewPager viewPager) {
        adapter = new TrackOrderFragment.ViewPagerAdapter(fm);
        adapter.addFrag(new OrderListAllFragment(), tabs[0]);
        adapter.addFrag(new OrderListReceivedFragment(), tabs[1]);
        adapter.addFrag(new OrderListShippedFragment(), tabs[2]);
        adapter.addFrag(new OrderListDeliveredFragment(), tabs[3]);
        adapter.addFrag(new OrderListCancelledFragment(), tabs[4]);
        adapter.addFrag(new OrderListReturnedFragment(), tabs[5]);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string._title_order_track);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(true);
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        final List<Fragment> mFragmentList = new ArrayList<>();
        final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            data.putInt("pos", position);
            Fragment fragment = null;
            if (position == 0) {
                fragment = new OrderListAllFragment();
            } else if (position == 1) {
                fragment = new OrderListReceivedFragment();
            } else if (position == 2) {
                fragment = new OrderListShippedFragment();
            } else if (position == 3) {
                fragment = new OrderListDeliveredFragment();
            } else if (position == 4) {
                fragment = new OrderListCancelledFragment();
            } else if (position == 5) {
                fragment = new OrderListReturnedFragment();
            }
            assert fragment != null;
            fragment.setArguments(data);
            return fragment;
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(@NotNull Object object) {
            return POSITION_NONE;
        }
    }

}