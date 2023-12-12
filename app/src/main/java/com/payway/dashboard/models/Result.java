package com.payway.dashboard.models;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("transaction")
    private Transaction transaction;

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("custom_token")
    private String custom_token;

    public Result(Boolean error, String message, Transaction transaction) {
        this.error = error;
        this.message = message;
        this.transaction = transaction;
    }


    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getCustom_token() {
        return custom_token;
    }
}
