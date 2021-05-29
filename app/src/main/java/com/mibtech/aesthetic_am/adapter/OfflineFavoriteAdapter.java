package com.mibtech.aesthetic_am.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.fragment.FavoriteFragment;
import com.mibtech.aesthetic_am.fragment.ProductDetailFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.DatabaseHelper;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.model.PriceVariation;
import com.mibtech.aesthetic_am.model.Product;

public class OfflineFavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    public final int resource;
    final Context context;
    final Activity activity;
    final Session session;
    final DatabaseHelper databaseHelper;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    public boolean isLoading;
    public ArrayList<Product> mDataset = new ArrayList<>();
    SpannableString spannableString;
    String taxPercentage;


    public OfflineFavoriteAdapter(Context context, ArrayList<Product> myDataset, int resource) {
        this.context = context;
        this.activity = (Activity) context;
        this.mDataset = myDataset;
        this.resource = resource;
        this.session = new Session(activity);
        databaseHelper = new DatabaseHelper(activity);
        taxPercentage = "0";
    }

    public void add(int position, Product item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(resource, parent, false);
            return new ViewHolderRow(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof ViewHolderRow) {
            final ViewHolderRow holder = (ViewHolderRow) holderparent;
            holder.setIsRecyclable(false);
            final Product product = mDataset.get(position);

            try {
                taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
            } catch (Exception e) {

            }

            final ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
            if (priceVariations.size() == 1) {
                holder.spinner.setVisibility(View.INVISIBLE);
                holder.lytSpinner.setVisibility(View.INVISIBLE);
            }
            if (!product.getIndicator().equals("0")) {
                holder.imgIndicator.setVisibility(View.VISIBLE);
                if (product.getIndicator().equals("1"))
                    holder.imgIndicator.setImageResource(R.drawable.ic_veg_icon);
                else if (product.getIndicator().equals("2"))
                    holder.imgIndicator.setImageResource(R.drawable.ic_non_veg_icon);
            }
            holder.productName.setText(Html.fromHtml(product.getName()));

            Picasso.get()
                    .load(product.getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgThumb);

            CustomAdapter customAdapter = new CustomAdapter(context, priceVariations, holder, product);
            holder.spinner.setAdapter(customAdapter);

            holder.lytMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Constant.CartValues.size() > 0) {
                        ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                    }

                    AppCompatActivity activity1 = (AppCompatActivity) context;
                    Fragment fragment = new ProductDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("vpos", priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition());
                    bundle.putString("id", product.getId());
                    bundle.putInt("position", position);
                    bundle.putString(Constant.FROM, "fragment");

                    fragment.setArguments(bundle);

                    activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

                }
            });


            holder.txtqty.setText(databaseHelper.CheckOrderExists(product.getPriceVariations().get(0).getId(), product.getId()));

            holder.imgFav.setImageResource(R.drawable.ic_is_favorite);

            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseHelper.AddOrRemoveFavorite(product.getId(), false);
                    mDataset.remove(product);
                    FavoriteFragment.offlineFavoriteAdapter.notifyDataSetChanged();
                    FavoriteFragment.recyclerView.setAdapter(FavoriteFragment.offlineFavoriteAdapter);
                }
            });

            SetSelectedData(holder, priceVariations.get(0), product);


        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        Product product = mDataset.get(position);
        if (product != null)
            return Integer.parseInt(product.getId());
        else
            return position;
    }

    public void setLoaded() {
        isLoading = false;
    }

    @SuppressLint("SetTextI18n")
    public void SetSelectedData(final ViewHolderRow holder, final PriceVariation extra, Product product) {
        try {
            holder.Measurement.setText(extra.getMeasurement() + extra.getMeasurement_unit_name());
            holder.productPrice.setText(session.getData(Constant.currency) + extra.getPrice());

            if (session.isUserLoggedIn()) {

                if (Constant.CartValues.containsKey(extra.getId())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        holder.txtqty.setText("" + Constant.CartValues.get(extra.getId()));
                    }
                }
            } else {
                if (session.getData(extra.getId()) != null) {
                    holder.txtqty.setText(session.getData(extra.getId()));
                } else {
                    holder.txtqty.setText(extra.getCart_count());
                }
            }

            holder.txtstatus.setText(extra.getServe_for());

            double price, oPrice;
            String taxPercentage = "0";
            try {
                taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
            } catch (Exception e) {

            }
            if (extra.getDiscounted_price().equals("0") || extra.getDiscounted_price().equals("")) {
                holder.lytDiscount.setVisibility(View.INVISIBLE);
                price = ((Float.parseFloat(extra.getPrice()) + ((Float.parseFloat(extra.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
            } else {
                price = ((Float.parseFloat(extra.getDiscounted_price()) + ((Float.parseFloat(extra.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                oPrice = (Float.parseFloat(extra.getPrice()) + ((Float.parseFloat(extra.getPrice()) * Float.parseFloat(taxPercentage)) / 100));

                holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.originalPrice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + oPrice));

                holder.lytDiscount.setVisibility(View.VISIBLE);
                holder.showDiscount.setText(extra.getDiscountpercent().replace("(", "").replace(")", ""));
            }
            holder.productPrice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + price));

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                holder.txtstatus.setVisibility(View.VISIBLE);
                holder.txtstatus.setTextColor(Color.RED);
                holder.qtyLyt.setVisibility(View.GONE);
            } else {
                holder.txtstatus.setVisibility(View.GONE);
                holder.qtyLyt.setVisibility(View.VISIBLE);
            }

            holder.txtqty.setText(databaseHelper.CheckOrderExists(extra.getId(), extra.getProduct_id()));

            holder.imgAdd.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(holder.txtqty.getText().toString());
                    if (count < Float.parseFloat(extra.getStock())) {
                        if (count < Integer.parseInt(session.getData(Constant.max_cart_items_count))) {
                            count++;
                            holder.txtqty.setText("" + count);
                            databaseHelper.AddOrderData(extra.getId(), extra.getProduct_id(), "" + count);
                            databaseHelper.getTotalItemOfCart(activity);
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.imgMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(holder.txtqty.getText().toString());
                    if (!(count <= 0)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (count != 0) {
                                count--;
                                holder.txtqty.setText("" + count);
                                databaseHelper.AddOrderData(extra.getId(), extra.getProduct_id(), "" + count);
                                databaseHelper.getTotalItemOfCart(activity);
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public final ImageButton imgAdd;
        public final ImageButton imgMinus;
        final TextView productName;
        final TextView productPrice;
        final TextView txtqty;
        final TextView Measurement;
        final TextView showDiscount;
        final TextView originalPrice;
        final TextView txtstatus;
        final ImageView imgThumb;
        final ImageView imgFav;
        final ImageView imgIndicator;
        final CardView lytMain;
        final RelativeLayout lytDiscount, lytSpinner;
        final AppCompatSpinner spinner;
        final RelativeLayout qtyLyt;

        public ViewHolderRow(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.txtprice);
            showDiscount = itemView.findViewById(R.id.showDiscount);
            originalPrice = itemView.findViewById(R.id.txtoriginalprice);
            Measurement = itemView.findViewById(R.id.txtmeasurement);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            imgIndicator = itemView.findViewById(R.id.imgIndicator);
            imgAdd = itemView.findViewById(R.id.btnaddqty);
            imgMinus = itemView.findViewById(R.id.btnminusqty);
            txtqty = itemView.findViewById(R.id.txtqty);
            qtyLyt = itemView.findViewById(R.id.qtyLyt);
            imgFav = itemView.findViewById(R.id.imgFav);
            lytMain = itemView.findViewById(R.id.lytMain);
            spinner = itemView.findViewById(R.id.spinner);
            lytDiscount = itemView.findViewById(R.id.lytDiscount);
            lytSpinner = itemView.findViewById(R.id.lytSpinner);

        }

    }

    public class CustomAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<PriceVariation> extraList;
        final LayoutInflater inflter;
        final ViewHolderRow holder;
        final Product product;

        public CustomAdapter(Context applicationContext, ArrayList<PriceVariation> extraList, ViewHolderRow holder, Product product) {
            this.context = applicationContext;
            this.extraList = extraList;
            this.holder = holder;
            this.product = product;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return extraList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
//            TextView price = view.findViewById(R.id.txtprice);

            PriceVariation extra = extraList.get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
//            price.setText(session.getData(Constant.currency) + extra.getPrice());

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(context.getResources().getColor(R.color.red));
//                price.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(context.getResources().getColor(R.color.black));
//                price.setTextColor(context.getResources().getColor(R.color.black));
            }

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    PriceVariation priceVariation = extraList.get(i);
                    SetSelectedData(holder, priceVariation, product);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

}
