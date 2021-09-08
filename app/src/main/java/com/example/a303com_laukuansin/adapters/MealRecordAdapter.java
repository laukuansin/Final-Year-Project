package com.example.a303com_laukuansin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.domains.Meal;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MealRecordAdapter extends RecyclerView.Adapter<MealRecordAdapter.ViewHolder> {
    private List<Meal> _mealRecordList;
    private Context _context;
    private OnActionListener _listener;

    public MealRecordAdapter(List<Meal> _mealRecordList, Context _context) {
        this._mealRecordList = _mealRecordList;
        this._context = _context;

        if (_context instanceof OnActionListener){
            _listener = (OnActionListener)_context;
        } else{
            throw new RuntimeException(_context.toString() + " must implement OnActionListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_meal_record,parent,false);
        return new MealRecordAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal mealRecord = _mealRecordList.get(position);

        //set meal name
        holder._mealNameView.setText(mealRecord.getMealName());
        //set quantity and serving
        holder._quantityServingView.setText(String.format("%.1f %2$s", mealRecord.getQuantity(), mealRecord.getServingUnit()));
        //set calories
        holder._caloriesView.setText(String.format("%1$d Calories",(int) Math.round(mealRecord.getCalories())));
        //when click the layout
        holder._layout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.editMealRecord(mealRecord);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _mealRecordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout _layout;
        private TextView _mealNameView;
        private TextView _quantityServingView;
        private TextView _caloriesView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _layout = itemView.findViewById(R.id.item_block);
            _mealNameView = itemView.findViewById(R.id.mealName);
            _quantityServingView = itemView.findViewById(R.id.quantityServing);
            _caloriesView = itemView.findViewById(R.id.calories);
        }
    }

    public interface OnActionListener{
        void editMealRecord(Meal meal);
    }
}

