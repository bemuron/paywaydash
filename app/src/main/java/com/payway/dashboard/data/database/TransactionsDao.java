package com.payway.dashboard.data.database;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.payway.dashboard.models.Transaction;

import java.util.List;

@Dao
public interface TransactionsDao {

    /*
    insert categories into db
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    //void insertTransactions(Transaction transaction);
    void insertTransactions(Transaction[] transaction);

    @Query("Delete from transactions")
    void deleteAllTransactions();

    @Query("SELECT transaction_id, type, amount, service, category, transaction_date from transactions ORDER BY transaction_id ASC")
    DataSource.Factory<Integer, Transaction> getAllTransactions();

    //get the daily transaction types
    //@Query("SELECT transaction_id, strftime('%Y-%m-%d', transaction_date) AS transaction_date, type from transactions ORDER BY strftime('%Y-%m-%d', transaction_date) DESC")
    //LiveData<List<Transaction>> getDailyTransactionTypes();

    //@Query("SELECT count(transaction_id) AS trx_num, type, amount, transaction_id, category FROM transactions GROUP BY type")
    @Query("SELECT transaction_id, type, amount, service, category, transaction_date FROM transactions")
    LiveData<List<Transaction>> getDailyTransactionTypes();

    //get the most used category in a month and year
    @Query("SELECT category, count(category) AS trx_num, transaction_id,amount " +
            "from transactions WHERE strftime('%m', transaction_date) = :monthValue AND strftime('%y', transaction_date) = :yearValue GROUP BY category")
    LiveData<List<Transaction>> getMonthlyMostUsedCategory(String monthValue, String yearValue);

    //get the most used service in a month and year
    @Query("SELECT service, count(service) AS trx_num, transaction_id, amount, category " +
            "from transactions WHERE strftime('%m', transaction_date) = :monthValue AND strftime('%y', transaction_date) = :yearValue GROUP BY service")
    LiveData<List<Transaction>> getMonthlyMostUsedService(String monthValue, String yearValue);

    //get the transactions based on the type
    @Query("SELECT type, strftime('%Y-%m-%d', transaction_date) AS transaction_date, transaction_id,category, amount from transactions WHERE type = :type GROUP BY strftime('%Y-%m-%d', transaction_date)")
    LiveData<List<Transaction>> getTransactionsByType(String type);

    @Query("SELECT COUNT(transaction_id) FROM transactions")
    int countTransactionsInDb();

    @Query("SELECT transaction_id, transaction_date, amount, service, type, " +
            "category from transactions WHERE transaction_id = :transaction_id")
    LiveData<List<Transaction>> getSingleTransaction(int transaction_id);
}
