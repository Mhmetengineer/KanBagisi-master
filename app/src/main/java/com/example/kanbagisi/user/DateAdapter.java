package com.example.kanbagisi.user;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kanbagisi.MyHelper;
import com.example.kanbagisi.R;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {

    Context mContext;
    String[] days = {"Pazartesi", "salı", "çarşamba", "perşembe", "cuma", "cumartesi", "pazar"};
    MyHelper myHelper;
    public static ArrayList<SelectDateModel> selectDates,oldSelectDates;
    SelectDateModel selectDate;
    SingleDateAndTimePickerDialog singleDateAndTimePickerDialog;
    ArrayList<Date> beginDates,endDates;

    private SimpleDateFormat mSimpleDateFormat;

    public DateAdapter(Context mContext) {
        this.mContext = mContext;
        selectDates = new ArrayList<SelectDateModel>();
        myHelper = new MyHelper();
        beginDates=new ArrayList<Date>();
        endDates=new ArrayList<Date>();

    }
    public DateAdapter(Context mContext,ArrayList<SelectDateModel> oldSelectDates) {

        this.mContext = mContext;
        this.oldSelectDates =oldSelectDates;
        selectDates = new ArrayList<SelectDateModel>();
        myHelper = new MyHelper();
        beginDates=new ArrayList<Date>();
        endDates=new ArrayList<Date>();
        for(int i=0;i<7;i++){
            selectDate = new SelectDateModel();
            selectDates.add(selectDate);
            for(int k=0;k<oldSelectDates.size();k++){
                if(oldSelectDates.get(k).getSelectedDay()-1==i){
                    selectDates.set(i,oldSelectDates.get(k));
                }}
        }

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.select_day_row, parent, false);
        mSimpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return new MyViewHolder(view);
    }

    private Date oldStringTimetoDate(String date){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = df.parse(date);
            //d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        Date date = new Date();
        beginDates.add(date);
        endDates.add(date);
        if(position%2==1)
            holder.linLayForBackground.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        else
            holder.linLayForBackground.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));

        if(oldSelectDates!=null){
            try{
                if(selectDates.get(position).isSelected()){
                    holder.cbDay.setChecked(true);
                    holder.tvBeginTime.setText(myHelper.timeToDate(selectDates.get(position).getBeginTime()));
                    holder.tvEndTime.setText(myHelper.timeToDate(selectDates.get(position).getEndTime()));
                    holder.tvBeginTime.setVisibility(View.VISIBLE);
                    holder.tvEndTime.setVisibility(View.VISIBLE);
                    beginDates.set(position ,oldStringTimetoDate(myHelper.timeToDate(selectDates.get(position).getBeginTime())));
                    endDates.set(position ,oldStringTimetoDate(myHelper.timeToDate(selectDates.get(position).getEndTime())));
                }  }catch (Exception e){}
        }

        holder.cbDay.setText(days[position]);

        holder.cbDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    int k=-1;
                    for(int i=0;i<selectDates.size();i++){
                        if(selectDates.get(i).getSelectedDay()==position+1){
                            i=k;
                        }
                    }
                    if(k!=-1){
                        selectDate=selectDates.get(k);
                    }else{
                        selectDate = new SelectDateModel();}
                    selectDate.setSelectedDay(position + 1);
                    selectDate.setSelected(true);
                    if(k==-1){
                        selectDates.add(selectDate);
                    }else{
                        selectDates.set(k,selectDate);
                    }

                    holder.tvBeginTime.setVisibility(View.VISIBLE);
                    holder.tvEndTime.setVisibility(View.VISIBLE);
                } else {
                    for(int i=0;i<selectDates.size();i++){
                        if(selectDates.get(i).getSelectedDay()==position+1){
                            selectDates.get(i).setSelected(false);
                        }
                    }


                    holder.tvBeginTime.setText("Seçiniz");
                    holder.tvEndTime.setText("Seçiniz");
                    holder.tvBeginTime.setVisibility(View.INVISIBLE);
                    holder.tvEndTime.setVisibility(View.INVISIBLE);

                }
            }
        });
        holder.tvBeginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialog.Builder(mContext)
                        .minutesStep(15)
                        .mainColor(mContext.getResources().getColor(R.color.green))
                        .displayDays(false)
                        .title("Başlangıç saatinizi seçiniz.")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                if (holder.tvEndTime.getText().equals("Seçiniz") || Integer.parseInt(myHelper.dateToInt(mSimpleDateFormat.format(endDates.get(position).getTime()))) > Integer.parseInt(myHelper.dateToInt(mSimpleDateFormat.format(date.getTime())))) {
                                    beginDates.set(position ,date);
                                    holder.tvBeginTime.setText(mSimpleDateFormat.format(date.getTime()));
                                    for(int i=0;i<selectDates.size();i++){
                                        if(selectDates.get(i).getSelectedDay()==position+1){
                                            selectDates.get(i).setBeginTime(myHelper.dateToInt(mSimpleDateFormat.format(date.getTime())));
                                        }
                                    }
                                } else {
                                    Toast.makeText(mContext, "Başlangıç saatiniz bitiş saatinizden sonra olamaz.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .display();
            }
        });

        holder.tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialog.Builder(mContext)
                        .minutesStep(15)
                        .displayDays(false)
                        .mainColor(mContext.getResources().getColor(R.color.green))
                        .title("Bitiş saatinizi seçiniz")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                if (!holder.tvBeginTime.getText().equals("Seçiniz")) {
                                    if (beginDates.get(position).getTime() < date.getTime()) {
                                        endDates.set(position ,date);
                                        holder.tvEndTime.setText(mSimpleDateFormat.format(date.getTime()));
                                        for(int i=0;i<selectDates.size();i++){
                                            if(selectDates.get(i).getSelectedDay()==position+1){
                                                selectDates.get(i).setEndTime(myHelper.dateToInt(mSimpleDateFormat.format(date.getTime())));
                                            }
                                        }
                                    } else {
                                        Toast.makeText(mContext, "Başlangıç saatinizden sonraki bir zamanı seçiniz.", Toast.LENGTH_SHORT).show();
                                        holder.tvEndTime.setText("Seçiniz");
                                    }
                                } else {
                                    Toast.makeText(mContext, "Önce Başlangıç saatini belirtiniz.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .display();
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.length;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cbDay)
        CheckBox cbDay;
        @BindView(R.id.tvBeginTime)
        TextView tvBeginTime;
        @BindView(R.id.tvEndTime)
        TextView tvEndTime;
        @BindView(R.id.linLayForBackground)
        LinearLayout linLayForBackground;



        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
