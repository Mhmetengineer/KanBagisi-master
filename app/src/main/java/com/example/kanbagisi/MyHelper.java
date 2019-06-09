package com.example.kanbagisi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.kanbagisi.service.ApiService;
import com.example.kanbagisi.service.UtilsApi;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MyHelper {
    Context mContext;
    public static SweetAlertDialog dialog;
    public MyHelper() {
    }

    public MyHelper(Context mContext) {
        this.mContext = mContext;

    }
    public void createProgress(){
        dialog = new SweetAlertDialog(mContext,SweetAlertDialog.PROGRESS_TYPE);
    }

    public void showProgress(String title,String description){

        if(title!=null) dialog.setTitle(title);
        if(description!=null) dialog.setContentText(description);
        dialog.show();
        dialog.setCancelable(false);
    }
    public void closeProgress(){
        dialog.cancel();
    }

    public void closeKeyboard(Activity mContext){
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){}
    }

    public String dateToInt(String date){
        String[] separated = date.split(":");
        return separated[0]+separated[1];
    }
    public int getCurrentDayIndex(){
        int dayIndex=0; //Starting Monday
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:dayIndex=1;break;
            case Calendar.TUESDAY:dayIndex=2;break;
            case Calendar.WEDNESDAY:dayIndex=3;break;
            case Calendar.THURSDAY:dayIndex=4;break;
            case Calendar.FRIDAY:dayIndex=5;break;
            case Calendar.SATURDAY:dayIndex=6;break;
            case Calendar.SUNDAY:dayIndex=7;break;
        }
        return dayIndex;
    }

    public String timeToDate(String time){
        time=("0000" + time).substring(time.length());
        String stringTime = time.substring(0, 2) + ":" + time.substring(2, time.length());
        return(stringTime);
    }

    public void sendSMS(String GSMNO,String message,boolean isFind){

        try {

            final ApiService[] mApiService = new ApiService[1];
            mApiService[0] = UtilsApi.getAPIService();
            mApiService[0].sendMessage(GSMNO, message).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                   if(isFind && response.isSuccessful()){
                       SweetAlertDialog pDialog = new SweetAlertDialog(mContext,SweetAlertDialog.SUCCESS_TYPE);
                       pDialog.getProgressHelper().setBarColor(Color.parseColor("#00ff00"));
                       pDialog.setTitleText("Kan ihtiyacınız karşı tarafa bildirilmiştir.");
                       pDialog.setConfirmText("Tamam");
                       pDialog.setContentText("Umarız en kısa sürede ihtiyacınızı karşılarsınız.");
                       pDialog.setCancelable(false);
                       pDialog.show();
                   }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(mContext, "Bir sorunla karşılaşıldı lütfen tekrar deneyiniz.1", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext, "Bir sorunla karşılaşıldı lütfen tekrar deneyiniz.2", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
