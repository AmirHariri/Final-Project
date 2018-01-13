package com.cangetinshape.chefbook;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

/**
 * Created by Amir on 1/9/2018.
 */

public class ToastAdListener extends AdListener {
    private Context mContext;
    private String mErrorReason;

    public ToastAdListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void onAdLoaded() {
        //Toast.makeText(mContext, "onAdLoaded()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdOpened() {
        //Toast.makeText(mContext, "onAdOpened()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClosed() {
        Toast.makeText(mContext, "WelCome to ChefBook!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdLeftApplication() {
        Toast.makeText(mContext, "Thanks for checking the ad!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        mErrorReason = "";
        switch(errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                mErrorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                mErrorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                mErrorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                mErrorReason = "No fill";
                break;
        }
        Toast.makeText(mContext,
                String.format("onAdFailedToLoad(%s)", mErrorReason),
                Toast.LENGTH_SHORT).show();
    }

    public String getErrorReason() {
        return mErrorReason == null ? "" : mErrorReason;
    }

}
