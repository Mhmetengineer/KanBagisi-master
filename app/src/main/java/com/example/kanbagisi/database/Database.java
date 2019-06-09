package com.example.kanbagisi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.kanbagisi.MyHelper;
import com.example.kanbagisi.Values;
import com.example.kanbagisi.user.SelectDateModel;
import com.example.kanbagisi.user.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;

    private static final String DATABASE_NAME = "sqllite_database";//database adı

    private static final String TABLE_USER = "users";
    private static final String TABLE_DATE = "dates";
    private static final String TABLE_CITY = "cities";
    private static final String TABLE_DISTRICT = "districts";

    private static String USER_ID = "user_id";
    private static String USER_NAME = "user_name";
    private static String USER_SURNAME = "user_surname";
    private static String USER_TC = "user_tc";
    private static String USER_PHONENUMBER = "user_phonenumber";
    private static String USER_ADDRESS = "user_address";
    private static String USER_BLOODGROUP = "user_bloodGroup";
    private static String USER_MAIL = "user_mail";
    private static String USER_CITY = "user_city";
    private static String USER_DISTRICT = "user_district";
    private static String USER_BENEFACTOR = "user_benefactor";

    private static String DATE_ID = "date_id";
    private static String DATE_DAY_NUMBER = "date_day_number";//pazartesi=1
    private static String DATE_BEGIN = "date_begin";
    private static String DATE_END = "date_end";

    private static String CITY_ID = "city_id";
    private static String CITY_NAME = "city_name";

    private static String DISTRICT_ID = "district_id";
    private static String DISTRICT_NAME = "district_name";
    public Context mContext;
    String ilveilceler;
    MyHelper myHelper;
    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext=context;
        ilveilceler= Values.cityanddistrict;
        myHelper=new MyHelper();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_NAME + " TEXT,"
                + USER_SURNAME + " TEXT,"
                + USER_TC + " TEXT,"
                + USER_PHONENUMBER + " TEXT,"
                + USER_ADDRESS + " TEXT,"
                + USER_BLOODGROUP + " INT,"
                + USER_MAIL + " TEXT,"
                + USER_CITY + " INTEGER,"
                + USER_DISTRICT + " INTEGER,"
                + USER_BENEFACTOR + " BOOLEAN" + ")";

        String CREATE_TABLE_DATE = "CREATE TABLE " + TABLE_DATE + "("
                + DATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_ID + " INTEGER,"
                + DATE_DAY_NUMBER + " INTEGER,"
                + DATE_BEGIN + " INTEGER,"
                + DATE_END + " INTEGER" + ")";

        String CREATE_TABLE_CITY = "CREATE TABLE " + TABLE_CITY + "("
                + CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CITY_NAME + " TEXT" + ")";

        String CREATE_TABLE_DISTRICT = "CREATE TABLE " + TABLE_DISTRICT + "("
                + DISTRICT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CITY_ID + " INTEGER ,"
                + DISTRICT_NAME + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_DATE);
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_DISTRICT);
    }

    public void saveCitiesAndDistricts(){

        JSONArray jsonArr = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cityValues = new ContentValues();
        ContentValues districtValues = new ContentValues();
        try {
            jsonArr = new JSONArray(ilveilceler);
            for(int i=0;i<jsonArr.length();i++){
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                cityValues.put(CITY_NAME, jsonObj.getString("il"));
                db.insert(TABLE_CITY, null, cityValues);
                for(int k=0;k<jsonObj.getJSONArray("ilceleri").length();k++){
                    districtValues.put(DISTRICT_NAME, jsonObj.getJSONArray("ilceleri").getString(k));
                    districtValues.put(CITY_ID, i);
                    db.insert(TABLE_DISTRICT, null, districtValues);
                }
            }

            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<DistictModel> getDistrictfromCity(int cityID){
        String selectQuery = "SELECT * FROM " + TABLE_DISTRICT+ " WHERE city_id="+cityID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String deger=null;
        ArrayList<DistictModel> districts=new ArrayList<DistictModel>();
        DistictModel distictModel= new DistictModel(-1, "İlçe seçiniz \uD83D\uDD3B\n");
        districts.add(distictModel);
        if (cursor.moveToFirst()) {
            do {  distictModel = new DistictModel(cursor.getInt(0),cursor.getString(2));
                districts.add(distictModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return districts;
    }
    public ArrayList<String> getCities(){
        String selectQuery = "SELECT * FROM " + TABLE_CITY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String deger=null;
        ArrayList<String> districts=new ArrayList<String>();
        districts.add("İl seçiniz \uD83D\uDD3B\n");
        if (cursor.moveToFirst()) {
            do {
                districts.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return districts;
    }
    public ArrayList<UserModel> getBenefactors(int bloodGroup,int districtID){
        String otherBlood=" ";
        String alwaysGiveGroup=" OR user_bloodGroup=7";

        if(bloodGroup%2==0){
             otherBlood=" OR user_bloodGroup="+(bloodGroup+1)+" OR user_bloodGroup=6"+alwaysGiveGroup;
        }
        if(bloodGroup==4){
            otherBlood=" OR user_bloodGroup=0 OR user_bloodGroup=1 OR user_bloodGroup=2 OR user_bloodGroup=3 OR user_bloodGroup=5 "+" OR user_bloodGroup=6"+alwaysGiveGroup;
        }
        if(bloodGroup==5){
            otherBlood="  OR user_bloodGroup=1 OR user_bloodGroup=3 OR user_bloodGroup=5 "+alwaysGiveGroup;
        }



        String selectQuery = "SELECT * FROM " + TABLE_USER+ " WHERE (user_bloodGroup="+bloodGroup +otherBlood+" ) AND (user_district="+districtID+")";
       // String selectQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int rowCount = cursor.getCount();
        if(rowCount<1)return null;
        cursor.moveToFirst();
        ArrayList<UserModel> userModelArrayList=new ArrayList<UserModel>();
        if (cursor.moveToFirst()) {
            do {  UserModel userModel = new UserModel(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getInt(6),cursor.getString(7),cursor.getInt(8),cursor.getInt(9),(cursor.getInt(10) == 1),getSelectDataModel(cursor.getInt(0),
                    myHelper.getCurrentDayIndex(),false));
                if(getSelectDataModel(cursor.getInt(0),
                        myHelper.getCurrentDayIndex(),false)!=null)
                userModelArrayList.add(userModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userModelArrayList;
    }

    public ArrayList<SelectDateModel> getSelectDataModel(int userID, int selectedDay, boolean isAll){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String selectQuery= null;
        if(isAll){
            selectQuery = "SELECT * FROM " + TABLE_DATE+ " WHERE user_id="+userID;

        }else{
            selectQuery = "SELECT * FROM " + TABLE_DATE+ " WHERE user_id="+userID+" AND date_day_number="+selectedDay+ " AND date_begin <="+myHelper.dateToInt(mSimpleDateFormat.format(currentTime))+" AND date_end >="+myHelper.dateToInt(mSimpleDateFormat.format(currentTime));

        }
         //String selectQuery = "SELECT * FROM " + TABLE_DATE+ " WHERE user_id="+userID+" AND date_day_number="+selectedDay;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int rowCount = cursor.getCount();
        if(rowCount<1)return null;
        cursor.moveToFirst();
        ArrayList<SelectDateModel> selectDateModels=new ArrayList<SelectDateModel>();
        if (cursor.moveToFirst()) {
            do {
                SelectDateModel selectDateModel = new SelectDateModel();
                selectDateModel.setBeginTime(cursor.getInt(3)+"");
                selectDateModel.setSelected(true);
                selectDateModel.setSelectedDay(cursor.getInt(2));
                selectDateModel.setEndTime(cursor.getInt(4)+"");
                selectDateModels.add(selectDateModel);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return selectDateModels;
    }

    public boolean saveUser(UserModel userModel){
        Boolean isSuccess=false;
        int isbenefactor=0;
        if(userModel.getBenefactor())isbenefactor=1;
        if(!haveUserControl(userModel.getTc(),isbenefactor)) {
            Toast.makeText(mContext, "Bu TC no kayıtlıdır." , Toast.LENGTH_SHORT).show();
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, userModel.getName());
        values.put(USER_SURNAME, userModel.getSurname());
        values.put(USER_TC, userModel.getTc());
        values.put(USER_CITY, userModel.getCity());
        values.put(USER_DISTRICT, userModel.getDistrict());
        values.put(USER_ADDRESS, userModel.getAdress());
        values.put(USER_BENEFACTOR, userModel.getBenefactor());
        values.put(USER_BLOODGROUP, userModel.getBloodGroup());
        values.put(USER_MAIL, userModel.getMail());
        values.put(USER_PHONENUMBER, userModel.getPhoneNumber());
        long id = db.insert(TABLE_USER, null, values);
        if(id>0)isSuccess=true;
        int user_id=(int)id;

        if(userModel.getBenefactor()){
        for(int i=0;i<userModel.getSelectDateArrayList().size();i++){
            if(userModel.getSelectDateArrayList().get(i).isSelected()){
                ContentValues valuesTime = new ContentValues();
                valuesTime.put(USER_ID,(int)user_id);
                valuesTime.put(DATE_DAY_NUMBER, userModel.getSelectDateArrayList().get(i).getSelectedDay() );
                valuesTime.put(DATE_BEGIN, Integer.parseInt(userModel.getSelectDateArrayList().get(i).getBeginTime()));
                valuesTime.put(DATE_END, Integer.parseInt(userModel.getSelectDateArrayList().get(i).getEndTime()));
                id=db.insert(TABLE_DATE, null, valuesTime);
                if(id<0)isSuccess=false;
            }
        }}
        db.close();
        return isSuccess;
    }
    public boolean editUser(String TC,UserModel userModel){
        int isbenefactor=0;
        if(userModel.getBenefactor())isbenefactor=1;
        if(!TC.equals(userModel.getTc())){
        if(!haveUserControl(userModel.getTc(),isbenefactor)) {
            Toast.makeText(mContext, "Bu TC no kayıtlıdır." , Toast.LENGTH_SHORT).show();
            return false;
        } }
        Boolean isSuccess=false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, userModel.getName());
        values.put(USER_SURNAME, userModel.getSurname());
        values.put(USER_TC, userModel.getTc());
        values.put(USER_CITY, userModel.getCity());
        values.put(USER_DISTRICT, userModel.getDistrict());
        values.put(USER_ADDRESS, userModel.getAdress());
        values.put(USER_BENEFACTOR, userModel.getBenefactor());
        values.put(USER_BLOODGROUP, userModel.getBloodGroup());
        values.put(USER_MAIL, userModel.getMail());
        values.put(USER_PHONENUMBER, userModel.getPhoneNumber());
        int intSuccess = db.update(TABLE_USER, values, USER_TC + " = ? AND "+USER_BENEFACTOR+" = ?",
                new String[] { TC, Long.toString(isbenefactor)});

        if(intSuccess>0) isSuccess=true;

        if(userModel.getBenefactor()){db.delete(TABLE_DATE, USER_ID + " = ?",new String[]{Long.toString(getUserID(TC))} );
            for(int i=0;i<userModel.getSelectDateArrayList().size();i++){
                if(userModel.getSelectDateArrayList().get(i).isSelected()){
                    ContentValues valuesTime = new ContentValues();
                    valuesTime.put(USER_ID,getUserID(TC));
                    valuesTime.put(DATE_DAY_NUMBER, userModel.getSelectDateArrayList().get(i).getSelectedDay() );
                    valuesTime.put(DATE_BEGIN, Integer.parseInt(userModel.getSelectDateArrayList().get(i).getBeginTime()));
                    valuesTime.put(DATE_END, Integer.parseInt(userModel.getSelectDateArrayList().get(i).getEndTime()));
                    long id=db.insert(TABLE_DATE, null, valuesTime);
                    if(id<0)isSuccess=false;
                }
            }

        }

        db.close();

        return isSuccess;
    }

    public int getUserID(String TC){
        String selectQuery = "SELECT  * FROM " + TABLE_USER+ " WHERE user_tc="+TC;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int id =0;
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            id=cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
    public UserModel getUser(String TC,int isBenefactor){
        String selectQuery = "SELECT  * FROM " + TABLE_USER+ " WHERE user_tc="+TC+" AND user_benefactor="+isBenefactor;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int rowCount = cursor.getCount();
        UserModel userModel = new UserModel();
        if(rowCount<1)return null;
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
              userModel = new UserModel(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getInt(6),cursor.getString(7),cursor.getInt(8),cursor.getInt(9),(cursor.getInt(10) == 1),getSelectDataModel(cursor.getInt(0),
                    myHelper.getCurrentDayIndex(),true));
        }
        cursor.close();
        db.close();
        return userModel;
    }

    public boolean haveUserControl(String TC,int isBenefactor){
        Boolean isSuccess=false;
        String countQuery = "SELECT  * FROM " + TABLE_USER+ " WHERE user_tc="+TC+" AND user_benefactor="+isBenefactor;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        if(rowCount>0)isSuccess=false;
        else
            isSuccess=true;
        return isSuccess;
    }
    public int getCityCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CITY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        return rowCount;
    }



    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_USER + "'");
        db.delete(TABLE_DATE, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_DATE+ "'");
        db.delete(TABLE_CITY, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_CITY+ "'");
        db.delete(TABLE_DISTRICT, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_DISTRICT+ "'");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }



}