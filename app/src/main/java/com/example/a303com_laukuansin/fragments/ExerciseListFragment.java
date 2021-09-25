package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.ExerciseDetailActivity;
import com.example.a303com_laukuansin.activities.ExerciseListActivity;
import com.example.a303com_laukuansin.adapters.ExerciseListAdapter;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.Exercise;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ExerciseListFragment extends BaseFragment {
    private String date;
    private RecyclerView _exerciseListRecyclerView;
    private RetrieveExerciseList _retrieveExerciseList = null;

    public ExerciseListFragment() {
    }

    public static ExerciseListFragment newInstance(String date) {
        ExerciseListFragment fragment = new ExerciseListFragment();
        Bundle args = new Bundle();
        args.putString(ExerciseListActivity.DATE_KEY, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ExerciseListActivity.DATE_KEY)) {
                date = getArguments().getString(ExerciseListActivity.DATE_KEY, "");
            }
        }
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);
        initialization(view);
        loadData();
        return view;
    }

    private void initialization(View view)
    {
        //bind view with id
        _exerciseListRecyclerView = view.findViewById(R.id.exerciseListRecyclerView);

        //setup recyclerview
        _exerciseListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _exerciseListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        _exerciseListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    }

    private void loadData()
    {
        if(_retrieveExerciseList==null)
        {
            _retrieveExerciseList = new RetrieveExerciseList();
            _retrieveExerciseList.execute();
        }
    }

    private class RetrieveExerciseList extends AsyncTask<Void,Void,Void>
    {
        private SweetAlertDialog _progressDialog;

        public RetrieveExerciseList() {
            _progressDialog = showProgressDialog("Loading...",getResources().getColor(R.color.yellow_900));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            //get the Collection reference
            CollectionReference collectionReference = database.collection("ExerciseList");
            //get the exercise list which sort by exercise name
            collectionReference.orderBy("exerciseName").addSnapshotListener((value, error) -> {
                if(_progressDialog.isShowing())
                    _progressDialog.dismiss();

                if(error!=null)
                {
                    //show error with dialog
                    ErrorAlert(error.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                    _retrieveExerciseList = null;
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    List<Exercise> _exerciseList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot: value.getDocuments())
                    {
                        Exercise exercise = new Exercise();
                        Map<String, Object> documentMapData = documentSnapshot.getData();
                        exercise.setExerciseID(documentSnapshot.getId());
                        exercise.setExerciseName(documentMapData.get("exerciseName").toString());
                        exercise.setExerciseIcon(documentMapData.get("iconURL").toString());
                        _exerciseList.add(exercise);
                    }
                    ExerciseListAdapter adapter = new ExerciseListAdapter( getContext(),_exerciseList);
                    _exerciseListRecyclerView.setAdapter(adapter);

                });
            });
            return null;
        }
    }

    public void selectExercise(Exercise exercise)
    {
        Intent intent = new Intent(getContext(), ExerciseDetailActivity.class);
        intent.putExtra(ExerciseDetailActivity.DATE_KEY,date);
        intent.putExtra(ExerciseDetailActivity.EXERCISE_ID_KEY, exercise.getExerciseID());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
