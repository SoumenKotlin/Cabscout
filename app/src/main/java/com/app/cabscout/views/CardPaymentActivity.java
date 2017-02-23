package com.app.cabscout.views;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CardPaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = CardPaymentActivity.class.getSimpleName();

    Toolbar toolbar;
    EditText editCardNumber, editCardName, editCardExpiry, editCardCVV;
    String cardNumber, cardCVC;
    int cardExpMonth, cardExpYear;
    TextView clickPay;
    AppCompatActivity activity = this;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editCardNumber = (EditText) findViewById(R.id.editCardNumber);
        editCardName = (EditText) findViewById(R.id.editCardName);
        editCardExpiry = (EditText) findViewById(R.id.editCardExpiry);
        editCardCVV = (EditText) findViewById(R.id.editCardCVV);

        clickPay = (TextView) findViewById(R.id.clickPay);
        clickPay.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.clickPay:
                cardNumber = editCardNumber.getText().toString();
                String cardExpiry = editCardExpiry.getText().toString();
                cardCVC = editCardCVV.getText().toString();

                if (cardNumber.isEmpty() || cardExpiry.isEmpty() || cardCVC.isEmpty() || editCardName.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please fill all the details.", Toast.LENGTH_SHORT).show();
                    break;
                } else if (cardNumber.length() < 16) {
                    editCardNumber.setError("Please enter the valid card number");
                } else if (cardCVC.length() < 3) {
                    editCardCVV.setError("Please enter the valid CVC");
                } else if (cardExpiry.length() < 7 && !cardExpiry.contains("/")) {
                    editCardExpiry.setError("Please enter the valid card expiry date");
                } else {
                    String[] expiry = cardExpiry.split("/");
                    String expiryYear = expiry[expiry.length - 1];
                    String expiryMonth = expiry[expiry.length - 2];
                    cardExpMonth = Integer.parseInt(expiryMonth);
                    cardExpYear = Integer.parseInt(expiryYear);
                    chargeCard();
                }

                break;
        }
    }

    public void chargeCard() {
        try {

            Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);

            if (!card.validateCard()) {
                Toast.makeText(activity, "Please enter the valid card details", Toast.LENGTH_SHORT).show();
                return;
            }

            Stripe stripe = new Stripe("pk_test_p5SDwXZtnUCuxh8lerOmoios");

            stripe.createToken(card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                            String stripeToken = token.getId();
                            com.stripe.Stripe.apiKey = "sk_test_FkufAD5FdTWNszkBNs9Q2XY0";

                            //  String token = request.getParameter("stripeToken");

                            Map<String, Object> params = new HashMap<>();
                            params.put("amount", 1000);
                            params.put("currency", "usd");
                            params.put("description", "Example charge");
                            params.put("source", stripeToken);

                            try {
                                Charge charge = Charge.create(params);
                                Log.e(TAG, ""+charge.getAmount());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );

        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
