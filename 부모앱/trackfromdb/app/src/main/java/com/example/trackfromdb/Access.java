package com.example.trackfromdb;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//값저장용
import android.content.SharedPreferences;

public class Access extends AppCompatActivity{

    private EditText Editpname;
    private EditText Editcphone;

    //한번만켜져라
    Global global_wc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access);

        //한번만켜저라
        global_wc = (Global) getApplication();

        global_wc.setWc(0);

        Intent it = getIntent();

        Editpname = (EditText) findViewById(R.id.edit_pname);
        Editcphone = (EditText) findViewById(R.id.edit_cphone);

        TextView tv_1 = (TextView) findViewById(R.id.string_1);

        String title1 = "권한허가를 위해 아이의 이름과 아이의 휴대폰번호를 입력해주세요.";
        tv_1.setText(title1);

        Button buttonSend = (Button) findViewById(R.id.send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String distance = EditDistance.getText().toString();
                String cname = Editpname.getText().toString();
                String cphone = Editcphone.getText().toString();

                //Toast.makeText(Access.this, "아이이름:" + cname + ", 아이 번호:" + cphone, Toast.LENGTH_LONG).show();

                Editpname.setText(""); Editcphone.setText("");

                SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
                SharedPreferences.Editor editor = a.edit();
                editor.putString("mytablename", cname+cphone);
                editor.commit();

                //한번만켜져라
                global_wc.setWc(1);
                finish();

                //php 연결. access.php
                //허가 되면 intent로 setting.java화면으로 연결
            }
        });

    }

    public void closeAccess(View v) {
        //finish();
    }
}