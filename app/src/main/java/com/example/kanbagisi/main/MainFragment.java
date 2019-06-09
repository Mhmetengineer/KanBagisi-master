package com.example.kanbagisi.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.kanbagisi.MyHelper;
import com.example.kanbagisi.R;
import com.example.kanbagisi.database.Database;
import com.example.kanbagisi.user.UserFragment;
import com.example.kanbagisi.service.ApiService;
import com.example.kanbagisi.service.UtilsApi;
import com.example.kanbagisi.userlist.ShowBenefactorFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    @BindView(R.id.btnTransfuseBlood)
    Button btnTransfuseBlood;
    @BindView(R.id.btnFindBlood)
    Button btnFindBlood;
    @BindView(R.id.cbHaveAccount)
    CheckBox cbHaveAccount;
    @BindView(R.id.linLayHaveAccount)
    LinearLayout linLayHaveAccount;
    @BindView(R.id.edtTC)
    EditText edtTC;
    @BindView(R.id.btnLoginBenefactor)
    Button btnLoginBenefactor;
    @BindView(R.id.btnLoginFindBlood)
    Button btnLoginFindBlood;
    Database db;
    MyHelper myHelper;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        cbControl();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        db=new Database(getActivity());
        myHelper=new MyHelper(getActivity());
        return view;
    }

    private void cbControl(){
        if (cbHaveAccount.isChecked()) linLayHaveAccount.setVisibility(View.VISIBLE);
        else
            linLayHaveAccount.setVisibility(View.GONE);
    }

    @OnClick(R.id.cbHaveAccount)
    public void onViewClicked() {
       cbControl();
    }

    @OnClick(R.id.btnTransfuseBlood)
    public void onBtnTransfuseBloodClicked() {

        createNewUser(true,null);
    }

    @OnClick(R.id.btnFindBlood)
    public void onBtnFindBloodClicked() {
        createNewUser(false,null);
    }



    public void createNewUser(Boolean isBenefactor,String TC) {
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("isBenefactor", isBenefactor);
        if(TC!=null)
        mBundle.putString("TC", TC);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment newUserFragment = new UserFragment();
        newUserFragment.setArguments(mBundle);
        transaction.replace(R.id.linLayScreen, newUserFragment, null);
        transaction.addToBackStack(null);
        transaction.commit();
        //myHelper.closeKeyboard(getActivity());
    }

    private boolean inputControl(String TC){

        if(TC.length()!=11) {
            edtTC.setError("Lütfen 11 haneli TC bilginizi giriniz");
           return false;
        }
        return true;
    }

    @OnClick(R.id.btnLoginBenefactor)
    public void onBtnLoginBenefactorClicked() {
        if(inputControl(edtTC.getText().toString().trim())){
        if(!db.haveUserControl(edtTC.getText().toString().trim(),1)){
          createNewUser(true,edtTC.getText().toString());
        }else{
            Toast.makeText(getActivity(), "Kan veren kaydınız bulunamamıştır.", Toast.LENGTH_SHORT).show();
        }
            myHelper.closeKeyboard(getActivity());
        }
    }

    @OnClick(R.id.btnLoginFindBlood)
    public void onBtnLoginFindBloodClicked() {
        if(inputControl(edtTC.getText().toString().trim())){
        if(!db.haveUserControl(edtTC.getText().toString().trim(),0)){
            createNewUser(false,edtTC.getText().toString());
        }else{
            Toast.makeText(getActivity(), "Kan arayan kaydınız bulunamamıştır.", Toast.LENGTH_SHORT).show();
        }
    }
        myHelper.closeKeyboard(getActivity());}
}
