package com.example.a303com_laukuansin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.domains.Exercise;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExerciseRecordAdapter extends RecyclerView.Adapter<ExerciseRecordAdapter.ViewHolder>{
    private final Context _context;
    private final List<Exercise> _exerciseRecordList;
    private OnActionListener _listener;


    public ExerciseRecordAdapter(Context _context, List<Exercise> _exerciseRecordList) {
        this._context = _context;
        this._exerciseRecordList = _exerciseRecordList;

        if (_context instanceof OnActionListener){
            _listener = (OnActionListener)_context;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_exercise_record,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseRecordAdapter.ViewHolder holder, int position) {
        Exercise exerciseRecord = _exerciseRecordList.get(position);

        //set exercise name
        holder._exerciseNameView.setText(exerciseRecord.getExerciseName());
        //set duration
        holder._durationView.setText(String.format("%1$d minutes", exerciseRecord.getDuration()));
        //set calories
        holder._caloriesView.setText(String.format("%1$d Calories",(int) Math.round(exerciseRecord.getCalories())));
        //when click the layout
        holder._layout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.editExerciseRecord(exerciseRecord);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _exerciseRecordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout _layout;
        private TextView _exerciseNameView;
        private TextView _durationView;
        private TextView _caloriesView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _layout = itemView.findViewById(R.id.item_block);
            _exerciseNameView = itemView.findViewById(R.id.exerciseName);
            _durationView = itemView.findViewById(R.id.duration);
            _caloriesView = itemView.findViewById(R.id.calories);
        }
    }

    public interface OnActionListener{
        void editExerciseRecord(Exercise exercise);
    }
}
