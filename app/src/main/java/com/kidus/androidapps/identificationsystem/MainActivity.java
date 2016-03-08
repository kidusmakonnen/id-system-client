package com.kidus.androidapps.identificationsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    Button mScanButton;
    TextView mResultTextView;
    ImageView mAccessImageView;
    WebView mWebView;
    String responseJSON;
    String photo_location = "http://10.0.0.4/ID_SYSTEM/employee_photos/";

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
        String request = "http://10.0.0.4/ID_SYSTEM/getemployee.php?data=" + qrData +
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
                String full_name = "";
                String department = "";
                String gender = "";
                String photo_filename="";
                try {
                    allowed = jsonObject.getBoolean("allowed");
                    full_name = jsonObject.getString("full_name");
                    department = jsonObject.getString("department");
                    gender = jsonObject.getString("gender");
                    photo_filename = jsonObject.getString("photo");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mResultTextView = (TextView) findViewById(R.id.employee_information_text_view);
                String txt = "Name<br /><b>" + full_name + "</b><br />Department<br /><b>" + department + "</b>";
                txt += "<br />Gender<br /><b>" + gender + "</b>";
                mResultTextView.setText(Html.fromHtml(txt));

                mAccessImageView = (ImageView) findViewById(R.id.access_image_view);

                if (allowed) {
                    mAccessImageView.setImageDrawable(getResources().getDrawable(R.drawable.granted));
                } else {
                    mAccessImageView.setImageDrawable(getResources().getDrawable(R.drawable.denied));
                }



                new DownloadImageTask((ImageView) findViewById(R.id.employee_photo_image_view))
                        .execute(photo_location + photo_filename);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public String prepareEmployeeInfo(String full_name, String gender, String department) {
        String txt = "Name<br /><b>" + full_name + "</b><br />Department<br /><b>" + department + "</b>";
        txt += "<br />Gender<br /><b>" + gender + "</b>";
        return txt;
    }
}
