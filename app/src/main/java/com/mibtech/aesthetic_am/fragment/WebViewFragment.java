package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.VolleyCallback;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class WebViewFragment extends Fragment {
    public ProgressBar prgLoading;
    public WebView mWebView;
    public String type;
    View root;
    Activity activity;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_web_view, container, false);
        setHasOptionsMenu(true);
        activity = getActivity();
        type = getArguments().getString("type");

        prgLoading = root.findViewById(R.id.prgLoading);
        mWebView = root.findViewById(R.id.webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        try {
            if (ApiConfig.isConnected(activity)) {
                switch (type) {
                    case "Privacy Policy":
                        GetContent(Constant.GET_PRIVACY, "privacy");
                        break;
                    case "Terms & Conditions":
                        GetContent(Constant.GET_TERMS, "terms");
                        break;
                    case "Contact Us":
                        GetContent(Constant.GET_CONTACT, "contact");
                        break;
                    case "About Us":
                        GetContent(Constant.GET_ABOUT_US, "about");
                        break;
                }
                activity.invalidateOptionsMenu();
            }
        } catch (Exception e) {

        }

        return root;
    }


    public void GetContent(final String type, final String key) {
        prgLoading.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(type, Constant.GetVal);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (!obj.getBoolean(Constant.ERROR)) {

                            String privacyStr = obj.getString(key);
                            mWebView.setVerticalScrollBarEnabled(true);
                            mWebView.loadDataWithBaseURL("", privacyStr, "text/html", "UTF-8", "");

                            prgLoading.setVisibility(View.GONE);
                        } else {
                            prgLoading.setVisibility(View.GONE);
                            Toast.makeText(getContext(), obj.getString(Constant.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                        prgLoading.setVisibility(View.GONE);
                    } catch (JSONException e) {

                        prgLoading.setVisibility(View.GONE);

                    }
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getArguments().getString("type");
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
        menu.findItem(R.id.toolbar_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}