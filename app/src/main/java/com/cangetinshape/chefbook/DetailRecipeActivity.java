package com.cangetinshape.chefbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cangetinshape.chefbook.Adapter.IngredientAdapter;
import com.cangetinshape.chefbook.data.BestRecipeContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class DetailRecipeActivity extends AppCompatActivity {

    private static final String TAG = DetailRecipeActivity.class.getSimpleName();

    BestRecipe mCurrentBestRecipe;
    BestRecipe mToServerBestRecipe;
    String[] mIngredient;
    String[] mAmounts;
    String[] mScales;
    //mIngredientObject contains all the three mIngredient, mAmounts, mScales
    ArrayList<BestRecipe.Ingredients> mIngredientObject = new ArrayList<>();
    String[] mSteps;
    private IngredientAdapter mIngredientAdapter;

    ImageView mainImageView, secondImageView, thirdImageView, forthImageView;
    int recepieId;
    TextView titleTV;
    String[] imageAddress;

    FirebaseUser mUser;
    //FirebaseAuth mFirebaseAuth;
    //This instance variable is the Entry Point that our app has access to database
    FirebaseDatabase mFirebaseDatabase;
    //this instance is to access specific part of database
    DatabaseReference mRecipeDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mRecipePhotoStorageReference;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe);

        //mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        Toolbar myToolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);
        //mUserNameTV = findViewById(R.id.user_name__tv);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mAdView = findViewById(R.id.ad_view_detail);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        titleTV = findViewById(R.id.title_tv);
        TextView categoryTV = findViewById(R.id.category_tv_toshow);
        TextView creatorTV = findViewById(R.id.added_by_tv);

        mainImageView = findViewById(R.id.main_iv);
        secondImageView = findViewById(R.id.second_iv);
        thirdImageView = findViewById(R.id.third_iv);
        forthImageView = findViewById(R.id.forth_iv);

        TextView servingsTV = findViewById(R.id.recipe_serving_tv);

        TextView prepTimeTV = findViewById(R.id.prep_time_tv_int);
        TextView yeildTImeTV = findViewById(R.id.yeild_time_tv_int);
        TextView cookingTimeTV = findViewById(R.id.cook_time_tv_int);
        TextView totalTimeTV = findViewById(R.id.list_total_time_tv_int);

        Intent intent = getIntent();
        recepieId = intent.getIntExtra("RECEPIE_ID", 0);
        mCurrentBestRecipe = (BestRecipe) intent.getSerializableExtra("BEST_RECIPE");
        String uniqueChildName = mCurrentBestRecipe.getRecipeTitle() + mCurrentBestRecipe.getUserID();

        mToServerBestRecipe = mCurrentBestRecipe;

        mRecipeDatabaseReference = mFirebaseDatabase.getReference().child("recipes").child(uniqueChildName);
        mRecipePhotoStorageReference = mFirebaseStorage.getReference().child("photos");

        if (mCurrentBestRecipe != null) {
            titleTV.setText(mCurrentBestRecipe.getRecipeTitle());
            categoryTV.setText(mCurrentBestRecipe.getCategory());
            if (mUser != null) {
                creatorTV.setText(mUser.getDisplayName());
            }

            imageAddress = new String[]{mCurrentBestRecipe.getImages().size() >= 1 ? mCurrentBestRecipe.getImages().get(0) : null,
                    mCurrentBestRecipe.getImages().size() >= 2 ? mCurrentBestRecipe.getImages().get(1) : null,
                    mCurrentBestRecipe.getImages().size() >= 3 ? mCurrentBestRecipe.getImages().get(2) : null,
                    mCurrentBestRecipe.getImages().size() >= 4 ? mCurrentBestRecipe.getImages().get(3) : null};
            loadImages();

            String servingsString = getResources().getString(R.string.servings_txt) +
                    " " + String.valueOf(mCurrentBestRecipe.getServings());
            servingsTV.setText(servingsString);

            String timeSuffix = " " + getResources().getString(R.string.prep_time_hint_text);
            String prepTimeStr = String.valueOf(mCurrentBestRecipe.getPrepTime()) + timeSuffix;
            String cookingTimeStr = String.valueOf(mCurrentBestRecipe.getCookingTime()) + timeSuffix;
            String yeildTimeStr = String.valueOf(mCurrentBestRecipe.getYeildTime()) + timeSuffix;
            String totalTimeStr = String.valueOf(mCurrentBestRecipe.getTotalTime()) + timeSuffix;

            prepTimeTV.setText(prepTimeStr);
            cookingTimeTV.setText(cookingTimeStr);
            yeildTImeTV.setText(yeildTimeStr);
            totalTimeTV.setText(totalTimeStr);

            String allIngredient = mCurrentBestRecipe.getIngredients();
            mIngredient = StringUtils.convertStringToArray(allIngredient);
            String allAmounts = mCurrentBestRecipe.getAmount();
            mAmounts = StringUtils.convertStringToArray(allAmounts);
            String allScales = mCurrentBestRecipe.getScale();
            mScales = StringUtils.convertStringToArray(allScales);
            if (mIngredient.length == mAmounts.length && mIngredient.length == mScales.length) {
                for (int i = 0; i < mIngredient.length; i++) {
                    Log.i(TAG, mIngredient[i] + " ...." + mAmounts[i] + "...." + mScales[i] + ".....");
                    mIngredientObject.add(new BestRecipe.Ingredients(mIngredient[i], mAmounts[i], mScales[i]));
                }
            } else {
                Log.e(TAG, "There are missing information in the bestrecipe ingredients ");
            }

            ListView ingredientListView = findViewById(R.id.ingredient_lv);
            if (mIngredientObject != null) {
                mIngredientAdapter = new IngredientAdapter(this, 0, mIngredientObject);
                ingredientListView.setAdapter(mIngredientAdapter);
                Utils.dynamicallySetListViewHeight(ingredientListView);
            }

            String allSteps = mCurrentBestRecipe.getSteps();
            mSteps = StringUtils.convertStringToArray(allSteps);
            ArrayAdapter<String> stepAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, mSteps);
            ListView stepListView = findViewById(R.id.steps_lv);
            stepListView.setAdapter(stepAdapter);
            Utils.dynamicallySetListViewHeight(stepListView);
        }

        //on click the smaler size images switch the small size with the large image in the UI
        secondImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapImageadress(v);
            }
        });
        thirdImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapImageadress(v);
            }
        });
        forthImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapImageadress(v);
            }
        });

    }

    //helper method to load 4 images if available and if not load the placeholder
    public void loadImages() {

        //check for main Picture
        if (imageAddress[0] == null) {
            Picasso.with(this).load(R.drawable.pic_placeholder).fit().into(mainImageView);
        } else {
            Picasso.with(this).load(new File(imageAddress[0])).fit()
                    .placeholder(R.drawable.pic_placeholder).error(R.drawable.pic_placeholder).into(mainImageView);
        }
        //check for second Picture
        if (imageAddress[1] == null) {
            Picasso.with(this).load(R.drawable.pic_placeholder).fit().into(secondImageView);
        } else {
            Picasso.with(this).load(new File(imageAddress[1])).fit()
                    .placeholder(R.drawable.pic_placeholder).error(R.drawable.pic_placeholder).into(secondImageView);
        }
        //check for third Picture
        if (imageAddress[2] == null) {
            Picasso.with(this).load(R.drawable.pic_placeholder).fit().into(thirdImageView);
        } else {
            Picasso.with(this).load(new File(imageAddress[2])).fit()
                    .placeholder(R.drawable.pic_placeholder).error(R.drawable.pic_placeholder).into(thirdImageView);
        }
        //check for forth Picture
        if (imageAddress[3] == null) {
            Picasso.with(this).load(R.drawable.pic_placeholder).fit().into(forthImageView);
        } else {
            Picasso.with(this).load(new File(imageAddress[3])).fit()
                    .placeholder(R.drawable.pic_placeholder).error(R.drawable.pic_placeholder).into(forthImageView);
        }
    }

    //helper method to switch the images in the UI
    public void swapImageadress(View v) {
        int imageViewId = v.getId();

        switch (imageViewId) {
            case (R.id.second_iv):
                String temp = imageAddress[0];
                imageAddress[0] = imageAddress[1];
                imageAddress[1] = temp;
                break;
            case (R.id.third_iv):
                temp = imageAddress[0];
                imageAddress[0] = imageAddress[2];
                imageAddress[2] = temp;
                break;
            case (R.id.forth_iv):
                temp = imageAddress[0];
                imageAddress[0] = imageAddress[3];
                imageAddress[3] = temp;
                break;
        }
        loadImages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem saveOnlineItem = menu.findItem(R.id.action_save_online);
        if (mUser != null) {
            saveOnlineItem.setEnabled(true);
            saveOnlineItem.setVisible(true);
            return true;
        } else {
            saveOnlineItem.setEnabled(false);
            saveOnlineItem.setVisible(false);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            AlertDialog deletAlert = confirmDelete();
            deletAlert.show();
            return true;
        }
        if (id == R.id.action_share) {
            startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(getString(R.string.share_invitation_text))
                    .getIntent(), getString(R.string.action_share)));
            return true;
        }
        if (id == R.id.action_save_online) {
            final ArrayList<String> onlineImagesAddress = new ArrayList<>();
            if (mCurrentBestRecipe.getImages().size() > 0) {

                for (int i = 0; i < mCurrentBestRecipe.getImages().size(); i++) {
                    Uri imageUri = Uri.fromFile(new File(mCurrentBestRecipe.getImages().get(i)));
                    StorageReference imageRef = mRecipePhotoStorageReference.child(imageUri.getLastPathSegment());
                    imageRef.putFile(imageUri)
                            .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // When the image has successfully uploaded, we get its download URL
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    onlineImagesAddress.add(taskSnapshot.getDownloadUrl().toString());
                                    mToServerBestRecipe.setImages(onlineImagesAddress);
                                    mRecipeDatabaseReference.setValue(mToServerBestRecipe);
                                }
                            });
                }
                Toast.makeText(this, getString(R.string.saving_to_database_alert_text), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.saving_to_database_with_noimage), Toast.LENGTH_SHORT).show();
                mToServerBestRecipe.setImages(null);
                mRecipeDatabaseReference.setValue(mToServerBestRecipe);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //create AlertDialog for deleting the current database item
    private AlertDialog confirmDelete() {
        return (new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle(getString(R.string.delete_text))
                .setMessage(getString(R.string.dialog_delet_text))
                .setIcon(R.drawable.ic_delete_forever_black_24dp)
                .setPositiveButton(getString(R.string.delete_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //delete the item in database
                        // Build appropriate uri with String row id appended
                        String stringId = Integer.toString(recepieId);
                        Uri uri = BestRecipeContract.BestRecipeEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(stringId).build();
                        try {
                            int numberOfRows = getContentResolver().delete(uri, null, null);
                            if (numberOfRows == 1) {
                                Toast.makeText(DetailRecipeActivity.this, getString(R.string.deletion_confirm_text) + titleTV.getText() + getString(R.string.recipe_toast_text), Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, getString(R.string.failed_to_load_data));
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create());
    }

}
