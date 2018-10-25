package com.example.byunchangbin.business;

import android.content.Intent;
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

public class NoticePopupActivity extends AppCompatActivity {
    private Button cancelButton, deleteButton;
    private TextView txtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_noticepopup);

        txtText = (TextView)findViewById(R.id.txtText);
        cancelButton = (Button)findViewById(R.id.cancelButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);

        final String code = getIntent().getStringExtra("code");
        txtText.setText(getIntent().getStringExtra("title"));

        // popupキャンセルボタン
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);

                //アクティビティ(ポップアップ)けす
                finish();
            }
        });

        //メニューの削除ボタン
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NoticeDelete().execute();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                Toast.makeText(getApplicationContext(),"선택 메뉴가 삭제되었습니다.", Toast.LENGTH_LONG).show();

                //アクティビティ(ポップアップ)けす
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



    //選択メニュー消す
    class NoticeDelete extends AsyncTask<Void, Void, String> {

        String target;
        String noticeid = getIntent().getStringExtra("noticeid");


        @Override
        protected void onPreExecute(){
            target = "http://sola0722.cafe24.com/NoticeDelete.php?id=";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target+noticeid);
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
