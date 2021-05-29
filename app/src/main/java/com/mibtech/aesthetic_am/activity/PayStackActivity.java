package com.mibtech.aesthetic_am.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.fragment.WalletTransactionFragment;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.PaymentModelClass;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.ui.CreditCardEditText;

public class PayStackActivity extends AppCompatActivity {
    public String email, cardNumber, cvv;
    public int expiryMonth, expiryYear;
    Toolbar toolbar;
    Session session;
    Activity activity;
    TextView tvPayable;
    Map<String, String> sendParams;
    PaymentModelClass paymentModelClass;
    double payableAmount = 0;
    String from;
    //variables
    private Card card;
    private Charge charge;
    private EditText emailField;
    private CreditCardEditText cardNumberField;
    private EditText expiryMonthField;
    private EditText expiryYearField;
    private EditText cvvField;
    CardView cardViewHamburger;
    TextView toolbarTitle;
    ImageView imageMenu;

    public static void setPaystackKey(String publicKey) {
        PaystackSdk.setPublicKey(publicKey);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiConfig.transparentStatusAndNavigation(this);
        //init paystack sdk
        PaystackSdk.initialize(getApplicationContext());
        setContentView(R.layout.activity_pay_stack);
        getAllWidgets();
        setPaystackKey(Constant.PAYSTACK_KEY);
        activity = PayStackActivity.this;
        session = new Session(activity);

        paymentModelClass = new PaymentModelClass(activity);
        sendParams = (Map<String, String>) getIntent().getSerializableExtra("params");
        payableAmount = Double.parseDouble(sendParams.get(Constant.FINAL_TOTAL));
        from = sendParams.get(Constant.FROM);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbarTitle.setText(getString(R.string.paystack));

        imageMenu.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        cardViewHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        emailField.setText(session.getData(Constant.EMAIL));
        tvPayable.setText(session.getData(Constant.currency) + payableAmount);
    }

    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvPayable = findViewById(R.id.tvPayable);
        emailField = findViewById(R.id.edit_email_address);
        cardNumberField = findViewById(R.id.edit_card_number);
        expiryMonthField = findViewById(R.id.edit_expiry_month);
        expiryYearField = findViewById(R.id.edit_expiry_year);
        cvvField = findViewById(R.id.edit_cvv);

        cardViewHamburger = findViewById(R.id.cardViewHamburger);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        imageMenu = findViewById(R.id.imageMenu);
    }

    /**
     * Method to perform the charging of the card
     */
    private void performCharge() {
        //create a Charge object
        String[] amount = String.valueOf(payableAmount * 100).split("\\.");
        charge = new Charge();
        charge.setCard(card); //set the card to charge
        charge.setEmail(email); //dummy email address
        charge.setAmount(Integer.parseInt(amount[0])); //test amount
        PaystackSdk.chargeCard(PayStackActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                String paymentReference = transaction.getReference();
                verifyReference(String.valueOf(charge.getAmount()), paymentReference, charge.getEmail());
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String cardNumber = cardNumberField.getText().toString();
        if (TextUtils.isEmpty(cardNumber)) {
            cardNumberField.setError("Required.");
            valid = false;
        } else {
            cardNumberField.setError(null);
        }


        String expiryMonth = expiryMonthField.getText().toString();
        if (TextUtils.isEmpty(expiryMonth)) {
            expiryMonthField.setError("Required.");
            valid = false;
        } else {
            expiryMonthField.setError(null);
        }

        String expiryYear = expiryYearField.getText().toString();
        if (TextUtils.isEmpty(expiryYear)) {
            expiryYearField.setError("Required.");
            valid = false;
        } else {
            expiryYearField.setError(null);
        }

        String cvv = cvvField.getText().toString();
        if (TextUtils.isEmpty(cvv)) {
            cvvField.setError("Required.");
            valid = false;
        } else {
            cvvField.setError(null);
        }

        return valid;
    }

    public void PayButton(View view) {
        if (!validateForm()) {
            return;
        }
        try {
            email = emailField.getText().toString().trim();
            cardNumber = cardNumberField.getText().toString().trim();
            expiryMonth = Integer.parseInt(expiryMonthField.getText().toString().trim());
            expiryYear = Integer.parseInt(expiryYearField.getText().toString().trim());
            cvv = cvvField.getText().toString().trim();

            //String cardNumber = "4084 0840 8408 4081";
            //int expiryMonth = 11; //any month in the future
            //int expiryYear = 18; // any year in the future
            //String cvv = "408";
            card = new Card(cardNumber, expiryMonth, expiryYear, cvv);

            paymentModelClass.showProgressDialog();
            if (card.isValid()) {
                performCharge();
            } else {
                paymentModelClass.hideProgressDialog();
                Toast.makeText(PayStackActivity.this, "Card is not Valid", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

        }
    }

    public void verifyReference(String amount, String reference, String email) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.VERIFY_PAYSTACK, Constant.GetVal);
        params.put(Constant.AMOUNT, amount);
        params.put(Constant.REFERENCE, reference);
        params.put(Constant.EMAIL, email);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString(Constant.STATUS);
                        if (from.equals(Constant.WALLET)) {
                            onBackPressed();
                            new WalletTransactionFragment().AddWalletBalance(activity, new Session(activity), WalletTransactionFragment.amount, WalletTransactionFragment.msg, reference);
                        } else if (from.equals(Constant.PAYMENT)) {
                            paymentModelClass.PlaceOrder(activity, getString(R.string.paystack), reference, status.equalsIgnoreCase("success"), sendParams, status);
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.VERIFY_PAYMENT_REQUEST, params, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}