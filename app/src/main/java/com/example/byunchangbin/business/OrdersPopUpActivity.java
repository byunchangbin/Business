package com.example.byunchangbin.business;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrdersPopUpActivity extends AppCompatActivity {
    private Button cancelButton, deleteButton;
    private TextView txtphonenumberText,txtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_orderspopup);

        txtphonenumberText = (TextView)findViewById(R.id.txtphonenumberText);
        txtText = (TextView)findViewById(R.id.txtText);
        cancelButton = (Button)findViewById(R.id.cancelButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);
        System.out.println(getIntent().getStringExtra("phonenember"));

        final String code = getIntent().getStringExtra("code");
        final String phonenember = getIntent().getStringExtra("phonenember");
        txtphonenumberText.setText(phonenember);

        // popupキャンセルボタン
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);

                //アクティビティ(ポップアップ)消す
                finish();
            }
        });

        //注文確認ボタン
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Salse().execute();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                Uri n = Uri.parse("smsto: " + txtphonenumberText.getText());
                Intent intent1 = new Intent(Intent.ACTION_SENDTO, n);
                String t = txtText.getText().toString();
                intent1.putExtra("sms_body", t);
                startActivity(intent1);
                Toast.makeText(getApplicationContext(),"전송버튼을 눌러 주세요.", Toast.LENGTH_LONG).show();
                //アクティビティ(ポップアップ)消す
                new OrdersDelete().execute();
                finish();
            }
        });




        Intent intent = getIntent();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Popupウィンドー以外の空間クリック時、外の画面に転換されないようにする関数

        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Popup時、Android 戻るボタンがクリックできないようにする関数
        return;
    }

    //注文済みのメニュー消す
    class OrdersDelete extends AsyncTask<Void, Void, String> {

        String target;
        String menuname = getIntent().getStringExtra("menuname");
        String userid = getIntent().getStringExtra("userid");
        String code = getIntent().getStringExtra("code");



        @Override
        protected void onPreExecute(){
            target = "http://sola0722.cafe24.com/OrdersDelete.php?menuname=";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                System.out.println(menuname+userid+code);
                URL url = new URL(target + menuname +"&userid=" + userid + "&code=" + code);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        public void onProgressUpdate(Void...values){
            super.onProgressUpdate();
        }
        @Override
        public void onPostExecute(String result){
        }
    }

    //売上リストで注文メニューを送る関数
    class Salse extends AsyncTask<Void, Void, String> {

        String target;
        String id = getIntent().getStringExtra("id");


        @Override
        protected void onPreExecute(){
            target = "http://sola0722.cafe24.com/SalseList.php?id="+id;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onProgressUpdate(Void...values){
            super.onProgressUpdate();
        }
        @Override
        public void onPostExecute(String result){
        }
    }
}
