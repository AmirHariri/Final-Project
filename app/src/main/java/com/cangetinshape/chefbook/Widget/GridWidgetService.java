package com.cangetinshape.chefbook.Widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.cangetinshape.chefbook.R;
import com.cangetinshape.chefbook.StringUtils;
import com.cangetinshape.chefbook.data.BestRecipeContract;

/**
 * Created by Amir on 12/26/2017.
 */

public class GridWidgetService extends RemoteViewsService {
    private static final String LOG_TAG = GridWidgetService.class.getSimpleName();
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.e(LOG_TAG, "onGetViewFactory: " + "Service called");
        return new GridRemoteViewsFactory(this.getApplicationContext(),intent);
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String LOG_TAG = GridWidgetService.class.getSimpleName();
    Context mContext;
    Cursor mCursor;

    String[] mRecipeTitle;
    String[] mImageDir;

    public GridRemoteViewsFactory(Context applicationContext,Intent intent) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        String titlesString = preferences.getString
                (BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE,"");
        String imagesString = preferences.getString
                (BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_1, "");

        mRecipeTitle = StringUtils.convertStringToArray(titlesString);
        mImageDir = StringUtils.convertStringToArray(imagesString);
        Log.e(LOG_TAG,"the titles passed to Widget is : "+ mRecipeTitle);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (mRecipeTitle == null) return 0;
        return mRecipeTitle.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mRecipeTitle == null ) return null;

        String firstImageDir = mImageDir[position];
        String recipeTitle = mRecipeTitle[position];

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget);

        Bitmap bitmap = BitmapFactory.decodeFile(mImageDir[position]);
        views.setImageViewBitmap(R.id.widget_recipe_image, bitmap);
        views.setTextViewText(R.id.widget_recipe_title, mRecipeTitle[position]);

            Bundle extras = new Bundle();
            //extras.putInt(BestRecipeContract.BestRecipeEntry._ID, id);
            extras.putString(BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE, recipeTitle);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widget_recipe_image, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}



