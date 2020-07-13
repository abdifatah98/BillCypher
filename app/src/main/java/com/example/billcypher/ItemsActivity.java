package com.example.billcypher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class ItemsActivity extends AppCompatActivity {

    ArrayList<Double> prices = new ArrayList<Double>();
    ArrayList<String> items = new ArrayList<String>();
    private TextView itemView;
    private TextView priceView;
    private EditText peopleInput;
    private EditText serviceInput;
    private EditText taxInput;
    private Button button;

    Integer numOfPeople;
    Double serviceCharge;
    Double tax;
    Double sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        itemView = findViewById(R.id.items_list);
        priceView = findViewById(R.id.prices_list);
        peopleInput = findViewById(R.id.people_input);
        serviceInput = findViewById(R.id.service_input);
        taxInput = findViewById(R.id.tax_input);
        button = findViewById(R.id.submit_button);

        prices = (ArrayList<Double>) getIntent().getSerializableExtra("Price");
        items = getIntent().getStringArrayListExtra("Items");

        final StringBuilder itemList = new StringBuilder();
        StringBuilder priceList = new StringBuilder();
        for(String i: items){
            itemList.append(i).append("\n");
        }
        for(Double p: prices){
            priceList.append(p).append("\n");
        }

        itemView.setText(itemList.toString());
        priceView.setText(priceList.toString());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    getSum();

                    numOfPeople = Integer.valueOf(peopleInput.getText().toString());
                    serviceCharge = Double.valueOf(serviceInput.getText().toString());
                    tax = Double.valueOf(taxInput.getText().toString());

                    getOwedAmount(numOfPeople, serviceCharge, tax);

                    Intent GoToBill = new Intent(getApplicationContext(), BillActivity.class);
                    GoToBill.putExtra("Sum", sum);
                    startActivity(GoToBill);
                }
            }
        });


    }

    private void getOwedAmount(Integer numOfPeople, Double serviceCharge, Double tax) {
        sum+=tax;
        if(serviceCharge!=0){
            sum*=(serviceCharge/100.0+1);
        }
        sum/=numOfPeople;
        sum=round(sum, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private Double getSum() {
        sum = 0.0;
        for(int i = 0; i < prices.size(); i++){
            sum += prices.get(i);
        }
        return sum;
    }

    private boolean validateFields() {
        if (peopleInput.getText().length() == 0) {
            peopleInput.setError("Your Input is Invalid");
            return false;
        } else if (serviceInput.getText().length() == 0) {
            serviceInput.setError("Your Input is Invalid, if no service charge write 100");
            return false;
        } else if (taxInput.getText().length() == 0) {
            taxInput.setError("Your Input is Invalid");
            return false;
        } else if (Double.parseDouble(serviceInput.getText().toString())>100.00) {
            serviceInput.setError("Please do not enter values greater than 100");
            return false;
        } else if (Integer.parseInt(peopleInput.getText().toString())<2) {
            peopleInput.setError("Please enter more than 1 person");
            return false;
        } else {
            return true;
        }
    }
}
