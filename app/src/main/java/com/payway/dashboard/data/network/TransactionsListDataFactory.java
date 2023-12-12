package com.payway.dashboard.data.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.payway.dashboard.data.DashboardRepository;
import com.payway.dashboard.models.Transaction;


public class TransactionsListDataFactory extends DataSource.Factory<Integer, Transaction>{
    private DashboardRepository repository;

    //creating the mutable live data
    private MutableLiveData<PageKeyedDataSource<Integer, Transaction>> browsedArticlesLiveDataSource = new MutableLiveData<>();
    //private MutableLiveData<BrowsedJobsDataSource> browsedJobsLiveDataSource = new MutableLiveData<>();
    private DataSource<Integer, Transaction> transactionDataSource;

    public TransactionsListDataFactory(Context mContext) {
    }

    @NonNull
    @Override
    public DataSource<Integer, Transaction> create() {
        //getting our data source object
        TransactionsListDataSource transactionsListDataSource = new TransactionsListDataSource();

        //posting the datasource to get the values
        browsedArticlesLiveDataSource.postValue(transactionsListDataSource);

        transactionDataSource = transactionsListDataSource;
        //returning the datasource
        return transactionsListDataSource;
    }


    //getter for itemlivedatasource
    public DataSource<Integer, Transaction> getBrowsedTransactionsLiveDataSource() {
        return transactionDataSource;
    }

    /*public MutableLiveData<PageKeyedDataSource<Integer, Article>> getBrowsedArticlesLiveDataSource() {
        return browsedArticlesLiveDataSource;
    }*/
}
