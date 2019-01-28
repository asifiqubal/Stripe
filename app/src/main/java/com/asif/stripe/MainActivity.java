package com.asif.stripe;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    //View attribute
    private EditText editTextCardNumber;
    private EditText editTextCardExpMonth;
    private EditText editTextCardExpYear;
    private EditText editTextCvc;
    private Button buttonPay;

    //
    Context context;

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
                String cardNumber = editTextCardNumber.getText().toString();
                int cardExpMonth = Integer.valueOf(editTextCardExpMonth.getText().toString());
                int cardExpYear = Integer.valueOf(editTextCardExpYear.getText().toString());
                String cardCVC = editTextCvc.getText().toString();

                Card card = new Card(cardNumber,cardExpMonth,cardExpYear,cardCVC);

                if(!card.validateCard()){
                    Toast.makeText(context,"Your Card is not validate. \n Please Enter Valid Card Info",Toast.LENGTH_LONG).show();
                }else {
                    Stripe stripe = new Stripe(context,"pk_test_TYooMQauvdEDq54NiTphI7jx");
                    stripe.createToken(card,
                            new TokenCallback() {
                                @Override
                                public void onError(Exception error) {
                                    Toast.makeText(context,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onSuccess(Token token) {
                                    Toast.makeText(context,"Success :" +token.getId(),Toast.LENGTH_LONG).show();
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
        buttonPay = (Button) findViewById(R.id.buttonPay);
    }

    @Override
    protected void onResume(){
        super.onResume();


    }
}
