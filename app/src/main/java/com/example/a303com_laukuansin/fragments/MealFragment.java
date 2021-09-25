package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.MealActivity;
import com.example.a303com_laukuansin.activities.MealDetailActivity;
import com.example.a303com_laukuansin.activities.SearchMealActivity;
import com.example.a303com_laukuansin.adapters.MealRecordAdapter;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.Meal;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.example.a303com_laukuansin.utilities.ProgressAnimation;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MealFragment extends BaseFragment {
    private String date;
    private User user;
    private RetrieveMealRecord _retrieveMealRecord = null;
    private LinearProgressIndicator _mealProgressBar;
    private TextView _mealProgressView, _breakfastProgressView, _lunchProgressView, _dinnerProgressView, _snackProgressView;
    private RecyclerView _breakfastRecyclerView, _lunchRecyclerView, _dinnerRecyclerView, _snackRecyclerView;
    private FirebaseFirestore database;
    private LinearLayout _breakfastLayout,_lunchLayout,_dinnerLayout,_snackLayout,_containerLayout;

    public MealFragment() {
        user = getSessionHandler().getUser();
    }

    public static MealFragment newInstance(String date) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putString(MealActivity.DATE_KEY, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(MealActivity.DATE_KEY)) {
                date = getArguments().getString(MealActivity.DATE_KEY, "");
            }
        }
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal, container, false);
        initialization(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(date, user);//load data
    }

    private void loadData(String date, User user) {
        if (_retrieveMealRecord == null) {
            _retrieveMealRecord = new RetrieveMealRecord(date, user);
            _retrieveMealRecord.execute();
        }
    }

    private void initialization(View view) {
        //bind view with id
        _mealProgressView = view.findViewById(R.id.mealProgressView);
        _mealProgressBar = view.findViewById(R.id.mealProgressBar);
        _breakfastProgressView = view.findViewById(R.id.breakfastProgress);
        _lunchProgressView = view.findViewById(R.id.lunchProgress);
        _dinnerProgressView = view.findViewById(R.id.dinnerProgress);
        _snackProgressView = view.findViewById(R.id.snackProgress);
        _breakfastRecyclerView = view.findViewById(R.id.breakfastRecyclerView);
        _lunchRecyclerView = view.findViewById(R.id.lunchRecyclerView);
        _dinnerRecyclerView = view.findViewById(R.id.dinnerRecyclerView);
        _snackRecyclerView = view.findViewById(R.id.snackRecyclerView);
        _breakfastLayout = view.findViewById(R.id.breakfastLayout);
        _lunchLayout = view.findViewById(R.id.lunchLayout);
        _dinnerLayout = view.findViewById(R.id.dinnerLayout);
        _snackLayout = view.findViewById(R.id.snackLayout);
        _containerLayout = view.findViewById(R.id.containerLayout);
        ImageView _addBreakfastButton = view.findViewById(R.id.addBreakfastMealButton);
        ImageView _addLunchButton = view.findViewById(R.id.addLunchMealButton);
        ImageView _addDinnerButton = view.findViewById(R.id.addDinnerMealButton);
        ImageView _addSnackButton = view.findViewById(R.id.addSnackMealButton);

        //initialize database
        database = FirebaseFirestore.getInstance();

        //setup recyclerview
        setupRecyclerView(_breakfastRecyclerView);
        setupRecyclerView(_lunchRecyclerView);
        setupRecyclerView(_dinnerRecyclerView);
        setupRecyclerView(_snackRecyclerView);

        //setup add meal click
        setupAddMealClick(_addBreakfastButton, "Breakfast");
        setupAddMealClick(_addLunchButton, "Lunch");
        setupAddMealClick(_addDinnerButton, "Dinner");
        setupAddMealClick(_addSnackButton, "Snack");
    }

    private class RetrieveMealRecord extends AsyncTask<Void, Void, Void> {
        private String date;
        private User user;
        private double totalCalories = 0, breakfastCalories = 0, lunchCalories = 0, dinnerCalories = 0, snackCalories = 0;
        private SweetAlertDialog _progressDialog;

        public RetrieveMealRecord(String date, User user) {
            this.date = date;
            this.user = user;
            _progressDialog = showProgressDialog("Loading...", getResources().getColor(R.color.green_A700));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //date format
            DateFormat format = new SimpleDateFormat("dd MMM yyyy");
            //if the date argument is "Today"
            if (date.equals("Today")) {
                date = format.format(new Date());//get current date
            }

            //set collection path
            String COLLECTION_PATH = String.format("MealRecords/%1$s/Records", user.getUID());

            //get the Collection reference
            //collection path = MealRecords/UID/Date
            CollectionReference collectionReference = database.collection(COLLECTION_PATH);
            //get the meal record
            collectionReference.whereEqualTo("date", date).get().addOnSuccessListener(queryDocumentSnapshots -> getActivity().runOnUiThread(() -> {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();

                //initialize the meal list
                List<Meal> _breakfastRecordList = new ArrayList<>();
                List<Meal> _lunchMealRecordList = new ArrayList<>();
                List<Meal> _dinnerMealRecordList = new ArrayList<>();
                List<Meal> _snackMealRecordList = new ArrayList<>();

                //loop the document
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Meal meal = new Meal();
                    Map<String, Object> documentMapData = document.getData();
                    meal.setMealRecordID(document.getId());
                    if (documentMapData.get("foodID") != null) {
                        meal.setNixItemID(documentMapData.get("foodID").toString());
                    }
                    if (documentMapData.get("foodBarcode") != null) {
                        meal.setFoodBarcode(documentMapData.get("foodBarcode").toString());
                    }
                    meal.setDate(documentMapData.get("date").toString());
                    meal.setCalories((double) documentMapData.get("calories"));
                    meal.setMealName(documentMapData.get("foodName").toString());
                    meal.setQuantity((double) documentMapData.get("quantity"));
                    meal.setMealType(documentMapData.get("mealType").toString());
                    meal.setServingUnit(documentMapData.get("servingUnit").toString());
                    meal.setFoodWeightInGram((double) documentMapData.get("foodWeight"));
                    if (documentMapData.get("foodImageURL") != null) {
                        meal.setFoodImageURL(documentMapData.get("foodImageURL").toString());
                    }

                    //switch case see the meal type
                    switch (meal.getMealType()) {
                        case "Breakfast": {
                            _breakfastRecordList.add(meal);
                            breakfastCalories += meal.getCalories();
                            break;
                        }
                        case "Lunch": {
                            _lunchMealRecordList.add(meal);
                            lunchCalories += meal.getCalories();
                            break;
                        }
                        case "Dinner": {
                            _dinnerMealRecordList.add(meal);
                            dinnerCalories += meal.getCalories();
                            break;
                        }
                        case "Snack": {
                            _snackMealRecordList.add(meal);
                            snackCalories += meal.getCalories();
                            break;
                        }
                        default: {
                            Log.d("Error:", "Unknown class");
                            break;
                        }
                    }
                }
                //get total calories
                totalCalories = breakfastCalories + lunchCalories + dinnerCalories + snackCalories;

                //set progress bar
                _mealProgressBar.setMax((int) Math.round(user.getDailyCaloriesEaten()));
                _mealProgressBar.clearAnimation();

                //create animation, from 0 animate to current value
                ProgressAnimation animation = new ProgressAnimation(_mealProgressBar, 0, (int) Math.round(totalCalories));
                animation.setDuration(1000);//set 2 milliseconds animation
                _mealProgressBar.setAnimation(animation);//start animation


                _mealProgressView.setText(String.format("%1$s of %2$s Calories Eaten", (int) Math.round(totalCalories), (int) Math.round(user.getDailyCaloriesEaten())));
                _breakfastProgressView.setText(String.format("%1$s/%2$s Calories", (int) Math.round(breakfastCalories), (int) Math.round(user.getSuggestBreakfastCalorieEaten())));
                _lunchProgressView.setText(String.format("%1$s/%2$s Calories", (int) Math.round(lunchCalories), (int) Math.round(user.getSuggestLunchCalorieEaten())));
                _dinnerProgressView.setText(String.format("%1$s/%2$s Calories", (int) Math.round(dinnerCalories), (int) Math.round(user.getSuggestDinnerCalorieEaten())));
                _snackProgressView.setText(String.format("%1$s/%2$s Calories", (int) Math.round(snackCalories), (int) Math.round(user.getSuggestSnackCalorieEaten())));

                //show recyclerview
                _breakfastRecyclerView.setVisibility(View.VISIBLE);
                _lunchRecyclerView.setVisibility(View.VISIBLE);
                _dinnerRecyclerView.setVisibility(View.VISIBLE);
                _snackRecyclerView.setVisibility(View.VISIBLE);

                //setup the adapter for each recyclerview
                setupAdapter(_breakfastRecordList, _breakfastRecyclerView);
                setupAdapter(_lunchMealRecordList, _lunchRecyclerView);
                setupAdapter(_dinnerMealRecordList, _dinnerRecyclerView);
                setupAdapter(_snackMealRecordList, _snackRecyclerView);

                //setup each meal layout click
                if(_breakfastRecordList.isEmpty())
                {
                    _breakfastRecyclerView.setVisibility(View.GONE);
                    _breakfastLayout.setEnabled(false);
                }
                else{
                    _breakfastLayout.setEnabled(true);
                    _breakfastRecyclerView.setVisibility(View.VISIBLE);
                    setupMealLayoutClick(_breakfastLayout, _containerLayout, _breakfastRecyclerView);
                }
                //if lunch record is empty
                if(_lunchMealRecordList.isEmpty())
                {
                    _lunchRecyclerView.setVisibility(View.GONE);
                    _lunchLayout.setEnabled(false);
                }
                else{
                    _lunchRecyclerView.setVisibility(View.VISIBLE);
                    _lunchLayout.setEnabled(true);
                    setupMealLayoutClick(_lunchLayout, _containerLayout, _lunchRecyclerView);
                }
                //if dinner record is empty
                if(_dinnerMealRecordList.isEmpty())
                {
                    _dinnerRecyclerView.setVisibility(View.GONE);
                    _dinnerLayout.setEnabled(false);
                }
                else{
                    _dinnerRecyclerView.setVisibility(View.VISIBLE);
                    _dinnerLayout.setEnabled(true);
                    setupMealLayoutClick(_dinnerLayout, _containerLayout, _dinnerRecyclerView);
                }
                //if snack record is empty
                if(_snackMealRecordList.isEmpty())
                {
                    _snackRecyclerView.setVisibility(View.GONE);
                    _snackLayout.setEnabled(false);
                }
                else{
                    _snackRecyclerView.setVisibility(View.VISIBLE);
                    _snackLayout.setEnabled(true);
                    setupMealLayoutClick(_snackLayout, _containerLayout, _snackRecyclerView);
                }

            })).addOnFailureListener(e -> {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();
                //show error with dialog
                ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                _retrieveMealRecord = null;
            });

            _retrieveMealRecord = null;

            return null;
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void setupAdapter(List<Meal> list, RecyclerView recyclerView) {
        MealRecordAdapter adapter = new MealRecordAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setupMealLayoutClick(LinearLayout mealLayout, LinearLayout containerLayout, RecyclerView recyclerView) {
        mealLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                TransitionManager.beginDelayedTransition(containerLayout, new AutoTransition());//delay the transition
                if (recyclerView.getVisibility() == View.VISIBLE) {//if recycler view is shown
                    recyclerView.setVisibility(View.GONE);//hide the recycler view
                } else {//if recycler view is no shown
                    recyclerView.setVisibility(View.VISIBLE);//shown the recycler view
                }
            }
        });
    }

    private void setupAddMealClick(ImageView button, String mealType) {
        button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), SearchMealActivity.class);
                intent.putExtra(SearchMealActivity.DATE_KEY, date);
                intent.putExtra(SearchMealActivity.MEAL_TYPE_KEY, mealType);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void editMealRecord(Meal meal) {
        Intent intent = new Intent(getContext(), MealDetailActivity.class);
        intent.putExtra(MealDetailActivity.DATE_KEY, date);
        intent.putExtra(MealDetailActivity.MEAL_TYPE_KEY, meal.getMealType());
        if (!meal.getNixItemID().isEmpty()) {
            intent.putExtra(MealDetailActivity.FOOD_ID_KEY, meal.getNixItemID());
        }
        if (!meal.getFoodBarcode().isEmpty()) {
            intent.putExtra(MealDetailActivity.FOOD_BARCODE_KEY, meal.getFoodBarcode());
        }
        if (!meal.getFoodImageURL().isEmpty()) {
            intent.putExtra(MealDetailActivity.FOOD_IMAGE_URL_KEY, meal.getFoodImageURL());
        }
        intent.putExtra(MealDetailActivity.MEAL_RECORD_ID_KEY, meal.getMealRecordID());
        intent.putExtra(MealDetailActivity.FOOD_NAME_KEY, meal.getMealName());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
