package com.cangetinshape.chefbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cangetinshape.chefbook.Adapter.BestRecipeAdapter;
import com.cangetinshape.chefbook.Widget.RecipeWidgetProvider;
import com.cangetinshape.chefbook.data.BestRecipeContract;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RECIPE_LOADER_ID = 0;
    public static final int RC_SIGN_IN = 1;
    private static final String CURRENT_POSITION = "position";
    private static final String FIRST_RUN = "firstrun";

    // Member variables for the adapter and RecyclerView
    RecyclerView mRecyclerView;
    BestRecipeAdapter bestRecipeAdapter;
    static int TABLET_COLUMN = 2;
    static int SMART_PONE_CULOMN = 1;
    private Cursor mRecipeCursor;
    BestRecipe mRecipeForWidget;
    private int mPosition;

    public String mUserName;
    //Firebase Instance variables
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser mUser;
    TextView mUserNameTV;
    String mUserID;

    private AdView mAdView;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mUserNameTV = findViewById(R.id.user_name__tv);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        //ab.setTitle("MyList");

        mAdView = findViewById(R.id.ad_view);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mFirebaseAuth = FirebaseAuth.getInstance();


        mRecyclerView = findViewById(R.id.recycler_view);
        bestRecipeAdapter = new BestRecipeAdapter(this);
        mRecyclerView.setAdapter(bestRecipeAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, returnGridViewColumnBaseOnScreenSize(this)));

        bestRecipeAdapter.setOnItemClickListener(new BestRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                mPosition = position;
                //get all the information from the current recipe cursor and put it to BestRecipe object
                boolean movedToPosition = mRecipeCursor.moveToPosition(position);
                int recipeIdColumn = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry._ID);
                int recipeId = mRecipeCursor.getInt(recipeIdColumn);
                int titleColumn = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE);
                String title = mRecipeCursor.getString(titleColumn);
                int servingsId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_SERVINGS);
                int servings = mRecipeCursor.getInt(servingsId);
                int categoryId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_CATEGORY);
                String category = mRecipeCursor.getString(categoryId);
                int prepTimeId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_PREPARATION_TIME);
                int prepTime = mRecipeCursor.getInt(prepTimeId);
                int cookingTimeId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_COOKING_TIME);
                int cookingTime = mRecipeCursor.getInt(cookingTimeId);
                int yeildTimeId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_YEILD_TIME);
                int yeildTime = mRecipeCursor.getInt(yeildTimeId);
                int totalTimeId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_TOTAL_TIME);
                int totalTime = mRecipeCursor.getInt(totalTimeId);

                int ingredientId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_INGREDIENTS);
                String ingredients = mRecipeCursor.getString(ingredientId);
                int amountId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_INGREDIENTS_AMOUNT);
                String amounts = mRecipeCursor.getString(amountId);
                int scaleId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_SCALES);
                String scales = mRecipeCursor.getString(scaleId);
                int stepId = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_STEPS);
                String steps = mRecipeCursor.getString(stepId);

                int image1Id = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_1);
                String image1Dir = mRecipeCursor.getString(image1Id);
                int image2Id = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_2);
                String image2Dir = mRecipeCursor.getString(image2Id);
                int image3Id = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_3);
                String image3Dir = mRecipeCursor.getString(image3Id);
                int image4Id = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_4);
                String image4Dir = mRecipeCursor.getString(image4Id);
                ArrayList<String> imagesDir = new ArrayList<>();
                if (image1Dir != null) imagesDir.add(image1Dir);
                if (image2Dir != null) imagesDir.add(image2Dir);
                if (image3Dir != null) imagesDir.add(image3Dir);
                if (image4Dir != null) imagesDir.add(image4Dir);

                //initialize bestRecipe object
                BestRecipe bestRecipe = new BestRecipe(title, category, servings, prepTime, cookingTime,
                        yeildTime, totalTime, ingredients, amounts, scales, steps, imagesDir, mUserName, mUserID);
                mRecipeForWidget = bestRecipe;

                Intent detaileRecepieActivityIntent = new Intent(itemView.getContext(), DetailRecipeActivity.class);
                detaileRecepieActivityIntent.putExtra("RECEPIE_ID", recipeId);
                detaileRecepieActivityIntent.putExtra("BEST_RECIPE", bestRecipe);
                startActivity(detaileRecepieActivityIntent);
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION)) {
            mPosition = savedInstanceState.getInt(CURRENT_POSITION);
        }
        FloatingActionButton addFAB = findViewById(R.id.add_fab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewRecipeIntent = new Intent(MainActivity.this, AddNewRecipeActivity.class);
                startActivity(addNewRecipeIntent);
            }
        });
        getSupportLoaderManager().initLoader(RECIPE_LOADER_ID, null, this);
        sendDataToWidget();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mUser = user;
                if (user != null) {
                    //user is signed in
                    onSignedInInitialize(user.getDisplayName());
                    mUserID = user.getUid();
                    //Toast.makeText(MainActivity.this, "You are now signed in, Wellcome to ChefBook!",Toast.LENGTH_LONG).show();
                } else {
                    //user is signed out
                    onSignedOutCleanUp();

                    SharedPreferences signInPreference = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    boolean isFirstRun = signInPreference.getBoolean(FIRST_RUN, true);
                    if (isFirstRun) {
                        // Code to run once
                        AlertDialog signInAlert = confirmSignIn();
                        signInAlert.show();

                        SharedPreferences.Editor editor = signInPreference.edit();
                        editor.putBoolean(FIRST_RUN, false);
                        editor.commit();
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.signed_in_text), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.sign_in_cancel_text), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {
            // Initialize a Cursor, this will hold all the recipe data
            Cursor mRecipeData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mRecipeData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mRecipeData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(BestRecipeContract.BestRecipeEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE);
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.failed_to_load_data));
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mRecipeData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bestRecipeAdapter.swapCursor(data);
        mRecipeCursor = data;
        sendDataToWidget();
        if (mPosition != RecyclerView.NO_POSITION) {
            mRecyclerView.findViewHolderForAdapterPosition(mPosition);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(RECIPE_LOADER_ID, null, this);
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bestRecipeAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem signInItem = menu.findItem(R.id.action_sign_in);
        MenuItem signOutItem = menu.findItem(R.id.action_sign_out);
        MenuItem browseOnline = menu.findItem(R.id.action_online_recipes);
        if (mUser != null) {
            signOutItem.setEnabled(true);
            signOutItem.setVisible(true);
            signInItem.setEnabled(false);
            signInItem.setVisible(false);
            browseOnline.setEnabled(true);
            browseOnline.setVisible(true);
            return true;
        } else {
            signInItem.setEnabled(true);
            signInItem.setVisible(true);
            signOutItem.setEnabled(false);
            signOutItem.setVisible(false);
            browseOnline.setEnabled(false);
            browseOnline.setVisible(false);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //Intent settingsIntent = new Intent(this, SettingsActivity.class);
            //startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_sign_out) {
            AuthUI.getInstance().signOut(this);
            onSignedOutCleanUp();
            setUserDisplayName();
            this.invalidateOptionsMenu();
            return true;
        }
        if (id == R.id.action_sign_in) {
            onSignedOutCleanUp();
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
            this.invalidateOptionsMenu();
            return true;
        }
        if (id == R.id.action_online_recipes) {
            Intent sharedOnlineIntent = new Intent(this, OnlineRecipesActivity.class);
            startActivity(sharedOnlineIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendDataToWidget() {

        if (mRecipeCursor != null && !mRecipeCursor.isClosed()) {
            final int firstImageIndex = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_1);
            final int recipeTitleIndex = mRecipeCursor.getColumnIndex(BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE);
            ArrayList<String> imageArrays = new ArrayList<>();
            ArrayList<String> titleArrays = new ArrayList<>();
            for (int i = 0; i <= mRecipeCursor.getCount(); i++) {
                if (mRecipeCursor.moveToPosition(i)) {
                    String firstImagePath = mRecipeCursor.getString(firstImageIndex);
                    String title = mRecipeCursor.getString(recipeTitleIndex);
                    imageArrays.add(firstImagePath);
                    titleArrays.add(title);
                }
            }
            String imagesString = StringUtils.convertArrayListToString(imageArrays);
            String titlesString = StringUtils.convertArrayListToString(titleArrays);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE, titlesString).apply();
            editor.putString(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_1, imagesString).apply();
            //Log.e(TAG, titlesString  + imagesString);
            //refresh the widget after openning the app
            RecipeWidgetProvider.sendRefreshBroadcast(this);
        }
    }

    private void onSignedInInitialize(String displayName) {
        mUserName = displayName;
        setUserDisplayName();
    }

    private void onSignedOutCleanUp() {

        mUserName = "";
    }

    private void setUserDisplayName() {
        String displayNametxt;
        if (mUserName.equals("")) displayNametxt = mUserName;
        else displayNametxt = getString(R.string.sign_in_as) + mUserName;
        mUserNameTV.setText(displayNametxt);
    }

    //create AlertDialog to check if user wants to sign in or not
    private AlertDialog confirmSignIn() {
        return (new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle(getString(R.string.sign_in_text))
                .setMessage(getString(R.string.sign_in_now_text))
                //.setIcon(R.drawable.ic_delete_forever_black_24dp)
                .setPositiveButton(getString(R.string.sign_in_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setTheme(R.style.FirebaseLoginTheme)
                                        .setLogo(R.drawable.logo_sign_in)
                                        .setIsSmartLockEnabled(false)
                                        .setAvailableProviders(
                                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                        .build(),
                                RC_SIGN_IN);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.later_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create());
    }

    //helper method to check for the device screen size
    public static int returnGridViewColumnBaseOnScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            return TABLET_COLUMN;
        } else {
            return SMART_PONE_CULOMN;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(CURRENT_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

}
