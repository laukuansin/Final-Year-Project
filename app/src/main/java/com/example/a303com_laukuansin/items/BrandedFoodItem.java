package com.example.a303com_laukuansin.items;

import android.view.View;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.domains.BrandedFood;
import com.example.a303com_laukuansin.domains.CommonFood;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BrandedFoodItem extends AbstractItem<BrandedFoodItem,BrandedFoodItem.ViewHolder> {
    private BrandedFood _brandedFood;

    public BrandedFoodItem() {
    }

    public BrandedFood getBrandedFood()
    {
        return _brandedFood;
    }

    public BrandedFoodItem withBrandedFoodView(BrandedFood brandedFood)
    {
        _brandedFood = brandedFood;
        return this;
    }
    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new BrandedFoodItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_branded_food;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder._foodName.setText(_brandedFood.getName());
        holder._brandName.setText(_brandedFood.getBrandName());
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView _foodName;
        private TextView _brandName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _foodName = itemView.findViewById(R.id.foodName);
            _brandName = itemView.findViewById(R.id.brandName);
        }
    }
}
