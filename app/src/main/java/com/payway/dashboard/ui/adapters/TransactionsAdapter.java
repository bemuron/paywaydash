package com.payway.dashboard.ui.adapters;

import static android.provider.Settings.System.getString;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
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
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.payway.dashboard.R;
import com.payway.dashboard.helpers.SessionManager;
import com.payway.dashboard.models.Transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TransactionsAdapter extends PagedListAdapter<Transaction, TransactionsAdapter.ItemViewHolder> {
    private static final String LOG_TAG = TransactionsAdapter.class.getSimpleName();
    private List<Transaction> transactionList;
    private LayoutInflater inflater;
    private Context context;
    private TransactionsListAdapterListener listener;
    private Transaction transaction;

    public TransactionsAdapter(Context context, TransactionsListAdapterListener listener){
        super(DIFF_CALLBACK);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView trx_type, trx_service, trx_amount, trx_date,
                trx_category;

        public ItemViewHolder(View view) {
            super(view);
            trx_type = view.findViewById(R.id.trxTypeValue);
            trx_service = view.findViewById(R.id.trxServiceValue);
            trx_amount = view.findViewById(R.id.trxAmountValue);
            trx_date = view.findViewById(R.id.trxDateValue);
            trx_category = view.findViewById(R.id.trxCategoryValue);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactions_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        transaction = getItem(position);
        String trxDate = null;

        //date format for dates coming from server
        SimpleDateFormat mysqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        //the format we want them in
        DateFormat myFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH);

        try{
            Date d = mysqlDateTimeFormat.parse(transaction.getTransaction_date());
            d.setTime(d.getTime());
            trxDate = myFormat.format(d);
        }catch (Exception e){
            e.printStackTrace();
        }

        // displaying text view data
        holder.trx_type.setText(transaction.getType());
        holder.trx_date.setText(trxDate);
        holder.trx_service.setText(transaction.getService());
        holder.trx_amount.setText(context.getString(R.string.amount, transaction.getAmount()));
        holder.trx_category.setText(transaction.getCategory());

    }

    private static DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Transaction>() {
                @Override
                public boolean areItemsTheSame(Transaction oldItem, Transaction newItem) {
                    Log.e(LOG_TAG, "Transaction items are the same");
                    return oldItem.getTransaction_id() == newItem.getTransaction_id();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Transaction oldItem, @NonNull Transaction newItem) {
                    Log.e(LOG_TAG, "Transaction old list == new list");
                    return oldItem.equals(newItem);
                }
            };

    public interface TransactionsListAdapterListener {

        void onTransactionRowClicked(int position);

    }

}
