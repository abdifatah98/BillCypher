package com.example.billcypher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class BillActivity extends AppCompatActivity {

    private TextView owedView;
    Double sum = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        owedView = findViewById(R.id.owed_amount);
        sum = getIntent().getDoubleExtra("Sum", sum);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Each Person Owes £").append(sum);

        owedView.setText(stringBuilder.toString());
    }
}
