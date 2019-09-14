package com.bazzillion.ingrid.shelfie.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.R;

public class CrossAppFunctions {

    public static int checkNetworkState(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null || !activeNetwork.isConnected()) {
            Toast.makeText(context, context.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return -1;
        }

        return 1;
    }

}
