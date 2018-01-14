package com.cangetinshape.chefbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cangetinshape.chefbook.data.BestRecipeContract;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddNewRecipeActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private static final String FILE_PROVIDER_AUTHORITY = "com.cangetinshape.chefbook.fileprovider";

    private static final String TAG = AddNewRecipeActivity.class.getSimpleName();
    //private static final int RECIPE_PICTURE_LOADER_ID = 1;

    private String mTempPhotoPath;
    private ImageView mFirstImage, mSecondImage, mThirdImage, mForthImage;
    private Button mAddStep;
    private Bitmap mResultsBitmap;
    LinearLayout stepsContainer;
    LinearLayout ingredientContainer;
    private Button mAddIngredient;
    TextView ingredientHint;
    TextView stepsHint;
    private int mId;

    //instances for saveInstanceState
    private String[] ingredientArray;
    String[] ingredientAmountArray;
    String[] ingredientScaleArray;
    String[] allStepArray;

    private EditText mRecipeTitle, mServings, mPrepTime, mCookingTime, mYeildTime, mTotalTime;
    private Spinner mCategory;
    private List<EditText> mIngredients, mAmount, mUnit;
    private List<EditText> mSteps;

    String mFirstImageDir = null;
    String mSecondImageDir = null;
    String mThirdImageDir = null;
    String mForthImageDir = null;

    String mAllIngredientsString;
    String mAllAmountsString;
    String mAllScaleString;
    String mAllStepsString;

    private ContentValues mCv;
    private Uri mUri;

    ArrayList<String> mIngredientArray = new ArrayList<>();
    ArrayList<String> mIngredientAmountArray = new ArrayList<>();
    ArrayList<String> mScaleArray = new ArrayList<>();
    ArrayList<String> mStepsArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);
        //Images initialized
        mFirstImage = findViewById(R.id.first_image);
        mSecondImage = findViewById(R.id.second_image);
        mThirdImage = findViewById(R.id.third_image);
        mForthImage = findViewById(R.id.forth_image);
        //Initialize all the input parameters

        mRecipeTitle = findViewById(R.id.title_et);
        mServings = findViewById(R.id.servint_et);
        mCategory = findViewById(R.id.category_spinner);
        mPrepTime = findViewById(R.id.prep_time_et);
        mYeildTime = findViewById(R.id.yeild_time_et);
        mCookingTime = findViewById(R.id.cook_time_et);
        mTotalTime = findViewById(R.id.total_time_et);

        ingredientHint = findViewById(R.id.hint_ingredient_tv);
        stepsHint = findViewById(R.id.hint_steps_tv);

        // to add more ingeredient if needed
        ingredientContainer = findViewById(R.id.ingredient_container);
        mAddIngredient = findViewById(R.id.add_ingredient);
        mAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addIngredientView = layoutInflater.inflate(R.layout.ingredient_row, null);
                Button buttonRemove = addIngredientView.findViewById(R.id.remove_ingredient);
                final View.OnClickListener thisListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) addIngredientView.getParent()).removeView(addIngredientView);
                        //listAllIngredientsAddView();
                    }
                };
                buttonRemove.setOnClickListener(thisListener);
                ingredientContainer.addView(addIngredientView);
                ingredientHint.setVisibility(View.GONE);
                //listAllIngredientsAddView();
            }
        });

        // to add more steps if needed
        stepsContainer = findViewById(R.id.steps_container);
        mAddStep = findViewById(R.id.add_step);
        mAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.step_row, null);
                Button buttonRemove = addView.findViewById(R.id.remove_step);
                final View.OnClickListener thisListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) addView.getParent()).removeView(addView);
                        //listAllStepsAddView();
                    }
                };
                buttonRemove.setOnClickListener(thisListener);
                stepsContainer.addView(addView);
                stepsHint.setVisibility(View.GONE);
                //listAllStepsAddView();
            }
        });

    }

    public void takePicture(View view) {
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            //get the ImageView id clicked
            mId = view.getId();
            // Launch the camera if the permission exists
            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void launchCamera() {
        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
                //Log.i(TAG, "the photoFile path is:  " + photoFile.toString());
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();
                //Log.i(TAG, "the absolute path is : " + mTempPhotoPath);

                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);
                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Process the image and set it to the Next ImageView
            // Resample the saved image to fit the ImageView
            mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath);
            switch (mId) {
                case (R.id.first_image):
                    //mFirstImage.setImageBitmap(mResultsBitmap);
                    mFirstImageDir = BitmapUtils.saveImage(this, mResultsBitmap);
                    Picasso.with(this).load(new File(mFirstImageDir)).fit().into(mFirstImage);
                    break;
                case (R.id.second_image):
                    //mSecondImage.setImageBitmap(mResultsBitmap);
                    mSecondImageDir = BitmapUtils.saveImage(this, mResultsBitmap);
                    Picasso.with(this).load(new File(mSecondImageDir)).fit().into(mSecondImage);
                    break;
                case (R.id.third_image):
                    //mThirdImage.setImageBitmap(mResultsBitmap);
                    mThirdImageDir = BitmapUtils.saveImage(this, mResultsBitmap);
                    Picasso.with(this).load(new File(mThirdImageDir)).fit().into(mThirdImage);
                    break;
                case (R.id.forth_image):
                    //mForthImage.setImageBitmap(mResultsBitmap);
                    mForthImageDir = BitmapUtils.saveImage(this, mResultsBitmap);
                    Picasso.with(this).load(new File(mForthImageDir)).fit().into(mForthImage);
                    break;
            }
        } else {
            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath);
        }
    }

    public void saveRecipe(View view) {
        //check if any of the input text are empty
        if (mRecipeTitle.getText().length() == 0 || mPrepTime.getText().length() == 0 ||
                mCookingTime.getText().length() == 0 || mYeildTime.getText().length() == 0 ||
                mTotalTime.getText().length() == 0 || mServings.getText().length() == 0) {
            Toast.makeText(this, getString(R.string.complete_all_the_input_fields_alert), Toast.LENGTH_LONG).show();
            return;
        }
        listAllIngredientsAddView();
        mAllIngredientsString = StringUtils.convertArrayListToString(mIngredientArray);
        mAllAmountsString = StringUtils.convertArrayListToString(mIngredientAmountArray);
        mAllScaleString = StringUtils.convertArrayListToString(mScaleArray);

        listAllStepsAddView();
        mAllStepsString = StringUtils.convertArrayListToString(mStepsArray);

        addNewBestRecipe();
        finish();
    }

    private void addNewBestRecipe() {
        mCv = new ContentValues();
        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_RECIPE_TITLE, mRecipeTitle.getText().toString().trim());
        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_SERVINGS, Integer.parseInt(mServings.getText().toString()));
        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_CATEGORY, mCategory.getSelectedItem().toString());

        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_PREPARATION_TIME, Integer.parseInt(mPrepTime.getText().toString()));
        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_COOKING_TIME, Integer.parseInt(mCookingTime.getText().toString()));
        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_YEILD_TIME, Integer.parseInt(mYeildTime.getText().toString()));
        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_TOTAL_TIME, Integer.parseInt(mTotalTime.getText().toString()));

        if (mFirstImageDir != null)
            mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_1, mFirstImageDir);
        if (mSecondImageDir != null)
            mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_2, mSecondImageDir);
        if (mThirdImageDir != null)
            mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_3, mThirdImageDir);
        if (mForthImageDir != null)
            mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_PICTURE_IMAGE_4, mForthImageDir);

        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_INGREDIENTS, mAllIngredientsString);
        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_INGREDIENTS_AMOUNT, mAllAmountsString);
        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_SCALES, mAllScaleString);

        mCv.put(BestRecipeContract.BestRecipeEntry.COLUMN_STEPS, mAllStepsString);
        //Asyncronisly insert the data to database
        InsertToDatbaseLoader insertToDatbase = new InsertToDatbaseLoader();
        insertToDatbase.execute();

        if (mUri != null) {
            Toast.makeText(getBaseContext(), mUri.toString(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Servings saved as : " + mServings.getText().toString());
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class InsertToDatbaseLoader extends AsyncTask<Void, Void, Uri> {

        @Override
        protected Uri doInBackground(Void... voids) {
            return getContentResolver().insert(BestRecipeContract.BestRecipeEntry.CONTENT_URI, mCv);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            mUri = uri;
        }
    }

    // get all the input fields for Ingredient and add them to the proper Array
    private void listAllIngredientsAddView() {
        int childCount = ingredientContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View thisChild = ingredientContainer.getChildAt(i);
            EditText childEditTextIngredient = thisChild.findViewById(R.id.ingredient_et);
            String childTextIngredientViewValue = childEditTextIngredient.getText().toString().trim();
            EditText childEditTextIngredientAmount = thisChild.findViewById(R.id.ingredient_amount_et);
            String childTextIngredinetAmountValue = childEditTextIngredientAmount.getText().toString().trim();
            Spinner childSpinnerScale = thisChild.findViewById(R.id.scales_spinner);
            String childSpinnerScaleValue = childSpinnerScale.getSelectedItem().toString();
            mIngredientArray.add(childTextIngredientViewValue);
            mIngredientAmountArray.add(childTextIngredinetAmountValue);
            mScaleArray.add(childSpinnerScaleValue);
        }
    }

    //get the input Steps and  add them to mStepArray
    private void listAllStepsAddView() {
        int childCount = stepsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View thisChild = stepsContainer.getChildAt(i);
            EditText childEditTextSteps = thisChild.findViewById(R.id.step_et);
            String childTextViewStepsValue = childEditTextSteps.getText().toString().trim();
            mStepsArray.add(childTextViewStepsValue);
        }
    }

    @Override
    public void onBackPressed() {
        if (mRecipeTitle.getText().length() != 0 || mPrepTime.getText().length() != 0 ||
                mCookingTime.getText().length() != 0 || mYeildTime.getText().length() != 0 ||
                mServings.getText().length() != 0 || mTotalTime.getText().length() != 0
                || mFirstImageDir != null || mSecondImageDir != null || mThirdImageDir != null
                || mForthImageDir != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage(getString(R.string.return_from_add_new_recipe));
            builder.setPositiveButton(getString(R.string.yes_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    finish();
                }
            });
            builder.setNegativeButton(getString(R.string.no_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveImagesOnRotation(outState, mFirstImageDir, mSecondImageDir, mThirdImageDir, mForthImageDir);
        saveEditTextFields(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        retriveImagesOnRotation(savedInstanceState);
        retriveEditTextFileds(savedInstanceState);
    }

    public void saveImagesOnRotation(Bundle outState, String firstImageDir, String secondImageDir
            , String thirdImageDir, String forthImageDir) {
        if (firstImageDir != null) {
            outState.putString("first", firstImageDir);
        }
        if (secondImageDir != null) {
            outState.putString("second", secondImageDir);
        }
        if (thirdImageDir != null) {
            outState.putString("third", thirdImageDir);
        }
        if (forthImageDir != null) {
            outState.putString("forth", forthImageDir);
        }
    }

    public void retriveImagesOnRotation(Bundle savedInstanceState) {
        if (savedInstanceState.getString("first") != null) {
            mFirstImageDir = savedInstanceState.getString("first");
            Picasso.with(this).load(new File(mFirstImageDir)).fit().into(mFirstImage);
        }
        if (savedInstanceState.getString("second") != null) {
            mSecondImageDir = savedInstanceState.getString("first");
            Picasso.with(this).load(new File(mSecondImageDir)).fit().into(mSecondImage);
        }
        if (savedInstanceState.getString("third") != null) {
            mThirdImageDir = savedInstanceState.getString("first");
            Picasso.with(this).load(new File(mThirdImageDir)).fit().into(mThirdImage);
        }
        if (savedInstanceState.getString("forth") != null) {
            mForthImageDir = savedInstanceState.getString("first");
            Picasso.with(this).load(new File(mForthImageDir)).fit().into(mForthImage);
        }
    }

    public void saveEditTextFields(Bundle outState) {
        if (ingredientContainer.getChildCount() > 0) {
            listAllIngredientsAddView();
            String allIngredient = StringUtils.convertArrayListToString(mIngredientArray);
            outState.putString("ingredients", allIngredient);
            String allIngredientAmount = StringUtils.convertArrayListToString(mIngredientAmountArray);
            outState.putString("amounts", allIngredientAmount);
            String allScaleAmount = StringUtils.convertArrayListToString(mScaleArray);
            outState.putString("scales", allScaleAmount);
        }
        if (stepsContainer.getChildCount() > 0) {
            listAllStepsAddView();
            String allSteps = StringUtils.convertArrayListToString(mStepsArray);
            outState.putString("steps", allSteps);
        }
    }

    public void retriveEditTextFileds(Bundle savedInstanceState) {

        String allIngredient = savedInstanceState.getString("ingredients");
        if (allIngredient != null) {
            ingredientArray = StringUtils.convertStringToArray(allIngredient);
        }
        String allIngredientAmount = savedInstanceState.getString("amounts");
        if (allIngredientAmount != null) {
            ingredientAmountArray = StringUtils.convertStringToArray(allIngredientAmount);
        }
        String allScaleAmount = savedInstanceState.getString("scales");
        if (allScaleAmount != null) {
            ingredientScaleArray = StringUtils.convertStringToArray(allScaleAmount);
        }
        //get the maximum length of arrays
        int maximumLength = maximumLengthStringArray(ingredientArray, ingredientAmountArray, ingredientScaleArray);
        for (int i = 0; i < maximumLength; i++) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addIngredientView = layoutInflater.inflate(R.layout.ingredient_row, null);
            Button buttonRemove = addIngredientView.findViewById(R.id.remove_ingredient);
            final View.OnClickListener thisListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LinearLayout) addIngredientView.getParent()).removeView(addIngredientView);
                    //listAllIngredientsAddView();
                }
            };
            buttonRemove.setOnClickListener(thisListener);
            ingredientContainer.addView(addIngredientView);
            View thisChild = ingredientContainer.getChildAt(i);
            if (ingredientArray != null && ingredientArray.length > i) {
                if (ingredientArray[i] != null) {
                    EditText childEditTextIngredient = thisChild.findViewById(R.id.ingredient_et);
                    childEditTextIngredient.setText(ingredientArray[i]);
                }
            }
            if (ingredientAmountArray != null && ingredientAmountArray.length > i) {
                if (ingredientAmountArray[i] != null) {
                    EditText childEditTextIngredientAmount = thisChild.findViewById(R.id.ingredient_amount_et);
                    childEditTextIngredientAmount.setText(ingredientAmountArray[i]);
                }
            }
            if (ingredientScaleArray != null && ingredientScaleArray.length > i) {
                if (ingredientScaleArray[i] != null) {
                    Spinner childEditTextIngredientAmount = thisChild.findViewById(R.id.scales_spinner);

                    String compareValue = ingredientScaleArray[i];
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.scales_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    childEditTextIngredientAmount.setAdapter(adapter);
                    if (compareValue != null) {
                        int spinnerPosition = adapter.getPosition(compareValue);
                        childEditTextIngredientAmount.setSelection(spinnerPosition);
                    }
                }
            }

            ingredientHint.setVisibility(View.GONE);
        }

        String allSteps = savedInstanceState.getString("steps");
        if (allSteps != null) {
            allStepArray = StringUtils.convertStringToArray(allSteps);
        }
        int stepArrayLength = allStepArray != null ? allStepArray.length : 0;
        for (int i = 0; i < stepArrayLength; i++) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.step_row, null);
            Button buttonRemove = addView.findViewById(R.id.remove_step);
            final View.OnClickListener thisListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LinearLayout) addView.getParent()).removeView(addView);

                }
            };
            buttonRemove.setOnClickListener(thisListener);
            stepsContainer.addView(addView);
            View thisChild = stepsContainer.getChildAt(i);
            EditText childEditTextSteps = thisChild.findViewById(R.id.step_et);
            childEditTextSteps.setText(allStepArray[i]);

            stepsHint.setVisibility(View.GONE);
        }
    }

    //helper method to find the maximum length of 3 nullable string array
    public int maximumLengthStringArray(String[] aStringArray, String[] bStringArray, String[] cStringArray) {

        if (aStringArray != null) {
            if (bStringArray != null) {
                if (cStringArray != null) {
                    return Math.max(Math.max(aStringArray.length, bStringArray.length)
                            , Math.max(aStringArray.length, cStringArray.length));
                } else {
                    return Math.max(aStringArray.length, bStringArray.length);
                }
            } else {
                return aStringArray.length;
            }
        } else if (bStringArray != null) {
            if (cStringArray != null) {
                return Math.max(bStringArray.length, cStringArray.length);
            } else {
                return bStringArray.length;
            }
        } else if (cStringArray != null) {
            return cStringArray.length;
        }
        return 0;
    }

}
