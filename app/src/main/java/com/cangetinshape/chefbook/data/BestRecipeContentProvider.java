package com.cangetinshape.chefbook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Amir on 12/10/2017.
 */

public class BestRecipeContentProvider extends ContentProvider {
    public static final int RECIPE = 100;
    public static final int RECIPE_WITH_ID = 101;
    //public static final int RECIPE_WITH_CATEGORY = 102;

    private BestRecipeDbHelper mBestRecipeDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mBestRecipeDbHelper = new BestRecipeDbHelper(context);
        return false;
    }

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(BestRecipeContract.AUTHORITY, BestRecipeContract.PATH_MYBESTRECIPES, RECIPE);
        uriMatcher.addURI(BestRecipeContract.AUTHORITY, BestRecipeContract.PATH_MYBESTRECIPES + "/#", RECIPE_WITH_ID);
        //uriMatcher.addURI(BestRecipeContract.AUTHORITY, BestRecipeContract.PATH_MYBESTRECIPES + "/*", RECIPE_WITH_CATEGORY);
        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mBestRecipeDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;
        switch (match){
            case RECIPE:
                returnCursor = db.query(BestRecipeContract.BestRecipeEntry.TABLE_NAME,
                         projection, selection, selectionArgs,null,null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+ uri );
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mBestRecipeDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case RECIPE:
                long id = db.insert(BestRecipeContract.BestRecipeEntry.TABLE_NAME, null,values);
                if(id>0){//success
                    returnUri = ContentUris.withAppendedId(BestRecipeContract.BestRecipeEntry.CONTENT_URI,id);
                }else{
                    throw new android.database.SQLException("Fail to insert row " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unkown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String  selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mBestRecipeDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int tasksDeleted = 0;
        switch (match){
            case RECIPE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);

                tasksDeleted = db.delete(BestRecipeContract.BestRecipeEntry.TABLE_NAME,
                        "_id=?",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
