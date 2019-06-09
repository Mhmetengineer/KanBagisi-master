package com.example.kanbagisi.main;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.kanbagisi.MyHelper;
import com.example.kanbagisi.R;
import com.example.kanbagisi.database.Database;
import com.example.kanbagisi.main.MainFragment;
import com.example.kanbagisi.userlist.ShowBenefactorFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private Database db;
    MyHelper myHelper;
    int cityCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        db = new Database(getApplicationContext());
        myHelper=new MyHelper(getApplicationContext());
        cityCount=db.getCityCount();

        //db.resetTables();
        if(db.getCityCount()==0){
            try{
                SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
                pDialog.setCancelable(false);
                pDialog.setTitle("Lütfen Bekleyiniz");
                pDialog.setContentText("İl ve İlçeler sisteme yükleniyor..");
                pDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        db.saveCitiesAndDistricts();
                        if(db.getCityCount()>0){

                            pDialog.dismiss();
                            startMain();
                        }

                    }
                }, 2000);


            }catch (Exception e){}


        }else{
           startMain();
        }





    }

    private void startMain(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.linLayScreen, new MainFragment(), null);
        transaction.commit();
    }


}
