package com.example.byunchangbin.business;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SalseActivity extends AppCompatActivity {

    private SalseListAdapter adapter;
    private List<SalseList> SalseList;
    private ListView SalseListView;
    private TextView textSalse, textDate;
    private Button findButton;
    int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("매출현황");
        setContentView(R.layout.activity_salse);

        SalseListView = (ListView)findViewById(R.id.listView);
        SalseList = new ArrayList<SalseList>();
        adapter = new SalseListAdapter(getApplicationContext(),SalseList);
        SalseListView.setAdapter(adapter);
        textSalse = (TextView)findViewById(R.id.textSalse);
        textDate = (TextView)findViewById(R.id.btnchangedate);
        findButton = (Button)findViewById(R.id.findButton);

        //現在の日付

        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        UpdateNow();//画面にTextViewにアップデート

        //日付別の売上現況の照会
        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SalseActivity.this, mDateSetListener, mYear, mMonth, mDay).show();
            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalseList.clear();
                adapter.notifyDataSetChanged();
                new BackgroundTask().execute();
                new SalseSum().execute();
            }
        });

        //注文リトスのクラスアクセス
        new BackgroundTask().execute();

        new SalseSum().execute();

    }

    //日付リスナー部分

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //使用者が選択した日付を得る
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    //画面にTextViewにアップデート
                    UpdateNow();
                }
            };

    void UpdateNow(){

        textDate.setText(String.format("%d-%d-%d", mYear, mMonth + 1, mDay));
    }

    //注文完了リスト
    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;
        String code = getIntent().getStringExtra("code");
        String date = textDate.getText().toString();


        @Override
        protected void onPreExecute(){
            target = "http://sola0722.cafe24.com/Salse.php?code=";}

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target+code+"&date=" + date);
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
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int counter = 0;
                String id,menuname,price, count,userid,phonenumber;
                while (counter < jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(counter);
                    id = object.getString("id");
                    menuname = object.getString("menuname");
                    price = object.getString("price");
                    count = object.getString("count");
                    userid= object.getString("userid");
                    phonenumber= object.getString("phonenumber");
                    SalseList orders = new SalseList(id,menuname,price,count,userid,phonenumber);
                    SalseList.add(orders);
                    adapter.notifyDataSetChanged();
                    counter++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //売上
    class SalseSum extends AsyncTask<Void, Void, String> {

        String target;
        String code = getIntent().getStringExtra("code");
        String date = textDate.getText().toString();

        @Override
        protected void onPreExecute(){
            target = "http://sola0722.cafe24.com/SalseSum.php?code=";}

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target+code+"&date=" + date);
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
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int counter = 0;
                String price = null;
                while (counter < jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(counter);
                    price = object.getString("price");
                    counter++;
                }
                textSalse.setText(price + "원");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
