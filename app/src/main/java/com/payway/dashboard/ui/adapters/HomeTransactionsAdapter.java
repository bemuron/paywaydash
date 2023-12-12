package com.payway.dashboard.ui.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.payway.dashboard.R;
import com.payway.dashboard.models.Transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HomeTransactionsAdapter extends RecyclerView.Adapter<HomeTransactionsAdapter.HomeTransactionsViewHolder> {
    private static final String LOG_TAG = HomeTransactionsAdapter.class.getSimpleName();
    private List<Transaction> transactionList;
    private LayoutInflater inflater;
    private Context context;
    private HomeTransactionsListAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private Transaction transaction;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;

    public HomeTransactionsAdapter(Context context, HomeTransactionsListAdapterListener listener){
        inflater = LayoutInflater.from(context);
        this.context = context;
        //this.jobList = jobs;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();

    }

    class HomeTransactionsViewHolder extends RecyclerView.ViewHolder {

        public TextView payee_name, payee_number, trx_amount, trx_date;
        public LinearLayout trxContainer;

        public HomeTransactionsViewHolder(View view) {
            super(view);
            payee_name = view.findViewById(R.id.home_trx_payee);
            payee_number = view.findViewById(R.id.home_payee_number);
            trx_amount = view.findViewById(R.id.home_transaction_amount);
            trx_date = view.findViewById(R.id.home_transaction_date);
            trxContainer = view.findViewById(R.id.home_transactions_container);
        }
    }

    @NonNull
    @Override
    public HomeTransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_transactions_list_item, parent, false);
        view.setFocusable(true);
        return new HomeTransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeTransactionsViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        int jobStatus;
        String datePosted = null;

        Log.e(LOG_TAG, "Article status " + transaction);

        //make these texts bold
        SpannableStringBuilder postedBy = new SpannableStringBuilder("Posted By: ");
        SpannableStringBuilder date = new SpannableStringBuilder("Posted On: ");

        postedBy.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, postedBy.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        date.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, date.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //date format for dates coming from server
        SimpleDateFormat mysqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        //convert to UTC first to enable us convert to local time zone later
        mysqlDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        //the format we want them in
        DateFormat myFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH);

        long millis = 14400000; //add 4 hours to the mysql time to get the UG time

        //set the time zone
        Calendar cal = Calendar.getInstance();
        //TimeZone tz = TimeZone.getDefault();
        TimeZone timeZone = cal.getTimeZone();

        /* debug: is it local time? */
        //Log.e("Device Time zone: ", tz.getDisplayName());

        try{
            Date d = mysqlDateTimeFormat.parse(transaction.getTransaction_date()); //date is in utc now
            d.setTime(d.getTime() + millis);
            myFormat.setTimeZone(timeZone);//set the local timezone
            datePosted = myFormat.format(d);
        }catch (Exception e){
            e.printStackTrace();
        }

        // displaying text view data
        Log.e(LOG_TAG, "Payee " + transaction.getService());
        holder.payee_name.setText(transaction.getAmount());
        holder.payee_number.setText(transaction.getService());
        holder.trx_date.setText(date + datePosted);
        holder.trx_amount.setText(transaction.getAmount());

        // change the row state to activated
        //holder.itemView.setActivated(selectedItems.get(position, false));

        // apply click events
        applyClickEvents(holder, position);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of categories
     */
    @Override
    public int getItemCount() {
        if (null == transactionList) return 0;
        return transactionList.size();
    }

    public void swapTransactionList(final List<Transaction> newTrxs) {
        // If there was no forecast data, then recreate all of the list
        if (transactionList == null) {
            transactionList = newTrxs;
            notifyDataSetChanged();
        }

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return transactionList.size();
            }

            @Override
            public int getNewListSize() {
                return newTrxs.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return transactionList.get(oldItemPosition).getTransaction_id() ==
                        newTrxs.get(newItemPosition).getTransaction_id();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Transaction newTrx = newTrxs.get(newItemPosition);
                Transaction oldTrx = transactionList.get(oldItemPosition);
                return newTrx.getTransaction_id() == oldTrx.getTransaction_id();
            }
        });
        transactionList = newTrxs;
        result.dispatchUpdatesTo(this);
    }

    //handling different click events
    private void applyClickEvents(HomeTransactionsViewHolder holder, final int position) {
        holder.trxContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onHomeTransactionItemClicked(position);
            }
        });
    }

    public interface HomeTransactionsListAdapterListener {

        void onHomeTransactionItemClicked(int position);

    }

}
