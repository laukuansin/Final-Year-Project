package com.example.a303com_laukuansin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.domains.Exercise;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>{
    private final Context _context;
    private final List<Exercise> _exerciseList;
    private OnActionListener _listener;


    public ExerciseListAdapter(Context _context, List<Exercise> _exerciseList) {
        this._context = _context;
        this._exerciseList = _exerciseList;

        if (_context instanceof OnActionListener){
            _listener = (OnActionListener)_context;
        } else{
            throw new RuntimeException(_context.toString() + " must implement OnActionListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_exercise,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseListAdapter.ViewHolder holder, int position) {
        Exercise exercise = _exerciseList.get(position);

        holder._exerciseName.setText(exercise.getExerciseName());
        Glide.with(_context).load(exercise.getExerciseIcon()).placeholder(R.drawable.ic_image_holder).into(holder._exerciseIcon);
        holder._itemBlock.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.selectExercise(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _exerciseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout _itemBlock;
        private final TextView _exerciseName;
        private final ImageView _exerciseIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _exerciseName = itemView.findViewById(R.id.exerciseName);
            _exerciseIcon = itemView.findViewById(R.id.iconView);
            _itemBlock = itemView.findViewById(R.id.item_block);
        }
    }

    public interface OnActionListener{
        void selectExercise(Exercise exercise);
    }
}
