package com.kidus.androidapps.identificationsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kidus.androidapps.identificationsystem.R;

public class MainActivity extends AppCompatActivity {


    Button mScanButton;
    TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mScanButton = (Button) findViewById(R.id.scan_button);

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                scanIntegrator.initiateScan();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, intent);
        mResultTextView = (TextView) findViewById(R.id.result_text);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            mResultTextView.setText(scanContent);
        } else {
            mResultTextView.setText("Got Nothing :(");
        }
    }
}
