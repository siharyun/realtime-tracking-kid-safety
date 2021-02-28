package com.example.trackfromdb;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.app.Application;

//값저장용
import android.content.SharedPreferences;

import java.util.concurrent.atomic.AtomicInteger;


public class Setting extends AppCompatActivity {

    private EditText EditDistance;
    //변수 테스트
    //Global global_vs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);


        //distance용
        //global_vs = (Global) getApplication();
        EditDistance=(EditText)findViewById(R.id.edit_distance);

        TextView tv_2 = (TextView) findViewById(R.id.string_distance);
        Resources res = getResources();

        String title = "변경하고자 하는 반경을 입력해주세요.";
        tv_2.setText(title);

        Button buttonSend2 = (Button)findViewById(R.id.send_distance);
        buttonSend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String distance = EditDistance.getText().toString();
                double radius = Double.parseDouble(EditDistance.getText().toString());
                int int_radius = (int)radius;
                EditDistance.setText("");
                //글로벌테스트
                //global_vs.setAlarm_radius(radius);

                Toast.makeText(Setting.this, "반경이 "+radius+"m로 변경되었습니다.", Toast.LENGTH_LONG).show();

                SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
                SharedPreferences.Editor editor = a.edit();
                editor.putInt("myradius", int_radius);
                editor.commit();

                finish();
                /*
                Toast.makeText(Setting.this, "반경이 "+radius+"m로 변경되었습니다.", Toast.LENGTH_LONG).show();

                EditDistance.setText("");

                Intent radius_it=new Intent(Setting.this, MainActivity.class);
                radius_it.putExtra("radius",radius);
                startActivity(radius_it);*/

            }
        });

        Button buttonDisable = (Button)findViewById(R.id.distance_disable);
        buttonDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int int_radius = 0;
                EditDistance.setText("");
                //글로벌테스트
                //global_vs.setAlarm_radius(radius);

                Toast.makeText(Setting.this, "반경 사용이 중지되었습니다", Toast.LENGTH_LONG).show();

                SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
                SharedPreferences.Editor editor = a.edit();
                editor.putInt("myradius", int_radius);
                editor.commit();

                finish();
            }
        });




        /*int id_picture = res.getIdentifier("picture" + tag, "string", getPackageName());

        String picture = res.getString(id_picture);
        int id_img = res.getIdentifier(picture, "drawable", getPackageName());

        Drawable drawable = res.getDrawable(id_img);
        iv_picture.setBackground(drawable);*/
    }
    public void closePicture(View v){
        finish();
    }
}