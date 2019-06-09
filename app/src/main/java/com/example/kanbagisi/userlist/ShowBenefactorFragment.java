package com.example.kanbagisi.userlist;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kanbagisi.MyHelper;
import com.example.kanbagisi.R;
import com.example.kanbagisi.database.Database;
import com.example.kanbagisi.user.UserModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowBenefactorFragment extends Fragment {

    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.linLayForList)
    LinearLayout linLayForList;
    @BindView(R.id.spinnerPage)
    Spinner spinnerPage;
    @BindView(R.id.tvBack)
    TextView tvBack;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.linLayForPage)
    LinearLayout linLayForPage;
    private View view;
    @BindView(R.id.rvBenefactorList)
    RecyclerView rvBenefactorList;
    BenefactorAdapter benefactorAdapter;
    Database db;
    int bloodGroupIndex, districtID;
    MyHelper myHelper;
    String finderBloodGSM;
    private ArrayAdapter<String> dataAdapterForPage;
    private ArrayList<String> pageList;
    ArrayList<UserModel> userModels;

    public ShowBenefactorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_benefactor, container, false);
        ButterKnife.bind(this, view);
        pageList = new ArrayList<String>();

        myHelper = new MyHelper(getActivity());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            districtID = getArguments().getInt("districtID");
            bloodGroupIndex = getArguments().getInt("bloodGroupIndex");
            finderBloodGSM = getArguments().getString("finderBloodGSM");
        }
        db = new Database(getActivity());
        userModels = db.getBenefactors(bloodGroupIndex, districtID);


        int count = userModels.size() / 5;
        for (int i = 0; i <= count; i++) {
            pageList.add((i + 1) + "");
        }
        if(count==0 || userModels.size()==5 ){
            linLayForPage.setVisibility(View.GONE);
        }else{
            linLayForPage.setVisibility(View.VISIBLE);
        }

        dataAdapterForPage = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, pageList);
        dataAdapterForPage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPage.setAdapter(dataAdapterForPage);



                linLayForList.setVisibility(View.VISIBLE);

                benefactorAdapter = new BenefactorAdapter(getActivity(), userModels, finderBloodGSM);
                rvBenefactorList.setAdapter(benefactorAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                rvBenefactorList.setLayoutManager(linearLayoutManager);



        return view;
    }


    @OnClick(R.id.tvBack)
    public void onTvBackClicked() {
        spinnerPage.setSelection(spinnerPage.getSelectedItemPosition()-1);
    }

    @OnClick(R.id.tvNext)
    public void onTvNextClicked() {
        spinnerPage.setSelection(spinnerPage.getSelectedItemPosition()+1);
    }

    @OnItemSelected(R.id.spinnerPage)
    public void onSpinnerItemSelected(int index) {

        BenefactorAdapter.pageIndex = index;
        benefactorAdapter = new BenefactorAdapter(getActivity(), userModels, finderBloodGSM);
        benefactorAdapter.notifyDataSetChanged();
        rvBenefactorList.setAdapter(benefactorAdapter);
        if(index==0)tvBack.setVisibility(View.INVISIBLE);
        if(index==pageList.size()-1)tvNext.setVisibility(View.INVISIBLE);
        if(index>0)tvBack.setVisibility(View.VISIBLE);
        if(index<pageList.size()-1)tvNext.setVisibility(View.VISIBLE);
    }
}
