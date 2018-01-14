package com.cangetinshape.chefbook;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.cangetinshape.chefbook.Adapter.OnlineRecipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OnlineRecipesActivity extends AppCompatActivity {

    TextView mUserNameTV;
    FirebaseUser mUser;
    List<BestRecipe> mOnlineRecipes;
    Context mContext;

    RecyclerView mRecyclerView;
    OnlineRecipeAdapter mOnlineRecipeAdapter;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRecipeDatabaseReference;
    private static final String TAG = OnlineRecipesActivity.class.getSimpleName();
    private static final String LIST_STATE_KEY = "keystate";

    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mListState;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_recipes);

        Toolbar myToolbar = findViewById(R.id.od_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.chefbook);
        }
        mUserNameTV = findViewById(R.id.od_user_name_tv);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserNameTV.setText(mUser != null ? mUser.getDisplayName() : null);
        mContext = this;
        mOnlineRecipes = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRecipeDatabaseReference = mFirebaseDatabase.getReference("recipes");

        mRecyclerView = findViewById(R.id.od_recycler_view);
        mLayoutManager = new GridLayoutManager(mContext,
                MainActivity.returnGridViewColumnBaseOnScreenSize(OnlineRecipesActivity.this));
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecipeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BestRecipe onlineRecipe = postSnapshot.getValue(BestRecipe.class);
                        mOnlineRecipes.add(onlineRecipe);
                    }
                    mOnlineRecipeAdapter = new OnlineRecipeAdapter(mContext, mOnlineRecipes);
                    mRecyclerView.setAdapter(mOnlineRecipeAdapter);
                    mRecyclerView.setHasFixedSize(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLayoutManager.onRestoreInstanceState(mListState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable listState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        // Retrieve list state and list/item positions
        if (state != null) {
            mListState = state.getParcelable(LIST_STATE_KEY);
        }
    }

}
