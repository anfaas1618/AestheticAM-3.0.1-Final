package com.mibtech.aesthetic_am.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.fragment.ProductDetailFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.model.Product;

/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle1 extends RecyclerView.Adapter<AdapterStyle1.VideoHolder> {

    public ArrayList<Product> productList;
    public Activity activity;
    public int itemResource;
    Context context;

    public AdapterStyle1(Context context, Activity activity, ArrayList<Product> productList, int itemResource) {
        this.context = context;
        this.activity = activity;
        this.productList = productList;
        this.itemResource = itemResource;

    }

    @Override
    public int getItemCount() {
        int product;
        if (productList.size() > 4) {
            product = 4;
        } else {
            product = productList.size();
        }
        return product;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {
        final Product product = productList.get(position);


        Picasso.get()
                .load(product.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.thumbnail);


        holder.tvTitle.setText(product.getName());
        
        double price = 0;
        String taxPercentage = "0";
        try {
            taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
        } catch (Exception e) {

        }

        if (product.getPriceVariations().get(0).getDiscounted_price().equals("0") || product.getPriceVariations().get(0).getDiscounted_price().equals("")) {
            price = ((Float.parseFloat(product.getPriceVariations().get(0).getPrice()) + ((Float.parseFloat(product.getPriceVariations().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
        } else {
            price = ((Float.parseFloat(product.getPriceVariations().get(0).getDiscounted_price()) + ((Float.parseFloat(product.getPriceVariations().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
        }
        holder.tvPrice.setText(new Session(activity).getData(Constant.currency) + ApiConfig.StringFormat("" + price));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppCompatActivity activity1 = (AppCompatActivity) context;
                Fragment fragment = new ProductDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, product.getId());
                bundle.putString(Constant.FROM, "section");
                bundle.putInt("vpos", 0);
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();


            }
        });
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemResource, parent, false);
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

        public ImageView thumbnail;
        public TextView tvTitle, tvPrice;
        public RelativeLayout relativeLayout;

        public VideoHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            relativeLayout = itemView.findViewById(R.id.play_layout);

        }


    }
}