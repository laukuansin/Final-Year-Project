package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.BodyWeightActivity;
import com.example.a303com_laukuansin.activities.BodyWeightDetailActivity;
import com.example.a303com_laukuansin.adapters.BodyWeightRecordAdapter;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.BodyWeight;
import com.example.a303com_laukuansin.domains.Exercise;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BodyWeightFragment extends BaseFragment {
    private String date;
    private FirebaseFirestore database;
    private RetrieveBodyWeightRecords _retrieveRecords = null;
    private RecyclerView _bodyWeightRecyclerView;
    private TextView _bodyWeightProgressView;
    private final DateFormat format = new SimpleDateFormat("dd MMM yyyy");

    public BodyWeightFragment() {
    }

    public static BodyWeightFragment newInstance(String date) {
        BodyWeightFragment fragment = new BodyWeightFragment();
        Bundle args = new Bundle();
        args.putString(BodyWeightActivity.DATE_KEY, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(BodyWeightActivity.DATE_KEY)) {
                date = getArguments().getString(BodyWeightActivity.DATE_KEY, "");
            }
        }
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_weight, container, false);
        initialization(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //load body weight data
        loadData();
    }

    private void loadData()
    {
        if(_retrieveRecords==null)
        {
            _retrieveRecords = new RetrieveBodyWeightRecords();
            _retrieveRecords.execute();
        }
    }

    private void initialization(View view)
    {
        //bind view with id
        Toolbar _toolbar = view.findViewById(R.id.toolbar);
        _bodyWeightRecyclerView = view.findViewById(R.id.bodyWeightRecyclerView);
        _bodyWeightProgressView = view.findViewById(R.id.bodyWeightProgressView);
        Button _addWeightRecord = view.findViewById(R.id.addWeightRecord);

        //setup recyclerview
        _bodyWeightRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _bodyWeightRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //initialize database
        database = FirebaseFirestore.getInstance();
        //setup toolbar
        setupToolbar(_toolbar);

        //when click track weight button
        _addWeightRecord.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), BodyWeightDetailActivity.class);
                intent.putExtra(BodyWeightDetailActivity.DATE_KEY,date);
                startActivity(intent);
                //add animation sliding to next activity
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }

    private void setupToolbar(Toolbar toolbar) {
        //set support action bar
        ((BodyWeightActivity) getActivity()).setSupportActionBar(toolbar);
        //set back button
        ((BodyWeightActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set name to empty
        ((BodyWeightActivity) getActivity()).getSupportActionBar().setTitle("");
    }

    public void editBodyWeightRecord(BodyWeight bodyWeight)
    {
        Intent intent = new Intent(getContext(), BodyWeightDetailActivity.class);
        //if the record date is today
        if(format.format(new Date()).equals(bodyWeight.getDate()))
        {
            intent.putExtra(BodyWeightDetailActivity.DATE_KEY, "Today");
        }
        else{
            intent.putExtra(BodyWeightDetailActivity.DATE_KEY, bodyWeight.getDate());
        }
        intent.putExtra(BodyWeightDetailActivity.BODY_WEIGHT_RECORD_ID_KEY, bodyWeight.getBodyWeightRecordID());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private class RetrieveBodyWeightRecords extends AsyncTask<Void,Void,Void>
    {
        private User user;
        private List<BodyWeight> _bodyWeightList;

        public RetrieveBodyWeightRecords() {
            this.user = getSessionHandler().getUser();
            _bodyWeightList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                //set body weight collection path
                String BODY_WEIGHT_COLLECTION_PATH = String.format("BodyWeightRecords/%1$s/Records", user.getUID());
                //get the body weight record collection reference
                //collection path = BodyWeightRecords/UID/Records
                CollectionReference bodyWeightCollectionReference = database.collection(BODY_WEIGHT_COLLECTION_PATH);
                bodyWeightCollectionReference.addSnapshotListener((value, error) -> {
                    if (error!=null)
                    {
                        ErrorAlert(error.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                        _retrieveRecords = null;
                        return;
                    }

                    //loop all the body weight record
                    for(DocumentSnapshot documentSnapshot:value.getDocuments())
                    {
                        //create body weight class
                        BodyWeight bodyWeight = new BodyWeight();
                        Map<String, Object> documentMapData = documentSnapshot.getData();
                        bodyWeight.setBodyWeightRecordID(documentSnapshot.getId());
                        bodyWeight.setBodyWeight((double) documentMapData.get("bodyWeight"));
                        bodyWeight.setDate(documentMapData.get("date").toString());

                        _bodyWeightList.add(bodyWeight);
                    }
                    //sort the body weight in descending
                    Collections.sort(_bodyWeightList, BodyWeight.bodyWeightDescComparator);

                    switch (user.getTargetGoal()) {
                        case "Maintain Weight":
                            _bodyWeightProgressView.setText(String.format("Maintain %.2f kg", user.getTargetWeight()));
                            break;
                        case "Lose Weight": {
                            double weightToLost = user.getStartWeight() - user.getTargetWeight();//start:80, target :60

                            double progressWeight = user.getStartWeight() - user.getWeight();//current:80

                            //if the progress weight is less than 0, change to 0
                            if (progressWeight < 0) {
                                progressWeight = 0;
                            }
                            _bodyWeightProgressView.setText(String.format("%1$,.1f of %2$,.1f Lost", progressWeight, weightToLost));
                            break;
                        }
                        case "Gain Weight": {
                            double weightToGain = user.getTargetWeight() - user.getStartWeight();//start: 70 target 80

                            double progressWeight = user.getWeight() - user.getStartWeight();//current: 70

                            //if the progress weight is less than 0, change to 0
                            if (progressWeight < 0) {
                                progressWeight = 0;
                            }
                            _bodyWeightProgressView.setText(String.format("%1$,.1f of %2$,.1f Gained", progressWeight, weightToGain));
                            break;
                        }
                    }
                    //setup adapter
                    BodyWeightRecordAdapter adapter = new BodyWeightRecordAdapter(getContext(),_bodyWeightList);
                    _bodyWeightRecyclerView.setAdapter(adapter);
                });
                _retrieveRecords = null;
            });
            return null;
        }
    }
}
