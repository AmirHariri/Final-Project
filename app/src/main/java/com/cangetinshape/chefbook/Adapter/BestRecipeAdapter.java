package com.cangetinshape.chefbook.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cangetinshape.chefbook.R;
import com.cangetinshape.chefbook.data.BestRecipeContract;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Amir on 12/11/2017.
 */

public class BestRecipeAdapter extends RecyclerView.Adapter<BestRecipeAdapter.BestRecipeViewolder> {

    private Cursor mCursor;
    private Context mContext;

    private OnItemClickListener listener;

    public BestRecipeAdapter(Context context) {
        //this.mCursor = cursor;
        this.mContext =context;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public BestRecipeViewolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View recipeView = inflater.inflate(R.layout.recipe_list_item, parent, false);
        //recipeView.setFocusable(true);
        return new BestRecipeViewolder(recipeView);
    }

    @Override
    public void onBindViewHolder(BestRecipeViewolder holder, int position) {

            mCursor.moveToPosition(position);
            int idIndex = mCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry._ID);
            int titleIndex = mCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE);
            int categoryIndex = mCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_CATEGORY);
            int servingsIndex = mCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_SERVINGS);
            int totalTimeIndex = mCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_TOTAL_TIME);

            int firstImageIndex = mCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_1);

            int id = mCursor.getInt(idIndex);
            String recipeTitle = mCursor.getString(titleIndex);
            String recipeCategory = mCursor.getString(categoryIndex);
            int recipeServings = mCursor.getInt(servingsIndex);
            int recipeTotalTime = mCursor.getInt(totalTimeIndex);

            if (firstImageIndex>0) {
                String firstImagePath = mCursor.getString(firstImageIndex);
                if (firstImagePath != null )Picasso.with(this.mContext).load(new File(firstImagePath))
                        .placeholder(R.drawable.pic_placeholder).fit().into(holder.mListImageView);
            }

            holder.mListTitleTextView.setText(recipeTitle);
            holder.mListCategoryTextView.setText(recipeCategory);
            String servingsString = "Servings: " + String.valueOf(recipeServings);
            holder.mListServingsTextView.setText(servingsString);
            String totalTimeString = "Total Time: " + String.valueOf(recipeTotalTime);
            holder.mSListTotalTimeTextView.setText(totalTimeString);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor){
            return 0;
        }
        return mCursor.getCount();
    }


    public class BestRecipeViewolder extends RecyclerView.ViewHolder{
        ImageView mListImageView;
        TextView mListTitleTextView, mListServingsTextView, mListCategoryTextView,
                 mSListTotalTimeTextView;

        public BestRecipeViewolder(final View itemView) {
            super(itemView);
            mListImageView = itemView.findViewById(R.id.list_iv);
            mListTitleTextView = itemView.findViewById(R.id.list_title_tv);
            mListCategoryTextView = itemView.findViewById(R.id.list_category_tv);
            mListServingsTextView = itemView.findViewById(R.id.list_servings_tv);
            mSListTotalTimeTextView = itemView.findViewById(R.id.list_total_time_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned
        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
