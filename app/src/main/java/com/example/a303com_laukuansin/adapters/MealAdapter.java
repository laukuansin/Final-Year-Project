package com.example.a303com_laukuansin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.domains.MealType;
import com.example.a303com_laukuansin.utilities.ProgressAnimation;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {
    private final Context _context;
    private final List<MealType> _mealTypeList;

    public MealAdapter(Context _context, List<MealType> _mealTypeList) {
        this._context = _context;
        this._mealTypeList = _mealTypeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_meal_type,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealType mealType = _mealTypeList.get(position);

        //set meal name
        holder._mealType.setText(mealType.getType());
        //set the progress text
        holder._textProgress.setText(String.format("%1$d/%2$d", (int)Math.round(mealType.getCurrentCalorie()),(int) Math.round(mealType.getSuggestCalorie())));
        //set the maximum progress bar
        holder._progressBar.setMax((int)Math.round(mealType.getSuggestCalorie()));
        //set animation for progress bar
        ProgressAnimation animation = new ProgressAnimation(holder._progressBar,0, (int)Math.round(mealType.getCurrentCalorie()));
        animation.setDuration(2000);
        holder._progressBar.setAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return _mealTypeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearProgressIndicator _progressBar;
        private final TextView _mealType;
        private final TextView _textProgress;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _progressBar = itemView.findViewById(R.id.progressBar);
            _mealType = itemView.findViewById(R.id.mealType);
            _textProgress = itemView.findViewById(R.id.textProgressView);
        }
    }
}
