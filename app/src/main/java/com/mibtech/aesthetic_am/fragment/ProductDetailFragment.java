package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.adapter.AdapterStyle1;
import com.mibtech.aesthetic_am.adapter.SliderAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.DatabaseHelper;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.Favorite;
import com.mibtech.aesthetic_am.model.PriceVariation;
import com.mibtech.aesthetic_am.model.Product;
import com.mibtech.aesthetic_am.model.Slider;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.mibtech.aesthetic_am.helper.ApiConfig.AddOrRemoveFavorite;
import static com.mibtech.aesthetic_am.helper.ApiConfig.GetSettings;


public class ProductDetailFragment extends Fragment {
    static ArrayList<Slider> sliderArrayList;
    TextView showDiscount, tvMfg, tvMadeIn, txtProductName, txtqty, txtPrice, txtoriginalprice, txtMeasurement, txtstatus, tvTitleMadeIn, tvTitleMfg;
    WebView webDescription;
    ViewPager viewPager;
    Spinner spinner;
    LinearLayout lytSpinner;
    ImageView imgIndicator;
    LinearLayout mMarkersLayout, lytMfg, lytMadeIn;
    RelativeLayout lytmainprice, lytqty, lytDiscount;
    ScrollView scrollView;
    Session session;
    boolean favorite;
    ImageView imgFav;
    ImageButton imgAdd, imgMinus;
    LinearLayout lytshare, lytsave, lytSimilar;
    int size, count;
    View root;
    int vpos;
    String from, id;
    boolean isLogin;
    Product product;
    PriceVariation priceVariation;
    ArrayList<PriceVariation> priceVariationslist;
    DatabaseHelper databaseHelper;
    int position = 0;
    Button btnCart;
    Activity activity;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    TextView tvMore;
    ImageView imgReturnable, imgCancellable;
    TextView tvReturnable, tvCancellable;
    String taxPercentage;
    LottieAnimationView lottieAnimationView;
    ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_product_detail, container, false);

        setHasOptionsMenu(true);
        activity = getActivity();

        Constant.CartValues = new HashMap<>();

        session = new Session(activity);
        isLogin = session.isUserLoggedIn();
        databaseHelper = new DatabaseHelper(activity);

        from = getArguments().getString(Constant.FROM);


        taxPercentage = "0";

        vpos = getArguments().getInt("vpos", 0);
        id = getArguments().getString("id");

        if (from.equals("fragment") || from.equals("sub_cate") || from.equals("favorite") || from.equals("search")) {
            position = getArguments().getInt("position");
        }

        lytqty = root.findViewById(R.id.lytqty);
        scrollView = root.findViewById(R.id.scrollView);
        mMarkersLayout = root.findViewById(R.id.layout_markers);
        sliderArrayList = new ArrayList<>();
        viewPager = root.findViewById(R.id.viewPager);
        txtProductName = root.findViewById(R.id.txtproductname);
        txtoriginalprice = root.findViewById(R.id.txtoriginalprice);
        webDescription = root.findViewById(R.id.txtDescription);
        txtPrice = root.findViewById(R.id.txtprice);
        lytDiscount = root.findViewById(R.id.lytDiscount);
        txtMeasurement = root.findViewById(R.id.txtmeasurement);
        imgFav = root.findViewById(R.id.imgFav);
        lytmainprice = root.findViewById(R.id.lytmainprice);
        txtqty = root.findViewById(R.id.txtqty);
        txtstatus = root.findViewById(R.id.txtstatus);
        imgAdd = root.findViewById(R.id.btnaddqty);
        imgMinus = root.findViewById(R.id.btnminusqty);
        spinner = root.findViewById(R.id.spinner);
        lytSpinner = root.findViewById(R.id.lytSpinner);
        imgIndicator = root.findViewById(R.id.imgIndicator);
        showDiscount = root.findViewById(R.id.showDiscount);
        lytshare = root.findViewById(R.id.lytshare);
        lytsave = root.findViewById(R.id.lytsave);
        lytSimilar = root.findViewById(R.id.lytSimilar);
        tvMadeIn = root.findViewById(R.id.tvMadeIn);
        tvTitleMadeIn = root.findViewById(R.id.tvTitleMadeIn);
        tvMfg = root.findViewById(R.id.tvMfg);
        tvTitleMfg = root.findViewById(R.id.tvTitleMfg);
        lytMfg = root.findViewById(R.id.lytMfg);
        lytMadeIn = root.findViewById(R.id.lytMadeIn);
        btnCart = root.findViewById(R.id.btnCart);
        recyclerView = root.findViewById(R.id.recyclerView);
        relativeLayout = root.findViewById(R.id.relativeLayout);
        tvMore = root.findViewById(R.id.tvMore);

        tvReturnable = root.findViewById(R.id.tvReturnable);
        tvCancellable = root.findViewById(R.id.tvCancellable);
        imgReturnable = root.findViewById(R.id.imgReturnable);
        imgCancellable = root.findViewById(R.id.imgCancellable);

        lottieAnimationView = root.findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation("add_to_wish_list.json");

        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);

        GetProductDetail(id);
        GetSettings(activity);

        lytmainprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSimilar();
            }
        });

        lytSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSimilar();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fm.beginTransaction().add(R.id.container, new CartFragment()).addToBackStack(null).commit();
            }
        });

        lytshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytshare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = Constant.WebsiteUrl + "itemdetail/" + product.getSlug();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_via));
                        startActivity(shareIntent);
                    }
                });
            }
        });

        lytsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    favorite = product.isIs_favorite();
                    if (ApiConfig.isConnected(activity)) {
                        if (favorite) {
                            favorite = false;
                            lottieAnimationView.setVisibility(View.GONE);
                            product.setIs_favorite(false);
                            imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                        } else {
                            favorite = true;
                            product.setIs_favorite(true);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                        }
                        AddOrRemoveFavorite(activity, session, product.getId(), favorite);
                    }
                } else {
                    favorite = databaseHelper.getFavouriteById(product.getId());
                    if (favorite) {
                        favorite = false;
                        lottieAnimationView.setVisibility(View.GONE);
                        imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                    } else {
                        favorite = true;
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        lottieAnimationView.playAnimation();
                    }
                    databaseHelper.AddOrRemoveFavorite(product.getId(), favorite);
                }
                if (from.equals("fragment")) {
                    ProductListFragment.productArrayList.get(position).setIs_favorite(favorite);
                    ProductListFragment.mAdapter.notifyDataSetChanged();
                } else if (from.equals("sub_cate")) {
                    ProductListFragment.productArrayList.get(position).setIs_favorite(favorite);
                    ProductListFragment.mAdapter.notifyDataSetChanged();
                } else if (from.equals("favorite")) {
                    if (session.isUserLoggedIn()) {
                        Favorite favProduct = new Favorite();
                        favProduct.setId(product.getId());
                        favProduct.setProduct_id(product.getId());
                        favProduct.setName(product.getName());
                        favProduct.setSlug(product.getSlug());
                        favProduct.setSubcategory_id(product.getSubcategory_id());
                        favProduct.setImage(product.getImage());
                        favProduct.setStatus(product.getStatus());
                        favProduct.setDate_added(product.getDate_added());
                        favProduct.setCategory_id(product.getCategory_id());
                        favProduct.setIndicator(product.getIndicator());
                        favProduct.setManufacturer(product.getManufacturer());
                        favProduct.setMade_in(product.getMade_in());
                        favProduct.setReturn_status(product.getReturn_status());
                        favProduct.setCancelable_status(product.getCancelable_status());
                        favProduct.setTill_status(product.getTill_status());
                        favProduct.setPriceVariations(product.getPriceVariations());
                        favProduct.setOther_images(product.getOther_images());
                        favProduct.setIs_favorite(true);
                        if (favorite) {
                            FavoriteFragment.favoriteArrayList.add(favProduct);
                        } else {
                            FavoriteFragment.favoriteArrayList.remove(position);
                        }
                        FavoriteFragment.favoriteLoadMoreAdapter.notifyDataSetChanged();
                    } else {
                        if (favorite) {
                            FavoriteFragment.productArrayList.add(product);
                        } else {
                            FavoriteFragment.productArrayList.remove(position);
                        }
                        FavoriteFragment.offlineFavoriteAdapter.notifyDataSetChanged();
                    }
                } else if (from.equals("search")) {
                    SearchFragment.productArrayList.get(position).setIs_favorite(favorite);
                    SearchFragment.productAdapter.notifyDataSetChanged();
                }
            }
        });

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (ApiConfig.isConnected(activity)) {
                    Constant.CLICK = true;
                    count = Integer.parseInt(txtqty.getText().toString());
                    if (!(count <= 0)) {
                        if (count != 0) {
                            count--;
                            txtqty.setText("" + count);
                            if (isLogin) {
                                if (Constant.CartValues.containsKey(priceVariationslist.get(vpos).getId())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Constant.CartValues.replace(priceVariationslist.get(vpos).getId(), "" + count);
                                    } else {
                                        Constant.CartValues.remove(priceVariationslist.get(vpos).getId());
                                        Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                    }
                                } else {
                                    Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                }

                                ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                            } else {
                                databaseHelper.AddOrderData(priceVariationslist.get(vpos).getId(), priceVariation.getProduct_id(), "" + count);
                            }
                            NotifyData(count);
                        }

                    }
                }

            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (ApiConfig.isConnected(activity)) {
                    count = Integer.parseInt(txtqty.getText().toString());
                    if (!(count >= Float.parseFloat(priceVariationslist.get(vpos).getStock()))) {
                        if (count < Integer.parseInt(session.getData(Constant.max_cart_items_count))) {
                            Constant.CLICK = true;
                            count++;
                            txtqty.setText("" + count);
                            if (isLogin) {
                                if (Constant.CartValues.containsKey(priceVariationslist.get(vpos).getId())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Constant.CartValues.replace(priceVariationslist.get(vpos).getId(), "" + count);
                                    } else {
                                        Constant.CartValues.remove(priceVariationslist.get(vpos).getId());
                                        Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                    }
                                } else {
                                    Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                }
                                ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                            } else {
                                databaseHelper.AddOrderData(priceVariationslist.get(vpos).getId(), priceVariation.getProduct_id(), "" + count);
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                    NotifyData(count);
                }
            }
        });

        return root;
    }

    public void ShowSimilar() {
        Fragment fragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", product.getId());
        bundle.putString("cat_id", product.getCategory_id());
        bundle.putString(Constant.FROM, "similar");
        bundle.putString("name", "Similar Products");
        fragment.setArguments(bundle);
        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }


    void GetSimilarData(Product product) {
        ArrayList<Product> productArrayList = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SIMILAR_PRODUCT, Constant.GetVal);
        params.put(Constant.PRODUCT_ID, product.getId());
        params.put(Constant.CATEGORY_ID, product.getCategory_id());
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                                        JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                                        for (int j = 0; j < pricearray.length(); j++) {
                                            JSONObject obj = pricearray.getJSONObject(j);
                                            String discountpercent = "0";
                                            if (!obj.getString(Constant.DISCOUNTED_PRICE).equals("0")) {
                                                discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                                            }
                                            priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                                        }
                                        productArrayList.add(new Product(jsonObject.getString(Constant.TAX_PERCENT), jsonObject.getString(Constant.ROW_ORDER), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR)));
                                    } catch (JSONException e) {

                                    }
                                }
                            } catch (Exception e) {

                            }

                            AdapterStyle1 adapter = new AdapterStyle1(getContext(), activity, productArrayList, R.layout.offer_layout);
                            recyclerView.setAdapter(adapter);
                            relativeLayout.setVisibility(View.VISIBLE);
                        } else {
                            relativeLayout.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.GET_SIMILAR_PRODUCT_URL, params, false);
    }

    public void NotifyData(int count) {
        switch (from) {
            case "fragment":
                ProductListFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                ProductListFragment.mAdapter.notifyItemChanged(position, ProductListFragment.productArrayList.get(position));
                if (session.isUserLoggedIn()) {
                    ApiConfig.getCartItemCount(activity, session);
                } else {
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "favorite":
                if (session.isUserLoggedIn()) {
                    FavoriteFragment.favoriteArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                    FavoriteFragment.favoriteLoadMoreAdapter.notifyItemChanged(position, FavoriteFragment.favoriteArrayList.get(position));
                } else {
                    FavoriteFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                    FavoriteFragment.offlineFavoriteAdapter.notifyItemChanged(position, FavoriteFragment.productArrayList.get(position));
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "search":
                SearchFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                SearchFragment.productAdapter.notifyItemChanged(position, SearchFragment.productArrayList.get(position));
                if (!session.isUserLoggedIn()) {
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "section":
            case "share":
                if (!session.isUserLoggedIn()) {
                    databaseHelper.getTotalItemOfCart(activity);
                } else {
                    ApiConfig.getCartItemCount(activity, session);
                }
                activity.invalidateOptionsMenu();
                break;
        }
    }

    void GetProductDetail(final String productid) {
        scrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        if (from.equals("share")) {
            params.put(Constant.SLUG, productid);
        } else {
            params.put(Constant.PRODUCT_ID, productid);
        }
        if (session.isUserLoggedIn()) {
            params.put(Constant.USER_ID, session.getData(Constant.ID));
        }

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            product = new Product();
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                                        JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                                        for (int j = 0; j < pricearray.length(); j++) {
                                            JSONObject obj = pricearray.getJSONObject(j);
                                            String discountpercent = "0";
                                            if (!obj.getString(Constant.DISCOUNTED_PRICE).equals("0")) {
                                                discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                                            }
                                            priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                                        }
                                        product = new Product(jsonObject.getString(Constant.TAX_PERCENT), jsonObject.getString(Constant.ROW_ORDER), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR));
                                    } catch (JSONException ignored) {

                                    }
                                }
                            } catch (Exception ignored) {

                            }
                            priceVariationslist = product.getPriceVariations();

                            SetProductDetails(product);
                            GetSimilarData(product);

                        }
                        scrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    } catch (JSONException e) {
                        scrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    }
                }
            }
        }, activity, Constant.GET_PRODUCT_DETAIL_URL, params, false);
    }


    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    void SetProductDetails(final Product product) {
        try {

            txtProductName.setText(product.getName());
            try {
                taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
            } catch (Exception e) {

            }

            sliderArrayList = new ArrayList<>();

            JSONArray jsonArray = product.getOther_images();
            size = jsonArray.length();

            sliderArrayList.add(new Slider(product.getImage()));

            if (product.getMade_in().length() > 0) {
                lytMadeIn.setVisibility(View.VISIBLE);
                tvMadeIn.setText(product.getMade_in());
            }

            if (product.getManufacturer().length() > 0) {
                lytMfg.setVisibility(View.VISIBLE);
                tvMfg.setText(product.getManufacturer());
            }

            if (isLogin) {
                if (product.isIs_favorite()) {
                    favorite = true;
                    imgFav.setImageResource(R.drawable.ic_is_favorite);
                } else {
                    favorite = false;
                    imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                }
            } else {
                if (databaseHelper.getFavouriteById(product.getId())) {
                    imgFav.setImageResource(R.drawable.ic_is_favorite);
                } else {
                    imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                }
            }

            if (isLogin) {
                if (Constant.CartValues.containsKey(product.getPriceVariations().get(0).getId())) {
                    txtqty.setText("" + Constant.CartValues.get(product.getPriceVariations().get(0).getId()));
                } else {
                    txtqty.setText(product.getPriceVariations().get(0).getCart_count());
                }
            } else {
                txtqty.setText(databaseHelper.CheckOrderExists(product.getPriceVariations().get(0).getId(), product.getPriceVariations().get(0).getProduct_id()));
            }

            if (product.getReturn_status().equalsIgnoreCase("1")) {
                imgReturnable.setImageDrawable(getResources().getDrawable(R.drawable.ic_returnable));
                tvReturnable.setText(Integer.parseInt(session.getData(Constant.max_product_return_days)) + " Days Returnable.");
            } else {
                imgReturnable.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_returnable));
                tvReturnable.setText("Not Returnable.");
            }

            if (product.getCancelable_status().equalsIgnoreCase("1")) {
                imgCancellable.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancellable));
                tvCancellable.setText("Order Can Cancel Till Order " + ApiConfig.toTitleCase(product.getTill_status()) + ".");
            } else {
                imgCancellable.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_cancellable));
                tvCancellable.setText("Non Cancellable.");
            }


            for (int i = 0; i < jsonArray.length(); i++) {
                sliderArrayList.add(new Slider(jsonArray.getString(i)));
            }

            viewPager.setAdapter(new SliderAdapter(sliderArrayList, activity, R.layout.lyt_detail_slider, "detail"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, getContext());


            if (priceVariationslist.size() == 1) {
                spinner.setVisibility(View.INVISIBLE);
                lytSpinner.setVisibility(View.INVISIBLE);
                lytmainprice.setEnabled(false);
                priceVariation = priceVariationslist.get(0);
                session.setData(Constant.PRODUCT_VARIANT_ID, "" + 0);
                SetSelectedData();
            }

            if (!product.getIndicator().equals("0")) {
                imgIndicator.setVisibility(View.VISIBLE);
                if (product.getIndicator().equals("1"))
                    imgIndicator.setImageResource(R.drawable.ic_veg_icon);
                else if (product.getIndicator().equals("2"))
                    imgIndicator.setImageResource(R.drawable.ic_non_veg_icon);
            }
            ProductDetailFragment.CustomAdapter customAdapter = new ProductDetailFragment.CustomAdapter();
            spinner.setAdapter(customAdapter);

            webDescription.setVerticalScrollBarEnabled(true);
            webDescription.loadDataWithBaseURL("", product.getDescription(), "text/html", "UTF-8", "");
            webDescription.setBackgroundColor(getResources().getColor(R.color.white));
            txtProductName.setText(product.getName());

            spinner.setSelection(vpos);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int position) {
                    ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, getContext());
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    priceVariation = product.getPriceVariations().get(i);
                    vpos = i;
                    session.setData(Constant.PRODUCT_VARIANT_ID, "" + i);
                    SetSelectedData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            scrollView.setVisibility(View.VISIBLE);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.app_name);
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


    @SuppressLint("SetTextI18n")
    public void SetSelectedData() {

        txtMeasurement.setText(" ( " + priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + " ) ");
        txtstatus.setText(priceVariation.getServe_for());

        double price, oPrice;
        String taxPercentage = "0";
        try {
            taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
        } catch (Exception e) {

        }
        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
            lytDiscount.setVisibility(View.INVISIBLE);
            price = ((Float.parseFloat(priceVariation.getPrice()) + ((Float.parseFloat(priceVariation.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
        } else {
            price = ((Float.parseFloat(priceVariation.getDiscounted_price()) + ((Float.parseFloat(priceVariation.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
            oPrice = (Float.parseFloat(priceVariation.getPrice()) + ((Float.parseFloat(priceVariation.getPrice()) * Float.parseFloat(taxPercentage)) / 100));

            txtoriginalprice.setPaintFlags(txtoriginalprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtoriginalprice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + oPrice));

            lytDiscount.setVisibility(View.VISIBLE);
            showDiscount.setText(priceVariation.getDiscountpercent().replace("(", "").replace(")", ""));
        }
        txtPrice.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + price));


        if (isLogin) {
//            System.out.println("priceVariation.getId()) : " + Constant.CartValues);
            if (Constant.CartValues.containsKey(priceVariation.getId())) {
                txtqty.setText(Constant.CartValues.get(priceVariation.getId()));
            } else {
                txtqty.setText(priceVariation.getCart_count());
            }
        } else {
            txtqty.setText(databaseHelper.CheckOrderExists(priceVariation.getId(), priceVariation.getProduct_id()));
        }

        if (priceVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            txtstatus.setVisibility(View.VISIBLE);
            lytqty.setVisibility(View.GONE);
        } else {
            txtstatus.setVisibility(View.GONE);
            lytqty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(true);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, activity));
        activity.invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return product.getPriceVariations().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "SetTextI18n"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
//            TextView price = view.findViewById(R.id.txtprice);

            PriceVariation priceVariation = product.getPriceVariations().get(i);
            measurement.setText(priceVariation.getMeasurement() + " " + priceVariation.getMeasurement_unit_name());
//            price.setText(session.getData(Constant.currency) + priceVariation.getPrice());

            if (priceVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(getResources().getColor(R.color.red));
//                price.setTextColor(getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(getResources().getColor(R.color.black));
//                price.setTextColor(getResources().getColor(R.color.black));
            }

            return view;
        }
    }
}
