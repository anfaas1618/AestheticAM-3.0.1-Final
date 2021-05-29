package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.DrawerActivity;
import com.mibtech.aesthetic_am.activity.MidtransActivity;
import com.mibtech.aesthetic_am.activity.PayPalWebActivity;
import com.mibtech.aesthetic_am.activity.PayStackActivity;
import com.mibtech.aesthetic_am.activity.StripeActivity;
import com.mibtech.aesthetic_am.adapter.WalletTransactionAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.PaymentModelClass;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.Address;
import com.mibtech.aesthetic_am.model.WalletTransaction;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class WalletTransactionFragment extends Fragment implements PaytmPaymentTransactionCallback {
    public static String amount, msg;
    public static boolean payFromWallet = false;
    public static TextView tvBalance;
    View root;
    RecyclerView recyclerView;
    ArrayList<WalletTransaction> walletTransactions;
    SwipeRefreshLayout swipeLayout;
    NestedScrollView scrollView;
    RelativeLayout tvAlert;
    WalletTransactionAdapter walletTransactionAdapter;
    int total = 0;
    Activity activity;
    int offset = 0;
    TextView tvAlertTitle, tvAlertSubTitle;
    Button btnRechargeWallet;
    Session session;
    boolean isLoadMore = false;
    String paymentMethod = null;
    String customerId;
    private ShimmerFrameLayout mShimmerViewContainer;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_wallet_transection, container, false);

        activity = getActivity();
        session = new Session(activity);

        scrollView = root.findViewById(R.id.scrollView);
        recyclerView = root.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeLayout = root.findViewById(R.id.swipeLayout);
        tvAlert = root.findViewById(R.id.tvAlert);
        tvAlertTitle = root.findViewById(R.id.tvAlertTitle);
        tvAlertSubTitle = root.findViewById(R.id.tvAlertSubTitle);
        tvBalance = root.findViewById(R.id.tvBalance);
        btnRechargeWallet = root.findViewById(R.id.btnRechargeWallet);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);

        tvAlertTitle.setText(getString(R.string.no_wallet_history_found));
        tvAlertSubTitle.setText(getString(R.string.you_have_not_any_wallet_history_yet));

        setHasOptionsMenu(true);

        getTransactionData(activity, session);

        swipeLayout.setColorSchemeResources(R.color.colorPrimary);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
                offset = 0;
                getTransactionData(activity, session);
            }
        });

        ApiConfig.getWalletBalance(activity, session);

        tvBalance.setText(session.getData(Constant.currency) + Constant.WALLET_BALANCE);

        btnRechargeWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View dialogView = inflater.inflate(R.layout.dialog_wallet_recharge, null);
                alertDialog.setView(dialogView);
                alertDialog.setCancelable(true);
                final AlertDialog dialog = alertDialog.create();
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView tvDialogSend, tvDialogCancel, edtAmount, edtMsg;
                LinearLayout lytPayOption;
                RadioGroup lytPayment;
                RadioButton rbPayU, rbPayPal, rbRazorPay, rbPayStack, rbFlutterWave, rbMidTrans, rbStripe, rbPayTm;

                edtAmount = dialogView.findViewById(R.id.edtAmount);
                edtMsg = dialogView.findViewById(R.id.edtMsg);
                tvDialogCancel = dialogView.findViewById(R.id.tvDialogCancel);
                tvDialogSend = dialogView.findViewById(R.id.tvDialogRecharge);
                lytPayOption = dialogView.findViewById(R.id.lytPayOption);

                rbPayStack = dialogView.findViewById(R.id.rbPayStack);
                rbFlutterWave = dialogView.findViewById(R.id.rbFlutterWave);
                rbPayPal = dialogView.findViewById(R.id.rbPayPal);
                rbRazorPay = dialogView.findViewById(R.id.rbRazorPay);
                rbMidTrans = dialogView.findViewById(R.id.rbMidTrans);
                rbStripe = dialogView.findViewById(R.id.rbStripe);
                rbPayTm = dialogView.findViewById(R.id.rbPayTm);
                rbPayU = dialogView.findViewById(R.id.rbPayU);
                lytPayment = dialogView.findViewById(R.id.lytPayment);

                lytPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        RadioButton rb = (RadioButton) dialogView.findViewById(checkedId);
                        paymentMethod = rb.getTag().toString();
                    }
                });

                Map<String, String> params = new HashMap<>();
                params.put(Constant.SETTINGS, Constant.GetVal);
                params.put(Constant.GET_PAYMENT_METHOD, Constant.GetVal);
                //  System.out.println("=====params " + params.toString());
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {

                        if (result) {
                            try {
                                JSONObject objectbject = new JSONObject(response);
                                if (!objectbject.getBoolean(Constant.ERROR)) {
                                    if (objectbject.has("payment_methods")) {
                                        JSONObject object = objectbject.getJSONObject(Constant.PAYMENT_METHODS);
                                        if (object.has(Constant.payu_method)) {
                                            Constant.PAYUMONEY = object.getString(Constant.payu_method);
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
                                            isAddressAvailable();
                                        }
                                        if (object.has(Constant.paytm_payment_method)) {
                                            Constant.PAYTM = object.getString(Constant.paytm_payment_method);
                                            Constant.PAYTM_MERCHANT_ID = object.getString(Constant.paytm_merchant_id);
                                            Constant.PAYTM_MERCHANT_KEY = object.getString(Constant.paytm_merchant_key);
                                            Constant.PAYTM_MODE = object.getString(Constant.paytm_mode);
                                        }

                                        if (Constant.FLUTTERWAVE.equals("0") && Constant.PAYPAL.equals("0") && Constant.PAYUMONEY.equals("0") && Constant.COD.equals("0") && Constant.RAZORPAY.equals("0") && Constant.PAYSTACK.equals("0") && Constant.MIDTRANS.equals("0") && Constant.STRIPE.equals("0")) {
                                            lytPayOption.setVisibility(View.GONE);
                                        } else {
                                            lytPayOption.setVisibility(View.VISIBLE);

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
                                    } else {
                                        Toast.makeText(activity, getString(R.string.alert_payment_methods_blank), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (JSONException e) {

                            }
                        }
                    }
                }, activity, Constant.SETTING_URL, params, false);

                tvDialogSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtAmount.getText().toString().equals("")) {
                            edtAmount.requestFocus();
                            edtAmount.setError(getString(R.string.alert_enter_amount));
                        } else if (Double.parseDouble(edtAmount.getText().toString()) > Double.parseDouble(session.getData(Constant.user_wallet_refill_limit))) {
                            Toast.makeText(activity, getString(R.string.max_wallet_amt_error), Toast.LENGTH_SHORT).show();
                        } else if (Double.parseDouble(edtAmount.getText().toString().trim()) <= 0) {
                            edtAmount.requestFocus();
                            edtAmount.setError(getString(R.string.alert_recharge));
                        } else {
                            if (paymentMethod != null) {
                                amount = edtAmount.getText().toString().trim();
                                msg = edtMsg.getText().toString().trim();
                                RechargeWallet();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(activity, getString(R.string.select_payment_method), Toast.LENGTH_SHORT).show();
                            }
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
        });

        return root;
    }


    public void isAddressAvailable() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ADDRESSES, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(jsonObject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson g = new Gson();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    Address address = g.fromJson(jsonObject1.toString(), Address.class);
                                    if (address.getIs_default().equals("1")) {
                                        Constant.DefaultAddress = address.getAddress() + ", " + address.getLandmark() + ", " + address.getCity_name() + ", " + address.getArea_name() + ", " + address.getState() + ", " + address.getCountry() + ", " + activity.getString(R.string.pincode_) + address.getPincode();
                                        Constant.DefaultCity = address.getCity_name();
                                        Constant.DefaultPinCode = address.getPincode();
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.GET_ADDRESS_URL, params, false);
    }

    public void AddWalletBalance(Activity activity, Session session, String amount, String msg, String txID) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.ADD_WALLET_BALANCE, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.AMOUNT, amount);
        params.put(Constant.TYPE, Constant.CREDIT);
        params.put(Constant.MESSAGE, msg);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            DrawerActivity.tvWallet.setText(session.getData(Constant.currency) + ApiConfig.StringFormat(""+Double.parseDouble(object.getString(Constant.NEW_BALANCE))));
                            tvBalance.setText(session.getData(Constant.currency) + ApiConfig.StringFormat(""+Double.parseDouble(object.getString(Constant.NEW_BALANCE))));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.TRANSACTION_URL, params, true);
    }


    public void StartPayPalPayment(final Map<String, String> sendParams) {
        final Map<String, String> params = new HashMap<>();
        params.put(Constant.FIRST_NAME, session.getData(Constant.NAME));
        params.put(Constant.LAST_NAME, session.getData(Constant.NAME));
        params.put(Constant.PAYER_EMAIL, session.getData(Constant.EMAIL));
        params.put(Constant.ITEM_NAME, getString(R.string.wallet_recharge));
        params.put(Constant.ITEM_NUMBER, "wallet-refill-user-" + new Session(activity).getData(Constant.ID) + "-" + System.currentTimeMillis());
        params.put(Constant.AMOUNT, sendParams.get(Constant.FINAL_TOTAL));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                Intent intent = new Intent(getContext(), PayPalWebActivity.class);
                intent.putExtra(Constant.URL, response);
                intent.putExtra(Constant.ORDER_ID, "wallet-refill-user-" + new Session(activity).getData(Constant.ID) + "-" + System.currentTimeMillis());
                intent.putExtra(Constant.FROM, Constant.WALLET);
                intent.putExtra(Constant.PARAMS, (Serializable) sendParams);
                startActivity(intent);
            }
        }, getActivity(), Constant.PAPAL_URL, params, true);
    }

    public void callPayStack(final Map<String, String> sendParams) {
        Intent intent = new Intent(activity, PayStackActivity.class);
        intent.putExtra(Constant.PARAMS, (Serializable) sendParams);
        startActivity(intent);
    }


    void StartFlutterWavePayment() {
        new RavePayManager(this)
                .setAmount(Double.parseDouble(amount))
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
            new PaymentModelClass(getActivity()).TrasactionMethod(data, getActivity(), Constant.WALLET);
        } else if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            try {
                JSONObject details = new JSONObject(data.getStringExtra("response"));
                JSONObject jsonObject = details.getJSONObject(Constant.DATA);

                if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                    AddWalletBalance(activity, new Session(activity), amount, msg, jsonObject.getString("txRef"));
                    Toast.makeText(getContext(), getString(R.string.wallet_recharged), Toast.LENGTH_LONG).show();

                } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                    Toast.makeText(getContext(), getString(R.string.order_error), Toast.LENGTH_LONG).show();
                } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                    Toast.makeText(getContext(), getString(R.string.order_cancel), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {

            }
        }
    }

    public void RechargeWallet() {
        HashMap<String, String> sendparams = new HashMap<>();
        if (paymentMethod.equals(getString(R.string.pay_u))) {
            sendparams.put(Constant.MOBILE, session.getData(Constant.MOBILE));
            sendparams.put(Constant.USER_NAME, session.getData(Constant.NAME));
            sendparams.put(Constant.EMAIL, session.getData(Constant.EMAIL));
            new PaymentModelClass(getActivity()).OnPayClick(getActivity(), sendparams, Constant.WALLET, amount);
        } else if (paymentMethod.equals(getString(R.string.paypal))) {
            sendparams.put(Constant.FINAL_TOTAL, amount);
            sendparams.put(Constant.FIRST_NAME, session.getData(Constant.NAME));
            sendparams.put(Constant.LAST_NAME, session.getData(Constant.NAME));
            sendparams.put(Constant.PAYER_EMAIL, session.getData(Constant.EMAIL));
            sendparams.put(Constant.ITEM_NAME, getString(R.string.wallet_recharge_));
            sendparams.put(Constant.ITEM_NUMBER, System.currentTimeMillis() + Constant.randomNumeric(3));
            StartPayPalPayment(sendparams);
        } else if (paymentMethod.equals(getString(R.string.razor_pay))) {
            payFromWallet = true;
            CreateOrderId(Double.parseDouble(amount));
        } else if (paymentMethod.equals(getString(R.string.paystack))) {
            sendparams.put(Constant.FINAL_TOTAL, amount);
            sendparams.put(Constant.FROM, Constant.WALLET);
            callPayStack(sendparams);
        } else if (paymentMethod.equals(getString(R.string.flutterwave))) {
            StartFlutterWavePayment();
        } else if (paymentMethod.equals(getString(R.string.midtrans))) {
            sendparams.put(Constant.FINAL_TOTAL, amount);
            sendparams.put(Constant.USER_ID, session.getData(Constant.ID));
            CreateMidtransPayment(System.currentTimeMillis() + Constant.randomNumeric(3), amount, sendparams);
        } else if (paymentMethod.equals(getString(R.string.stripe))) {
            if (!Constant.DefaultAddress.equals("")) {
                sendparams.put(Constant.FINAL_TOTAL, amount);
                sendparams.put(Constant.USER_ID, session.getData(Constant.ID));
                Intent intent = new Intent(activity, StripeActivity.class);
                intent.putExtra(Constant.ORDER_ID, "wallet-refill-user-" + new Session(activity).getData(Constant.ID) + "-" + System.currentTimeMillis());
                intent.putExtra(Constant.FROM, Constant.WALLET);
                intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
                startActivity(intent);
            } else {
                Toast.makeText(activity, getString(R.string.address_msg), Toast.LENGTH_SHORT).show();
            }

        } else if (paymentMethod.equals(getString(R.string.paytm))) {
            startPayTmPayment();
        }
    }

    public void startPayTmPayment() {
        Map<String, String> params = new HashMap<>();

        params.put(Constant.ORDER_ID_, Constant.randomAlphaNumeric(20));
        params.put(Constant.CUST_ID, Constant.randomAlphaNumeric(10));
        params.put(Constant.TXN_AMOUNT, "" + amount);

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
                    paramMap.put(Constant.TXN_AMOUNT, "" + amount);

                    if (Constant.PAYTM_MODE.equals("sandbox")) {
                        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_LIVE_VAL);
                        paramMap.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_DEMO_VAL);
                        paramMap.put(Constant.WEBSITE, Constant.WEBSITE_DEMO_VAL);
                    } else if (Constant.PAYTM_MODE.equals("production")) {
                        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_DEMO_VAL);
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
                        AddWalletBalance(activity, new Session(activity), amount, msg, txnId);
                        Toast.makeText(getContext(), getString(R.string.wallet_recharged), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                }
            }
        }, Constant.VALID_TRANSACTION, params);
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String
            inFailingUrl) {

    }

    @Override
    public void onBackPressedCancelTransaction() {

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

    }

    public void CreateOrderId(double payAmount) {

        String[] amount = String.valueOf(payAmount * 100).split("\\.");

        Map<String, String> params = new HashMap<>();
        params.put("amount", amount[0]);
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
            Log.d("Payment : ", "Error in starting Razorpay Checkout", e);
        }
    }

    public void getTransactionData(Activity activity, Session session) {
        recyclerView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        walletTransactions = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_TRANSACTION, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.TYPE, Constant.TYPE_WALLET_TRANSACTION);
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            Gson g = new Gson();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    WalletTransaction transaction = g.fromJson(jsonObject1.toString(), WalletTransaction.class);
                                    walletTransactions.add(transaction);
                                } else {
                                    break;
                                }
                            }
                            if (offset == 0) {
                                walletTransactionAdapter = new WalletTransactionAdapter(getContext(), activity, walletTransactions);
                                walletTransactionAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(walletTransactionAdapter);
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (walletTransactions.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == walletTransactions.size() - 1) {
                                                        //bottom of list!
                                                        walletTransactions.add(null);
                                                        walletTransactionAdapter.notifyItemInserted(walletTransactions.size() - 1);
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                offset += Constant.LOAD_ITEM_LIMIT;
                                                                Map<String, String> params = new HashMap<>();
                                                                params.put(Constant.GET_USER_TRANSACTION, Constant.GetVal);
                                                                params.put(Constant.USER_ID, session.getData(Constant.ID));
                                                                params.put(Constant.TYPE, Constant.TYPE_WALLET_TRANSACTION);
                                                                params.put(Constant.OFFSET, "" + offset);
                                                                params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);

                                                                ApiConfig.RequestToVolley(new VolleyCallback() {
                                                                    @Override
                                                                    public void onSuccess(boolean result, String response) {

                                                                        if (result) {
                                                                            try {
                                                                                // System.out.println("====product  " + response);
                                                                                JSONObject objectbject1 = new JSONObject(response);
                                                                                if (!objectbject1.getBoolean(Constant.ERROR)) {

                                                                                    session.setData(Constant.TOTAL, objectbject1.getString(Constant.TOTAL));

                                                                                    walletTransactions.remove(walletTransactions.size() - 1);
                                                                                    walletTransactionAdapter.notifyItemRemoved(walletTransactions.size());

                                                                                    JSONObject object = new JSONObject(response);
                                                                                    JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                                    Gson g = new Gson();


                                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                                                                        if (jsonObject1 != null) {
                                                                                            WalletTransaction walletTransaction = g.fromJson(jsonObject1.toString(), WalletTransaction.class);
                                                                                            walletTransactions.add(walletTransaction);
                                                                                        } else {
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                    walletTransactionAdapter.notifyDataSetChanged();
                                                                                    walletTransactionAdapter.setLoaded();
                                                                                    isLoadMore = false;
                                                                                }
                                                                            } catch (JSONException e) {
                                                                                mShimmerViewContainer.stopShimmer();
                                                                                mShimmerViewContainer.setVisibility(View.GONE);
                                                                                recyclerView.setVisibility(View.GONE);
                                                                            }
                                                                        }
                                                                    }
                                                                }, activity, Constant.TRANSACTION_URL, params, false);

                                                            }
                                                        }, 0);
                                                        isLoadMore = true;
                                                    }

                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);

                        }
                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }

                }
            }
        }, activity, Constant.TRANSACTION_URL, params, false);

    }


    public void CreateMidtransPayment(String orderId, String
            grossAmount, Map<String, String> sendparams) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.ORDER_ID, "wallet-refill-user-" + new Session(activity).getData(Constant.ID) + "-" + System.currentTimeMillis());
        params.put(Constant.GROSS_AMOUNT, "" + (int) Math.round(Double.parseDouble(grossAmount)));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            Intent intent = new Intent(activity, MidtransActivity.class);
                            intent.putExtra(Constant.URL, jsonObject.getJSONObject(Constant.DATA).getString(Constant.REDIRECT_URL));
                            intent.putExtra(Constant.ORDER_ID, "wallet-refill-user-" + new Session(activity).getData(Constant.ID) + "-" + System.currentTimeMillis());
                            intent.putExtra(Constant.FROM, Constant.WALLET);
                            intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.MIDTRANS_PAYMENT_URL, params, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.wallet_history);
        Session.setCount(Constant.UNREAD_WALLET_COUNT, 0, getContext());
        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_wallet_history, Session.getCount(Constant.UNREAD_WALLET_COUNT, getContext()));
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