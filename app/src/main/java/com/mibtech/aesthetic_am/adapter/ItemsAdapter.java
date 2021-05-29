package com.mibtech.aesthetic_am.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.fragment.TrackerDetailFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.OrderTracker;

import static com.mibtech.aesthetic_am.fragment.TrackerDetailFragment.pBar;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.CartItemHolder> {

    final Activity activity;
    final ArrayList<OrderTracker> orderTrackerArrayList;
    final Session session;
    String from = "";

    public ItemsAdapter(Activity activity, ArrayList<OrderTracker> orderTrackerArrayList, String from) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
        this.from = from;
        session = new Session(activity);
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_items, null);
        return new CartItemHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CartItemHolder holder, final int position) {

        final OrderTracker order = orderTrackerArrayList.get(position);

        String payType = "";
        if (order.getPayment_method().equalsIgnoreCase("cod"))
            payType = activity.getResources().getString(R.string.cod);
        else
            payType = order.getPayment_method();
        String activeStatus = order.getActiveStatus().substring(0, 1).toUpperCase() + order.getActiveStatus().substring(1).toLowerCase();
        holder.txtqty.setText(order.getQuantity());

        String taxPercentage = order.getTax_percent();
        double price = 0;

        if (order.getDiscounted_price().equals("0") || order.getDiscounted_price().equals("")) {
            price = ((Float.parseFloat(order.getPrice()) + ((Float.parseFloat(order.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
        } else {
            price = ((Float.parseFloat(order.getDiscounted_price()) + ((Float.parseFloat(order.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
        }
        holder.txtprice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + price));

        holder.txtpaytype.setText(activity.getResources().getString(R.string.via) + payType);
        holder.txtstatus.setText(activeStatus);
        if (activeStatus.equalsIgnoreCase(Constant.AWAITING_PAYMENT)) {
            holder.txtstatus.setText(activity.getString(R.string.awaiting_payment));
        }
        holder.txtstatusdate.setText(order.getActiveStatusDate());
        holder.txtname.setText(order.getName() + "(" + order.getMeasurement() + order.getUnit() + ")");

        Picasso.get().
                load(order.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgorder);

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

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrderStatus(activity, order, Constant.CANCELLED, holder, from);
            }
        });
        holder.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                Date date = new Date();
                //System.out.println (myFormat.format (date));
                String inputString1 = order.getActiveStatusDate();
                String inputString2 = myFormat.format(date);
                try {
                    Date date1 = myFormat.parse(inputString1);
                    Date date2 = myFormat.parse(inputString2);
                    long diff = date2.getTime() - date1.getTime();
                    //  System.out.println("Days: "+TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <= Integer.parseInt(new Session(activity).getData(Constant.max_product_return_days))) {
                        updateOrderStatus(activity, order, Constant.RETURNED, holder, from);

                    } else {
                        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.product_return) + Integer.parseInt(new Session(activity).getData(Constant.max_product_return_days)) + activity.getString(R.string.day_max_limit), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(activity.getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();

                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        View snackbarView = snackbar.getView();
                        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                        textView.setMaxLines(5);
                        snackbar.show();

                    }
                } catch (ParseException e) {

                }
            }
        });
        if (from.equals("detail")) {

            if (order.getActiveStatus().equalsIgnoreCase("cancelled")) {
                holder.txtstatus.setTextColor(Color.RED);
                holder.btnCancel.setVisibility(View.GONE);
            } else if (order.getActiveStatus().equalsIgnoreCase("delivered")) {
                holder.btnCancel.setVisibility(View.GONE);
                if (order.getReturn_status().equalsIgnoreCase("1")) {
                    holder.btnReturn.setVisibility(View.VISIBLE);
                } else {
                    holder.btnReturn.setVisibility(View.GONE);
                }
            } else if (order.getActiveStatus().equalsIgnoreCase("returned")) {
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnReturn.setVisibility(View.GONE);
            } else {
                if (order.getCancelable_status().equalsIgnoreCase("1")) {
                    if (order.getTill_status().equalsIgnoreCase("received")) {
                        if (order.getActiveStatus().equalsIgnoreCase("received")) {
                            holder.btnCancel.setVisibility(View.VISIBLE);
                        } else {
                            holder.btnCancel.setVisibility(View.GONE);
                        }
                    } else if (order.getTill_status().equalsIgnoreCase("processed")) {
                        if (order.getActiveStatus().equalsIgnoreCase("received") || order.getActiveStatus().equalsIgnoreCase("processed")) {
                            holder.btnCancel.setVisibility(View.VISIBLE);
                        } else {
                            holder.btnCancel.setVisibility(View.GONE);
                        }
                    } else if (order.getTill_status().equalsIgnoreCase("shipped")) {
                        if (order.getActiveStatus().equalsIgnoreCase("received") || order.getActiveStatus().equalsIgnoreCase("processed") || order.getActiveStatus().equalsIgnoreCase("shipped")) {
                            holder.btnCancel.setVisibility(View.VISIBLE);
                        } else {
                            holder.btnCancel.setVisibility(View.GONE);
                        }
                    }
                } else {
                    holder.btnCancel.setVisibility(View.GONE);
                }
            }
        }
    }

    private void updateOrderStatus(final Activity activity, final OrderTracker order, final String status, final CartItemHolder holder, final String from) {

        final Map<String, String> params = new HashMap<>();
        params.put(Constant.UPDATE_ORDER_ITEM_STATUS, Constant.GetVal);
        params.put(Constant.ORDER_ITEM_ID, order.getId());
        params.put(Constant.ORDER_ID, order.getOrder_id());
        params.put(Constant.STATUS, status);


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        if (status.equals(Constant.CANCELLED)) {
            alertDialog.setTitle(activity.getResources().getString(R.string.cancel_order));
            alertDialog.setMessage(activity.getResources().getString(R.string.cancel_msg));
        } else if (status.equals(Constant.RETURNED)) {
            alertDialog.setTitle(activity.getResources().getString(R.string.return_order));
            alertDialog.setMessage(activity.getResources().getString(R.string.return_msg));
        }
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (pBar != null)
                    pBar.setVisibility(View.VISIBLE);
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        // System.out.println("================= " + response);
                        if (result) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (!object.getBoolean(Constant.ERROR)) {
                                    if (status.equals(Constant.CANCELLED)) {
                                        holder.btnCancel.setVisibility(View.GONE);
                                        holder.txtstatus.setText(status);
                                        holder.txtstatus.setTextColor(Color.RED);
                                        order.status = status;
                                        if (from.equals("detail")) {
                                            if (orderTrackerArrayList.size() == 1) {
                                                TrackerDetailFragment.btnCancel.setVisibility(View.GONE);
                                                TrackerDetailFragment.lyttracker.setVisibility(View.GONE);
                                            }
                                        }
                                        ApiConfig.getWalletBalance(activity, new Session(activity));
                                    } else {
                                        holder.btnReturn.setVisibility(View.GONE);
                                        holder.txtstatus.setText(status);
                                    }
                                    Constant.isOrderCancelled = true;
                                }
                                Toast.makeText(activity, object.getString("message"), Toast.LENGTH_LONG).show();
                                if (pBar != null)
                                    pBar.setVisibility(View.GONE);
                            } catch (JSONException e) {

                            }
                        }
                    }
                }, activity, Constant.ORDERPROCESS_URL, params, false);

            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        final TextView txtqty;
        final TextView txtprice;
        final TextView txtpaytype;
        final TextView txtstatus;
        final TextView txtstatusdate;
        final TextView txtname;
        final ImageView imgorder;
        final CardView carddetail;
        final RecyclerView recyclerView;
        final Button btnCancel;
        final Button btnReturn;
        final LinearLayout returnLyt;

        public CartItemHolder(View itemView) {
            super(itemView);

            txtqty = itemView.findViewById(R.id.txtqty);
            txtprice = itemView.findViewById(R.id.txtprice);
            txtpaytype = itemView.findViewById(R.id.txtpaytype);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            txtstatusdate = itemView.findViewById(R.id.txtstatusdate);
            txtname = itemView.findViewById(R.id.txtname);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            imgorder = itemView.findViewById(R.id.imgorder);
            carddetail = itemView.findViewById(R.id.carddetail);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            returnLyt = itemView.findViewById(R.id.returnLyt);
        }
    }

}
