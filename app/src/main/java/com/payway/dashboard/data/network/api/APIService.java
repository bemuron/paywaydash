package com.payway.dashboard.data.network.api;


import com.payway.dashboard.models.Transaction;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface APIService {

    //getting the transactions
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @GET("transactions")
    Call<Transaction> getTransactions();
}