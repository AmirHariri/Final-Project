package com.cangetinshape.chefbook.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cangetinshape.chefbook.BestRecipe;
import com.cangetinshape.chefbook.R;
import com.cangetinshape.chefbook.StringUtils;
import com.cangetinshape.chefbook.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir on 1/4/2018.
 */

public class OnlineRecipeAdapter extends RecyclerView.Adapter<OnlineRecipeAdapter.OnlineRecipeViewHolder> {

    private Context mContext;
    private List<BestRecipe> mOnlineRecipes;
    public int mCurrentPosition;


    private static final String TAG = OnlineRecipeAdapter.class.getSimpleName();

    public OnlineRecipeAdapter(Context context, List<BestRecipe> onlineRecipes) {
        mContext = context;
        mOnlineRecipes = onlineRecipes;
    }

    @Override
    public OnlineRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View recipeView = inflater.inflate(R.layout.online_recipe_list_item, parent, false);
        //recipeView.setFocusable(true);
        return new OnlineRecipeViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(OnlineRecipeViewHolder holder, int position) {
        mCurrentPosition =position;
        BestRecipe currentOnlinerecipe = mOnlineRecipes.get(position);
        String recipeTitle = currentOnlinerecipe.getRecipeTitle();
        String recipeCategory = currentOnlinerecipe.getCategory();
        int servigs = currentOnlinerecipe.getServings();
        int prepTime = currentOnlinerecipe.getPrepTime();
        int cookingTime = currentOnlinerecipe.getCookingTime();
        int yeildTime = currentOnlinerecipe.getYeildTime();
        int totalTime = currentOnlinerecipe.getTotalTime();
        String userName = currentOnlinerecipe.getUserName();
        String userNameToshow = mContext.getString(R.string.added_by_text) + userName;
        String userId = currentOnlinerecipe.getUserID();

        String allIngredients = currentOnlinerecipe.getIngredients();
        String[] ingredientArray = StringUtils.convertStringToArray(allIngredients);
        ArrayList<BestRecipe.Ingredients> mIngredientObject = new ArrayList<>();

        String allAmounts = currentOnlinerecipe.getAmount();
        String[] amountArray = StringUtils.convertStringToArray(allAmounts);
        String allScales = currentOnlinerecipe.getScale();
        String[] scaleArray = StringUtils.convertStringToArray(allScales);
        IngredientAdapter mIngredientAdapter ;
        if (ingredientArray.length == amountArray.length && ingredientArray.length == scaleArray.length) {
            for (int i = 0; i < ingredientArray.length; i++) {
                mIngredientObject.add(new BestRecipe.Ingredients(ingredientArray[i], amountArray[i], scaleArray[i]));
            }
        }else{
            Log.e(TAG,"There are missing information in the bestrecipe ingredients ");
        }
        if(mIngredientObject!= null) {
            mIngredientAdapter = new IngredientAdapter(mContext, 0, mIngredientObject);
            holder.mIngredientLV.setAdapter(mIngredientAdapter);
            Utils.dynamicallySetListViewHeight(holder.mIngredientLV);
        }

        String allSteps = currentOnlinerecipe.getSteps();
        String[] stepsArray = StringUtils.convertStringToArray(allSteps);
        ArrayAdapter<String> stepAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, stepsArray);
        holder.mStepsListLV.setAdapter(stepAdapter);
        Utils.dynamicallySetListViewHeight(holder.mStepsListLV);


        String firstImageAddress = currentOnlinerecipe.getImages().size() >= 1 ? currentOnlinerecipe.getImages().get(0): null;
        String secondImageAddress = currentOnlinerecipe.getImages().size() >= 2 ? currentOnlinerecipe.getImages().get(1): null;
        String thirdImageAddress = currentOnlinerecipe.getImages().size() >= 3 ? currentOnlinerecipe.getImages().get(2): null;
        String forthImageAddress = currentOnlinerecipe.getImages().size() >= 4 ? currentOnlinerecipe.getImages().get(3): null;

        holder.mListTitleTextView.setText(recipeTitle);
        holder.mListUserNameTextView.setText(userNameToshow);
        holder.mListCategoryTextView.setText(recipeCategory);
        String servingsString = mContext.getString(R.string.servings_txt)  +String.valueOf(servigs);
        holder.mListServingsTextView.setText(servingsString);
        holder.mListPrepTimeTextView.setText(String.valueOf(prepTime));
        holder.mListCookTimeTextView.setText(String.valueOf(cookingTime));
        holder.mListYeildTimeTextView.setText(String.valueOf(yeildTime));
        holder.mListTotalTimeTextView.setText(String.valueOf(totalTime));
        if (firstImageAddress != null ) Picasso.with(this.mContext).load(Uri.parse(firstImageAddress))
                .placeholder(R.drawable.pic_placeholder).fit().into(holder.mListIV1);
        if (secondImageAddress != null )Picasso.with(this.mContext).load(Uri.parse(secondImageAddress))
                .placeholder(R.drawable.pic_placeholder).fit().into(holder.mListIV2);
        if (thirdImageAddress != null )Picasso.with(this.mContext).load(Uri.parse(thirdImageAddress))
                .placeholder(R.drawable.pic_placeholder).fit().into(holder.mListIV3);
        if (forthImageAddress != null )Picasso.with(this.mContext).load(Uri.parse(forthImageAddress))
                .placeholder(R.drawable.pic_placeholder).fit().into(holder.mListIV4);
    }

    @Override
    public int getItemCount() {
        if(mOnlineRecipes != null){
            return mOnlineRecipes.size();
        }
        else
            return 0;
    }

    public class OnlineRecipeViewHolder extends RecyclerView.ViewHolder{

        TextView mListTitleTextView,mListUserNameTextView, mListCategoryTextView, mListServingsTextView
                ,mListPrepTimeTextView, mListCookTimeTextView,mListYeildTimeTextView
                ,mListTotalTimeTextView;
        ImageView mListIV1,mListIV2,mListIV3,mListIV4;
        ListView mIngredientLV, mStepsListLV;

        public OnlineRecipeViewHolder(View itemView) {
            super(itemView);
            mListTitleTextView = itemView.findViewById(R.id.od_list_title_tv);
            mListUserNameTextView = itemView.findViewById(R.id.od_list_username);
            mListCategoryTextView = itemView.findViewById(R.id.od_list_category_tv);
            mListServingsTextView = itemView.findViewById(R.id.od_list_servings_tv);
            mListPrepTimeTextView =itemView.findViewById(R.id.od_prep_time_tv_int);
            mListCookTimeTextView = itemView.findViewById(R.id.od_cook_time_tv_int);
            mListYeildTimeTextView = itemView.findViewById(R.id.od_yeild_time_tv_int);
            mListTotalTimeTextView = itemView.findViewById(R.id.od_list_total_time_tv_int);
            mListIV1 = itemView.findViewById(R.id.list_iv_1);
            mListIV2 = itemView.findViewById(R.id.list_iv_2);
            mListIV3 = itemView.findViewById(R.id.list_iv_3);
            mListIV4 = itemView.findViewById(R.id.list_iv_4);

            mIngredientLV = itemView.findViewById(R.id.od_ingredient_lv);
            mStepsListLV = itemView.findViewById(R.id.od_steps_lv);

        }
    }
}
