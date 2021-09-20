package com.example.a303com_laukuansin.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.adapters.ServingUnitAdapter;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.ServingUnit;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ServingUnitHelpFragment extends BaseFragment {
    private RecyclerView _recyclerView;
    private FirebaseFirestore database;
    private RetrieveServingUnit _retrieveServingUnit = null;

    public ServingUnitHelpFragment() {
    }

    public static ServingUnitHelpFragment newInstance() {
        return new ServingUnitHelpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serving_unit_help, container, false);
        initialization(view);
        //load data
        loadData();
        return view;
    }

    private void initialization(View view) {
        //bind view with id
        _recyclerView = view.findViewById(R.id.recyclerView);

        //setup recycler View
        _recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        _recyclerView.setItemAnimator(new DefaultItemAnimator());

        //initialize database
        database = FirebaseFirestore.getInstance();
    }

    private void loadData() {
        if (_retrieveServingUnit == null) {
            _retrieveServingUnit = new RetrieveServingUnit();
            _retrieveServingUnit.execute();
        }
    }

    private class RetrieveServingUnit extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() ->
                    database.collection("ServingUnitList").orderBy("servingUnitName").addSnapshotListener((value, error) -> {
                        if (error != null) {
                            ErrorAlert(error.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                            return;
                        }
                        List<ServingUnit> _servingUnitList = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            ServingUnit servingUnit = new ServingUnit();
                            Map<String, Object> documentMetaData = documentSnapshot.getData();
                            if (documentMetaData.get("servingUnitName") != null)
                                servingUnit.setServingUnitName(documentMetaData.get("servingUnitName").toString());
                            if (documentMetaData.get("imageURL") != null)
                                servingUnit.setImageUrl(documentMetaData.get("imageURL").toString());
                            if (documentMetaData.get("description") != null)
                                servingUnit.setDescription(documentMetaData.get("description").toString());
                            _servingUnitList.add(servingUnit);
                        }
                        ServingUnitAdapter adapter = new ServingUnitAdapter(getContext(), _servingUnitList);
                        _recyclerView.setAdapter(adapter);
                    }));
            return null;
        }
    }
}
