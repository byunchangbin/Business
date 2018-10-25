package com.example.byunchangbin.business;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    ViewFlipper Vf;
    Button BtnSignIn;
    TextView BtnSignUp;
    EditText inputID, inputPW,inputCODE;
    HttpPost httppost;
    StringBuffer buffer;
    org.apache.http.HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //タイトルバー消す
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        BtnSignUp = (TextView) findViewById(R.id.btn_signup);
        BtnSignIn = (Button)findViewById(R.id.btn_signin);
        inputID = (EditText)findViewById(R.id.user_id);
        inputPW = (EditText)findViewById(R.id.user_pw);
        inputCODE = (EditText)findViewById(R.id.user_code);

        BtnSignIn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(LoginActivity.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        login();
                    }
                }).start();
            }
        });

        BtnSignUp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    void login() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://sola0722.cafe24.com/LoginCompany.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", inputID.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", inputPW.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("code", inputCODE.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    tv.setText("Response from PHP : " + response);
                    dialog.dismiss();
                }
            });


            if (response.equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    }
                });

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("companyid",inputID.getText().toString());
                intent.putExtra("code",inputCODE.getText().toString());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }
}
