package com.mibtech.aesthetic_am.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.fragment.CartFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.DatabaseHelper;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.model.OfflineCart;


public class OfflineCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    final Activity activity;
    final ArrayList<OfflineCart> items;
    final DatabaseHelper databaseHelper;
    final Session session;
    final Context context;
    public boolean isLoading;


    public OfflineCartAdapter(Context context, Activity activity, ArrayList<OfflineCart> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
        databaseHelper = new DatabaseHelper(activity);
        session = new Session(context);
    }

    public void add(int position, OfflineCart item) {
        if (position != (getItemCount() + 1)) {
            items.add(position, item);
        } else {
            items.add(item);
        }
        notifyItemInserted(position);
    }

    public void removeItem(int position) {

        String taxPercentage1 = "0";
        OfflineCart cart = items.get(position);
        try {
            taxPercentage1 = (Double.parseDouble(cart.getTax_percentage()) > 0 ? cart.getTax_percentage() : "0");
        } catch (Exception e) {

        }

        double price = 0;
        if (cart.getDiscounted_price().equals("0") || cart.getDiscounted_price().equals("")) {
            price = ((Float.parseFloat(cart.getPrice()) + ((Float.parseFloat(cart.getPrice()) * Float.parseFloat(taxPercentage1)) / 100)));
        } else {
            price = ((Float.parseFloat(cart.getDiscounted_price()) + ((Float.parseFloat(cart.getDiscounted_price()) * Float.parseFloat(taxPercentage1)) / 100)));
        }

        databaseHelper.DeleteOrderData(cart.getId(), cart.getProduct_id());
        Constant.FLOAT_TOTAL_AMOUNT = Double.parseDouble(ApiConfig.StringFormat("" + (Constant.FLOAT_TOTAL_AMOUNT - (price * Integer.parseInt(databaseHelper.CheckOrderExists(cart.getId(), cart.getProduct_id()))))));
        CartFragment.SetData();

        items.remove(cart);
        notifyDataSetChanged();
        Constant.FLOAT_TOTAL_AMOUNT = 0.00;
        databaseHelper.getTotalItemOfCart(activity);
        activity.invalidateOptionsMenu();
        if (getItemCount() == 0) {
            CartFragment.lytempty.setVisibility(View.VISIBLE);
            CartFragment.lytTotal.setVisibility(View.GONE);
        }
        showUndoSnackbar(cart, position);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_cartlist, parent, false);
            return new ProductHolderItems(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof ProductHolderItems) {
            final ProductHolderItems holder = (ProductHolderItems) holderparent;
            final OfflineCart cart = items.get(position);

            Picasso.get()
                    .load(cart.getItem().get(0).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgproduct);

            holder.txtproductname.setText(cart.getItem().get(0).getName());
            holder.txtmeasurement.setText(cart.getItem().get(0).getMeasurement() + "\u0020" + cart.getItem().get(0).getUnit());
            double price, oPrice;
            String taxPercentage = "0";
            try {
                taxPercentage = (Double.parseDouble(cart.getTax_percentage()) > 0 ? cart.getTax_percentage() : "0");
            } catch (Exception e) {

            }
            holder.txtprice.setText(new Session(activity).getData(Constant.currency) + (cart.getDiscounted_price().equals("0") ? cart.getPrice() : cart.getDiscounted_price()));

            if (cart.getDiscounted_price().equals("0") || cart.getDiscounted_price().equals("")) {
                price = ((Float.parseFloat(cart.getPrice()) + ((Float.parseFloat(cart.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
            } else {
                price = ((Float.parseFloat(cart.getDiscounted_price()) + ((Float.parseFloat(cart.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                oPrice = ((Float.parseFloat(cart.getPrice()) + ((Float.parseFloat(cart.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
                holder.txtoriginalprice.setPaintFlags(holder.txtoriginalprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.txtoriginalprice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + oPrice));
            }
            holder.txtprice.setText(new Session(activity).getData(Constant.currency) + ApiConfig.StringFormat("" + price));

            holder.txtproductname.setText(cart.getItem().get(0).getName());
            holder.txtmeasurement.setText(cart.getItem().get(0).getMeasurement() + "\u0020" + cart.getItem().get(0).getUnit());

            holder.txtQuantity.setText(databaseHelper.CheckOrderExists(cart.getId(), cart.getProduct_id()));

            holder.txttotalprice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + price * Integer.parseInt(databaseHelper.CheckOrderExists(cart.getId(), cart.getProduct_id()))));

            Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT + (price * Integer.parseInt(databaseHelper.CheckOrderExists(cart.getId(), cart.getProduct_id())));
            CartFragment.SetData();

            final double finalPrice = price;
            holder.btnaddqty.setOnClickListener(view -> {

                if (ApiConfig.isConnected(activity)) {
                    if (!(Integer.parseInt(holder.txtQuantity.getText().toString()) >= Float.parseFloat(cart.getItem().get(0).getStock()))) {
                        if (!(Integer.parseInt(holder.txtQuantity.getText().toString()) + 1 > Integer.parseInt(session.getData(Constant.max_cart_items_count)))) {
                            int count = Integer.parseInt(holder.txtQuantity.getText().toString());
                            count++;
                            cart.getItem().get(0).setCart_count("" + count);
                            holder.txtQuantity.setText("" + count);
                            holder.txttotalprice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + finalPrice * count));
                            Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT + finalPrice;
                            databaseHelper.AddOrderData(cart.getId(), cart.getProduct_id(), "" + count);
                            CartFragment.SetData();
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                }

            });

            holder.btnminusqty.setOnClickListener(view -> {
                if (ApiConfig.isConnected(activity)) {
                    if (Integer.parseInt(holder.txtQuantity.getText().toString()) > 1) {
                        int count = Integer.parseInt(holder.txtQuantity.getText().toString());
                        count--;
                        cart.getItem().get(0).setCart_count("" + count);
                        holder.txtQuantity.setText("" + count);
                        holder.txttotalprice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + finalPrice * count));
                        Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT - finalPrice;
                        databaseHelper.AddOrderData(cart.getId(), cart.getProduct_id(), "" + count);
                        CartFragment.SetData();
                    }
                }
            });

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        OfflineCart cart = items.get(position);
        if (cart != null)
            return Integer.parseInt(cart.getId());
        else
            return position;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public static class ProductHolderItems extends RecyclerView.ViewHolder {
        final ImageView imgproduct;
        final ImageView btnminusqty;
        final ImageView btnaddqty;
        final TextView txtproductname;
        final TextView txtmeasurement;
        final TextView txtprice;
        final TextView txtoriginalprice;
        final TextView txtQuantity;
        final TextView txttotalprice;

        public ProductHolderItems(@NonNull View itemView) {
            super(itemView);
            imgproduct = itemView.findViewById(R.id.imgproduct);

            btnminusqty = itemView.findViewById(R.id.btnminusqty);
            btnaddqty = itemView.findViewById(R.id.btnaddqty);

            txtproductname = itemView.findViewById(R.id.txtproductname);
            txtmeasurement = itemView.findViewById(R.id.txtmeasurement);
            txtprice = itemView.findViewById(R.id.txtprice);
            txtoriginalprice = itemView.findViewById(R.id.txtoriginalprice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txttotalprice = itemView.findViewById(R.id.txttotalprice);
        }
    }

    void showUndoSnackbar(OfflineCart cart, int position) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.undo_message), Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(activity, R.color.gray));
        snackbar.setAction(activity.getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                String taxPercentage1 = "0";
                try {
                    taxPercentage1 = (Double.parseDouble(cart.getTax_percentage()) > 0 ? cart.getTax_percentage() : "0");
                } catch (Exception ignored) {

                }

                double price = 0;
                if (cart.getItem().get(0).getDiscounted_price().equals("0") || cart.getItem().get(0).getDiscounted_price().equals("")) {
                    price = ((Float.parseFloat(cart.getItem().get(0).getPrice()) + ((Float.parseFloat(cart.getItem().get(0).getPrice()) * Float.parseFloat(taxPercentage1)) / 100)));
                } else {
                    price = ((Float.parseFloat(cart.getItem().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItem().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage1)) / 100)));
                }

                add(position, cart);

                notifyDataSetChanged();
                CartFragment.SetData();
                CartFragment.isSoldOut = false;
                Constant.TOTAL_CART_ITEM = getItemCount();
                Constant.FLOAT_TOTAL_AMOUNT += (price * Integer.parseInt(cart.getItem().get(0).getCart_count()));
                CartFragment.values.put(items.get(position).getItem().get(0).getId(), cart.getItem().get(0).getCart_count());
                CartFragment.SetData();
                activity.invalidateOptionsMenu();

            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }
}