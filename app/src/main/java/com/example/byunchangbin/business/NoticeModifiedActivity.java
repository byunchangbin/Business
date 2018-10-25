package com.example.byunchangbin.business;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NoticeModifiedActivity extends AppCompatActivity{

        private EditText noticeTitle, noticeName,noticeDetails;
        private TextView noticeDateCreated;
        private Button modifiedButon, deleteButton;
        String noticeid,code;
        private NoticeModifiedList list;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_noticemodified);

            noticeTitle = (EditText) findViewById(R.id.noticetitle);
            noticeName = (EditText) findViewById(R.id.noticename);
            noticeDateCreated = (TextView)findViewById(R.id.noticedatecreated);
            noticeDetails = (EditText) findViewById(R.id.noticedetails);
            modifiedButon = (Button)findViewById(R.id.modified_button);
            deleteButton = (Button)findViewById(R.id.delete_button);

            noticeTitle.setText(getIntent().getStringExtra("title"));
            noticeName.setText(getIntent().getStringExtra("name"));
            noticeDateCreated.setText(getIntent().getStringExtra("datecreated"));

            code = getIntent().getStringExtra("code");
            noticeid = getIntent().getStringExtra("noticeid");

            //お知らせの説明クラス呼び出し
            new BackgroundTask().execute();


            //お知らせの修正ボタン
            modifiedButon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MenuModified().execute();
                    Toast toast = Toast.makeText(NoticeModifiedActivity.this,"수정되었습니다.",Toast.LENGTH_SHORT);
                    toast.show();

                }
            });

            //お知らせの削除ボタン
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NoticeModifiedActivity.this, NoticePopupActivity.class);
                    intent.putExtra("code", code);
                    intent.putExtra("noticeid", noticeid);
                    intent.putExtra("title", noticeTitle.getText().toString());
                    startActivity(intent);
                }
            });




        }



//DBでコードに合うお知らせ事項の説明テーブルを得る。
class BackgroundTask extends AsyncTask<Void, Void, String> {

    String target;

    @Override
    protected void onPreExecute(){
        noticeid = getIntent().getStringExtra("noticeid");
        code = getIntent().getStringExtra("code");
        System.out.println(code);

        target = "http://sola0722.cafe24.com/NoticeDetail.php?noticeid=";
    }
    @Override
    protected String doInBackground(Void... voids) {
        try{
            URL url = new URL(target+noticeid+"&code="+code);
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
            int count = 0;
            String detail;
            while (count < jsonArray.length()){
                JSONObject object = jsonArray.getJSONObject(count);
                detail = object.getString("detail");
                list = new NoticeModifiedList(detail);
                list.setDetail(detail);
                count++;
            }
            noticeDetails.setText(list.getDetail());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

    class MenuModified extends AsyncTask<Void, Void, String> {

        String target;
        String id = getIntent().getStringExtra("noticeid");
        String title = noticeTitle.getText().toString();
        String name = noticeName.getText().toString();
        String detail = noticeDetails.getText().toString();



        @Override
        protected void onPreExecute(){
            target = "http://sola0722.cafe24.com/NoticeModified.php?title=";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                System.out.println(title+name+detail+id);
                URL url = new URL(target+title+"&name="+name+"&detail="+detail+"&id="+id);
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
