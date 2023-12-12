package com.payway.dashboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.payway.dashboard.app.AppController;
import com.payway.dashboard.data.database.DashboardDatabase;
import com.payway.dashboard.data.database.TransactionsDao;
import com.payway.dashboard.data.network.FetchRecentTransactions;
import com.payway.dashboard.helpers.SessionManager;
import com.payway.dashboard.models.Transaction;

import java.util.List;

public class DashboardRepository {
    private static final String TAG = DashboardRepository.class.getSimpleName();
    private static DashboardRepository instance;
    private boolean mInitialized = false;
    private boolean mTrxInitialized = false;
    private AppExecutors mExecutors;
    private TransactionsDao mTransactionsDao;
    private FetchRecentTransactions fetchRecentTransactions;
    private LiveData<Transaction[]> transactionsFromNetwork;

    private SessionManager session;
    // For Singleton instantiation
    private static final Object LOCK = new Object();

    //constructor that gets a handle to the db and initializes the member
    //variables
    public DashboardRepository(Application application){
        DashboardDatabase db = DashboardDatabase.getInstance(application);
        session = new SessionManager(application);
        mTransactionsDao = db.transactionsDao();
        fetchRecentTransactions = FetchRecentTransactions.getInstance(application);
        transactionsFromNetwork = fetchRecentTransactions.getCommonTransactions();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppController());

        mExecutors = AppExecutors.getInstance();
        instance = this;

        fetchTransactions();
    }

    public TransactionsDao getTransactionsDao(){
        return mTransactionsDao;
    }

    private void fetchTransactions(){
        mExecutors.diskIO().execute(() -> {
            Log.d(TAG, " transactions fetch called");
            fetchRecentTransactions.GetTransactions();;
        });

        transactionsFromNetwork.observeForever(transactions -> mExecutors.diskIO().execute(() -> {
            //deleteOldTransactions();
            // Insert transactions into database
            mTransactionsDao.insertTransactions(transactions);
            //Log.d(TAG, "Old transactions deleted");

            Log.d(TAG, transactions.length +" transactions inserted");
        }));
    }

    public static DashboardRepository getInstance(){
        return  instance;
    }

    //delete old transactions in the db
    private void deleteOldTransactions() {
        mTransactionsDao.deleteAllTransactions();
    }

    //fetch the transactions in the db
    /*public LiveData<List<Transaction>> getAllTrxs(){
        initializeTransactionData();
        return mTransactionsDao.getAllTransactions();
    }*/

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    private synchronized void initializeTransactionData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mTrxInitialized) return;
        mTrxInitialized = true;

        mExecutors.diskIO().execute(() -> {
            if (isTransactionFetchNeeded()) {
                startFetchTransactionsService();
            }
        });

        if (isTransactionFetchNeeded()) {
            mExecutors.diskIO().execute(this::startFetchTransactionsService);
        }
    }

    private boolean isTransactionFetchNeeded() {
        Log.e(TAG, "Is app in background value = "+AppController.isAppInBg);
        return (AppController.isAppInBg);
    }

    //start the service to fetch the transactions
    private void startFetchTransactionsService() {
        fetchRecentTransactions.startFetchTransactionsService();
    }

    //get daily transaction types
    public LiveData<List<Transaction>> getDailyTransactionTypes(){
        return mTransactionsDao.getDailyTransactionTypes();
    }

    ////get the most used category in a month and year
    public LiveData<List<Transaction>> getMonthlyMostUsedCategory(String monthValue, String yearValue){
        return mTransactionsDao.getMonthlyMostUsedCategory(monthValue, yearValue);
    }

    //get the most used service in a month and year
    public LiveData<List<Transaction>> getMonthlyMostUsedService(String monthValue, String yearValue){
        return mTransactionsDao.getMonthlyMostUsedService(monthValue, yearValue);
    }

    //get transactions by type
    public LiveData<List<Transaction>> getTransactionsByType(String type){
        return mTransactionsDao.getTransactionsByType(type);
    }
}
