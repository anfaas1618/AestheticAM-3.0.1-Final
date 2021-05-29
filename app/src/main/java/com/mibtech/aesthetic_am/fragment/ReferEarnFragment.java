package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;


public class ReferEarnFragment extends Fragment {
    View root;
    TextView txtrefercoin, txtcode, txtcopy, txtinvite;
    Session session;
    String preText = "";
    Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_refer_earn, container, false);
        activity = getActivity();
        setHasOptionsMenu(true);


        session = new Session(getContext());
        txtrefercoin = root.findViewById(R.id.txtrefercoin);
        if (session.getData(Constant.refer_earn_method).equals("rupees")) {
            preText = session.getData(Constant.currency) + session.getData(Constant.refer_earn_bonus);
        } else {
            preText = session.getData(Constant.refer_earn_bonus) + "% ";
        }
        txtrefercoin.setText(getString(R.string.refer_text_1) + preText + getString(R.string.refer_text_2) + session.getData(Constant.currency) + session.getData(Constant.min_refer_earn_order_amount) + getString(R.string.refer_text_3) + session.getData(Constant.currency) + session.getData(Constant.max_refer_earn_amount) + ".");
        txtcode = root.findViewById(R.id.txtcode);
        txtcopy = root.findViewById(R.id.txtcopy);
        txtinvite = root.findViewById(R.id.txtinvite);

        txtinvite.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(getContext(), R.drawable.ic_share), null, null, null);
        txtcode.setText(session.getData(Constant.REFERRAL_CODE));
        txtcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", txtcode.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), R.string.refer_code_copied, Toast.LENGTH_SHORT).show();
            }
        });

        txtinvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtcode.getText().toString().equals("code")) {
                    try {
                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.refer_share_msg_1)
                                + getResources().getString(R.string.app_name) + getString(R.string.refer_share_msg_2)
                                + "\n " + Constant.WebsiteUrl + "refer/" + txtcode.getText().toString());
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_frnd_title)));
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.refer_code_alert_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.refere);
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