package com.mibtech.aesthetic_am.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.model.Cart;

/**
 * Created by shree1 on 3/16/2017.
 */

public class CheckoutItemListAdapter extends RecyclerView.Adapter<CheckoutItemListAdapter.ItemHolder> {

    public ArrayList<Cart> carts;
    public Activity activity;
    Context context;
    Session session;

    public CheckoutItemListAdapter(Context context, Activity activity, ArrayList<Cart> carts) {
        try {
            this.context = context;
            this.activity = activity;
            this.carts = carts;
            session = new Session(context);
        } catch (Exception ignored) {

        }
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final ItemHolder holder, final int position) {
        try {

            final Cart cart = carts.get(position);

            float price;
            if (cart.getItems().get(0).getDiscounted_price().equals("0")) {
                price = Float.parseFloat(cart.getItems().get(0).getPrice());
            } else {
                price = Float.parseFloat(cart.getItems().get(0).getDiscounted_price());
            }

            String taxPercentage = cart.getItems().get(0).getTax_percentage();

            holder.tvItemName.setText(cart.getItems().get(0).getName() + " (" + cart.getItems().get(0).getMeasurement() + " " + ApiConfig.toTitleCase(cart.getItems().get(0).getUnit()) + ")");
            holder.tvQty.setText(activity.getString(R.string.qty_1) + cart.getQty());
            holder.tvPrice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + price));

            if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                holder.tvTaxTitle.setText(cart.getItems().get(0).getTax_title());
                holder.tvTaxAmount.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + (Integer.parseInt(cart.getQty()) * ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100))));
            } else {
                holder.tvTaxTitle.setText(cart.getItems().get(0).getTax_title());
                holder.tvTaxAmount.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + (Integer.parseInt(cart.getQty()) * ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100))));
            }
            if (cart.getItems().get(0).getTax_percentage().equals("0")) {
                holder.tvTaxTitle.setText("TAX");
            }
            holder.tvTaxPercent.setText("(" + cart.getItems().get(0).getTax_percentage() + "%)");

            if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                holder.tvSubTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + (Integer.parseInt(cart.getQty()) * (Float.parseFloat(cart.getItems().get(0).getPrice()) + ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)))));
            } else {
                holder.tvSubTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + (Integer.parseInt(cart.getQty()) * (Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)))));
            }
        } catch (Exception ignored) {

        }
    }

    @NotNull
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_checkout_item_list, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        public final TextView tvItemName;
        public final TextView tvQty;
        public final TextView tvPrice;
        public final TextView tvSubTotal;
        public final TextView tvTaxPercent;
        public final TextView tvTaxTitle;
        public final TextView tvTaxAmount;

        public ItemHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubTotal = itemView.findViewById(R.id.tvSubTotal);
            tvTaxPercent = itemView.findViewById(R.id.tvTaxPercent);
            tvTaxTitle = itemView.findViewById(R.id.tvTaxTitle);
            tvTaxAmount = itemView.findViewById(R.id.tvTaxAmount);
        }


    }
}

