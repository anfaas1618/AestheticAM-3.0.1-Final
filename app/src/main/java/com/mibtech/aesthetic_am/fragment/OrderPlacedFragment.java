package com.mibtech.aesthetic_am.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class OrderPlacedFragment extends Fragment {
    View root;
    Activity activity;
    ProgressBar progressBar;
    Button btnShopping, btnSummary;
    LottieAnimationView lottieAnimationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_order_placed, container, false);
        activity = getActivity();
        Session session = new Session(activity);
        progressBar = root.findViewById(R.id.progressBar);
        btnShopping = root.findViewById(R.id.btnShopping);
        btnSummary = root.findViewById(R.id.btnSummary);
        lottieAnimationView = root.findViewById(R.id.lottieAnimationView);
        setHasOptionsMenu(true);

        RemoveAllItemFromCart(activity, session);

        return root;
    }

    public void RemoveAllItemFromCart(final Activity activity, Session session) {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.REMOVE_FROM_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            Constant.CartValues.clear();
                            Constant.TOTAL_CART_ITEM = 0;
                            activity.invalidateOptionsMenu();
                            progressBar.setVisibility(View.GONE);
                            lottieAnimationView.playAnimation();

                            btnSummary.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MainActivity.homeClicked = false;
                                    MainActivity.categoryClicked = false;
                                    MainActivity.favoriteClicked = false;
                                    MainActivity.trackingClicked = false;
                                    MainActivity.active = null;
                                    startActivity(new Intent(activity, MainActivity.class).putExtra(Constant.FROM, "tracker").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }
                            });

                            btnShopping.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(activity, MainActivity.class).putExtra(Constant.FROM, "").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                }
                            });

                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);

                    }
                }
            }
        }, activity, Constant.CART_URL, params, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.toolbar.setVisibility(View.GONE);
        lottieAnimationView.setAnimation("placed-order.json");
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
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }

}