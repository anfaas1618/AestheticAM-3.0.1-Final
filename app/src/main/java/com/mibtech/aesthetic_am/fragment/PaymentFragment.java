package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.activity.MidtransActivity;
import com.mibtech.aesthetic_am.activity.PayPalWebActivity;
import com.mibtech.aesthetic_am.activity.PayStackActivity;
import com.mibtech.aesthetic_am.activity.StripeActivity;
import com.mibtech.aesthetic_am.adapter.DateAdapter;
import com.mibtech.aesthetic_am.adapter.SlotAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.PaymentModelClass;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.BookingDate;
import com.mibtech.aesthetic_am.model.Slot;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PaymentFragment extends Fragment implements PaytmPaymentTransactionCallback {
    public static final String TAG = CheckoutFragment.class.getSimpleName();
    public static String customerId;
    public static String razorPayId;
    public static String paymentMethod = "";
    public static String deliveryTime = "";
    public static String deliveryDay = "";
    public static String pCode = "";
    RadioGroup lytPayment;
    public static Map<String, String> sendparams;
    public static RecyclerView recyclerView;
    public static SlotAdapter adapter;
    public LinearLayout paymentLyt, deliveryTimeLyt, lytPayOption, lytOrderList, lytCLocation, processLyt;
    public ArrayList<String> variantIdList, qtyList, dateList;
    TextView tvSubTotal, txttotalitems, tvSelectDeliveryDate, tvWltBalance, tvProceedOrder, tvConfirmOrder, tvPayment, tvDelivery;
    double subtotal = 0.0, usedBalance = 0.0, totalAfterTax = 0.0, taxAmt = 0.0, pCodeDiscount = 0.0;
    RadioButton rbCOD, rbPayU, rbPayPal, rbRazorPay, rbPayStack, rbFlutterWave, rbMidTrans, rbStripe, rbPayTm;
    ArrayList<BookingDate> bookingDates;
    RelativeLayout confirmLyt, lytWallet;
    RecyclerView recyclerViewDates;
    Calendar StartDate, EndDate;
    ScrollView scrollPaymentLyt;
    ArrayList<Slot> slotList;
    DateAdapter dateAdapter;
    int mYear, mMonth, mDay;
    String address = null;
    ImageView imgRefresh;
    Activity activity;
    CheckBox chWallet;
    Button btnApply;
    Session session;
    double total;
    View root;
    private ShimmerFrameLayout mShimmerViewContainer;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_payment, container, false);
        activity = getActivity();
        Constant.selectedDatePosition = 0;
        session = new Session(getActivity());
        getAllWidgets(root);
        setHasOptionsMenu(true);
        total = getArguments().getDouble("total");
        subtotal = getArguments().getDouble("subtotal");

        taxAmt = getArguments().getDouble("taxAmt");
        Constant.SETTING_TAX = getArguments().getDouble("tax");
        pCodeDiscount = getArguments().getDouble("pCodeDiscount");
        pCode = getArguments().getString("pCode");
        address = getArguments().getString("address");
        variantIdList = getArguments().getStringArrayList("variantIdList");
        qtyList = getArguments().getStringArrayList("qtyList");

        tvSubTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + subtotal));
        txttotalitems.setText(Constant.TOTAL_CART_ITEM + " Items");

        if (ApiConfig.isConnected(getActivity())) {
            ApiConfig.getWalletBalance(getActivity(), session);

            GetPaymentConfig();
            chWallet.setTag("false");
            tvWltBalance.setText("Total Balance: " + session.getData(Constant.currency) + ApiConfig.StringFormat("" + Constant.WALLET_BALANCE));
            if (Constant.WALLET_BALANCE == 0) {
                lytWallet.setVisibility(View.GONE);
            } else {
                lytWallet.setVisibility(View.VISIBLE);
            }

            tvProceedOrder.setOnClickListener(v -> PlaceOrderProcess());

            chWallet.setOnClickListener(view -> {
                if (chWallet.getTag().equals("false")) {
                    chWallet.setChecked(true);
                    lytWallet.setVisibility(View.VISIBLE);

                    if (Constant.WALLET_BALANCE >= subtotal) {
                        usedBalance = subtotal;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + session.getData(Constant.currency) + ApiConfig.StringFormat("" + (Constant.WALLET_BALANCE - usedBalance)));
                        paymentMethod = Constant.WALLET;
                        lytPayOption.setVisibility(View.GONE);
                    } else {
                        usedBalance = Constant.WALLET_BALANCE;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + session.getData(Constant.currency) + "0.00");
                        lytPayOption.setVisibility(View.VISIBLE);
                    }
                    subtotal = (subtotal - usedBalance);
                    tvSubTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + subtotal));
                    chWallet.setTag("true");

                } else {
                    walletUncheck();
                }

            });

        }
        confirmLyt.setVisibility(View.VISIBLE);
        scrollPaymentLyt.setVisibility(View.VISIBLE);
        lytPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                try {
                    RadioButton rb = (RadioButton) root.findViewById(checkedId);
                    paymentMethod = rb.getTag().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    public void getAllWidgets(View root) {
        recyclerView = root.findViewById(R.id.recyclerView);

        rbPayTm = root.findViewById(R.id.rbPayTm);
        rbPayStack = root.findViewById(R.id.rbPayStack);
        rbFlutterWave = root.findViewById(R.id.rbFlutterWave);
        rbCOD = root.findViewById(R.id.rbCOD);
        lytPayment = root.findViewById(R.id.lytPayment);
        rbPayU = root.findViewById(R.id.rbPayU);
        rbPayPal = root.findViewById(R.id.rbPayPal);
        rbRazorPay = root.findViewById(R.id.rbRazorPay);
        rbMidTrans = root.findViewById(R.id.rbMidTrans);
        rbStripe = root.findViewById(R.id.rbStripe);


        tvDelivery = root.findViewById(R.id.tvSummary);
        tvPayment = root.findViewById(R.id.tvPayment);
        chWallet = root.findViewById(R.id.chWallet);
        lytPayOption = root.findViewById(R.id.lytPayOption);
        lytOrderList = root.findViewById(R.id.lytOrderList);
        lytCLocation = root.findViewById(R.id.lytCLocation);
        lytWallet = root.findViewById(R.id.lytWallet);
        paymentLyt = root.findViewById(R.id.paymentLyt);
        tvProceedOrder = root.findViewById(R.id.tvProceedOrder);
        tvConfirmOrder = root.findViewById(R.id.tvConfirmOrder);
        processLyt = root.findViewById(R.id.processLyt);
        tvSelectDeliveryDate = root.findViewById(R.id.tvSelectDeliveryDate);
        deliveryTimeLyt = root.findViewById(R.id.deliveryTimeLyt);
        imgRefresh = root.findViewById(R.id.imgRefresh);
        recyclerViewDates = root.findViewById(R.id.recyclerViewDates);
        tvSubTotal = root.findViewById(R.id.tvSubTotal);
        txttotalitems = root.findViewById(R.id.txttotalitems);
        confirmLyt = root.findViewById(R.id.confirmLyt);
        scrollPaymentLyt = root.findViewById(R.id.scrollPaymentLyt);
        tvWltBalance = root.findViewById(R.id.tvWltBalance);
        btnApply = root.findViewById(R.id.btnApply);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);

    }

    public void GetPaymentConfig() {
        recyclerView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_PAYMENT_METHOD, Constant.GetVal);
        //  System.out.println("=====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
//                        System.out.println("=======payment res " + response);
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            if (objectbject.has(Constant.PAYMENT_METHODS)) {
                                JSONObject object = objectbject.getJSONObject(Constant.PAYMENT_METHODS);
                                if (object.has(Constant.cod_payment_method)) {
                                    Constant.COD = object.getString(Constant.cod_payment_method);
                                }
                                if (object.has(Constant.payu_method)) {
                                    Constant.PAYUMONEY = object.getString(Constant.payu_method);
                                    Constant.PAYUMONEY_MODE = object.getString(Constant.payumoney_mode);
                                    Constant.MERCHANT_KEY = object.getString(Constant.PAY_M_KEY);
                                    Constant.MERCHANT_ID = object.getString(Constant.PAYU_M_ID);
                                    Constant.MERCHANT_SALT = object.getString(Constant.PAYU_SALT);
                                    ApiConfig.SetAppEnvironment(activity);
                                }
                                if (object.has(Constant.razor_pay_method)) {
                                    Constant.RAZORPAY = object.getString(Constant.razor_pay_method);
                                    Constant.RAZOR_PAY_KEY_VALUE = object.getString(Constant.RAZOR_PAY_KEY);
                                }
                                if (object.has(Constant.paypal_method)) {
                                    Constant.PAYPAL = object.getString(Constant.paypal_method);
                                }
                                if (object.has(Constant.paystack_method)) {
                                    Constant.PAYSTACK = object.getString(Constant.paystack_method);
                                    Constant.PAYSTACK_KEY = object.getString(Constant.paystack_public_key);
                                }
                                if (object.has(Constant.flutterwave_payment_method)) {
                                    Constant.FLUTTERWAVE = object.getString(Constant.flutterwave_payment_method);
                                    Constant.FLUTTERWAVE_ENCRYPTION_KEY_VAL = object.getString(Constant.flutterwave_encryption_key);
                                    Constant.FLUTTERWAVE_PUBLIC_KEY_VAL = object.getString(Constant.flutterwave_public_key);
                                    Constant.FLUTTERWAVE_SECRET_KEY_VAL = object.getString(Constant.flutterwave_secret_key);
                                    Constant.FLUTTERWAVE_SECRET_KEY_VAL = object.getString(Constant.flutterwave_secret_key);
                                    Constant.FLUTTERWAVE_CURRENCY_CODE_VAL = object.getString(Constant.flutterwave_currency_code);
                                }
                                if (object.has(Constant.midtrans_payment_method)) {
                                    Constant.MIDTRANS = object.getString(Constant.midtrans_payment_method);
                                }
                                if (object.has(Constant.stripe_payment_method)) {
                                    Constant.STRIPE = object.getString(Constant.stripe_payment_method);
                                }
                                if (object.has(Constant.paytm_payment_method)) {
                                    Constant.PAYTM = object.getString(Constant.paytm_payment_method);
                                    Constant.PAYTM_MERCHANT_ID = object.getString(Constant.paytm_merchant_id);
                                    Constant.PAYTM_MERCHANT_KEY = object.getString(Constant.paytm_merchant_key);
                                    Constant.PAYTM_MODE = object.getString(Constant.paytm_mode);
                                }

                                setPaymentMethod();
                            } else {
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                Toast.makeText(activity, getString(R.string.alert_payment_methods_blank), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    public void setPaymentMethod() {
        if (subtotal > 0) {
            if (Constant.FLUTTERWAVE.equals("0") && Constant.PAYPAL.equals("0") && Constant.PAYUMONEY.equals("0") && Constant.COD.equals("0") && Constant.RAZORPAY.equals("0") && Constant.PAYSTACK.equals("0") && Constant.MIDTRANS.equals("0") && Constant.STRIPE.equals("0")) {
                lytPayOption.setVisibility(View.GONE);
            } else {
                lytPayOption.setVisibility(View.VISIBLE);

                if (Constant.COD.equals("1")) {
                    rbCOD.setVisibility(View.VISIBLE);
                }
                if (Constant.PAYUMONEY.equals("1")) {
                    rbPayU.setVisibility(View.VISIBLE);
                }
                if (Constant.RAZORPAY.equals("1")) {
                    rbRazorPay.setVisibility(View.VISIBLE);
                }
                if (Constant.PAYSTACK.equals("1")) {
                    rbPayStack.setVisibility(View.VISIBLE);
                }
                if (Constant.FLUTTERWAVE.equals("1")) {
                    rbFlutterWave.setVisibility(View.VISIBLE);
                }
                if (Constant.PAYPAL.equals("1")) {
                    rbPayPal.setVisibility(View.VISIBLE);
                }
                if (Constant.MIDTRANS.equals("1")) {
                    rbMidTrans.setVisibility(View.VISIBLE);
                }
                if (Constant.STRIPE.equals("1")) {
                    rbStripe.setVisibility(View.VISIBLE);
                }
                if (Constant.PAYTM.equals("1")) {
                    rbPayTm.setVisibility(View.VISIBLE);
                }
            }
            getTimeSlots();
        } else {
            lytWallet.setVisibility(View.GONE);
            lytPayOption.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void walletUncheck() {
        paymentMethod = "";
        lytPayOption.setVisibility(View.VISIBLE);
        tvWltBalance.setText(getString(R.string.total) + session.getData(Constant.currency) + Constant.WALLET_BALANCE);
        subtotal = (subtotal + usedBalance);
        tvSubTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + subtotal));
        chWallet.setChecked(false);
        chWallet.setTag("false");
    }

    public void getTimeSlots() {
        GetTimeSlotConfig(session, getActivity());
    }


    public void GetTimeSlotConfig(final Session session, Activity activity) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_TIME_SLOT_CONFIG, Constant.GetVal);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    if (!jsonObject1.getBoolean(Constant.ERROR)) {
                        JSONObject jsonObject = new JSONObject(jsonObject1.getJSONObject(Constant.TIME_SLOT_CONFIG).toString());

                        session.setData(Constant.IS_TIME_SLOTS_ENABLE, jsonObject.getString(Constant.IS_TIME_SLOTS_ENABLE));
                        session.setData(Constant.DELIVERY_STARTS_FROM, jsonObject.getString(Constant.DELIVERY_STARTS_FROM));
                        session.setData(Constant.ALLOWED_DAYS, jsonObject.getString(Constant.ALLOWED_DAYS));

                        if (session.getData(Constant.IS_TIME_SLOTS_ENABLE).equals(Constant.GetVal)) {
                            deliveryTimeLyt.setVisibility(View.VISIBLE);

                            StartDate = Calendar.getInstance();
                            EndDate = Calendar.getInstance();
                            mYear = StartDate.get(Calendar.YEAR);
                            mMonth = StartDate.get(Calendar.MONTH);
                            mDay = StartDate.get(Calendar.DAY_OF_MONTH);

                            int DeliveryStartFrom = Integer.parseInt(session.getData(Constant.DELIVERY_STARTS_FROM)) - 1;
                            int DeliveryAllowFrom = Integer.parseInt(session.getData(Constant.ALLOWED_DAYS));

                            StartDate.add(Calendar.DATE, DeliveryStartFrom);
                            EndDate.add(Calendar.DATE, (DeliveryStartFrom + DeliveryAllowFrom));

                            dateList = ApiConfig.getDates(StartDate.get(Calendar.DATE) + "-" + (StartDate.get(Calendar.MONTH) + 1) + "-" + StartDate.get(Calendar.YEAR), EndDate.get(Calendar.DATE) + "-" + (EndDate.get(Calendar.MONTH) + 1) + "-" + EndDate.get(Calendar.YEAR));
                            setDateList(dateList);

                            GetTimeSlots();

                        } else {
                            deliveryTimeLyt.setVisibility(View.GONE);
                            deliveryDay = "Date : N/A";
                            deliveryTime = "Time : N/A";

                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                    }
                } catch (JSONException e) {
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    public void GetTimeSlots() {
        slotList = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("get_time_slots", Constant.GetVal);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);

                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray("time_slots");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object1 = jsonArray.getJSONObject(i);
                                slotList.add(new Slot(object1.getString("id"), object1.getString("title"), object1.getString("last_order_time")));
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapter = new SlotAdapter(deliveryTime, getActivity(), slotList);
                            recyclerView.setAdapter(adapter);

                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, getActivity(), Constant.SETTING_URL, params, true);
    }

    public void setDateList(ArrayList<String> datesList) {
        bookingDates = new ArrayList<>();
        for (int i = 0; i < datesList.size(); i++) {
            String[] date = datesList.get(i).split("-");

            BookingDate bookingDate1 = new BookingDate();
            bookingDate1.setDate(date[0]);
            bookingDate1.setMonth(date[1]);
            bookingDate1.setYear(date[2]);
            bookingDate1.setDay(date[3]);

            bookingDates.add(bookingDate1);
        }
        dateAdapter = new DateAdapter(getActivity(), bookingDates);

        recyclerViewDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDates.setAdapter(dateAdapter);
    }


    @SuppressLint("SetTextI18n")
    public void PlaceOrderProcess() {
        totalAfterTax = (total + Constant.SETTING_DELIVERY_CHARGE + taxAmt);
        if (deliveryDay.length() == 0) {
            Toast.makeText(getContext(), getString(R.string.select_delivery_day), Toast.LENGTH_SHORT).show();
            return;
        } else if (deliveryTime.length() == 0) {
            Toast.makeText(getContext(), getString(R.string.select_delivery_time), Toast.LENGTH_SHORT).show();
            return;
        } else if (paymentMethod.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.select_payment_method), Toast.LENGTH_SHORT).show();
            return;
        }
        sendparams = new HashMap<>();
        sendparams.put(Constant.PLACE_ORDER, Constant.GetVal);
        sendparams.put(Constant.USER_ID, session.getData(Constant.ID));
        sendparams.put(Constant.TAX_AMOUNT, "" + taxAmt);
        sendparams.put(Constant.TOTAL, "" + total);
        sendparams.put(Constant.TAX_PERCENT, "" + Constant.SETTING_TAX);
        sendparams.put(Constant.FINAL_TOTAL, "" + subtotal);
        sendparams.put(Constant.PRODUCT_VARIANT_ID, String.valueOf(variantIdList));
        sendparams.put(Constant.QUANTITY, String.valueOf(qtyList));
        sendparams.put(Constant.MOBILE, session.getData(Constant.MOBILE));
        sendparams.put(Constant.DELIVERY_CHARGE, "" + Constant.SETTING_DELIVERY_CHARGE);
        sendparams.put(Constant.DELIVERY_TIME, (deliveryDay + " - " + deliveryTime));
        sendparams.put(Constant.KEY_WALLET_USED, chWallet.getTag().toString());
        sendparams.put(Constant.KEY_WALLET_BALANCE, String.valueOf(usedBalance));
        sendparams.put(Constant.PAYMENT_METHOD, paymentMethod);
        if (!pCode.isEmpty()) {
            sendparams.put(Constant.PROMO_CODE, pCode);
            sendparams.put(Constant.PROMO_DISCOUNT, ApiConfig.StringFormat("" + pCodeDiscount));
        }
        sendparams.put(Constant.ADDRESS, address);
        sendparams.put(Constant.LONGITUDE, session.getCoordinates(Constant.LONGITUDE));
        sendparams.put(Constant.LATITUDE, session.getCoordinates(Constant.LATITUDE));
        sendparams.put(Constant.EMAIL, session.getData(Constant.EMAIL));

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_order_confirm, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvDialogCancel, tvDialogConfirm, tvDialogItemTotal, tvDialogDeliveryCharge, tvDialogTotal, tvDialogPCAmount, tvDialogWallet, tvDialogFinalTotal;
        LinearLayout lytDialogPromo, lytDialogWallet;
        EditText tvSpecialNote;

        lytDialogPromo = dialogView.findViewById(R.id.lytDialogPromo);
        lytDialogWallet = dialogView.findViewById(R.id.lytDialogWallet);
        tvDialogItemTotal = dialogView.findViewById(R.id.tvDialogItemTotal);
        tvDialogDeliveryCharge = dialogView.findViewById(R.id.tvDialogDeliveryCharge);
        tvDialogTotal = dialogView.findViewById(R.id.tvDialogTotal);
        tvDialogPCAmount = dialogView.findViewById(R.id.tvDialogPCAmount);
        tvDialogWallet = dialogView.findViewById(R.id.tvDialogWallet);
        tvDialogFinalTotal = dialogView.findViewById(R.id.tvDialogFinalTotal);
        tvDialogCancel = dialogView.findViewById(R.id.tvDialogCancel);
        tvDialogConfirm = dialogView.findViewById(R.id.tvDialogConfirm);
        tvSpecialNote = dialogView.findViewById(R.id.tvSpecialNote);

        if (pCodeDiscount > 0) {
            lytDialogPromo.setVisibility(View.VISIBLE);
            tvDialogPCAmount.setText("- " + session.getData(Constant.currency) + pCodeDiscount);
        } else {
            lytDialogPromo.setVisibility(View.GONE);
        }

        if (chWallet.getTag().toString().equals("true")) {
            lytDialogWallet.setVisibility(View.VISIBLE);
            tvDialogWallet.setText("- " + session.getData(Constant.currency) + usedBalance);
        } else {
            lytDialogWallet.setVisibility(View.GONE);
        }

        tvDialogItemTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + total));
        tvDialogDeliveryCharge.setText(Constant.SETTING_DELIVERY_CHARGE > 0 ? session.getData(Constant.currency) + ApiConfig.StringFormat("" + Constant.SETTING_DELIVERY_CHARGE) : getString(R.string.free));
        tvDialogTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + totalAfterTax));
        tvDialogFinalTotal.setText(session.getData(Constant.currency) + ApiConfig.StringFormat("" + subtotal));
        tvDialogConfirm.setOnClickListener(v -> {
            sendparams.put(Constant.ORDER_NOTE, tvSpecialNote.getText().toString().trim());
            if (paymentMethod.equals(getResources().getString(R.string.codpaytype)) || paymentMethod.equals(getString(R.string.wallettype))) {
                ApiConfig.RequestToVolley((result, response) -> {
                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                if (chWallet.getTag().toString().equals("true")) {
                                    ApiConfig.getWalletBalance(getActivity(), session);
                                }
                                dialog.dismiss();
                                MainActivity.fm.beginTransaction().add(R.id.container, new OrderPlacedFragment()).commit();
                            } else {
                                Toast.makeText(getActivity(), object.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                        }
                    }
                }, getActivity(), Constant.ORDERPROCESS_URL, sendparams, true);
                dialog.dismiss();
            } else {
                sendparams.put(Constant.USER_NAME, session.getData(Constant.NAME));
                if (paymentMethod.equals(getString(R.string.pay_u))) {
                    dialog.dismiss();
                    sendparams.put(Constant.MOBILE, session.getData(Constant.MOBILE));
                    sendparams.put(Constant.USER_NAME, session.getData(Constant.NAME));
                    sendparams.put(Constant.EMAIL, session.getData(Constant.EMAIL));
                    new PaymentModelClass(activity).OnPayClick(getActivity(), sendparams, Constant.PAYMENT, sendparams.get(Constant.FINAL_TOTAL));
                } else if (paymentMethod.equals(getString(R.string.paypal))) {
                    dialog.dismiss();
                    sendparams.put(Constant.FROM, Constant.PAYMENT);
                    sendparams.put(Constant.STATUS, Constant.AWAITING_PAYMENT);
                    PlaceOrder(activity, getString(R.string.midtrans), System.currentTimeMillis() + Constant.randomNumeric(3), true, sendparams, "paypal");
                } else if (paymentMethod.equals(getString(R.string.razor_pay))) {
                    dialog.dismiss();
                    CreateOrderId(subtotal);

                } else if (paymentMethod.equals(getString(R.string.paystack))) {
                    dialog.dismiss();
                    sendparams.put(Constant.FROM, Constant.PAYMENT);
                    Intent intent = new Intent(activity, PayStackActivity.class);
                    intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
                    startActivity(intent);
                } else if (paymentMethod.equals(getString(R.string.midtrans))) {
                    dialog.dismiss();
                    sendparams.put(Constant.FROM, Constant.PAYMENT);
                    sendparams.put(Constant.STATUS, Constant.AWAITING_PAYMENT);
                    PlaceOrder(activity, getString(R.string.midtrans), System.currentTimeMillis() + Constant.randomNumeric(3), true, sendparams, "midtrans");
                } else if (paymentMethod.equals(getString(R.string.stripe))) {
                    dialog.dismiss();
                    sendparams.put(Constant.FROM, Constant.PAYMENT);
                    sendparams.put(Constant.STATUS, Constant.AWAITING_PAYMENT);
                    PlaceOrder(activity, getString(R.string.stripe), System.currentTimeMillis() + Constant.randomNumeric(3), true, sendparams, "stripe");
                } else if (paymentMethod.equals(getString(R.string.flutterwave))) {
                    dialog.dismiss();
                    StartFlutterWavePayment();
                } else if (paymentMethod.equals(getString(R.string.paytm))) {
                    dialog.dismiss();
                    startPayTmPayment();
                }
            }
        });

        tvDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void CreateOrderId(double payble) {
        Map<String, String> params = new HashMap<>();
        params.put("amount", "" + Math.round(payble) + "00");
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            startPayment(object.getString("id"), object.getString("amount"));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, getActivity(), Constant.Get_RazorPay_OrderId, params, true);

    }

    public void startPayment(String orderId, String payAmount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.mipmap.ic_launcher);

        try {
            JSONObject options = new JSONObject();
            options.put(Constant.NAME, session.getData(Constant.NAME));
            options.put(Constant.ORDER_ID, orderId);
            options.put(Constant.CURRENCY, "INR");
            options.put(Constant.AMOUNT, payAmount);

            JSONObject preFill = new JSONObject();
            preFill.put(Constant.EMAIL, session.getData(Constant.EMAIL));
            preFill.put(Constant.CONTACT, session.getData(Constant.MOBILE));
            options.put("prefill", preFill);

            checkout.open(getActivity(), options);
        } catch (Exception e) {
            Log.d(TAG, "Error in starting Razorpay Checkout", e);
        }
    }


    public void PlaceOrder(final Activity activity, final String paymentType, final String txnid, boolean issuccess, final Map<String, String> sendparams, final String status) {
        if (issuccess) {
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                if (status.equals("stripe")) {
                                    CreateStripePayment(object.getString(Constant.ORDER_ID), ApiConfig.StringFormat("" + subtotal));
                                } else if (status.equals("midtrans")) {
                                    CreateMidtransPayment(object.getString(Constant.ORDER_ID), ApiConfig.StringFormat("" + subtotal));
                                } else if (status.equals("paypal")) {
                                    StartPayPalPayment(sendparams);
                                } else {
                                    AddTransaction(activity, object.getString(Constant.ORDER_ID), paymentType, txnid, status, activity.getString(R.string.order_success), sendparams);
                                    MainActivity.fm.beginTransaction().add(R.id.container, new OrderPlacedFragment()).commit();
                                }
                            }
                        } catch (JSONException e) {

                        }
                    }
                }
            }, activity, Constant.ORDERPROCESS_URL, sendparams, false);
        } else {
            AddTransaction(activity, "", getString(R.string.razor_pay), txnid, status, getString(R.string.order_failed), sendparams);
        }
    }

    public void CreateMidtransPayment(String orderId, String grossAmount) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.ORDER_ID, orderId);
        params.put(Constant.GROSS_AMOUNT, grossAmount.split(",")[0]);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            Intent intent = new Intent(activity, MidtransActivity.class);
                            intent.putExtra(Constant.URL, jsonObject.getJSONObject(Constant.DATA).getString(Constant.REDIRECT_URL));
                            intent.putExtra(Constant.ORDER_ID, orderId);
                            intent.putExtra(Constant.FROM, Constant.PAYMENT);
                            intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.MIDTRANS_PAYMENT_URL, params, true);
    }

    public void CreateStripePayment(String orderId, String grossAmount) {
        Intent intent = new Intent(activity, StripeActivity.class);
        intent.putExtra(Constant.ORDER_ID, orderId);
        intent.putExtra(Constant.FROM, Constant.PAYMENT);
        intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
        startActivity(intent);
    }

    public void AddTransaction(Activity activity, String orderId, String paymentType, String txnid, final String status, String message, Map<String, String> sendparams) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put(Constant.ADD_TRANSACTION, Constant.GetVal);
        transparams.put(Constant.USER_ID, sendparams.get(Constant.USER_ID));
        transparams.put(Constant.ORDER_ID, orderId);
        transparams.put(Constant.TYPE, paymentType);
        transparams.put(Constant.TAX_PERCENT, "" + Constant.SETTING_TAX);
        transparams.put(Constant.TRANS_ID, txnid);
        transparams.put(Constant.AMOUNT, sendparams.get(Constant.FINAL_TOTAL));
        transparams.put(Constant.STATUS, status);
        transparams.put(Constant.MESSAGE, message);
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        transparams.put("transaction_date", df.format(c));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            if (status.equals(Constant.FAILED)) {

                            }
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, transparams, false);
    }

    public void StartPayPalPayment(final Map<String, String> sendParams) {

        final Map<String, String> params = new HashMap<>();
        params.put(Constant.FIRST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.LAST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.PAYER_EMAIL, sendParams.get(Constant.EMAIL));
        params.put(Constant.ITEM_NAME, "Card Order");
        params.put(Constant.ITEM_NUMBER, System.currentTimeMillis() + Constant.randomNumeric(3));
        params.put(Constant.AMOUNT, sendParams.get(Constant.FINAL_TOTAL));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                Intent intent = new Intent(getContext(), PayPalWebActivity.class);
                intent.putExtra(Constant.URL, response);
                intent.putExtra(Constant.ORDER_ID, params.get(Constant.ITEM_NUMBER));
                intent.putExtra(Constant.FROM, Constant.PAYMENT);
                intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
                startActivity(intent);
            }
        }, getActivity(), Constant.PAPAL_URL, params, true);
    }


    public void startPayTmPayment() {
        Map<String, String> params = new HashMap<>();

        params.put(Constant.ORDER_ID_, Constant.randomAlphaNumeric(20));
        params.put(Constant.CUST_ID, Constant.randomAlphaNumeric(10));
        params.put(Constant.TXN_AMOUNT, "" + subtotal);
        if (Constant.PAYTM_MODE.equals("sandbox")) {
            params.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_DEMO_VAL);
            params.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_DEMO_VAL);
            params.put(Constant.WEBSITE, Constant.WEBSITE_DEMO_VAL);
        } else if (Constant.PAYTM_MODE.equals("production")) {
            params.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_LIVE_VAL);
            params.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_LIVE_VAL);
            params.put(Constant.WEBSITE, Constant.WEBSITE_LIVE_VAL);
        }

//        System.out.println("====" + params.toString());
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object = jsonObject.getJSONObject(Constant.DATA);
//                    System.out.println("=======res  " + response);

                    PaytmPGService Service = null;
                    if (Constant.PAYTM_MODE.equals("sandbox")) {
                        Service = PaytmPGService.getStagingService(Constant.PAYTM_ORDER_PROCESS_DEMO_VAL);
                    } else if (Constant.PAYTM_MODE.equals("production")) {
                        Service = PaytmPGService.getProductionService();
                    }

                    customerId = object.getString(Constant.CUST_ID);
                    //creating a hashmap and adding all the values required

                    HashMap<String, String> paramMap = new HashMap<>();
                    paramMap.put(Constant.MID, Constant.PAYTM_MERCHANT_ID);
                    paramMap.put(Constant.ORDER_ID_, jsonObject.getString("order id"));
                    paramMap.put(Constant.CUST_ID, object.getString(Constant.CUST_ID));
                    paramMap.put(Constant.TXN_AMOUNT, "" + subtotal);

                    if (Constant.PAYTM_MODE.equals("sandbox")) {
                        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_DEMO_VAL);
                        paramMap.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_DEMO_VAL);
                        paramMap.put(Constant.WEBSITE, Constant.WEBSITE_DEMO_VAL);
                    } else if (Constant.PAYTM_MODE.equals("production")) {
                        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_LIVE_VAL);
                        paramMap.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_LIVE_VAL);
                        paramMap.put(Constant.WEBSITE, Constant.WEBSITE_LIVE_VAL);
                    }

                    paramMap.put(Constant.CALLBACK_URL, object.getString(Constant.CALLBACK_URL));
                    paramMap.put(Constant.CHECKSUMHASH, jsonObject.getString("signature"));

                    //creating a paytm order object using the hashmap
                    PaytmOrder order = new PaytmOrder(paramMap);

                    //intializing the paytm service
                    Service.initialize(order, null);

                    //finally starting the payment transaction
                    Service.startPaymentTransaction(getActivity(), true, true, this);

                } catch (JSONException e) {

                }
            }
        }, Constant.GENERATE_PAYTM_CHECKSUM, params);


    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        String orderId = bundle.getString(Constant.ORDERID);

        String status = bundle.getString(Constant.STATUS_);
        if (status.equalsIgnoreCase(Constant.TXN_SUCCESS)) {
            verifyTransaction(orderId);
        }
    }

    /**
     * Verifying the transaction status once PayTM transaction is over
     * This makes server(own) -> server(PayTM) call to verify the transaction status
     */
    public void verifyTransaction(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getJSONObject("body").getJSONObject("resultInfo").getString("resultStatus");
                    if (status.equalsIgnoreCase("TXN_SUCCESS")) {
                        String txnId = jsonObject.getJSONObject("body").getString("txnId");
                        PlaceOrder(getActivity(), getString(R.string.paytm), txnId, true, sendparams, Constant.SUCCESS);
                    }
                } catch (JSONException e) {

                }
            }
        }, Constant.VALID_TRANSACTION, params);
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(getActivity(), "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(getContext(), s + bundle.toString(), Toast.LENGTH_LONG).show();
    }

    void StartFlutterWavePayment() {
        new RavePayManager(this)
                .setAmount(subtotal)
                .setEmail(session.getData(Constant.EMAIL))
                .setCurrency(Constant.FLUTTERWAVE_CURRENCY_CODE_VAL)
                .setfName(session.getData(Constant.FIRST_NAME))
                .setlName(session.getData(Constant.LAST_NAME))
                .setNarration(getString(R.string.app_name) + getString(R.string.shopping))
                .setPublicKey(Constant.FLUTTERWAVE_PUBLIC_KEY_VAL)
                .setEncryptionKey(Constant.FLUTTERWAVE_ENCRYPTION_KEY_VAL)
                .setTxRef(System.currentTimeMillis() + "Ref")
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptBankTransferPayments(true)
                .acceptBarterPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptSaBankPayments(true)
                .acceptFrancMobileMoneyPayments(true)
                .acceptZmMobileMoneyPayments(true)
                .acceptUssdPayments(true)
                .acceptUkPayments(true)
                .acceptMpesaPayments(true)
                .shouldDisplayFee(true)
                .onStagingEnv(false)
                .showStagingLabel(false)
                .initialize();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RaveConstants.RAVE_REQUEST_CODE && data != null) {
            new PaymentModelClass(activity).TrasactionMethod(data, getActivity(), Constant.PAYMENT);
        } else if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null && data.getStringExtra("response") != null) {
            try {
                JSONObject details = new JSONObject(data.getStringExtra("response"));
                JSONObject jsonObject = details.getJSONObject(Constant.DATA);

                if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                    Toast.makeText(getContext(), getString(R.string.order_placed1), Toast.LENGTH_LONG).show();
                    new PaymentModelClass(getActivity()).PlaceOrder(getActivity(), getString(R.string.flutterwave), jsonObject.getString("txRef"), true, sendparams, Constant.SUCCESS);
                } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                    new PaymentModelClass(getActivity()).PlaceOrder(getActivity(), "", "", false, sendparams, Constant.PENDING);
                    Toast.makeText(getContext(), getString(R.string.order_error), Toast.LENGTH_LONG).show();
                } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                    new PaymentModelClass(getActivity()).PlaceOrder(getActivity(), "", "", false, sendparams, Constant.FAILED);
                    Toast.makeText(getContext(), getString(R.string.order_cancel), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.payment);
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

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }


}