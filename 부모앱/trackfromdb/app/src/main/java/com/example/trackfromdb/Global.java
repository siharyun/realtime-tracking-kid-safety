package com.example.trackfromdb;

import android.app.Application;
import android.widget.Toast;

public class Global extends Application {
    private int wc = 1;

    public int getWc(){
        return wc;
    }

    public void setWc(int new_wc){
        this.wc = new_wc;
        //Toast.makeText(this, "새 설정값 : " + alarm_radius , Toast.LENGTH_LONG).show();
    }
}
