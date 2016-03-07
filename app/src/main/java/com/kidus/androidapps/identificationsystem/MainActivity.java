package com.kidus.androidapps.identificationsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kidus.androidapps.identificationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {


    Button mScanButton;
    TextView mResultTextView;
    WebView mWebView;
    String responseJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //mWebView = (WebView) findViewById(R.id.webView);
        mScanButton = (Button) findViewById(R.id.scan_button);

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                scanIntegrator.initiateScan();
                //identify3("7_fdaed99748112760c3185d12e35c8127", 3);
                //getJSON("http://192.168.183.1/ID_SYSTEM/getemployee.php?data=7_fdaed99748112760c3185d12e35c8127&premisesId=1");



                // mResultTextView = (TextView) findViewById(R.id.textView2);
                //mResultTextView.setText(responseJSON);



            }
        });
//        mWebView.loadUrl("http://192.168.56.1/id_system/getEmployee.php?data=7_fdaed99748112760c3185d12e35c8127&premisesId=3");
//        String x = mWebView.toString();
//        String y = x;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, intent);
        String qrData = scanningResult.getContents();
        String request = "http://192.168.183.1/ID_SYSTEM/getemployee.php?data=" + qrData +
                "&premisesId=1";
        getJSON(request);

    }

    private void getJSON(String url) {
        class GetJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Identifying...",null,true,true);
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                responseJSON = s;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                boolean allowed=false;
                String full_name="";
                try {
                    allowed = jsonObject.getBoolean("allowed");
                    full_name = jsonObject.getString("full_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mResultTextView = (TextView) findViewById(R.id.textView2);
                if (allowed)
                mResultTextView.setText(full_name + " is allowed:D");
                else
                    mResultTextView.setText(full_name + "is not allowed :(");
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);
    }
}
