package com.example.kanbagisi.userlist;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kanbagisi.MyHelper;
import com.example.kanbagisi.R;
import com.example.kanbagisi.Values;
import com.example.kanbagisi.user.UserModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BenefactorAdapter extends RecyclerView.Adapter<BenefactorAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<UserModel> userModelArrayList;
    MyHelper myHelper;
    String finderBloodGSM;
    View otherView;
    public static int pageIndex=0;


    public BenefactorAdapter(Context mContext, ArrayList<UserModel> userModelArrayList,String finderBloodGSM) {
        this.mContext = mContext;
        this.userModelArrayList = userModelArrayList;
        myHelper = new MyHelper(mContext);
        this.finderBloodGSM=finderBloodGSM;
        otherView = LayoutInflater.from(mContext).inflate(R.layout.fragment_show_benefactor, null, false);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.benefactor_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
    try {
        holder.tvBloodGroup.setText(Values.bloodGroups[userModelArrayList.get(position+(pageIndex*5)).getBloodGroup()+1]);
        holder.tvName.setText(userModelArrayList.get(position+(pageIndex*5)).getName()+"\n"+userModelArrayList.get(position+(pageIndex*5)).getSurname());
      //  holder.tvPhone.setText(userModelArrayList.get(position).getPhoneNumber());
        holder.tvTime.setText("( "+myHelper.timeToDate(userModelArrayList.get(position+(pageIndex*5)).getSelectDateArrayList().get(0).getBeginTime()) + " - " + myHelper.timeToDate(userModelArrayList.get(position+(pageIndex*5)).getSelectDateArrayList().get(0).getEndTime())+" )");

        holder.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myHelper.isNetworkConnected()) {
                    myHelper.createProgress();
                    myHelper.showProgress("Kişiyle irtibat kuruluyor","Lütfen bekleyiniz...");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           myHelper.closeProgress();
                            myHelper.sendSMS(userModelArrayList.get(position+(pageIndex*5)).getPhoneNumber(), finderBloodGSM + " numaralı kişinin acil kana ihtiyacı vardır.", true);
                        }
                    }, 2000);
                }else{
                    SweetAlertDialog pDialog = new SweetAlertDialog(mContext,  SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff0000"));
                    pDialog.setTitleText("İşlem Başarısız !!");
                    pDialog.setContentText("İnternet bağlantınız olmadan kişiyle iletişim kuramazsınız. Lütfen internete bağlanıp tekrar deneyiniz.");
                    pDialog.setCancelable(false);
                    pDialog.setConfirmText("Tamam");
                    pDialog.show();
                }
            }
        });
    }catch (Exception e){}}


    @Override
    public int getItemCount() {
        if (userModelArrayList == null)
            return 0;
        if (userModelArrayList.size()>5){
            if((userModelArrayList.size()-(pageIndex*5))>5){
            return 5;
            }else{
            return(userModelArrayList.size()-(pageIndex*5))%6;
            }
        }

        return userModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvBloodGroup)
        TextView tvBloodGroup;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.btnRequest)
        Button btnRequest;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
