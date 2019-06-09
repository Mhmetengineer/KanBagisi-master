package com.example.kanbagisi.user;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kanbagisi.MyHelper;
import com.example.kanbagisi.R;
import com.example.kanbagisi.database.Database;
import com.example.kanbagisi.database.DistictModel;
import com.example.kanbagisi.main.MainFragment;
import com.example.kanbagisi.userlist.ShowBenefactorFragment;
import com.github.vacxe.phonemask.PhoneMaskManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.kanbagisi.Values.bloodGroups;

public class UserFragment extends Fragment {


    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtSurname)
    EditText edtSurname;
    @BindView(R.id.edtTC)
    EditText edtTC;
    @BindView(R.id.spinnerCity)
    Spinner spinnerCity;
    @BindView(R.id.spinnerDistrict)
    Spinner spinnerDistrict;
    ArrayList<DistictModel> distictModels;

    @BindView(R.id.edtMail)
    EditText edtMail;
    @BindView(R.id.btnCreateUser)
    Button btnCreateUser;
    @BindView(R.id.edtAddress)
    EditText edtAddress;

    @BindView(R.id.rvDays)
    RecyclerView rvDays;
    @BindView(R.id.linLayForBenefactor)
    LinearLayout linLayForBenefactor;
    @BindView(R.id.edtPhoneNumber)
    EditText edtPhoneNumber;
    @BindView(R.id.tvHeader)
    TextView tvHeader;
    @BindView(R.id.tvUserDataHeader)
    TextView tvUserDataHeader;
    private ArrayAdapter<String> dataAdapterForBlood, dataAdapterForCity, dataAdapterForDistict;
    Boolean success = false;
    private int cityID, districtID, bloodGroupIndex;

    @BindView(R.id.spinnerBlood)
    Spinner spinnerBlood;
    boolean isEdit = false;
    private Database db;
    Boolean isBenefactor;
    private DateAdapter dateAdapter;
    private PhoneMaskManager phoneMaskManager;
    String TC;
    MyHelper myHelper;
    UserModel oldUser;
    UserModel newUser;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        edtName.requestFocus();
        myHelper = new MyHelper(getActivity());

        Bundle bundle = this.getArguments();
        phoneMaskManager = new PhoneMaskManager();
        phoneMaskManager.withMask("(###) ###-##-##");
        phoneMaskManager.withRegion("+90");
        phoneMaskManager.bindTo((EditText) view.findViewById(R.id.edtPhoneNumber));

        if (bundle != null) {
            isBenefactor = getArguments().getBoolean("isBenefactor");
            TC = getArguments().getString("TC", null);
            if (TC == null) {
                isEdit = false;
            } else isEdit = true;
        }
        if(isEdit){
            tvHeader.setVisibility(View.VISIBLE);
            tvUserDataHeader.setVisibility(View.VISIBLE);
        }

        if (isBenefactor) {
            linLayForBenefactor.setVisibility(View.VISIBLE);
            if (!isEdit) {
                tvHeader.setText("KAN VERMEK İÇİN YENİ KAYIT");
                btnCreateUser.setText("KAYDI OLUŞTURMAK İÇİN TIKLAYIN");
            } else {
                tvHeader.setText("BİLGİLERİ GÜNCELLE");
                btnCreateUser.setText("KAYDI GÜNCELLEMEK İÇİN TIKLAYIN");
            }
            tvHeader.setClickable(false);
        } else {
            if (!isEdit) {
                tvHeader.setText("KAN ARAMAK İÇİN YENİ KAYIT");
                btnCreateUser.setText("KAN ARA");
            } else {
                tvHeader.setText("KAN BAĞIŞÇISI ARA");
                btnCreateUser.setText("GÜNCELLE VE ARA");
                tvHeader.setClickable(true);
            }
        }


        db = new Database(getActivity());

        dataAdapterForBlood = new ArrayAdapter<String>(getActivity(), R.layout.color_spinner_layout, android.R.id.text1, bloodGroups);
        dataAdapterForBlood.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dataAdapterForCity = new ArrayAdapter<String>(getActivity(), R.layout.color_spinner_layout, android.R.id.text1, db.getCities());
        dataAdapterForCity.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerBlood.setAdapter(dataAdapterForBlood);
        spinnerCity.setAdapter(dataAdapterForCity);
        if (isEdit) getAndfillData();
        if (isEdit && isBenefactor) {
            dateAdapter = new DateAdapter(getActivity(), oldUser.getSelectDateArrayList());
        } else {
            dateAdapter = new DateAdapter(getActivity());
        }
        rvDays.setAdapter(dateAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        rvDays.setLayoutManager(linearLayoutManager);
        return view;
    }

    private void getAndfillData() {
        oldUser = new UserModel();
        if (isBenefactor) {
            oldUser = db.getUser(TC, 1);
        } else {
            oldUser = db.getUser(TC, 0);
        }
        edtName.setText(oldUser.getName());
        edtSurname.setText(oldUser.getSurname());
        edtMail.setText(oldUser.getMail());
        edtAddress.setText(oldUser.getAdress());
        edtPhoneNumber.setText(oldUser.getPhoneNumber().substring(3, oldUser.getPhoneNumber().length()));
        edtTC.setText(oldUser.getTc());
        spinnerCity.setSelection(oldUser.getCity() + 1);
        spinnerBlood.setSelection(oldUser.getBloodGroup() + 1);
    }


    @OnItemSelected(R.id.spinnerCity)
    public void onSpinnerItemSelected(int index) {
        cityID = index - 1;
        if (index == 0) {
            spinnerDistrict.setVisibility(View.GONE);
            edtAddress.setVisibility(View.GONE);
        } else {

            ArrayList<String> districts = new ArrayList<String>();
            int selectedDistrictIndex = 0;
            distictModels = db.getDistrictfromCity(index - 1);
            for (int i = 0; i < distictModels.size(); i++) {
                districts.add(distictModels.get(i).getDistrict());
                if (isEdit) {
                    if (distictModels.get(i).getDistrictID() == oldUser.getDistrict()) {
                        selectedDistrictIndex = i;
                    }
                }
            }
            dataAdapterForDistict = new ArrayAdapter<String>(getActivity(), R.layout.color_spinner_layout, android.R.id.text1, districts);
            dataAdapterForDistict.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerDistrict.setAdapter(dataAdapterForDistict);
            spinnerDistrict.setVisibility(View.VISIBLE);
            spinnerDistrict.setSelection(selectedDistrictIndex);
            edtAddress.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tvHeader)
    public void onViewClickedHeader() {
        clickedOk();
    }

    @OnItemSelected(R.id.spinnerDistrict)
    public void onSpinnerItemSelectedDistrict(int index) {
        districtID = distictModels.get(index).getDistrictID();
    }

    @OnItemSelected(R.id.spinnerBlood)
    public void onSpinnerItemSelectedBlood(int index) {
        bloodGroupIndex = index - 1;
    }


    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean stringControl(EditText edt,int lenght){
        if(edt.getText().toString().trim().length()<lenght){
            edt.setError("Lütfen bilgileri doğru giriniz");
            edt.requestFocus();
            return false;
        }
        return true;
    }


    @OnClick(R.id.btnCreateUser)
    public void onViewClicked() {
        clickedOk();
    }

    public void clickedOk(){
        if(!stringControl(edtName,3))
            return;

        if(!stringControl(edtSurname,2))
            return;


        if (!TcKimlikKontrol(edtTC.getText().toString().trim())) {
            edtTC.setError("Lütfen TCnizi giriniz");
            edtTC.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtTC, InputMethodManager.SHOW_IMPLICIT);
            return;
        }

        if (phoneMaskManager.getPhone().length() < 13) {
            edtPhoneNumber.setError("Lütfen telefon numaranızı giriniz");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (!isValidEmail(edtMail.getText().toString().trim())) {
            edtMail.setError("Lütfen mail bilgilerinizi giriniz");
            edtMail.requestFocus();
            return;
        }


        if (cityID < 0) {
            spinnerCity.setFocusable(true);
            spinnerCity.setFocusableInTouchMode(true);
            spinnerCity.requestFocus();
            Toast.makeText(getActivity(), "Lütfen il seçiniz", Toast.LENGTH_SHORT).show();
            return;
        }



        if (districtID < 0) {
            spinnerDistrict.setFocusable(true);
            spinnerDistrict.setFocusableInTouchMode(true);
            spinnerDistrict.requestFocus();
            Toast.makeText(getActivity(), "Lütfen ilçe seçiniz", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bloodGroupIndex < 0) {
            spinnerBlood.setFocusable(true);
            spinnerBlood.setFocusableInTouchMode(true);
            spinnerBlood.requestFocus();
            Toast.makeText(getActivity(), "Lütfen kan grubunuzu seçiniz", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBenefactor) {
            Boolean error = false;
            int selectedDayCount = 0;
            for (int i = 0; i < DateAdapter.selectDates.size(); i++) {
                if (DateAdapter.selectDates.get(i).isSelected()) {
                    selectedDayCount++;
                    if (DateAdapter.selectDates.get(i).getBeginTime() == null || DateAdapter.selectDates.get(i).getEndTime() == null) {
                        error = true;
                    }
                }
            }
            if (selectedDayCount < 1) {
                Toast.makeText(getActivity(), "Lütfen müsait olduğunuz günü veya günleri belirtiniz.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (error) {
                Toast.makeText(getActivity(), "Lütfen müsait olduğunuz zamanın başlangıç ve bitiş saatlerini belirtiniz", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        newUser = new UserModel(edtName.getText().toString().trim(), edtSurname.getText().toString().trim(), edtTC.getText().toString().trim(), phoneMaskManager.getPhone(), edtAddress.getText().toString(), bloodGroupIndex, edtMail.getText().toString(), cityID, districtID, isBenefactor, DateAdapter.selectDates);

        String popupText = null;
        Boolean isSuccess;
        if (isEdit) {
            isSuccess = db.editUser(TC, newUser);
        } else {
            isSuccess = db.saveUser(newUser);
        }
        if (isSuccess) {
            if (isEdit) {
                if (isBenefactor) {
                    popupText = "Bilgileriniz başarıyla güncellenmiştir.";
                } else {
                    findblood();
                }
            } else {
                if (isBenefactor) {
                    popupText = "Bilgileriniz sisteme kaydedilmiştir.";
                } else {findblood();
                }
            }

            if (isBenefactor && !isEdit && myHelper.isNetworkConnected()) {
                myHelper.sendSMS(newUser.getPhoneNumber(), "Sayın " + newUser.getName() + " " + newUser.getSurname() + " Kan verme kaydınız oluşturuldu. Bölgenizde, belirlediğiniz kriterlerde kan arama durumlarında bilgilendirileceksiniz. İyi günler.", false);
            }

            if (isBenefactor) {
                SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                pDialog.setTitleText(popupText);
                pDialog.setConfirmText("Tamam");
                pDialog.setCancelable(false);
                pDialog.show();

                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pDialog.dismiss();

                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.linLayScreen, new MainFragment(), null);
                        transaction.commit();

                    }
                }); }else {

            }
        } else {
            popupText="Bu TC numarası önceden kayıtlıdır. Bilgileriniz güncellenip devam edilsin mi?";
            SweetAlertDialog dialog = new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE);
            dialog.setTitle("Kayıt başarısız");
            dialog.setContentText(popupText);
            dialog.setConfirmText("Evet");
            dialog.setCancelText("Hayır");
            dialog.setCancelable(false);
            dialog.show();
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dialog.dismiss();
                    TC=newUser.getTc();
                    isEdit=true;
                    clickedOk();
                }
            });
        }
    }

    public static boolean TcKimlikKontrol(String kimlikNo) {
        // değişkenler
        int tekHaneler = 0;
        int ciftHaneler = 0;
        int kural;
        int toplam = 0;

        // 1. kural 11 hane kontrolü
        if (kimlikNo.length() < 11) {
            return false;
        }
        // 2. kural 0 ile başlayamaz
        if (kimlikNo.startsWith("0")) {
            return false;
        }
        // diğer kurallar için rakamlar tek tek parse edilip diziye atılır
        char arrChar[] = kimlikNo.toCharArray();
        int[] arr = new int[arrChar.length];
        for (int i = 0; i < arrChar.length; i++) {
            arr[i] = Integer.parseInt(arrChar[i] + "");
        }
        // 3. kural son rakam tek olamaz. çift sayı kontrolü için mod 2 uygulanır ve kalan o dan farklı ise geçersiz sayılır.
        if (arr[10] % 2 != 0) {
            return false;
        }
        // 4. kural ilk dokuz rakamdan tek haneler ile çift haneler toplamı
        for (int i = 0; i < 9; i++) {
            toplam += arr[i];
            if ((i % 2) == 0) {
                // TC kimlik numarasının 1,3,5,7,9 toplamı
                tekHaneler += arr[i];
            } else {
                //TC kimlik numarasının 2,4,6,8 toplamı
                ciftHaneler += arr[i];
            }
        }

        // ilk on hanenin toplamı 5.kuralda kullanacağız.
        toplam += arr[9];

        kural = ((tekHaneler * 7) - ciftHaneler);
        // 4. kurala göre kural değişkeninde kalan değerin mod 10'nu alındığında kalan T.C. Kimlik Numarasının onuncu hanesi vermeli. farklı ise şartı sağlamadığını gösterir
        if (kural % 10 != arr[9]) {
            return false;
        }
        // 5. kural T.C. Kimlik numarasının ilk on hanesinin toplamının mod 10'nundan kalan T.C. Kimlik numarasının 11. hanesine eşittir. Değilse şart sağlanmamış demektir.
        if ((toplam % 10) != arr[10]) {
            return false;
        }
        // yukarıda olumsuz bir durum yoksa demekki gelen T.C. Kimlik Numarası doğrudur.
        return true;
    }

    private void findblood(){
        ArrayList<UserModel>  findUserModels = db.getBenefactors(bloodGroupIndex, districtID);
        myHelper.createProgress();
        myHelper.showProgress("Kan aranıyor" ,"Lütfen bekleyiniz...");
        FragmentManager manager = getFragmentManager();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myHelper.closeProgress();
                if(findUserModels.size() ==0){
                    SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff0000"));
                    pDialog.setTitleText("Aradığınız kan bulunamamıştır.");
                    pDialog.setContentText("Bölgenizde belirlediğiniz kriterlerde kan bulunamamıştır. Farklı zamanlarda veya yakın çevrelerde de aramak kan bulmanıza yardımcı olabilir. ");
                    pDialog.setCancelable(false);
                    pDialog.setConfirmText("Tamam");
                    pDialog.show();

                }else{
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    ShowBenefactorFragment showBenefactorFragment = new ShowBenefactorFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("bloodGroupIndex", bloodGroupIndex);
                    mBundle.putInt("districtID", districtID);
                    mBundle.putString("finderBloodGSM", newUser.getPhoneNumber());
                    showBenefactorFragment.setArguments(mBundle);
                    transaction.replace(R.id.linLayScreen, showBenefactorFragment, null);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        }, 2000);
    }


}
