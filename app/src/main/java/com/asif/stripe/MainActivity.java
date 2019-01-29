package com.asif.stripe;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.Stripe;
import com.stripe.android.model.Token;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    //View attribute
    private EditText editTextCardNumber;
    private EditText editTextCardExpMonth;
    private EditText editTextCardExpYear;
    private EditText editTextCvc;
    private EditText editTextCostoumerName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextAmount;
    private Button buttonPay;

    //
    Context context;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();


        initializeView();
        initializeClickListner();

    }

    private void initializeClickListner() {
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String cardNumber = editTextCardNumber.getText().toString();
                int cardExpMonth = Integer.valueOf(editTextCardExpMonth.getText().toString());
                int cardExpYear = Integer.valueOf(editTextCardExpYear.getText().toString());
                String cardCVC = editTextCvc.getText().toString();

                Card card = new Card(cardNumber,cardExpMonth,cardExpYear,cardCVC);

                if(!card.validateCard()){
                    Toast.makeText(context,"Your Card is not validate. \n Please Enter Valid Card Info",Toast.LENGTH_LONG).show();
                }else {
                    com.stripe.android.Stripe stripe = new com.stripe.android.Stripe(context,"pk_test_TYooMQauvdEDq54NiTphI7jx");
                    stripe.createToken(card,
                            new TokenCallback() {
                                @Override
                                public void onError(Exception error) {
                                    Toast.makeText(context,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onSuccess(Token token) {
                                    Stripe.apiKey="sk_test_4eC39HqLyjWDarjtT1zdp7dc";
                                    int amount = (int) (Float.valueOf(editTextAmount.getText().toString())*100);
                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                    final Map<String,Object> params = new HashMap<>();
                                    params.put("amount",amount);
                                    params.put("currency", "usd");
                                    params.put("description", "Test Charge");
                                    params.put("source", token.getId());
                                    Map<String, String> metadata = new HashMap<>();
                                    metadata.put("order_id", "test_order_"+timestamp.getTime());
                                    metadata.put("customer_name", editTextCostoumerName.getText().toString());
                                    metadata.put("phone", editTextPhone.getText().toString());
                                    metadata.put("email", editTextEmail.getText().toString());
                                    metadata.put("receipt_email", editTextEmail.getText().toString());
                                    params.put("metadata", metadata);

                                    RequastToCharge requastToCharge = new RequastToCharge();
                                    String crgid = null;
                                    try {
                                        crgid = requastToCharge.execute(params).get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(context,"Success :" +crgid,Toast.LENGTH_LONG).show();

                                }
                            });
                }

            }
        });
    }

    private void initializeView() {
        editTextCardNumber = (EditText) findViewById(R.id.editTextCardNumber);
        editTextCardExpMonth = (EditText) findViewById(R.id.editTextCardExpMonth);
        editTextCardExpYear = (EditText) findViewById(R.id.editTextCardExpYear);
        editTextCvc = (EditText) findViewById(R.id.editTextCvc);
        editTextCostoumerName =(EditText) findViewById(R.id.editTextCustomerName);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        buttonPay = (Button) findViewById(R.id.buttonPay);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait....");
        progressDialog.setTitle("Stripe");
    }
    @Override
    protected void onResume(){
        super.onResume();
    }

    private class RequastToCharge extends AsyncTask<Map<String, Object>, Void, String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String result;
            try {
                Charge charge = Charge.create(maps[0]);
                Log.i("crg_id",charge.getId());
                result = charge.getId();
                //Toast.makeText(context,"Success :" +charge.getId(),Toast.LENGTH_LONG).show();
            } catch (StripeException e) {
                e.printStackTrace();
                result = e.getLocalizedMessage();
                Log.e("Error Crg :",e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }
}
