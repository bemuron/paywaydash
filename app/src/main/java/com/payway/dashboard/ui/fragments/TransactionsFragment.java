package com.payway.dashboard.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.payway.dashboard.R;
import com.payway.dashboard.models.Transaction;
import com.payway.dashboard.ui.adapters.TransactionsAdapter;
import com.payway.dashboard.ui.viewmodels.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionsFragment extends Fragment implements TransactionsAdapter.TransactionsListAdapterListener{
    private static final String LOG = TransactionsFragment.class.getSimpleName();
    private static final String FRAGMENT_NAME = "fragment_name";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DashboardViewModel dashboardViewModel;
    private int mPosition = RecyclerView.NO_POSITION;
    private OnTransactionListClickListener mCallback;
    private List<Transaction> transactionList = new ArrayList<Transaction>();
    private TransactionsAdapter transactionsAdapter;
    private TextView emptyView;
    private ProgressBar progressBar;
    private ProgressDialog pDialog;
    private String mFragmentName;
    public static TransactionsFragment transactionsFragment;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentName fragment name.
     * @return A new instance of fragment TransactionsFragment.
     */
    public static TransactionsFragment newInstance(String fragmentName) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_NAME, fragmentName);
        fragment.setArguments(args);
        transactionsFragment = fragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mFragmentName = getArguments().getString(FRAGMENT_NAME);
        }

        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mFragmentName);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(LOG, e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transactions,container,false);

        getAllWidgets(rootView);
        setAdapter();
        fetchTransactionList();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // clear all current menu items
        menu.clear();

        // Add the new menu items
        //inflater.inflate(R.menu.articles_menu, menu);
    }

    //get the widgets
    public void getAllWidgets(View view){
        swipeRefreshLayout = view.findViewById(R.id.transactions_list_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable(getActivity())){
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                    hideBar();
                    swipeRefreshLayout.setRefreshing(false);
                }else {
                    if (dashboardViewModel.mTransactionsListDataFactory.getBrowsedTransactionsLiveDataSource() != null) {
                        //dashboardViewModel.refreshTransactionsList();
                    } else {
                        fetchTransactionList();
                    }
                }
            }
        });
        // Progress bar
        progressBar = view.findViewById(R.id.transaction_list_progress_bar);
        showBar();
        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        recyclerView = view.findViewById(R.id.transaction_list_recycler_view);
        emptyView = view.findViewById(R.id.transaction_empty_list_view);
    }

    //setting up the recycler view adapter
    private void setAdapter()
    {
        transactionsAdapter = new TransactionsAdapter(getActivity(),this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(transactionsAdapter);
    }

    //returned from ArticlesListDataSource of the data set is empty or not
    public void isListEmpty(boolean isEmpty){
        if (isEmpty) {
            Log.e(LOG, "List is empty");
            emptyView.setText(R.string.empty_transactions_list);
            emptyView.setVisibility(View.VISIBLE);
            //recyclerView.setVisibility(View.GONE);
        }else{
            Log.e(LOG, "List not empty");
        }
        hideBar();
        swipeRefreshLayout.setRefreshing(false);
    }

    //get the list of transactions
    private void fetchTransactionList(){
        dashboardViewModel.getTransactionsList().observe(getViewLifecycleOwner(), transactionPagedList -> {
            transactionList = transactionPagedList;
            transactionsAdapter.submitList(transactionPagedList);

            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            recyclerView.smoothScrollToPosition(mPosition);
        });
        Log.e(LOG, "Transactions list size outside " + transactionList.size());
        if (transactionList.size() > 0){
            hideBar();
        }
    }

    // callback called in the parent activity to get the id and name of article clicked on
    public interface OnTransactionListClickListener {
        void transactionListClick(int articleId, String articleTitle);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            //mCallback = (OnTransactionListClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTransactionListClickListener");
        }
    }

    private void showBar() {
        progressBar.setVisibility(View.VISIBLE);
        /*try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }*/
    }

    private void hideBar() {
        progressBar.setVisibility(View.INVISIBLE);
        /*try {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }*/
    }

    //method to check for internet connection
    public static boolean isNetworkAvailable(Context context) {
        if(context == null)  return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }

            else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_status", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_status", "" + e.getMessage());
                }
            }
        }
        Log.i("update_status","Network is available : FALSE ");
        return false;
    }

    public void onTransactionRowClicked(int position) {
        Transaction transaction = transactionList.get(position);
        mCallback.transactionListClick(transaction.getTransaction_id(), transaction.getService());
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}