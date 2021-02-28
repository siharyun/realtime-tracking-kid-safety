package com.example.locationtracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class Setting extends AppCompatActivity{

    private static String IP_ADDRESS = "home114.myds.me";
    private static String TAG = "Child setting";

    private EditText Editcname;
    private EditText Editcphone;

    //글로벌 table
    //Global tablename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        Intent it = getIntent();

        Editcname = (EditText) findViewById(R.id.edit_cname);
        Editcphone = (EditText) findViewById(R.id.edit_cphone);

        TextView tv_1 = (TextView) findViewById(R.id.string_1);

        String title1 = "부모의 권한허가를 위해 자신의 이름과 휴대폰번호를 입력해주세요.";
        tv_1.setText(title1);

        Button buttonSend = (Button) findViewById(R.id.send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String distance = EditDistance.getText().toString();
                String cname = Editcname.getText().toString();
                String cphone = Editcphone.getText().toString();

                Toast.makeText(Setting.this, "아이이름:" + cname + ", 아이 번호:"+cphone, Toast.LENGTH_LONG).show();

                Editcname.setText(""); Editcphone.setText("");

                //php 연결. access.php
                sendDB(cname,cphone);
                //허가 되면 intent로 setting.java화면으로 연결
                finish();
            }
        });

    }

    protected void sendDB(String cn, String cph){
        InsertData2 task = new InsertData2();
        String cname= cn; String cphone=cph;
        task.execute("http://" + IP_ADDRESS + "/child_create.php",cname,cphone);
        Toast.makeText(Setting.this, "삽입할 data \n이름: " + cname + "\n번호: " + cphone, Toast.LENGTH_LONG).show();

        //글로벌 테이블변수에 받은 아이 이름 삽입
        //tablename = (Global) getApplication();
        //tablename.setTable(cn);
        SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
        SharedPreferences.Editor editor = a.edit();
        editor.putString("tablename", cn+cph);
        editor.commit();

    }

    class InsertData2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Setting.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            // mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String cname = params[1];
            String cphone = params[2];

            String serverURL = params[0];
            String postParameters = "cname=" + cname+ "&cphone=" + cphone;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    public void closeAccess(View v) {
        finish();
    }
}