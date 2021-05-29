package com.mibtech.aesthetic_am.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.fragment.TrackerDetailFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.model.OrderTracker;


public class TrackerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    final Activity activity;
    final ArrayList<OrderTracker> orderTrackerArrayList;
    final Context context;
    public boolean isLoading;


    public TrackerAdapter(Context context, Activity activity, ArrayList<OrderTracker> orderTrackerArrayList) {
        this.context = context;
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
    }

    public void add(int position, OrderTracker item) {
        orderTrackerArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_trackorder, parent, false);
            return new TrackerHolderItems(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof TrackerHolderItems) {
            final TrackerHolderItems holder = (TrackerHolderItems) holderparent;
            final OrderTracker order = orderTrackerArrayList.get(position);
            holder.txtorderid.setText(activity.getString(R.string.order_number) + order.getOrder_id());
            String[] date = order.getDate_added().split("\\s+");
            holder.txtorderdate.setText(activity.getString(R.string.ordered_on) + date[0]);
            holder.txtorderamount.setText(activity.getString(R.string.for_amount_on) + new Session(context).getData(Constant.currency) + ApiConfig.StringFormat(order.getTotal()));

            holder.carddetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new TrackerDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", "");
                    bundle.putSerializable("model", order);
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            });

            holder.status.setText(ApiConfig.toTitleCase(order.getStatus()));

            if (order.getActiveStatus().equals(Constant.RECEIVED)) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.received_status_bg));
            } else if (order.getActiveStatus().equals(Constant.SHIPPED)) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.shipped_status_bg));
            } else if (order.getActiveStatus().equals(Constant.DELIVERED)) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.delivered_status_bg));
            } else if (order.getActiveStatus().equals(Constant.CANCELLED)) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.returned_and_cancel_status_bg));
            } else if (order.getActiveStatus().equals(Constant.RETURNED)) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.returned_and_cancel_status_bg));
            } else if (order.getActiveStatus().equalsIgnoreCase(Constant.AWAITING_PAYMENT)) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.awaiting_status_bg));
                holder.status.setText(R.string.awaiting_payment);
            }


            ArrayList<String> items = new ArrayList<>();
            for (int i = 0; i < order.getItemsList().size(); i++) {
                items.add(order.getItemsList().get(i).getName());
            }
            holder.tvItems.setText(Arrays.toString(items.toArray()).replace("]", "").replace("[", ""));
            holder.tvTotalItems.setText(items.size() + activity.getString(R.string.item));

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return orderTrackerArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public class TrackerHolderItems extends RecyclerView.ViewHolder {
        final TextView txtorderid;
        final TextView txtorderdate;
        final TextView carddetail;
        final TextView txtorderamount;
        final TextView tvTotalItems;
        final TextView tvItems;
        final TextView status;
        final CardView cardView;

        public TrackerHolderItems(View itemView) {
            super(itemView);
            txtorderid = itemView.findViewById(R.id.txtorderid);
            txtorderdate = itemView.findViewById(R.id.txtorderdate);
            carddetail = itemView.findViewById(R.id.carddetail);
            txtorderamount = itemView.findViewById(R.id.txtorderamount);
            tvTotalItems = itemView.findViewById(R.id.tvTotalItems);
            tvItems = itemView.findViewById(R.id.tvItems);
            status = itemView.findViewById(R.id.status);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}