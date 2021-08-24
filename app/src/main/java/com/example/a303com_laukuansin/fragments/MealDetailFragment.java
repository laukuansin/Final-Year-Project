package com.example.a303com_laukuansin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.MealActivity;
import com.example.a303com_laukuansin.activities.MealDetailActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.FoodDetail;
import com.example.a303com_laukuansin.domains.Meal;
import com.example.a303com_laukuansin.domains.Measure;
import com.example.a303com_laukuansin.domains.FoodPhoto;
import com.example.a303com_laukuansin.requests.MealDetailRequest;
import com.example.a303com_laukuansin.responses.MealDetailResponse;
import com.example.a303com_laukuansin.utilities.ApiClient;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.example.a303com_laukuansin.utilities.QuantityValueFilter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private String date;
    private String mealType;
    private String foodName;
    private String foodID;
    private double calories, proteins, carbs, fiber, fats, grams;
    private double caloriesMultipliers, proteinsMultipliers, carbsMultipliers, fiberMultipliers, fatsMultipliers;
    private TextView _foodNameView, _foodBrandView, _caloriesView, _proteinsView, _carbohydratesView, _fatsView, _fiberView;
    private TextInputLayout _inputQuantity;
    private AutoCompleteTextView _autoCompleteServing;
    private ImageView _foodImage, _proteinIcon, _fatIcon, _carbsIcon, _fiberIcon;
    private HashMap<String, Double> servingUnitMap;
    private RetrieveCommonFoodDetail _getCommonFoodDetail = null;
    private RetrieveBrandedFoodDetail _getBrandedFoodDetail = null;
    private FirebaseFirestore database;

    public MealDetailFragment() {
    }

    public static MealDetailFragment newInstance(String date, String mealType, String foodName, String foodID) {
        MealDetailFragment fragment = new MealDetailFragment();
        Bundle args = new Bundle();
        args.putString(MealDetailActivity.DATE_KEY, date);
        args.putString(MealDetailActivity.MEAL_TYPE_KEY, mealType);
        args.putString(MealDetailActivity.FOOD_ID_KEY, foodID);
        args.putString(MealDetailActivity.FOOD_NAME_KEY, foodName);
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
        } else{
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
                if (!charSequence.toString().isEmpty()) {
                    calculateFoodNutrition();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    public void saveMeal() {
        String servingUnit = _autoCompleteServing.getText().toString();
        double quantity = 0;
        boolean check = true;
        if (servingUnit.isEmpty()) {
            ErrorAlert("Please select one serving unit.", sweetAlertDialog -> sweetAlertDialog.dismiss()).show();
            check = false;
        }
        String quantityStr = _inputQuantity.getEditText().getText().toString();
        if (quantityStr.isEmpty()) {
            ErrorAlert("Quantity cannot be empty!", sweetAlertDialog -> sweetAlertDialog.dismiss()).show();
            check = false;
        } else {
            quantity = Double.parseDouble(_inputQuantity.getEditText().getText().toString());
            if (quantity <= 0) {
                ErrorAlert("Quantity less than 0!", sweetAlertDialog -> sweetAlertDialog.dismiss()).show();
                check = false;
            } else {
                _inputQuantity.setError(null);
            }
        }

        if (check)//if no error
        {
            //add meal record to database
            addMealRecordToDatabase(servingUnit,quantity);
        }
    }

    private void addMealRecordToDatabase(String servingUnit, double quantity)
    {
        //create progress dialog
        SweetAlertDialog _progressDialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        _progressDialog.setContentText("Adding...");
        _progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.green_A700));
        _progressDialog.setCancelable(false);
        _progressDialog.show();

        //date format
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        //if the date argument is not "Today"
        if(date.equals("Today"))
        {
            date = format.format(new Date());//get current date
        }
        //the collection path, example: MealRecords/UID/Date/MealType
        String COLLECTION_PATH = String.format("MealRecords/%1$s/%2$s",getSessionHandler().getUser().getUID(),date);
        //get meal record reference
        CollectionReference mealRecordRef = database.collection(COLLECTION_PATH);
        //create meal record class
        Map<String, Object> mealRecordMap = new HashMap<>();//create hash map to store the user's data
        mealRecordMap.put("foodName",foodName);
        if(!foodID.isEmpty())
        {
            mealRecordMap.put("foodID",foodID);
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
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss()).show();
        });

    }

    private class RetrieveCommonFoodDetail extends AsyncTask<Void, Void, Void> {
        //progress dialog
        private SweetAlertDialog _progressDialog;
        private MealDetailRequest _request;//create meal detail request which is POST

        public RetrieveCommonFoodDetail(String foodName) {
            _progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            _request = new MealDetailRequest(foodName);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress dialog
            _progressDialog.setContentText("Loading...");
            _progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
            _progressDialog.setCancelable(false);
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
                            //if API call failure
                            final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.body().message) ? response.body().message : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                            snackbar.show();
                        }
                    } else {
                        //if API call failure
                        final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.message()) ? response.message() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                        snackbar.show();
                    }
                    _getCommonFoodDetail = null;
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
            _progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            this.foodID = foodID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress dialog
            _progressDialog.setContentText("Loading...");
            _progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.green_A700));
            _progressDialog.setCancelable(false);
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
                            //if API call failure
                            final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.body().message) ? response.body().message : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                            snackbar.show();
                        }
                    } else {
                        //if API call failure
                        final Snackbar snackbar = MealDetailFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.message()) ? response.message() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                        snackbar.show();
                    }
                    _getCommonFoodDetail = null;
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

    private void setupFoodDetail(FoodDetail foodDetail) {
        foodName = foodDetail.foodName;
        //set food name
        _foodNameView.setText(foodName);
        //set brand name if brand name is no null
        if (foodDetail.brandName != null) {
            _foodBrandView.setVisibility(View.VISIBLE);
            _foodBrandView.setText(foodDetail.brandName);
        }
        //set the default drop down text
        _autoCompleteServing.setText(foodDetail.servingUnit, false);
        //set the default quantity
        _inputQuantity.getEditText().setText(String.valueOf(foodDetail.quantity));

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
                                        .load(image)
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
        if (foodDetail.measures == null) {
            servingUnitMap.put(foodDetail.servingUnit, foodDetail.weightGrams);
        } else {
            for (Measure measure : foodDetail.measures) {
                servingUnitList.add(measure.servingUnit);
                servingUnitMap.put(measure.servingUnit, measure.getGramPerServing());
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.item_serving_unit, servingUnitList);
        _autoCompleteServing.setAdapter(arrayAdapter);

        //get nutrition value
        calories = foodDetail.calories;
        carbs = foodDetail.carbohydrate;
        proteins = foodDetail.proteins;
        fats = foodDetail.fats;
        fiber = foodDetail.fiber;
        grams = foodDetail.weightGrams;

        //to get the multipliers of each nutrition, which mean 1 gram have x nutrition value
        caloriesMultipliers = foodDetail.calories / grams;
        carbsMultipliers = foodDetail.carbohydrate / grams;
        proteinsMultipliers = foodDetail.proteins / grams;
        fatsMultipliers = foodDetail.fats / grams;
        fiberMultipliers = foodDetail.fiber / grams;

        setNutritionView();
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
