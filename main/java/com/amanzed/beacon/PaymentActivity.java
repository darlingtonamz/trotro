package com.amanzed.beacon;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Amanze on 5/28/2016.
 */
public class PaymentActivity extends AppCompatActivity {
    Button cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_main);

        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PaymentActivity.this, TrotroActivity.class);
                startActivity(in);
                PaymentActivity.this.finish();
            }
        });
    }
}
