package com.asif.stripe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
    ///AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();
        //dialog = new AlertDialog(context);
        //runOnUiThread();
        //alert("shdflhdslf","dsfkls");
        //dialog.show();

        initializeView();
        initializeClickListner();

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void initializeClickListner() {
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    progressDialog.show();
                    String cardNumber = editTextCardNumber.getText().toString();
                    int cardExpMonth = Integer.valueOf(editTextCardExpMonth.getText().toString());
                    int cardExpYear = Integer.valueOf(editTextCardExpYear.getText().toString());
                    String cardCVC = editTextCvc.getText().toString();

                    Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);

                    if (!card.validateCard()) {
                        progressDialog.dismiss();
                        String msg = "Your Card is not validate. \n Please Enter Valid Card Info";
                        alert(msg, "Card Not Valid");
                        //Toast.makeText(context,"Your Card is not validate. \n Please Enter Valid Card Info",Toast.LENGTH_LONG).show();
                    } else {
                        com.stripe.android.Stripe stripe = new com.stripe.android.Stripe(context, "pk_test_8iKVwsSxm54QIveAPOJmLYYa");
                        stripe.createToken(card,
                                new TokenCallback() {
                                    @Override
                                    public void onError(Exception error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void onSuccess(Token token) {
                                        Stripe.apiKey = "sk_test_qPIWrxe3QefpEO2vRgdFkyFv";
                                        int amount = (int) (Float.valueOf(editTextAmount.getText().toString()) * 100);
                                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                        final Map<String, Object> params = new HashMap<>();
                                        params.put("amount", amount);
                                        params.put("currency", "usd");
                                        params.put("description", "Test Charge");
                                        params.put("source", token.getId());
                                        Map<String, String> metadata = new HashMap<>();
                                        metadata.put("order_id", "test_order_" + timestamp.getTime());
                                        metadata.put("customer_name", editTextCostoumerName.getText().toString());
                                        metadata.put("phone", editTextPhone.getText().toString());
                                        metadata.put("email", editTextEmail.getText().toString());
                                        metadata.put("receipt_email", editTextEmail.getText().toString());
                                        params.put("metadata", metadata);

                                        RequastToCharge requastToCharge = new RequastToCharge();
                                        RequastToChargeDtails requastToChargeDtails = new RequastToChargeDtails();
                                        Charge charge = null;
                                        try {
                                            String crgid = requastToCharge.execute(params).get();
                                            charge = requastToChargeDtails.execute(crgid).get();
                                            
                                        } catch (InterruptedException | ExecutionException  e) {
                                            progressDialog.dismiss();
                                            e.printStackTrace();
                                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                        if (charge != null) {
                                            Map<String,String> metData = new HashMap<>();
                                            metadata = charge.getMetadata();
                                            StringBuilder sb =new StringBuilder();
                                            sb.append("Successfully Charged from your card \n");
                                            sb.append("Customer Name:" +metadata.get("customer_name")+"\n");
                                            sb.append("Amount:" +(double)charge.getAmount()/100+"\n");
                                            sb.append("Order Id:" +metadata.get("order_id")+"\n");
                                            sb.append("Email:" +metadata.get("email")+"\n");
                                            //String msg = "Successfully Charged from your card. \n Your Charged Id is : " ;
                                            alert(sb.toString(), "Success");
                                        } else {
                                            String msg = "An Error ! \n Please Try again";
                                            alert(msg, "Failed");
                                        }
                                        //Toast.makeText(context,"Success :" +crgid,Toast.LENGTH_LONG).show();

                                    }
                                });
                    }

                }
            }
        });
    }

    private boolean validate(){
        if (editTextCardNumber.getText().toString().isEmpty()){
            String errorMessage = "Please Enter Your Card Number";
            editTextCardNumber.setError(errorMessage);
            editTextCardNumber.requestFocus();
            return  false;
        }
        if (editTextEmail.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()){
            String errorMessage = "Please Enter Your Valid Email";
            editTextEmail.setError(errorMessage);
            editTextEmail.requestFocus();
            return  false;
        }
        if (editTextCardExpMonth.getText().toString().isEmpty()){
            String errorMessage = "Please Enter Card Exp. Date";
            editTextEmail.setError(errorMessage);
            editTextEmail.requestFocus();
            return  false;
        }
        if (editTextCardExpYear.getText().toString().isEmpty()){
            String errorMessage = "Please Enter Card Exp. Date";
            editTextEmail.setError(errorMessage);
            editTextEmail.requestFocus();
            return  false;
        }
        if (editTextCvc.getText().toString().isEmpty()){
            String errorMessage = "Please Enter Card Exp. Date";
            editTextEmail.setError(errorMessage);
            editTextEmail.requestFocus();
            return  false;
        }
        if (editTextCostoumerName.getText().toString().isEmpty()){
            String errorMessage = "Please Enter Your Name";
            editTextEmail.setError(errorMessage);
            editTextEmail.requestFocus();
            return  false;
        }
        if (editTextPhone.getText().toString().isEmpty()){
            String errorMessage = "Please Enter Your Phone Number";
            editTextEmail.setError(errorMessage);
            editTextEmail.requestFocus();
            return  false;
        }
        if (editTextAmount.getText().toString().isEmpty()){
            String errorMessage = "Please Enter Amount To Pay";
            editTextEmail.setError(errorMessage);
            editTextEmail.requestFocus();
            return  false;
        }
        else
            return true;
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

    private void alert(String message, String titel){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder.setTitle(titel)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.alert_dark_frame)
                .show();
    }

    private class RequastToCharge extends AsyncTask<Map<String, Object>, Void, String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String result = null;
            try {
                Charge charge = Charge.create(maps[0]);
                Log.i("crg_id",charge.getId());
                result = charge.getId();
                //Toast.makeText(context,"Success :" +charge.getId(),Toast.LENGTH_LONG).show();
            } catch (StripeException e) {
                e.printStackTrace();
                //result = e.getLocalizedMessage();
                Log.e("Error Crg :",e.getLocalizedMessage());
            }
            return result;
        }
        

        
    }
    
    private class RequastToChargeDtails extends AsyncTask<String, Void, Charge> {
        @Override
        protected Charge doInBackground(String... src) {
            Charge charge;
            try {
                Log.i("Charge Id:",String.valueOf(src[0]));
                charge = Charge.retrieve(String.valueOf(src[0]));
            } catch (StripeException e) {
                charge = null;
                e.printStackTrace();
            }
            return charge;
        }
    
        @Override
        protected void onPostExecute(Charge charge) {
            super.onPostExecute(charge);
            progressDialog.dismiss();
        }
    }
}
