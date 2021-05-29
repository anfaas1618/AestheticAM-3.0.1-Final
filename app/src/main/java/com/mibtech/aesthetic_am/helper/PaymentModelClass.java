package com.mibtech.aesthetic_am.helper;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.fragment.CheckoutFragment;
import com.mibtech.aesthetic_am.fragment.WalletTransactionFragment;

public class PaymentModelClass {
    public final Activity activity;
    final String TAG = CheckoutFragment.class.getSimpleName();
    public PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    public String status, udf5, udf4, udf3, udf2, udf1, email, firstName, productInfo, amount, txnId, key, addedOn, msg, Product, address;
    Map<String, String> sendparams;
    ProgressDialog mProgressDialog;

    public PaymentModelClass(Activity activity) {
        this.activity = activity;
    }

    //this method calculate hash from sdk
    public static String hashCal(String type, String hashString) {
        StringBuilder hash = new StringBuilder();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(type);
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {


        }
        return hash.toString();
    }

    public static String hashCal1(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();

        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte[] messageDigest = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage(activity.getString(R.string.processing));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void OnPayClick(Activity activity, Map<String, String> sendparams, String OrderType, String amount) {
        try{

        this.sendparams = sendparams;

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Done");
        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle(activity.getResources().getString(R.string.app_name));
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        String txnId = System.currentTimeMillis() + "";
        String phone = sendparams.get(Constant.MOBILE);
        String firstName = sendparams.get(Constant.USER_NAME);
        String email = sendparams.get(Constant.EMAIL);
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";
        AppEnvironment appEnvironment = ((ApiConfig) activity.getApplication()).getAppEnvironment();
        //builder.setAmount(amount)
        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(OrderType)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());
        try {
            mPaymentParams = builder.build();
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);
            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, activity, R.style.AppTheme, true);
            // generateHashFromServer(mPaymentParams);

        } catch (Exception e) {
            Toast.makeText(activity, "build " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }}
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        try {
            stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

            AppEnvironment appEnvironment = ((ApiConfig) activity.getApplication()).getAppEnvironment();
            stringBuilder.append(appEnvironment.salt());

            String hash = hashCal("SHA-512", stringBuilder.toString());
            paymentParam.setMerchantHash(hash);

        } catch (Exception e) {

        }
        return paymentParam;
    }

    public void TrasactionMethod(Intent data, Activity activity, String from) {
        // System.out.println("========transaction  call");
        TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
        ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

        // Check which object is non-null
        if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
            //System.out.println("========transaction response "+transactionResponse.getPayuResponse());
            AppEnvironment appEnvironment = ((ApiConfig) activity.getApplication()).getAppEnvironment();

            // Response from Payumoney
            String payuResponse = transactionResponse.getPayuResponse();
            try {

                JSONObject jsonObject = new JSONObject(payuResponse);
                //System.out.println("***payURes  " + jsonObject.toString());
                String hash_from_response = jsonObject.getJSONObject("result").getString("hash");
                status = jsonObject.getJSONObject("result").getString("status");
                email = jsonObject.getJSONObject("result").getString("email");
                firstName = jsonObject.getJSONObject("result").getString("firstname");
                productInfo = jsonObject.getJSONObject("result").getString("productinfo");
                amount = jsonObject.getJSONObject("result").getString("amount");
                txnId = jsonObject.getJSONObject("result").getString("txnid");
                key = jsonObject.getJSONObject("result").getString("key");
                addedOn = jsonObject.getJSONObject("result").getString("addedon");
                msg = jsonObject.getJSONObject("result").getString("error_Message");
                udf1 = jsonObject.getJSONObject("result").getString("udf1");
                udf2 = jsonObject.getJSONObject("result").getString("udf2");
                udf3 = jsonObject.getJSONObject("result").getString("udf3");
                udf4 = jsonObject.getJSONObject("result").getString("udf4");
                udf5 = jsonObject.getJSONObject("result").getString("udf5");

                String hasCal = appEnvironment.salt() + "|" + status + "||||||" + udf5 + "|" + udf4 + "|" + udf3 + "|" + udf2 + "|" + udf1 + "|" + email + "|" + firstName + "|" + productInfo + "|" + amount + "|" + txnId + "|" + key;
                String hash = hashCal1(hasCal);

                if (hash_from_response.equals(hash)) {
                    if (status.equals(Constant.SUCCESS)) {
                        if (from.equals(Constant.PAYMENT)) {
                            PlaceOrder(activity, activity.getResources().getString(R.string.onlinepaytype), txnId, true, sendparams, status);
                        } else if (from.equals(Constant.WALLET)) {
                            new WalletTransactionFragment().AddWalletBalance(activity, new Session(activity), WalletTransactionFragment.amount, WalletTransactionFragment.msg, txnId);
                        }
                    } else if (status.equals("failure")) {
                        PlaceOrder(activity, activity.getResources().getString(R.string.onlinepaytype), txnId, false, sendparams, status);
                        Toast.makeText(activity, activity.getString(R.string.transaction_failed_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        PlaceOrder(activity, activity.getResources().getString(R.string.onlinepaytype), txnId, false, sendparams, status);
                        Toast.makeText(activity, activity.getString(R.string.transaction_failed_msg), Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e) {

            }

        } else if (resultModel != null && resultModel.getError() != null) {
            Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
        } else {
            Log.d(TAG, "Both objects are null!");
        }
    }

    public void PlaceOrder(final Activity activity, final String paymentType, final String txnid, boolean issuccess, final Map<String, String> sendparams, final String status) {
        showProgressDialog();
        if (issuccess) {
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                activity.finish();
                                AddTransaction(object.getString(Constant.ORDER_ID), paymentType, txnid, status, activity.getResources().getString(R.string.order_success), sendparams);
                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Constant.FROM, "payment_success");
                                activity.startActivity(intent);
                            } else {
                                hideProgressDialog();
                            }
                        } catch (JSONException e) {
                            hideProgressDialog();

                        }
                    }
                }
            }, activity, Constant.ORDERPROCESS_URL, sendparams, false);
        } else {
            hideProgressDialog();
            AddTransaction("", activity.getResources().getString(R.string.onlinepaytype), txnid, status, "Order Failed", sendparams);
        }
    }

    public void AddTransaction(String orderId, String paymentType, String txnid, final String status, String message, Map<String, String> sendparams) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put(Constant.ADD_TRANSACTION, Constant.GetVal);
        transparams.put(Constant.USER_ID, sendparams.get(Constant.USER_ID));
        transparams.put(Constant.ORDER_ID, orderId);
        transparams.put(Constant.TYPE, paymentType);
        transparams.put(Constant.TRANS_ID, txnid);
        transparams.put(Constant.AMOUNT, sendparams.get(Constant.FINAL_TOTAL));
        transparams.put(Constant.STATUS, status);
        transparams.put(Constant.MESSAGE, message);
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        transparams.put("transaction_date", df.format(c));
        //System.out.println ("====trans params " + transparams);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                //System.out.println ("=================*transaction- " + response);
                if (result) {
                    try {
                        hideProgressDialog();
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            if (!status.equals(Constant.SUCCESS) || !status.equals("capture") || !status.equals("challenge") || !status.equals("pending")) {
                                activity.finish();
                            }
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, transparams, false);
    }
}