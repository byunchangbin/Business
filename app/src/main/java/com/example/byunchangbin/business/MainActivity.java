package com.example.byunchangbin.business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView MenuButton,salseButton, orderButton, NoticeButton;
    private TextView textViewCode, textViewName;
    private ImageView mainImage;
    private BackPressCloseHandler backPressCloseHandler;
    private ImageBack task;
    private Bitmap bmp;
    private String logoimageUrl = "http://sola0722.cafe24.com/logoimage/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);

        final String companyid = getIntent().getStringExtra("companyid");
        final String code = getIntent().getStringExtra("code");
        final String tel = getIntent().getStringExtra("phonenumber");

        NoticeButton = (ImageView)findViewById(R.id.notice_button);
        MenuButton = (ImageView) findViewById(R.id.menu_button);
        orderButton = (ImageView) findViewById(R.id.order_button);
        salseButton = (ImageView) findViewById(R.id.salse_button);
        textViewCode = (TextView)findViewById(R.id.textViewCode);
        textViewName = (TextView)findViewById(R.id.textViewName);
        mainImage = (ImageView)findViewById(R.id.mainImage);

        textViewName.setText(companyid);
        textViewCode.setText(code);

        task = new ImageBack();
        try {
            bmp = task.execute(logoimageUrl+code+".jpg").get();
            mainImage.setImageBitmap(bmp);
        } catch(Exception e) {
            e.printStackTrace();
        }

        //お知らせのボタン
        NoticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
                intent.putExtra("code",code);
                startActivity(intent);
            }
        });

        //メニューのボタン
        MenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                intent.putExtra("code",code);
                startActivity(intent);
            }
        });

        //注文リストのボタン
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
                intent.putExtra("code",code);
                intent.putExtra("phponenumber",tel);
                startActivity(intent);
            }
        });
        //売上現況リストボタン
        salseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SalseActivity.class);
                intent.putExtra("code",code);
                startActivity(intent);
            }
        });

    }


    //イメージを得る
    private class ImageBack extends AsyncTask<String, Integer, Bitmap> {
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

}
