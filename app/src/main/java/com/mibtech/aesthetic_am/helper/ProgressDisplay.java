package com.mibtech.aesthetic_am.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.mibtech.aesthetic_am.R;

public class ProgressDisplay {

    public static ProgressBar mProgressBar;

    @SuppressLint("UseCompatLoadingForDrawables")
    public ProgressDisplay(Activity context) {
        try {
            ViewGroup layout = (ViewGroup) (context).findViewById(android.R.id.content).getRootView();

            mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
            mProgressBar.setIndeterminateDrawable(context.getDrawable(R.drawable.custom_progress_dialog));
            mProgressBar.setIndeterminate(true);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rl = new RelativeLayout(context);
            rl.setGravity(Gravity.CENTER);
            rl.addView(mProgressBar);
            layout.addView(rl, params);
            hideProgress();
        } catch (Exception e) {

        }
    }


    public void showProgress() {
        if (mProgressBar.getVisibility() == View.GONE)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);

    }
}
