package com.payway.dashboard.data.network;

import static com.payway.dashboard.ui.fragments.TransactionsFragment.transactionsFragment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.payway.dashboard.app.AppController;
import com.payway.dashboard.data.network.api.APIService;
import com.payway.dashboard.data.network.api.LocalRetrofitApi;
import com.payway.dashboard.helpers.SessionManager;
import com.payway.dashboard.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsListDataSource extends PageKeyedDataSource<Integer, Transaction> {
    private static final String LOG_TAG = TransactionsListDataSource.class.getSimpleName();
    //private final Context mContext;
    //private final AppExecutors mExecutors;

    //the size of a page that we want
    public static final int PAGE_SIZE = 10;

    //we will start from the first page which is 1
    //this is an index value, so 1 is at position 0
    private static final int FIRST_PAGE = 0;

    private static final Object LOCK = new Object();
    private static TransactionsListDataSource sInstance;
    //private final Context mContext;
    //private final AppExecutors mExecutors;
    private SessionManager sessionManager;

    private List<Transaction> transactionList = new ArrayList<Transaction>();
    private Transaction transaction;
    // LiveData storing the latest downloaded jobs list
    private final MutableLiveData<List<Transaction>> mTransactionsForBrowsing;

    public TransactionsListDataSource() {
        mTransactionsForBrowsing = new MutableLiveData<>();
        // Session manager
        sessionManager = new SessionManager(AppController.getInstance().getApplicationContext());
    }

    /**
     * Get the singleton for this class
     */
    public static TransactionsListDataSource getInstance() {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new TransactionsListDataSource();
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    //returned articles for browsing
    public LiveData<List<Transaction>> getArticlesForBrowsing() {
        return mTransactionsForBrowsing;
    }

    /*
     * Step 1: This method is responsible to load the data initially
     * when app screen is launched for the first time.
     * We are fetching the first page data from the api
     * and passing it via the callback method to the UI.
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Transaction> callback) {
        Log.d(LOG_TAG, "Loading initial transactions started");

        APIService service = new LocalRetrofitApi().getRetrofitService();

        //defining the call
        Call<Transaction> call = service.getTransactions();

        //calling the com.emtech.retrofitexample.api
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                try{
                    /*if (response.body() != null && response.body().getTransactionsList() != null) {
                        Log.d(LOG_TAG, "Initial JSON not null");
                        if (response.body().getTransactionsList().size() == 0) {
                            Log.d(LOG_TAG, "List is empty (0)");
                            transactionsFragment.isListEmpty(true);
                        } else {
                            callback.onResult(response.body().getTransactionsList(), null, PAGE_SIZE);
                            transactionsFragment.isListEmpty(false);

                            Log.d(LOG_TAG, "Initial Size of list: " + response.body().getTransactionsList().size());
                            // If the code reaches this point, we have successfully performed our sync
                            Log.d(LOG_TAG, "Successfully got initial list of articles for browsing");
                        }
                    }*/
                }catch (Exception e){
                    //mArticlesForBrowsing.postValue(null);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                //print out any error we may get
                //probably server connection
                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }

    //this will load the previous page
    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Transaction> callback) {

        Log.d(LOG_TAG, "Loading previous list started");

        //Defining retrofit service
        APIService service = new LocalRetrofitApi().getRetrofitService();

        //defining the call
        Call<Transaction> call = service.getTransactions();

        //calling the com.emtech.retrofitexample.api
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                try{
                    //if the current page is greater than one
                    //we are decrementing the page number
                    //else there is no previous page
                    Integer adjacentKey = (params.key > 1) ? params.key - 1 : null;

                    //if response body is not null, we have some data
                    //count what we have in the response
                    /*if (response.body() != null && response.body().getTransactionsList() != null) {
                        Log.d(LOG_TAG, "Previous JSON not null");
                        //passing the loaded data
                        //and the previous page key
                        callback.onResult(transactionList, adjacentKey);

                        Log.d(LOG_TAG, "Previous Size of list: "+response.body().getTransactionsList().size());
                        // If the code reaches this point, we have successfully performed our sync
                        Log.d(LOG_TAG, "Successfully got previous list of jobs for browsing");
                    }*/
                }catch (Exception e){
                    e.printStackTrace();
                    mTransactionsForBrowsing.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                //print out any error we may get
                //probably server connection
                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }

    //this will load the next page
    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Transaction> callback) {
        APIService service = new LocalRetrofitApi().getRetrofitService();

        //defining the call
        Call<Transaction> call = service.getTransactions();

        //calling the com.emtech.retrofitexample.api
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                try{
                    //if the current page is greater than one
                    //we are incrementing the page number
                    //else there is no previous page
                    //Integer adjacentKey = (params.key > 1) ? params.key + 1 : null;

                    //if response body is not null, we have some data
                    //count what we have in the response
                    /*if (response.body() != null && response.body().getTransactionsList() != null) {
                        Log.d(LOG_TAG, "Next JSON not null");

                        Integer adjacentKey;
                        if (response.body().getPages_count() == params.key) {
                            adjacentKey = null;
                            Log.e(LOG_TAG, " adjacentKey = null | Pages count from server is " + response.body().getPages_count() + " " +
                                    "and current page count is " + params.key);

                            //passing the loaded data
                            //and the previous page key
                            callback.onResult(response.body().getTransactionsList(), adjacentKey);

                        }else if (response.body().getPages_count() < params.key && response.body().getPages_count() != 0) {
                            adjacentKey = null;
                            Log.e(LOG_TAG, " adjacentKey = null | Pages count from server is " + response.body().getPages_count() + " " +
                                    "and current page count is " + params.key);

                            //passing the loaded data
                            //and the previous page key
                            callback.onResult(response.body().getTransactionsList(), adjacentKey);

                        }else if (response.body().getPages_count() > params.key){
                            adjacentKey = params.key + 1;

                            Log.e(LOG_TAG, "Pages count from server is " + response.body().getPages_count() + " " +
                                    "and current page count is " + params.key);

                            //clear the previous search list if it has content
                            if (transactionList != null) {
                                transactionList.clear();
                            }

                            //passing the loaded data
                            //and the previous page key
                            callback.onResult(transactionList, adjacentKey);

                            Log.d(LOG_TAG, "Next Size of list: "+response.body().getTransactionsList().size());
                            // If the code reaches this point, we have successfully performed our sync
                            Log.d(LOG_TAG, "Successfully got next list of articles for browsing");
                        }

                        // When you are off of the main thread and want to update LiveData, use postValue.
                        // It posts the update to the main thread.
                        mTransactionsForBrowsing.postValue(transactionList);

                    }*/
                }catch (Exception e){
                    e.printStackTrace();
                    mTransactionsForBrowsing.postValue(transactionList);
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                //print out any error we may get
                //probably server connection
                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }
}
