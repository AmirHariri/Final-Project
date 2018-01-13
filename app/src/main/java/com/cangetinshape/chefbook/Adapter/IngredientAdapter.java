package com.cangetinshape.chefbook.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cangetinshape.chefbook.BestRecipe;
import com.cangetinshape.chefbook.R;

import java.util.ArrayList;

/**
 * Created by Amir on 12/20/2017.
 */

public class IngredientAdapter extends ArrayAdapter<BestRecipe.Ingredients> {
    public Context mContext;
    public ArrayList<BestRecipe.Ingredients> mIngredientsObject;

    public IngredientAdapter(Context context, int resource, ArrayList<BestRecipe.Ingredients> ingredientsObject) {
        super(context, 0, ingredientsObject);
        this.mContext = context;
        this.mIngredientsObject = ingredientsObject;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        final int mPosition = position;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.ingredient_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mIngredientTextView = listItemView.findViewById(R.id.ingredient_tv_toshow);
            viewHolder.mAmountTextView = listItemView.findViewById(R.id.amount_tv_toshow);
            viewHolder.mScaleTextView =listItemView.findViewById(R.id.scale_tv_toshow);

            viewHolder.mIngredientTextView.setText(mIngredientsObject.get(position).getIngredients());
            viewHolder.mAmountTextView.setText(mIngredientsObject.get(position).getAmounts());
            viewHolder.mScaleTextView.setText(mIngredientsObject.get(position).getScales());
            listItemView.setTag(viewHolder);
        }
        return listItemView;
    }



     private static class ViewHolder{
        TextView mIngredientTextView;
        TextView mAmountTextView;
        TextView mScaleTextView;


    }

}
