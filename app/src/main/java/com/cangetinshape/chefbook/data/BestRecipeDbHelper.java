package com.cangetinshape.chefbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Amir on 12/8/2017.
 */

public class BestRecipeDbHelper extends SQLiteOpenHelper {
   // The database name
    private static final String DATABASE_NAME = "bestrecipe.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public BestRecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BESTRECIPE_TABLE = "CREATE TABLE " +
                BestRecipeContract.BestRecipeEntry.TABLE_NAME + " (" +

                BestRecipeContract.BestRecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE + " TEXT NOT NULL, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_CREATOR + " TEXT, " +

                BestRecipeContract.BestRecipeEntry.COLUMN_SERVINGS + " INTEGER NOT NULL, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +

                BestRecipeContract.BestRecipeEntry.COLUMN_PREPARATION_TIME + " INTEGER NOT NULL, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_COOKING_TIME + " INTEGER NOT NULL, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_YEILD_TIME + " INTEGER NOT NULL, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_TOTAL_TIME + " INTEGER NOT NULL, " +


                BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_1 + " TEXT, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_2 + " TEXT, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_3 + " TEXT, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_4 + " TEXT, " +


                BestRecipeContract.BestRecipeEntry.COLUMN_INGREDIENTS + " TEXT, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_INGREDIENTS_AMOUNT + " TEXT, " +
                BestRecipeContract.BestRecipeEntry.COLUMN_SCALES + " TEXT, " +

                BestRecipeContract.BestRecipeEntry.COLUMN_STEPS + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_BESTRECIPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BestRecipeContract.BestRecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}

