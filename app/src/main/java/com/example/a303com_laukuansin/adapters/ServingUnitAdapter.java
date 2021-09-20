package com.example.a303com_laukuansin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.domains.ServingUnit;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServingUnitAdapter extends RecyclerView.Adapter<ServingUnitAdapter.ViewHolder>{
    private Context _context;
    private List<ServingUnit> _servingUnitList;

    public ServingUnitAdapter(Context _context, List<ServingUnit> _servingUnitList) {
        this._context = _context;
        this._servingUnitList = _servingUnitList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_serving_unit_help,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServingUnitAdapter.ViewHolder holder, int position) {
        ServingUnit servingUnit = _servingUnitList.get(position);

        holder._name.setText(servingUnit.getServingUnitName());
        Glide.with(_context).load(servingUnit.getImageUrl()).placeholder(R.drawable.ic_small_image_holder).into(holder._imageView);
        if(servingUnit.getDescription().isEmpty())
        {
            holder._description.setVisibility(View.GONE);
        }
        else{
            holder._description.setVisibility(View.VISIBLE);
            holder._description.setText(servingUnit.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return _servingUnitList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView _name;
        private final TextView _description;
        private final ImageView _imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _name = itemView.findViewById(R.id.name);
            _description = itemView.findViewById(R.id.description);
            _imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
