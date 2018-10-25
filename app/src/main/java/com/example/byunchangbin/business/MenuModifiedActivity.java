package com.example.byunchangbin.business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MenuModifiedActivity extends AppCompatActivity {

    private EditText menunameText, menupriceText, menudescriptionText;
    private Button modifiedButton, deleteButton;
    private ImageView menuImage;
    private back task;
    private Bitmap bpm;
    private String imgUrl = "http://sola0722.cafe24.com/uploads/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menumodified);

        menunameText=(EditText)findViewById(R.id.menunameText);
        menupriceText = (EditText)findViewById(R.id.menupriceText);
        menudescriptionText = (EditText)findViewById(R.id.menudescriptionText);
        modifiedButton = (Button)findViewById(R.id.modified_button);
        deleteButton = (Button)findViewById(R.id.delete_button);
        menuImage = (ImageView)findViewById(R.id.menuimage);



        menunameText.setText(getIntent().getStringExtra("menuname"));
        menupriceText.setText(getIntent().getStringExtra("menuprice"));
        menudescriptionText.setText(getIntent().getStringExtra("description"));

        String filename = getIntent().getStringExtra("filename");
        final String code = getIntent().getStringExtra("code");
        final String menuname = getIntent().getStringExtra("menuname");


        //メニューイメージを得る
        task = new back();
        try {
            bpm = task.execute(imgUrl+ filename + ".jpg").get();
            menuImage.setImageBitmap(bpm);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //メニュー修正ボタン
        modifiedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MenuModified().execute();
                Toast toast = Toast.makeText(MenuModifiedActivity.this,"수정되었습니다.",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //メニュー削除ボタン
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuModifiedActivity.this, MenuPopUpActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("menuname", menuname);
                startActivity(intent);
            }
        });


    }

    //サーバーからコードと合うイメージを得る
    private class back extends AsyncTask<String, Integer, Bitmap> {
        private Bitmap bmImg;

                @Override
                protected Bitmap doInBackground(String... urls) {
                    try {
                        URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }
    }

    //選択メニュー修正
    class MenuModified extends AsyncTask<Void, Void, String> {

        String target;
        String menuid = getIntent().getStringExtra("menuid");
        String menuname = menunameText.getText().toString();
        String menuprice = menupriceText.getText().toString();
        String menudescription = menudescriptionText.getText().toString();

        @Override
        protected void onPreExecute(){
            target = "http://sola0722.cafe24.com/MenuModified.php?menuname=";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target+menuname+"&price="+menuprice+"&description="+menudescription+"&menuid="+menuid);
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
