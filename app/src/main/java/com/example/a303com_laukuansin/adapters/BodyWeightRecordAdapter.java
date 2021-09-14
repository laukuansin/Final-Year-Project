package com.example.a303com_laukuansin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.domains.BodyWeight;
import com.example.a303com_laukuansin.domains.Exercise;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BodyWeightRecordAdapter extends RecyclerView.Adapter<BodyWeightRecordAdapter.ViewHolder>{
    private final Context _context;
    private final List<BodyWeight> _bodyWeightRecordList;
    private OnActionListener _listener;

    public BodyWeightRecordAdapter(Context _context, List<BodyWeight> _bodyWeightRecordList) {
        this._context = _context;
        this._bodyWeightRecordList = _bodyWeightRecordList;

        if (_context instanceof OnActionListener){
            _listener = (OnActionListener)_context;
        }
    }

    @NonNull
    @Override
    public BodyWeightRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_body_weight_record,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BodyWeightRecordAdapter.ViewHolder holder, int position) {
        BodyWeight bodyWeight = _bodyWeightRecordList.get(position);
        //set body weight
        holder._bodyWeight.setText(String.format("%.1f kg",bodyWeight.getBodyWeight()));
        //set date
        holder._date.setText(bodyWeight.getDate());
        //if the item is last position, hide the line indicator
        if(position==_bodyWeightRecordList.size()-1)
        {
            holder._indicatorLine.setVisibility(View.INVISIBLE);
        }
        else{
            holder._indicatorLine.setVisibility(View.VISIBLE);
        }
        holder._cardView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.editBodyWeightRecord(bodyWeight);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _bodyWeightRecordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView _bodyWeight;
        private final View _indicatorLine;
        private final TextView _date;
        private final MaterialCardView _cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _bodyWeight = itemView.findViewById(R.id.bodyWeightView);
            _date = itemView.findViewById(R.id.dateView);
            _indicatorLine = itemView.findViewById(R.id.indicatorLine);
            _cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public interface OnActionListener{
        void editBodyWeightRecord(BodyWeight bodyWeight);
    }
}
