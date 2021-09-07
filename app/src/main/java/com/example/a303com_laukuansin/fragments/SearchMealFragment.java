package com.example.a303com_laukuansin.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.BarcodeScannerActivity;
import com.example.a303com_laukuansin.activities.MealDetailActivity;
import com.example.a303com_laukuansin.activities.SearchMealActivity;
import com.example.a303com_laukuansin.activities.TrackWithImageActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.BrandedFood;
import com.example.a303com_laukuansin.domains.CommonFood;
import com.example.a303com_laukuansin.items.BrandedFoodItem;
import com.example.a303com_laukuansin.items.CommonFoodItem;
import com.example.a303com_laukuansin.responses.MealDetailResponse;
import com.example.a303com_laukuansin.responses.MealResponse;
import com.example.a303com_laukuansin.utilities.ApiClient;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMealFragment extends BaseFragment {
    private String date;
    private String mealType;
    private View _divider;
    private LinearLayout _barcodeImageLayout, _viewContainer, _waitingOrErrorLayout;
    private SearchMealWithAPI _searchMeal = null;//default is null
    private CheckBarcode _checkBarcode = null;
    private FastItemAdapter<CommonFoodItem> _commonFoodAdapter;
    private FastItemAdapter<BrandedFoodItem> _brandedFoodAdapter;
    private TextView _waitingOrErrorView, _commonFoodLabel, _brandedFoodLabel;
    private SpinKitView _loadingView;
    private static final int CAMERA_PERMISSION_CODE = 101;

    public SearchMealFragment() {
    }

    public static SearchMealFragment newInstance(String date, String mealType) {
        SearchMealFragment fragment = new SearchMealFragment();
        Bundle args = new Bundle();
        args.putString(SearchMealActivity.DATE_KEY, date);
        args.putString(SearchMealActivity.MEAL_TYPE_KEY, mealType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(SearchMealActivity.DATE_KEY)) {
                date = getArguments().getString(SearchMealActivity.DATE_KEY, "");
            }
            if (getArguments().containsKey(SearchMealActivity.MEAL_TYPE_KEY)) {
                mealType = getArguments().getString(SearchMealActivity.MEAL_TYPE_KEY, "");
            }
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_meal, container, false);

        initialization(view);

        _commonFoodAdapter.withSavedInstanceState(savedInstanceState);
        _brandedFoodAdapter.withSavedInstanceState(savedInstanceState);

        return view;
    }

    private void initialization(View view) {
        //bind view with ID
        _divider = view.findViewById(R.id.divider);
        _barcodeImageLayout = view.findViewById(R.id.barcodeImageLayout);
        _viewContainer = view.findViewById(R.id.viewContainer);
        LinearLayout _scanBarcodeLayout = view.findViewById(R.id.scanBarcodeLayout);
        LinearLayout _trackImageLayout = view.findViewById(R.id.trackImageLayout);
        _waitingOrErrorLayout = view.findViewById(R.id.waitingOrErrorLayout);
        _waitingOrErrorView = view.findViewById(R.id.waitingOrErrorView);
        _loadingView = view.findViewById(R.id.spinKitView);
        _commonFoodLabel = view.findViewById(R.id.commonFoodLabel);
        _brandedFoodLabel = view.findViewById(R.id.brandedFoodLabel);
        RecyclerView _commonFoodRecyclerView = view.findViewById(R.id.commonFoodRecyclerView);
        RecyclerView _brandedFoodRecyclerView = view.findViewById(R.id.brandedFoodRecyclerView);


        //setup recyclerview
        _commonFoodAdapter = new FastItemAdapter<>();
        _brandedFoodAdapter = new FastItemAdapter<>();
        _commonFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _commonFoodRecyclerView.setItemAnimator(new DefaultItemAnimator());
        _commonFoodRecyclerView.setAdapter(_commonFoodAdapter);
        _brandedFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _brandedFoodRecyclerView.setItemAnimator(new DefaultItemAnimator());
        _brandedFoodRecyclerView.setAdapter(_brandedFoodAdapter);

        //set adapter item click
        _commonFoodAdapter.withOnClickListener((v, adapter, item, position) -> {
            goToMealDetailActivity(item.getCommonFood().getName(), "","");
            return false;
        });
        _brandedFoodAdapter.withOnClickListener((v, adapter, item, position) -> {
            goToMealDetailActivity("", item.getBrandedFood().getID(),"");
            return false;
        });

        //when click scan barcode
        _scanBarcodeLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //check the camera permission
                if (checkCameraPermission()) {
                    openBarcodeScanner();
                }
            }
        });

        //when click track image
        _trackImageLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), TrackWithImageActivity.class);
                intent.putExtra(MealDetailActivity.DATE_KEY, date);
                intent.putExtra(MealDetailActivity.MEAL_TYPE_KEY, mealType);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }
    private void checkBarcode(String barcode)
    {
        if(_checkBarcode==null)
        {
            _checkBarcode = new CheckBarcode(barcode);
            _checkBarcode.execute();
        }
    }

    private boolean isDeviceSupportCamera() {
        //check device is support camera or not
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private boolean checkCameraPermission() {
        //if the device is no support camera
        if (!isDeviceSupportCamera()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(getContext())) {
                    //check permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    return false;
                }
            } else {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    final Snackbar snackbar = super.initSnackbar(android.R.id.content, "Your device does not support camera", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    return false;
                }
            }
        }
        return true;
    }

    private void openBarcodeScanner() {
        //open the barcode scanner
        IntentIntegrator.forSupportFragment(SearchMealFragment.this)
                .setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES)//product code
                .setOrientationLocked(false)
                .setCaptureActivity(BarcodeScannerActivity.class)
                .initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //permission call back
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Checking camera availability
                if (!isDeviceSupportCamera()) {
                    final Snackbar snackbar = super.initSnackbar(android.R.id.content, "Your device does not support camera", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                } else {
                    openBarcodeScanner();
                }
            } else {
                final Snackbar snackbar = super.initSnackbar(android.R.id.content, "Your device does not support camera", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result return by barcode scanner

        //get result
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String barcode = result.getContents();
                String barcodeFormat = result.getFormatName();

                //if the barcode format is UPC_E, the reason to change is because the food database UPC_E will not get the result
                //therefore when barcode format is upc e, it will convert to upc a format
                Log.d("format",result.getFormatName());
                if(barcodeFormat.equals("UPC_E"))
                {
                    barcode = convertUPCE_To_UPCA(barcode);
                }
                checkBarcode(barcode);
            }
        }
    }
    private String convertUPCE_To_UPCA(String barcode)
    {
        switch (barcode.charAt(6)) {
            case '0':
            case '1':
            case '2': {
                barcode = barcode.substring(1, 3) + barcode.charAt(6) + "0000"  + barcode.substring(3, 6) + barcode.charAt(7);
                break;
            }
            case '3': {
                barcode = barcode.substring(1, 4) + "00000" + barcode.substring(4,6) + barcode.charAt(7);
                break;
            }
            case '4': {
                barcode = barcode.substring(1, 5) + "00000" + barcode.charAt(5) + barcode.charAt(7);
                break;
            }
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                barcode = barcode.substring(1, 6) + "0000" + barcode.charAt(6) + barcode.charAt(7);
                break;
            }
        }
        return "0" + barcode;
    }

    private void goToMealDetailActivity(String foodName, String foodID,String foodBarcode) {
        Intent intent = new Intent(getContext(), MealDetailActivity.class);
        intent.putExtra(MealDetailActivity.DATE_KEY, date);
        intent.putExtra(MealDetailActivity.MEAL_TYPE_KEY, mealType);
        if (!foodName.isEmpty()) {
            intent.putExtra(MealDetailActivity.FOOD_NAME_KEY, foodName);
        }
        if (!foodID.isEmpty()) {
            intent.putExtra(MealDetailActivity.FOOD_ID_KEY, foodID);
        }
        if(!foodBarcode.isEmpty()) {
            intent.putExtra(MealDetailActivity.FOOD_BARCODE_KEY, foodBarcode);
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search for food");
        //when user click the search button
        searchView.setOnSearchClickListener(view -> {
            _barcodeImageLayout.setVisibility(View.GONE);
            _viewContainer.setVisibility(View.GONE);
            _commonFoodAdapter.clear();
            _brandedFoodAdapter.clear();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {//when user typing finish, and click search
                searchMeal(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //remove all item in adapter
                _commonFoodAdapter.clear();
                _brandedFoodAdapter.clear();
                _viewContainer.setVisibility(View.GONE);
                _commonFoodLabel.setVisibility(View.GONE);
                _brandedFoodLabel.setVisibility(View.GONE);
                _divider.setVisibility(View.GONE);
                return false;
            }
        });
        //when user close the search view
        searchView.setOnCloseListener(() -> {
            _commonFoodAdapter.clear();
            _brandedFoodAdapter.clear();
            _commonFoodLabel.setVisibility(View.GONE);
            _brandedFoodLabel.setVisibility(View.GONE);
            _divider.setVisibility(View.GONE);
            _barcodeImageLayout.setVisibility(View.VISIBLE);
            _waitingOrErrorLayout.setVisibility(View.VISIBLE);
            _waitingOrErrorView.setText("You can search by Food Name or Food Brand");
            _viewContainer.setVisibility(View.VISIBLE);
            return false;
        });

    }

    //function to search meal with query
    private void searchMeal(String query) {
        if (_searchMeal == null) {
            _searchMeal = new SearchMealWithAPI(query);
            _searchMeal.execute();
        }
    }

    //create instance of common food
    private CommonFoodItem createInstanceOfCommonFoodItem(CommonFood commonFood) {
        return new CommonFoodItem().withCommonFoodView(commonFood);
    }

    //create instance of branded food
    private BrandedFoodItem createInstanceOfBrandedFoodItem(BrandedFood brandedFood) {
        return new BrandedFoodItem().withBrandedFoodView(brandedFood);
    }

    private class CheckBarcode extends AsyncTask<Void,Void,Void>
    {
        private String barcode;
        private SweetAlertDialog _progressDialog;

        public CheckBarcode(String barcode) {
            this.barcode = barcode;
            _progressDialog = showProgressDialog("Scanning...",getResources().getColor(R.color.green_A700)) ;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //call get barcode food detail
            Call<MealDetailResponse> getBarcodeFoodAPICall = ApiClient.getNutritionixService().getBarcodeFoodDetail(barcode);
            getBarcodeFoodAPICall.enqueue(new Callback<MealDetailResponse>() {
                @Override
                public void onResponse(Call<MealDetailResponse> call, Response<MealDetailResponse> response) {
                    //close progress dialog
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    if(response.isSuccessful())
                    {
                        getActivity().runOnUiThread(() -> {
                            if(response.body().foodDetail==null)
                            {
                                ErrorAlert("Food barcode does not exists in food database", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                            }
                            else{
                                goToMealDetailActivity("", "",barcode);
                            }
                        });
                    }
                    else{
                        ErrorAlert("Food barcode does not exists in food database", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                    }
                }

                @Override
                public void onFailure(Call<MealDetailResponse> call, Throwable t) {
                    //close progress dialog
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    //if API call failure
                    final Snackbar snackbar = SearchMealFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(t.getMessage()) ? t.getMessage() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                    snackbar.show();
                }
            });
            _checkBarcode = null;
            return null;
        }
    }

    //async task to call the API
    private class SearchMealWithAPI extends AsyncTask<Void, Void, Void> {
        private final String query;

        public SearchMealWithAPI(String query) {
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _viewContainer.setVisibility(View.VISIBLE);
            _waitingOrErrorLayout.setVisibility(View.GONE);//hide waiting or error layout
            _loadingView.setVisibility(View.VISIBLE);//shown the loading view
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //call search food API
            Call<MealResponse> searchFoodAPICall = ApiClient.getNutritionixService().searchFoodWithQuery(query);
            searchFoodAPICall.enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                    //if API call success
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(() -> {
                            _loadingView.setVisibility(View.GONE);
                            if (response.body().commonFood.length > 0 || response.body().brandedFood.length > 0) {
                                _viewContainer.setVisibility(View.GONE);
                                if (response.body().brandedFood.length > 0 && response.body().commonFood.length > 0) {
                                    _divider.setVisibility(View.VISIBLE);
                                }
                                if (response.body().commonFood.length > 0) {
                                    _commonFoodLabel.setVisibility(View.VISIBLE);
                                    List<CommonFood> _commonFoodList = removeDuplicate(Arrays.asList(response.body().commonFood));//remove duplicate food
                                    //loop the response of meal
                                    for (CommonFood commonFood : _commonFoodList) {
                                        _commonFoodAdapter.add(_commonFoodAdapter.getAdapterItemCount(), createInstanceOfCommonFoodItem(commonFood));
                                    }
                                }
                                if (response.body().brandedFood.length > 0) {
                                    _brandedFoodLabel.setVisibility(View.VISIBLE);
                                    List<BrandedFood> _brandedFoodList = Arrays.asList(response.body().brandedFood);
                                    //loop the response of meal
                                    for (BrandedFood brandedFood : _brandedFoodList) {
                                        _brandedFoodAdapter.add(_brandedFoodAdapter.getAdapterItemCount(), createInstanceOfBrandedFoodItem(brandedFood));
                                    }
                                }

                            } else {
                                _waitingOrErrorView.setText(String.format("No meal found for key word \"%1$s\"", query));
                                _waitingOrErrorLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {//response fail
                        _viewContainer.setVisibility(View.GONE);
                        //if API call failure
                        final Snackbar snackbar = SearchMealFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(response.message()) ? response.message() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                        snackbar.show();
                    }

                }

                @Override
                public void onFailure(Call<MealResponse> call, Throwable t) {
                    _viewContainer.setVisibility(View.GONE);
                    //if API call failure
                    final Snackbar snackbar = SearchMealFragment.super.initSnackbar(android.R.id.content, !TextUtils.isEmpty(t.getMessage()) ? t.getMessage() : "Unknown Error", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Dismiss", view -> snackbar.dismiss());
                    snackbar.show();
                }
            });
            _searchMeal = null;
            return null;
        }
    }

    public List<CommonFood> removeDuplicate(List<CommonFood> duplicate) {
        //create a set data structure, to remove duplicate mealID
        Set<CommonFood> set = new TreeSet<>((mealA, mealB) -> {
            if (mealA.getID().equalsIgnoreCase(mealB.getID())) {
                return 0;
            }
            return 1;
        });
        //after that convert set to arraylist and return it
        set.addAll(duplicate);
        return new ArrayList<>(set);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            _commonFoodAdapter.withSavedInstanceState(savedInstanceState);
            _brandedFoodAdapter.withSavedInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (_commonFoodAdapter != null)
            outState = _commonFoodAdapter.saveInstanceState(outState);
        if (_brandedFoodAdapter != null)
            outState = _brandedFoodAdapter.saveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

}
