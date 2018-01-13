package com.cangetinshape.chefbook.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Amir on 12/8/2017.
 */

public class BestRecipeContract {

    // The authority, which is how the code knows which Content Provider to access
    public static final String AUTHORITY = "com.cangetinshape.chefbook";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "favoritelist" directory
    public static final String PATH_FAVORITELIST = "favoritelist";
    // This is the path for the "myRecipeList" directory
    public static final String PATH_MYBESTRECIPES = "mybestrecipes";



    public static final class BestRecipeEntry implements BaseColumns{
        // FavoriteRecipeListEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MYBESTRECIPES).build();


        public static final String TABLE_NAME = "bestrecipelist";
        public static final String COLUMN_RECIPE_ID = "recipeid";
        public static final String COLUMN_RECIPE_TITLE = "title";
        public static final String COLUMN_RECIPE_CREATOR = "creatorname";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PREPARATION_TIME = "preptime";
        public static final String COLUMN_COOKING_TIME = "cookingtime";
        public static final String COLUMN_YEILD_TIME = "yeildtime";
        public static final String COLUMN_TOTAL_TIME = "totaltime";
        public static final String COLUMN_PICTURE_IMAGE_1 = "pictureimage1";
        public static final String COLUMN_PICTURE_IMAGE_2 = "pictureimage2";
        public static final String COLUMN_PICTURE_IMAGE_3 = "pictureimage3";
        public static final String COLUMN_PICTURE_IMAGE_4 = "pictureimage4";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        public static final String COLUMN_INGREDIENTS_AMOUNT = "amount";
        public static final String COLUMN_SCALES = "scales";
        public static final String COLUMN_STEPS = "steps";

        public static final long INVALID_PLANT_ID = -1;


    }
}
