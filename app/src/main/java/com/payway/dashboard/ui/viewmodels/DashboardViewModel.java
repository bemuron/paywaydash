package com.payway.dashboard.ui.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.payway.dashboard.data.DashboardRepository;
import com.payway.dashboard.data.database.TransactionsDao;
import com.payway.dashboard.data.network.TransactionsListDataFactory;
import com.payway.dashboard.data.network.TransactionsListDataSource;
import com.payway.dashboard.models.Transaction;

import java.util.List;


public class DashboardViewModel extends AndroidViewModel {

    //private member variable to hold reference to the repository
    private DashboardRepository mRepository;
    private TransactionsDao transactionsDao;
    private Context mContext;

    //creating livedata for PagedList  and PagedKeyedDataSource
    private LiveData<PagedList<Transaction>> mTransactionsPagedList;

    public TransactionsListDataFactory mTransactionsListDataFactory;

    private LiveData<List<Transaction>> mAllTrxs, mDailyTrxsByType;



    //constructor that gets a reference to the repository and gets the categories
    public DashboardViewModel(Application application) {
        super(application);
        mRepository = new DashboardRepository(application);
        mContext = application.getApplicationContext();
        mDailyTrxsByType = mRepository.getDailyTransactionTypes();
        transactionsDao = mRepository.getTransactionsDao();
        //mAllTrxs = mRepository.getAllTrxs();

        //Getting PagedList for transactions
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(20).build();

        //Building the paged list
        mTransactionsPagedList = new LivePagedListBuilder<>(transactionsDao.getAllTransactions(), pagedListConfig)
                .build();
    }

    //this list of transactions is displayed on the home page
    public LiveData<List<Transaction>> getAllTrxs(){
        return mAllTrxs;
    }

    public LiveData<PagedList<Transaction>> getTransactionsList() {
        return mTransactionsPagedList;
    }

    public LiveData<List<Transaction>> getDailyTransactionTypes(){
        return mDailyTrxsByType;
    }

    ////get the most used category in a month and year
    public LiveData<List<Transaction>> getMonthlyMostUsedCategory(String monthValue, String yearValue){
        return mRepository.getMonthlyMostUsedCategory(monthValue, yearValue);
    }

    //get the most used service in a month and year
    public LiveData<List<Transaction>> getMonthlyMostUsedService(String monthValue, String yearValue){
        return mRepository.getMonthlyMostUsedService(monthValue, yearValue);
    }

    //get transactions by type
    public LiveData<List<Transaction>> getTransactionsByType(String type){
        return mRepository.getTransactionsByType(type);
    }
}
