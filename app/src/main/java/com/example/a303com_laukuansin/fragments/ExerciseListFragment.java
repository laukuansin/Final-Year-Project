package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.ExerciseDetailActivity;
import com.example.a303com_laukuansin.activities.ExerciseListActivity;
import com.example.a303com_laukuansin.adapters.ExerciseListAdapter;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.Exercise;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
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
    private ExerciseListAdapter adapter;
    private String date;
    private List<Exercise> _exerciseList;
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
        setHasOptionsMenu(true);
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
        //initialize the list
        _exerciseList = new ArrayList<>();
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
            CollectionReference collectionReference = database.collection("Exercises");
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

                    for (DocumentSnapshot documentSnapshot: value.getDocuments())
                    {
                        Exercise exercise = new Exercise();
                        Map<String, Object> documentMapData = documentSnapshot.getData();
                        exercise.setExerciseID(documentSnapshot.getId());
                        exercise.setExerciseName(documentMapData.get("exerciseName").toString());
                        exercise.setCaloriesBurnedPerKGPerMin((double) documentMapData.get("caloriesPerKGPerMin"));
                        exercise.setExerciseIcon(documentMapData.get("iconURL").toString());
                        _exerciseList.add(exercise);
                    }
                    adapter = new ExerciseListAdapter( getContext(),_exerciseList,getSessionHandler().getUser());
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

    public void openFilterDialog()
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_filter);
        LinearLayout _AtoZLayout = bottomSheetDialog.findViewById(R.id.AtoZSortLayout);
        LinearLayout _ZtoALayout = bottomSheetDialog.findViewById(R.id.ZtoASortLayout);
        LinearLayout _HighToLowLayout = bottomSheetDialog.findViewById(R.id.highToLowSortLayout);
        LinearLayout _LowToHighLayout = bottomSheetDialog.findViewById(R.id.lowToHighSortLayout);

        _AtoZLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //sort A to Z
                Collections.sort(_exerciseList,Exercise.exerciseNameAZComparator);
                adapter.notifyDataSetChanged();
                bottomSheetDialog.cancel();
            }
        });

        _ZtoALayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //sort Z to A
                Collections.sort(_exerciseList,Exercise.exerciseNameZAComparator);
                adapter.notifyDataSetChanged();
                bottomSheetDialog.cancel();
            }
        });

        _HighToLowLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //sort high to low of calories burnt
                Collections.sort(_exerciseList,Exercise.exerciseCaloriesHighToLowComparator);
                adapter.notifyDataSetChanged();
                bottomSheetDialog.cancel();
            }
        });

        _LowToHighLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //sort low to high of calories burnt
                Collections.sort(_exerciseList,Exercise.exerciseCaloriesLowToHighComparator);
                adapter.notifyDataSetChanged();
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }
}
