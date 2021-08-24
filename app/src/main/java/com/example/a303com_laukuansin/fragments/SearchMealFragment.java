package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.MealDetailActivity;
import com.example.a303com_laukuansin.activities.SearchMealActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.BrandedFood;
import com.example.a303com_laukuansin.domains.CommonFood;
import com.example.a303com_laukuansin.items.BrandedFoodItem;
import com.example.a303com_laukuansin.items.CommonFoodItem;
import com.example.a303com_laukuansin.responses.MealResponse;
import com.example.a303com_laukuansin.utilities.ApiClient;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMealFragment extends BaseFragment{
    private String date;
    private String mealType;
    private View _divider;
    private LinearLayout _barcodeImageLayout,_viewContainer,_waitingOrErrorLayout;
    private SearchMealWithAPI _searchMeal = null;//default is null
    private FastItemAdapter<CommonFoodItem> _commonFoodAdapter;
    private FastItemAdapter<BrandedFoodItem> _brandedFoodAdapter;
    private TextView _waitingOrErrorView,_commonFoodLabel,_brandedFoodLabel;
    private SpinKitView _loadingView;

    public SearchMealFragment() {
    }
    public static SearchMealFragment newInstance(String date, String mealType)
    {
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

    private void initialization(View view)
    {
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
        _commonFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) );
        _commonFoodRecyclerView.setItemAnimator(new DefaultItemAnimator());
        _commonFoodRecyclerView.setAdapter(_commonFoodAdapter);
        _brandedFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) );
        _brandedFoodRecyclerView.setItemAnimator(new DefaultItemAnimator());
        _brandedFoodRecyclerView.setAdapter(_brandedFoodAdapter);

        //set adapter item click
        _commonFoodAdapter.withOnClickListener((v, adapter, item, position) -> {
            goToMealDetailActivity(item.getCommonFood().getName(),"");
            return false;
        });
        _brandedFoodAdapter.withOnClickListener((v, adapter, item, position) -> {
            goToMealDetailActivity("",item.getBrandedFood().getID());
            return false;
        });

        //when click scan barcode
        _scanBarcodeLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }
        });

        //when click track image
        _trackImageLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }
        });

    }

    private void goToMealDetailActivity(String foodName,String foodID)
    {
        Intent intent = new Intent(getContext(), MealDetailActivity.class);
        intent.putExtra(MealDetailActivity.DATE_KEY,date);
        intent.putExtra(MealDetailActivity.MEAL_TYPE_KEY,mealType);
        if(!foodName.isEmpty())
        {
            intent.putExtra(MealDetailActivity.FOOD_NAME_KEY,foodName);
        }
        if(!foodID.isEmpty())
        {
            intent.putExtra(MealDetailActivity.FOOD_ID_KEY,foodID);
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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
    private void searchMeal(String query)
    {
        if(_searchMeal==null)
        {
            _searchMeal = new SearchMealWithAPI(query);
            _searchMeal.execute();
        }
    }
    //create instance of common food
    private CommonFoodItem createInstanceOfCommonFoodItem(CommonFood commonFood){
        return new CommonFoodItem().withCommonFoodView(commonFood);
    }

    //create instance of branded food
    private BrandedFoodItem createInstanceOfBrandedFoodItem(BrandedFood brandedFood){
        return new BrandedFoodItem().withBrandedFoodView(brandedFood);
    }

    //async task to call the API
    private class SearchMealWithAPI extends AsyncTask<Void,Void,Void>
    {
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
                    if(response.isSuccessful())
                    {
                        getActivity().runOnUiThread(() -> {
                            _loadingView.setVisibility(View.GONE);
                            if(response.body().commonFood.length>0||response.body().brandedFood.length>0) {
                                _viewContainer.setVisibility(View.GONE);
                                if(response.body().brandedFood.length>0&&response.body().commonFood.length>0)
                                {
                                    _divider.setVisibility(View.VISIBLE);
                                }
                                if(response.body().commonFood.length>0)
                                {
                                    _commonFoodLabel.setVisibility(View.VISIBLE);
                                    List<CommonFood> _commonFoodList = removeDuplicate(Arrays.asList(response.body().commonFood));//remove duplicate food
                                    //loop the response of meal
                                    for(CommonFood commonFood:_commonFoodList)
                                    {
                                        _commonFoodAdapter.add(_commonFoodAdapter.getAdapterItemCount(),createInstanceOfCommonFoodItem(commonFood));
                                    }
                                }
                                if(response.body().brandedFood.length>0)
                                {
                                    _brandedFoodLabel.setVisibility(View.VISIBLE);
                                    List<BrandedFood> _brandedFoodList = Arrays.asList(response.body().brandedFood);
                                    //loop the response of meal
                                    for(BrandedFood brandedFood:_brandedFoodList)
                                    {
                                        _brandedFoodAdapter.add(_brandedFoodAdapter.getAdapterItemCount(),createInstanceOfBrandedFoodItem(brandedFood));
                                    }
                                }

                            }
                            else {
                                _waitingOrErrorView.setText(String.format("No meal found for key word \"%1$s\"",query));
                                _waitingOrErrorLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else{//response fail
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

    public List<CommonFood> removeDuplicate(List<CommonFood>duplicate)
    {
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
