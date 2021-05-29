package com.mibtech.aesthetic_am.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.fragment.PaymentFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.model.BookingDate;

/**
 * Created by shree1 on 3/16/2017.
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.VideoHolder> {

    public final ArrayList<BookingDate> bookingDates;
    public final Activity activity;

    public DateAdapter(Activity activity, ArrayList<BookingDate> bookingDates) {
        this.activity = activity;
        this.bookingDates = bookingDates;
    }

    @Override
    public int getItemCount() {
        return bookingDates.size();
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, final int position) {
        final BookingDate bookingDate = bookingDates.get(position);

        if (Constant.selectedDatePosition == position) {
            PaymentFragment.deliveryDay = bookingDate.getDate() + "-" + ApiConfig.getMonth(Integer.parseInt(bookingDate.getMonth())) + "-" + bookingDate.getYear();
            holder.relativeLyt.setBackgroundResource(R.drawable.selected_date_shadow);
            holder.tvDay.setTextColor(ContextCompat.getColor(activity, R.color.white));
            holder.tvDate.setTextColor(ContextCompat.getColor(activity, R.color.white));
            holder.tvMonth.setTextColor(ContextCompat.getColor(activity, R.color.white));
        } else {

            holder.tvDay.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            holder.tvDate.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            holder.tvMonth.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            holder.relativeLyt.setBackgroundResource(R.drawable.date_shadow);
        }

        holder.relativeLyt.setPadding((int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp));


        holder.tvDay.setText(ApiConfig.getDayOfWeek(Integer.parseInt(bookingDate.getDay())));
        holder.tvDate.setText(bookingDate.getDate());
        holder.tvMonth.setText(ApiConfig.getMonth(Integer.parseInt(bookingDate.getMonth())));

        holder.relativeLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PaymentFragment.adapter != null) {
                    if (PaymentFragment.deliveryDay.length() > 0) {
                        Constant.selectedDatePosition = holder.getPosition();
                        notifyDataSetChanged();
                        PaymentFragment.deliveryTime = "";
                        PaymentFragment.adapter.notifyDataSetChanged();
                        PaymentFragment.recyclerView.setAdapter(PaymentFragment.adapter);
                    }
                }
            }
        });
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_date, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public final TextView tvDate;
        public final TextView tvMonth;
        public final TextView tvDay;
        final RelativeLayout relativeLyt;

        public VideoHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvDay = itemView.findViewById(R.id.tvDay);
            relativeLyt = itemView.findViewById(R.id.relativeLyt);
        }


    }
}
