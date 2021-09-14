package com.example.a303com_laukuansin.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.MealActivity;
import com.example.a303com_laukuansin.activities.MealDetailActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.FoodDetail;
import com.example.a303com_laukuansin.domains.Measure;
import com.example.a303com_laukuansin.domains.FoodPhoto;
import com.example.a303com_laukuansin.requests.MealDetailRequest;
import com.example.a303com_laukuansin.responses.MealDetailResponse;
import com.example.a303com_laukuansin.utilities.ApiClient;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.example.a303com_laukuansin.utilities.QuantityValueFilter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealDetailFragment extends BaseFragment {
    private String date,mealType,foodName,foodID,mealRecordID,foodBarcode;
    private double calories, proteins, carbs, fiber, fats, grams;
    private double caloriesMultipliers, proteinsMultipliers, carbsMultipliers, fiberMultipliers, fatsMultipliers;
    private TextView _foodNameView, _foodBrandView, _caloriesView, _proteinsView, _carbohydratesView, _fatsView, _fiberView;
    private TextInputLayout _inputQuantity;
    private AutoCompleteTextView _autoCompleteServing;
    private ImageView _foodImage, _proteinIcon, _fatIcon, _carbsIcon, _fiberIcon;
    private Map<String, Double> servingUnitMap;
    private RetrieveCommonFoodDetail _getCommonFoodDetail = null;
    private RetrieveBrandedFoodDetail _getBrandedFoodDetail = null;
    private RetrieveBarcodeFoodDetail _getBarcodeFoodDetail = null;
    private FirebaseFirestore database;
    private Button _addButton,_deleteButton,_updateButton;
    private DateFormat format = new SimpleDateFormat("dd MMM yyyy");

    public MealDetailFragment() {
    }

    public static MealDetailFragment newInstance(String date, String mealType, String foodName, String foodID,String mealRecordID,String foodBarcode) {
        MealDetailFragment fragment = new MealDetailFragment();
        Bundle args = new Bundle();
        args.putString(MealDetailActivity.DATE_KEY, date);
        args.putString(MealDetailActivity.MEAL_TYPE_KEY, mealType);
        args.putString(MealDetailActivity.FOOD_ID_KEY, foodID);
        args.putString(MealDetailActivity.FOOD_NAME_KEY, foodName);
        args.putString(MealDetailActivity.MEAL_RECORD_ID_KEY, mealRecordID);
        args.putString(MealDetailActivity.FOOD_BARCODE_KEY, foodBarcode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(MealDetailActivity.DATE_KEY)) {
                date = getArguments().getString(MealDetailActivity.DATE_KEY, "");
            }
            if (getArguments().containsKey(MealDetailActivity.MEAL_TYPE_KEY)) {
                mealType = getArguments().getString(MealDetailActivity.MEAL_TYPE_KEY, "");
            }
            if (getArguments().containsKey(MealDetailActivity.FOOD_NAME_KEY)) {
                foodName = getArguments().getString(MealDetailActivity.FOOD_NAME_KEY, "");
            }
            if (getArguments().containsKey(MealDetailActivity.FOOD_ID_KEY)) {
                foodID = getArguments().getString(MealDetailActivity.FOOD_ID_KEY, "");
            }
            if (getArguments().containsKey(MealDetailActivity.MEAL_RECORD_ID_KEY)) {
                mealRecordID = getArguments().getString(MealDetailActivity.MEAL_RECORD_ID_KEY, "");
            }
            if (getArguments().containsKey(MealDetailActivity.FOOD_BARCODE_KEY)) {
                foodBarcode = getArguments().getString(MealDetailActivity.FOOD_BARCODE_KEY, "");
            }
        }
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_detail, container, false);

        initialization(view);
        //the reason to differentiate is because the API to get common food and branded food is different
        if (!foodID.isEmpty()) {
            loadBrandedFood(foodID);
        }
        else if(!foodBarcode.isEmpty())
        {
            loadBarcodeFood(foodBarcode);
        }
        else{
            loadCommonFood(foodName);
        }
        return view;
    }


    private void initialization(View view) {
        //bind view with ID
        _foodImage = view.findViewById(R.id.foodImage);
        _foodNameView = view.findViewById(R.id.foodName);
        _foodBrandView = view.findViewById(R.id.foodBrand);
        _caloriesView = view.findViewById(R.id.totalCalories);
        TextInputEditText _quantityEditText = view.findViewById(R.id.quantityEditText);
        _inputQuantity = view.findViewById(R.id.quantityLayout);
        _autoCompleteServing = view.findViewById(R.id.servingUnitAutoComplete);
        _proteinsView = view.findViewById(R.id.proteinsView);
        _carbohydratesView = view.findViewById(R.id.carbsView);
        _fatsView = view.findViewById(R.id.fatsView);
        _fiberView = view.findViewById(R.id.fiberView);
        _proteinIcon = view.findViewById(R.id.proteinIcon);
        _fatIcon = view.findViewById(R.id.fatIcon);
        _fiberIcon = view.findViewById(R.id.fiberIcon);
        _carbsIcon = view.findViewById(R.id.carbsIcon);
        _addButton = view.findViewById(R.id.addButton);
        _updateButton = view.findViewById(R.id.updateButton);
        _deleteButton = view.findViewById(R.id.deleteButton);

        //initialize database
        database = FirebaseFirestore.getInstance();

        //initialize map
        servingUnitMap = new HashMap<>();

        //set quantity input filter, max digit before dot is 4, max digit after dot is 1, and max value is 5000
        _quantityEditText.setFilters(new InputFilter[]{new QuantityValueFilter(4, 1, 5000)});
        //add the text changed listener
        _quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //before text typing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    //disable add and update button
                    _addButton.setAlpha(0.2f);
                    _addButton.setEnabled(false);
                    _updateButton.setAlpha(0.2f);
                    _updateButton.setEnabled(false);
                }
                else{
                    //enable add and update button
                    _addButton.setAlpha(1.0f);
                    _addButton.setEnabled(true);
                    _updateButton.setAlpha(1.0f);
                    _updateButton.setEnabled(true);
                    calculateFoodNutrition();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _addButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                saveMeal();
            }
        });
        _updateButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                updateMeal();
            }
        });
        _deleteButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                SweetAlertDialog warningDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                warningDialog.setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this record!")
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(sweetAlertDialog -> deleteMeal(sweetAlertDialog))
                        .setCancelClickListener(Dialog::dismiss).show();
            }
        });

        //when choose the drop down
        _autoCompleteServing.setOnItemClickListener((adapterView, view1, position, l) -> {
            if(!_inputQuantity.getEditText().getText().toString().isEmpty())
                calculateFoodNutrition();
        });
    }

    private void loadCommonFood(String foodName) {
        //load the common food detail
        if (_getCommonFoodDetail == null) {
            _getCommonFoodDetail = new RetrieveCommonFoodDetail(foodName);
            _getCommonFoodDetail.execute();
        }
    }

    private void loadBrandedFood(String foodID) {
        //load the branded food detail
        if (_getBrandedFoodDetail == null) {
            _getBrandedFoodDetail = new RetrieveBrandedFoodDetail(foodID);
            _getBrandedFoodDetail.execute();
        }
    }

    private void loadBarcodeFood(String foodBarcode) {
        //load the barcode food detail
        if (_getBarcodeFoodDetail == null) {
            _getBarcodeFoodDetail = new RetrieveBarcodeFoodDetail(foodBarcode);
            _getBarcodeFoodDetail.execute();
        }
    }

    private void saveMeal() {
        String servingUnit = _autoCompleteServing.getText().toString();
        double quantity = 0;
        boolean check = true;
        if (servingUnit.isEmpty()) {//if serving unit is empty
            ErrorAlert("Please select one serving unit.", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
            check = false;
        }
        String quantityStr = _inputQuantity.getEditText().getText().toString();
        if (quantityStr.isEmpty()) {//if quantity is empty
            ErrorAlert("Quantity cannot be empty!", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
            check = false;
        } else {
            quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {//if(quantity is 0
                ErrorAlert("Quantity less than 0!", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                check = false;
            }
        }

        if (check)//if no error
        {
            //add meal record to database
            addMealRecordToDatabase(servingUnit,quantity);
        }
    }
    private void updateMeal()
    {
        String servingUnit = _autoCompleteServing.getText().toString();
        double quantity = 0;
        boolean check = true;
        if (servingUnit.isEmpty()) {//if serving unit is empty
            ErrorAlert("Please select one serving unit.", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
            check = false;
        }
        String quantityStr = _inputQuantity.getEditText().getText().toString();
        if (quantityStr.isEmpty()) {//if quantity is empty
            ErrorAlert("Quantity cannot be empty!", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
            check = false;
        } else {
            quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {//if quantity is 0
                ErrorAlert("Quantity less than 0!", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                check = false;
            }
        }

        if(check)//if no error
        {
            //add meal record to database
            updateMealRecordToDatabase(servingUnit,quantity);
        }
    }
    private void deleteMeal(SweetAlertDialog sweetAlertDialog)
    {
        sweetAlertDialog.dismiss();
        //create progress dialog
        SweetAlertDialog _progressDialog = showProgressDialog("Deleting...",getResources().getColor(R.color.green_A700));
        _progressDialog.show();

        //if the date argument is "Today"
        if(date.equals("Today"))
        {
            date = format.format(new Date());//get current date
        }
        //the document path, example: MealRecords/UID/Records/MealRecordID
        String DOCUMENT_PATH = String.format("MealRecords/%1$s/Records/%2$s",getSessionHandler().getUser().getUID(),mealRecordID);
        //get meal record document
        DocumentReference mealRecordRef = database.document(DOCUMENT_PATH);
        mealRecordRef.delete().addOnSuccessListener(unused -> {
            if(_progressDialog.isShowing())
                _progressDialog.dismiss();
            Toast.makeText(getContext(), "Delete meal success", Toast.LENGTH_SHORT).show();
            //finish current activity
            getActivity().finish();
        }).addOnFailureListener(e -> {
            if(_progressDialog.isShowing())
                _progressDialog.dismiss();
            Log.d("Error:",e.getMessage());
        });
    }


    private void addMealRecordToDatabase(String servingUnit, double quantity)
    {
        //create progress dialog
        SweetAlertDialog _progressDialog = showProgressDialog("Adding...",getResources().getColor(R.color.green_A700));
        _progressDialog.show();

        //if the date argument is "Today"
        if(date.equals("Today"))
        {
            date = format.format(new Date());//get current date
        }
        //the collection path, example: MealRecords/UID/Records
        String COLLECTION_PATH = String.format("MealRecords/%1$s/Records",getSessionHandler().getUser().getUID());
        //get meal record reference
        CollectionReference mealRecordRef = database.collection(COLLECTION_PATH);
        //create meal record class
        Map<String, Object> mealRecordMap = new HashMap<>();//create hash map to store the meal record's data
        mealRecordMap.put("date",date);
        mealRecordMap.put("foodName",foodName);
        if(!foodID.isEmpty())
        {
            mealRecordMap.put("foodID",foodID);
        }
        if(!foodBarcode.isEmpty())
        {
            mealRecordMap.put("foodBarcode",foodBarcode);
        }
        mealRecordMap.put("quantity",quantity);
        mealRecordMap.put("servingUnit",servingUnit);
        mealRecordMap.put("calories",calories);
        mealRecordMap.put("foodWeight",grams);
        mealRecordMap.put("mealType",mealType);

        mealRecordRef.add(mealRecordMap).addOnSuccessListener(documentReference -> {
            if (_progressDialog.isShowing())//cancel dialog
                _progressDialog.dismiss();

            //toast success message
            Toast.makeText(getContext(), "Add Meal Success", Toast.LENGTH_SHORT).show();
            //intent to meal activity
            Intent intent = new Intent(getContext(), MealActivity.class);
            startActivity(intent);
            //finish current activity
            getActivity().finish();
        }).addOnFailureListener(e -> {
            if (_progressDialog.isShowing())//cancel dialog
                _progressDialog.dismiss();
            //show error dialog
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
        });
    }

    private void updateMealRecordToDatabase(String servingUnit, double quantity)
    {
        //create progress dialog
        SweetAlertDialog _progressDialog = showProgressDialog("Updating...",getResources().getColor(R.color.green_A700));
        _progressDialog.show();

        //if the date argument is "Today"
        if(date.equals("Today"))
        {
            date = format.format(new Date());//get current date
        }
        //the document path, example: MealRecords/UID/Records/MealRecordID
        String DOCUMENT_PATH = String.format("MealRecords/%1$s/Records/%2$s",getSessionHandler().getUser().getUID(),mealRecordID);
        //get meal record document
        DocumentReference mealRecordRef = database.document(DOCUMENT_PATH);
        //create meal record class
        Map<String, Object> mealRecordMap = new HashMap<>();//create hash map to store the meal record's data
        mealRecordMap.put("quantity",quantity);
        mealRecordMap.put("servingUnit",servingUnit);
        mealRecordMap.put("calories",calories);
        mealRecordMap.put("foodWeight",grams);

        mealRecordRef.update(mealRecordMap).addOnSuccessListener(documentReference -> {
            if (_progressDialog.isShowing())//cancel dialog
                _progressDialog.dismiss();

            //toast success message
            Toast.makeText(getContext(), "Update Meal Success", Toast.LENGTH_SHORT).show();
            //finish current activity
            getActivity().finish();
        }).addOnFailureListener(e -> {
            if (_progressDialog.isShowing())//cancel dialog
                _progressDialog.dismiss();
            //show error dialog
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
        });

    }

    private class RetrieveCommonFoodDetail extends AsyncTask<Void, Void, Void> {
        //progress dialog
        private SweetAlertDialog _progressDialog;
        private MealDetailRequest _request;//create meal detail request which is POST

        public RetrieveCommonFoodDetail(String foodName) {
            _progressDialog = showProgressDialog("Loading...",getResources().getColor(R.color.green_A700));
            _request = new MealDetailRequest(foodName);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress dialog
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Call<MealDetailResponse> callCommonFoodDetailAPI = ApiClient.getNutritionixService().getCommonFoodDetail(_request);
            callCommonFoodDetailAPI.enqueue(new Callback<MealDetailResponse>() {
                @Override
                public void onResponse(Call<MealDetailResponse> call, Response<MealDetailResponse> response) {
                    //close progress dialog
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        if (response.body().foodDetail != null) {
                            getActivity().runOnUiThread(() -> setupFoodDetail(response.body().foodDetail[0]));
                        } else {
                            _getCommonFoodDetail = null;
                            //if API call failure
                            final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.body().message) ? response.body().message : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                            snackbar.show();
                        }
                    } else {
                        _getCommonFoodDetail = null;
                        //if API call failure
                        final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.message()) ? response.message() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<MealDetailResponse> call, Throwable t) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    //if API call failure
                    final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(t.getMessage()) ? t.getMessage() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                    snackbar.show();
                    _getCommonFoodDetail = null;
                }
            });
            return null;
        }
    }

    private class RetrieveBrandedFoodDetail extends AsyncTask<Void, Void, Void> {
        //progress dialog
        private SweetAlertDialog _progressDialog;
        private String foodID;//create meal detail request which is POST

        public RetrieveBrandedFoodDetail(String foodID) {
            _progressDialog = showProgressDialog("Loading...",getResources().getColor(R.color.green_A700));
            this.foodID = foodID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress dialog
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Call<MealDetailResponse> callCommonFoodDetailAPI = ApiClient.getNutritionixService().getBrandedFoodDetail(foodID);
            callCommonFoodDetailAPI.enqueue(new Callback<MealDetailResponse>() {
                @Override
                public void onResponse(Call<MealDetailResponse> call, Response<MealDetailResponse> response) {
                    //close progress dialog
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        if (response.body().foodDetail != null) {
                            getActivity().runOnUiThread(() -> setupFoodDetail(response.body().foodDetail[0]));
                        } else {
                            _getBrandedFoodDetail = null;
                            //if API call failure
                            final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.body().message) ? response.body().message : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                            snackbar.show();
                        }
                    } else {
                        _getBrandedFoodDetail = null;
                        //if API call failure
                        final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.message()) ? response.message() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<MealDetailResponse> call, Throwable t) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    //if API call failure
                    final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(t.getMessage()) ? t.getMessage() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                    snackbar.show();
                    _getBrandedFoodDetail = null;
                }
            });
            return null;
        }
    }

    private class RetrieveBarcodeFoodDetail extends AsyncTask<Void, Void, Void> {
        //progress dialog
        private SweetAlertDialog _progressDialog;
        private String barcode;//create meal detail request which is POST

        public RetrieveBarcodeFoodDetail(String barcode) {
            _progressDialog = showProgressDialog("Loading...",getResources().getColor(R.color.green_A700));
            this.barcode = barcode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress dialog
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Call<MealDetailResponse> callBarcodeFoodDetailAPI = ApiClient.getNutritionixService().getBarcodeFoodDetail(barcode);
            callBarcodeFoodDetailAPI.enqueue(new Callback<MealDetailResponse>() {
                @Override
                public void onResponse(Call<MealDetailResponse> call, Response<MealDetailResponse> response) {
                    //close progress dialog
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        if (response.body().foodDetail != null) {
                            getActivity().runOnUiThread(() -> setupFoodDetail(response.body().foodDetail[0]));
                        } else {
                            _getBarcodeFoodDetail = null;
                            //if API call failure
                            final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.body().message) ? response.body().message : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                            snackbar.show();
                        }
                    } else {
                        _getBarcodeFoodDetail = null;
                        //if API call failure
                        final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.message()) ? response.message() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<MealDetailResponse> call, Throwable t) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    //if API call failure
                    final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(t.getMessage()) ? t.getMessage() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                    snackbar.show();
                    _getBarcodeFoodDetail = null;
                }
            });
            return null;
        }
    }

    private void setupFoodDetail(FoodDetail foodDetail) {
        foodName = foodDetail.foodName;
        //set food name
        _foodNameView.setText(foodName);
        //set brand name if brand name is no null
        if (foodDetail.brandName != null) {
            _foodBrandView.setVisibility(View.VISIBLE);
            _foodBrandView.setText(foodDetail.brandName);
        }

        //to get the multipliers of each nutrition, which mean 1 gram have x nutrition value
        caloriesMultipliers = foodDetail.calories / foodDetail.weightGrams;
        carbsMultipliers = foodDetail.carbohydrate / foodDetail.weightGrams;
        proteinsMultipliers = foodDetail.proteins / foodDetail.weightGrams;
        fatsMultipliers = foodDetail.fats / foodDetail.weightGrams;
        fiberMultipliers = foodDetail.fiber / foodDetail.weightGrams;


        if(mealRecordID.isEmpty())//if add meal
        {
            _addButton.setVisibility(View.VISIBLE);
            _updateButton.setVisibility(View.GONE);
            _deleteButton.setVisibility(View.GONE);

            //set the default drop down text
            _autoCompleteServing.setText(foodDetail.servingUnit, false);
            //set the default quantity
            _inputQuantity.getEditText().setText(String.valueOf(Math.round(foodDetail.quantity*10.0)/10.0));

            //get the food weight gram
            grams = foodDetail.weightGrams/(Math.round(foodDetail.quantity*10.0)/10.0);
            //get nutrition value
            calories = foodDetail.calories;
            carbs = foodDetail.carbohydrate;
            proteins = foodDetail.proteins;
            fats = foodDetail.fats;
            fiber = foodDetail.fiber;
        }
        else{//if update meal
            _addButton.setVisibility(View.GONE);
            _updateButton.setVisibility(View.VISIBLE);
            _deleteButton.setVisibility(View.VISIBLE);

            getFoodRecordDetailFromDatabase(date);
        }


        //get food image detail
        FoodPhoto foodPhoto = foodDetail.photo;
        //set image with URL
        Glide.with(getContext())
                .load(foodPhoto.thumb)
                .centerInside()
                .placeholder(R.drawable.ic_image_holder)
                .into(_foodImage);

        //view image in dialog
        _foodImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                new StfalconImageViewer.Builder<>(getContext(), new String[]{foodPhoto.highres},
                        (imageView, image) ->
                                Glide.with(getContext())
                                        .load(image==null?foodPhoto.thumb:image)
                                        .centerInside()
                                        .placeholder(R.drawable.ic_image_holder)
                                        .into(imageView))
                        .withHiddenStatusBar(false)
                        .withTransitionFrom(_foodImage)
                        .allowZooming(true)
                        .allowSwipeToDismiss(true)
                        .show();
            }
        });
        //set the string list of the serving unit, the purpose is to display at drop down, because drop down required string array or list
        List<String> servingUnitList = new ArrayList<>();

        servingUnitMap.put(foodDetail.servingUnit, foodDetail.weightGrams/foodDetail.quantity);
        if(foodDetail.measures!=null&&foodDetail.measures.length>0) {
            for (Measure measure : foodDetail.measures) {
                if(!servingUnitMap.containsKey(measure.servingUnit))
                {
                    servingUnitList.add(measure.servingUnit);
                    servingUnitMap.put(measure.servingUnit, measure.getGramPerServing());
                }
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.item_serving_unit, servingUnitList);
        _autoCompleteServing.setAdapter(arrayAdapter);

        setNutritionView();
    }

    private void getFoodRecordDetailFromDatabase(String date)
    {
        //if the date argument is "Today"
        if (date.equals("Today")) {
            date = format.format(new Date());//get current date
        }
        String DOCUMENT_PATH = String.format("MealRecords/%1$s/Records/%2$s", getSessionHandler().getUser().getUID(), mealRecordID);
        //get the Document reference
        //document path = MealRecords/UID/Record/MealRecordID
        DocumentReference documentReference = database.document(DOCUMENT_PATH);
        //get meal record detail
        documentReference.get().addOnSuccessListener((value) -> {
            //set the default drop down text
            _autoCompleteServing.setText(value.getString("servingUnit"), false);
            //set the default quantity
            _inputQuantity.getEditText().setText(String.valueOf(value.getDouble("quantity")));

            grams = value.getDouble("foodWeight");
            //get nutrition value
            calories = caloriesMultipliers*grams;
            carbs = carbsMultipliers*grams;
            proteins = proteinsMultipliers*grams;
            fats = fatsMultipliers*grams;
            fiber = fiberMultipliers*grams;
        }).addOnFailureListener(e -> {
            //show error with dialog
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
        });
    }

    private void calculateFoodNutrition() {
        String servingUnit = _autoCompleteServing.getText().toString();
        double gramPerServing = 1;
        if (servingUnitMap.containsKey(servingUnit))
            gramPerServing = servingUnitMap.get(servingUnit);
        double quantity = Double.parseDouble(_inputQuantity.getEditText().getText().toString());
        grams = quantity * gramPerServing;

        calories = caloriesMultipliers * grams;
        proteins = proteinsMultipliers * grams;
        carbs = carbsMultipliers * grams;
        fats = fatsMultipliers * grams;
        fiber = fiberMultipliers * grams;

        setNutritionView();
    }

    private void setNutritionView() {
        //round off calories
        _caloriesView.setText(String.format("%1$d Calories", (int) Math.round(calories)));
        //round off proteins, fats, fiber, carbs with 1 decimal places
        _proteinsView.setText(String.format("%.1f g", Math.round(proteins * 10.0) / 10.0));
        _carbohydratesView.setText(String.format("%.1f g", Math.round(carbs * 10.0) / 10.0));
        _fatsView.setText(String.format("%.1f g", Math.round(fats * 10.0) / 10.0));
        _fiberView.setText(String.format("%.1f g", Math.round(fiber * 10.0) / 10.0));

        //if round off proteins is 0, decrease the alpha, else maintain
        if ((Math.round(proteins * 10.0) / 10.0) == 0)
            _proteinIcon.setAlpha(0.1f);
        else
            _proteinIcon.setAlpha(1f);
        //if round off carbohydrates is 0, decrease the alpha, else maintain
        if ((Math.round(carbs * 10.0) / 10.0) == 0)
            _carbsIcon.setAlpha(0.1f);
        else
            _carbsIcon.setAlpha(1f);
        //if round off fat is 0, decrease the alpha, else maintain
        if ((Math.round(fats * 10.0) / 10.0) == 0)
            _fatIcon.setAlpha(0.1f);
        else
            _fatIcon.setAlpha(1f);
        //if round off fiber is 0, decrease the alpha, else maintain
        if ((Math.round(fiber * 10.0) / 10.0) == 0)
            _fiberIcon.setAlpha(0.1f);
        else
            _fiberIcon.setAlpha(1f);
    }
}
