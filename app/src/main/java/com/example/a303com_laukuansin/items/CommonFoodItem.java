package com.example.a303com_laukuansin.items;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.domains.CommonFood;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommonFoodItem extends AbstractItem<CommonFoodItem, CommonFoodItem.ViewHolder>{
    private CommonFood _commonFood;

    public CommonFoodItem() {
    }

    public CommonFood getCommonFood()
    {
        return _commonFood;
    }

    public CommonFoodItem withCommonFoodView(CommonFood commonFood)
    {
        _commonFood = commonFood;
        return this;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new CommonFoodItem.ViewHolder(v);
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        final Context context = holder.itemView.getContext();
        holder._foodName.setText(_commonFood.getName());
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_common_food;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView _foodName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _foodName = itemView.findViewById(R.id.name);
        }
    }
}
