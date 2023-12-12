package com.payway.dashboard.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "transaction_id")
    private int transaction_id;
    @ColumnInfo(name = "transaction_date")
    private String transaction_date;
    @ColumnInfo(name = "amount")
    private int amount;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "service")
    private String service;
    @ColumnInfo(name = "category")
    private String category;
    @Ignore
    private int color = -1;
    @Ignore
    private Transaction[] transactionsList;
    @Ignore
    private int pages_count;

    @Ignore
    private int trx_num;


    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Transaction[] getTransactionsList() {
        return transactionsList;
    }

    public int getPages_count() {
        return pages_count;
    }

    public int getTrx_num() {
        return trx_num;
    }

    public void setTrx_num(int trx_num) {
        this.trx_num = trx_num;
    }
}
