package com.mibtech.aesthetic_am.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.fragment.AddressAddUpdateFragment;
import com.mibtech.aesthetic_am.fragment.AddressListFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.model.Address;

public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final Activity activity;
    final Context context;
    final ArrayList<Address> addresses;
    String id = "0";

    public AddressAdapter(Context context, Activity activity, ArrayList<Address> addresses) {
        this.context = context;
        this.activity = activity;
        this.addresses = addresses;
    }

    public void add(int position, Address item) {
        addresses.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.lyt_address_list, parent, false);
        return new AddressItemHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holderparent, final int position) {
        final AddressItemHolder holder = (AddressItemHolder) holderparent;
        final Address address = addresses.get(position);
        id = address.getId();

        holder.setIsRecyclable(false);

        if (Constant.selectedAddressId.equals(id)) {
            AddressListFragment.selectedAddress = address.getAddress() + ", " + address.getLandmark() + ", " + address.getCity_name() + ", " + address.getArea_name() + ", " + address.getState() + ", " + address.getCountry() + ", " + activity.getString(R.string.pincode_) + address.getPincode();
            Constant.DefaultPinCode = address.getPincode();
            Constant.DefaultCity = address.getCity_name();
            holder.tvName.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));

            holder.tvAddressType.setBackground(activity.getResources().getDrawable(R.drawable.right_btn_bg));

            holder.tvDefaultAddress.setBackground(activity.getResources().getDrawable(R.drawable.right_btn_bg));

            holder.imgSelect.setImageResource(R.drawable.ic_check_circle);
            holder.lytMain.setBackgroundResource(R.drawable.selected_shadow);

        } else {

            holder.tvName.setTextColor(ContextCompat.getColor(activity, R.color.gray));

            holder.tvAddressType.setBackground(activity.getResources().getDrawable(R.drawable.left_btn_bg));

            holder.tvDefaultAddress.setBackground(activity.getResources().getDrawable(R.drawable.left_btn_bg));

            holder.imgSelect.setImageResource(R.drawable.ic_uncheck_circle);
            holder.lytMain.setBackgroundResource(R.drawable.address_card_shadow);
        }

        holder.tvAddress.setText(address.getAddress() + ", " + address.getLandmark() + ", " + address.getCity_name() + ", " + address.getArea_name() + ", " + address.getState() + ", " + address.getCountry() + ", " + activity.getString(R.string.pincode_) + address.getPincode());
        Constant.DefaultPinCode = address.getPincode();
        Constant.DefaultCity = address.getCity_name();
        if (address.getIs_default().equals("1")) {
            holder.tvDefaultAddress.setVisibility(View.VISIBLE);
        }

        holder.lytMain.setPadding((int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp));
        holder.tvName.setText(address.getName());
        if (!address.getType().equalsIgnoreCase("")) {
            holder.tvAddressType.setText(address.getType());
        }

        holder.tvMobile.setText(address.getMobile());

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getResources().getString(R.string.delete_address));
                builder.setIcon(android.R.drawable.ic_delete);
                builder.setMessage(activity.getResources().getString(R.string.delete_address_msg));

                builder.setCancelable(false);
                builder.setPositiveButton(activity.getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (ApiConfig.isConnected(activity)) {
                            addresses.remove(address);
                            notifyDataSetChanged();
                            ApiConfig.removeAddress(activity, address.getId());
                        }
                        if (addresses.size() == 0) {
                            AddressListFragment.selectedAddress = "";
                            AddressListFragment.tvAlert.setVisibility(View.VISIBLE);
                        } else {
                            AddressListFragment.tvAlert.setVisibility(View.GONE);
                        }
                    }
                });

                builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.selectedAddressId = address.getId();
                new Session(activity).setData(Constant.LONGITUDE, address.getLongitude());
                new Session(activity).setData(Constant.LATITUDE, address.getLatitude());

                if (new Session(context).getData(Constant.area_wise_delivery_charge).equals("1")) {
                    Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY = Double.parseDouble(address.getMinimum_free_delivery_order_amount());
                    Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble(address.getDelivery_charges());
                }
                notifyDataSetChanged();
            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApiConfig.isConnected(activity)) {
                    Fragment fragment = new AddressAddUpdateFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("model", address);
                    bundle.putString("for", "update");
                    bundle.putInt("position", position);
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class AddressItemHolder extends RecyclerView.ViewHolder {

        final TextView tvName;
        final TextView tvAddress;
        final TextView tvAddressType;
        final TextView tvMobile;
        final TextView tvDefaultAddress;
        final ImageView imgEdit;
        final ImageView imgDelete;
        final ImageView imgSelect;
        final LinearLayout lytMain;

        public AddressItemHolder(@NonNull View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvAddressType = itemView.findViewById(R.id.tvAddressType);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvDefaultAddress = itemView.findViewById(R.id.tvDefaultAddress);

            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgSelect = itemView.findViewById(R.id.imgSelect);
            imgDelete = itemView.findViewById(R.id.imgDelete);


        }
    }
}