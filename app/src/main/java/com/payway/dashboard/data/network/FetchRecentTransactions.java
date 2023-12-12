package com.payway.dashboard.data.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.payway.dashboard.data.network.api.APIService;
import com.payway.dashboard.data.network.api.LocalRetrofitApi;
import com.payway.dashboard.helpers.SessionManager;
import com.payway.dashboard.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchRecentTransactions {
  private static final String LOG_TAG = FetchRecentTransactions.class.getSimpleName();

  // LiveData storing the latest downloaded weather forecasts
  private final MutableLiveData<Transaction[]> mDownloadedTransactions;

  // For Singleton instantiation
  private static final Object LOCK = new Object();
  private static FetchRecentTransactions sInstance;
  private final Context mContext;
  private SessionManager sessionManager;

  public FetchRecentTransactions(Context context) {
    mContext = context;
    mDownloadedTransactions = new MutableLiveData<Transaction[]>();
    // Session manager
    sessionManager = new SessionManager(context.getApplicationContext());
  }

  /**
   * Get the singleton for this class
   */
  public static FetchRecentTransactions getInstance(Context context) {
    Log.d(LOG_TAG, "Getting the network data source");
    if (sInstance == null) {
      synchronized (LOCK) {
        sInstance = new FetchRecentTransactions(context.getApplicationContext());
        Log.d(LOG_TAG, "Made new network data source");
      }
    }
    return sInstance;
  }

  public LiveData<Transaction[]> getCommonTransactions() {
    return mDownloadedTransactions;
  }

  /**
   * Starts an intent service to fetch the transactions.
   */
  public void startFetchTransactionsService() {
    Intent intentToFetch = new Intent(mContext, TransactionsIntentService.class);
    mContext.startService(intentToFetch);
    Log.d(LOG_TAG, "Fetch transactions service created");
  }

  public void GetTransactions() {
    Log.d(LOG_TAG, "Fetch transactions started");
    //APIService service = retrofit.create(APIService.class);
    APIService service = new LocalRetrofitApi().getRetrofitService();

    //defining the call
    Call<Transaction> call = service.getTransactions();

    call.enqueue(new Callback<Transaction>() {
      @Override
      public void onResponse(Call<Transaction> call, Response<Transaction> response) {

        //if response body is not null, we have some data
        //count what we have in the response
        if (response.body() != null && response.body().getTransactionsList().length > 0) {
          Log.d(LOG_TAG, "JSON not null and has " + response.body().getTransactionsList().length
                  + " values");

          // postValue to post the update to the main thread.
          mDownloadedTransactions.postValue(response.body().getTransactionsList());

          // If the code reaches this point, we have successfully performed our sync
          Log.d(LOG_TAG, "Successfully performed our sync");
        }
      }

      @Override
      public void onFailure(Call<Transaction> call, Throwable t) {
        Log.e(LOG_TAG, "Could not get transactions");
        Log.e(LOG_TAG, "ERROR: "+t.getMessage());
      }
    });

  }

}
