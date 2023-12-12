package com.payway.dashboard.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.payway.dashboard.R;
import com.payway.dashboard.helpers.SessionManager;
import com.payway.dashboard.models.Transaction;
import com.payway.dashboard.ui.activities.MainActivity;
import com.payway.dashboard.ui.adapters.HomeTransactionsAdapter;
import com.payway.dashboard.ui.viewmodels.DashboardViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment implements HomeTransactionsAdapter.HomeTransactionsListAdapterListener{
    private static final String TAG = HomeFragment.class.getSimpleName();

    public static final String USERNAME = "user_name";

    private TextView welcomeMsg;
    private String mFragmentName;
    private DashboardViewModel dashboardViewModel;
    private OnHomeProListClickListener mCallback;
    private OnHomeTransactionListClickListener mTransactionsCallback;
    private List<Transaction> transactionList = new ArrayList<Transaction>();
    private List<Transaction> transactionArrayList = new ArrayList<Transaction>();
    private HomeTransactionsAdapter transactionsAdapter;
    private SessionManager session;
    private SharedPreferences prefs;
    private BarChart barChart, serviceBarChart;
    private PieChart categoryPieChart;
    private TextView depositsTvValue, withdrawsTvValue;
    private int depositsValue = 0, withdrawsValue = 0;

    public HomeFragment(){

    }

    public static HomeFragment newInstance() {

        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //name = getArguments().getString(USERNAME);
        }

        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("PayWayDash");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //mCategoriesViewModel.start();
        /*try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("PayWayDash");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);

        if (!isNetworkAvailable(getActivity())){
            //Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
        // session manager
        session = new SessionManager(MainActivity.getActivityInstance());

        View rootView = inflater.inflate(R.layout.fragment_home,container,false);

        getAllWidgets(rootView);
        initPieChart();
        //initializeDailyTrxsBar();
        //setData(5, 100);


        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getActivityInstance());

        welcomeMsg.setText(getString(R.string.welcome, ""));

        //setTransactionsAdapter(rootView);
        //fetchTransactionsList();
        fetchDailyTransactionsByType();

        return  rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            //mTransactionsCallback = (OnHomeTransactionListClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHomeTransactionListClickListener");
        }
    }

    private void getAllWidgets(View view){
        welcomeMsg = view.findViewById(R.id.welcome_msg);
        barChart = view.findViewById(R.id.daily_trxs_bar);
        categoryPieChart = view.findViewById(R.id.category_pieChart);
        serviceBarChart = view.findViewById(R.id.service_pieChart);
        depositsTvValue = view.findViewById(R.id.deposits_snapshot_value);
        withdrawsTvValue = view.findViewById(R.id.withdraws_snapshot_value);

        depositsTvValue.setText(getActivity().getString(R.string.amount, depositsValue));
        withdrawsTvValue.setText(getActivity().getString(R.string.amount, withdrawsValue));
    }

    private void fetchDailyTransactionsByType(){
        dashboardViewModel.getDailyTransactionTypes().observe(getActivity(), dbTransactionList -> {
            transactionArrayList = dbTransactionList;
            //Log.e(TAG, "TRX SIZE = "+dailyTrxsByType.size());
            getSnapshotValues(transactionArrayList);
            showTrxTypeBarChart(transactionArrayList);
            showCategoryPieChart(transactionArrayList);
            showServiceBarChart(transactionArrayList);

        });
    }

    private void getSnapshotValues(List<Transaction> trxs){
        int depositNum = 0, withdrawNum = 0;

        for(int i = 0; i < trxs.size(); i++){
            if (Objects.equals(trxs.get(i).getType(), "Deposit")){
                depositNum = depositNum + trxs.get(i).getAmount();
            }else if (Objects.equals(trxs.get(i).getType(), "Withdraw")){
                withdrawNum = withdrawNum + trxs.get(i).getAmount();
            }
        }
        depositsValue = depositNum;
        withdrawsValue = withdrawNum;

    }

    //generate the transactions bar chart
    private void showTrxTypeBarChart(List<Transaction> trxsByType){
        //Log.e(TAG, "TRX TYPE = "+trxsByType.get(1).getType());

        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = "Transaction Types";
        int depositNum = 0, withdrawNum = 0;

        //input data
        for(int i = 0; i < trxsByType.size(); i++){
            if (Objects.equals(trxsByType.get(i).getType(), "Deposit")){
                depositNum++;
            }else{
                withdrawNum++;
            }
        }
        valueList.add((double)depositNum);
        valueList.add((double)withdrawNum);

        //fit the data into a bar
        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i+1, (float)valueList.get(i).floatValue());
            entries.add(barEntry);
        }

        //initialize x Axis Labels
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Deposit");
        xAxisLabel.add("Withdraw");
        xAxisLabel.add("");

        //initialize xAxis
        XAxis xAxis = barChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(14);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0 + 0.5f); //to center the bars inside the vertical grid lines we need + 0.5 step
        xAxis.setAxisMaximum(entries.size() + 0.5f); //to center the bars inside the vertical grid lines we need + 0.5 step

        xAxis.setLabelCount(xAxisLabel.size(), true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(0f); //labels x offset in dps
        xAxis.setYOffset(0f); //labels y offset in dps
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        });

        BarDataSet barDataSet = new BarDataSet(entries, title);
        barDataSet.setColor(Color.RED);
        barDataSet.setFormSize(15f);
        barDataSet.setDrawValues(false);
        barDataSet.setValueTextSize(12f);

        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        //barChart.animateXY(2000, 2000);
        barChart.setScaleEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);
        barChart.invalidate();
    }

    //show category usage pie chart
    private void showCategoryPieChart(List<Transaction> trxs){
        int tvNum = 0, airtimeNum = 0, internetNum = 0, mmNum = 0,
                utilityNum = 0, loansNum = 0, taxesNum = 0;
        //input data
        for(int i = 0; i < trxs.size(); i++){
            if (Objects.equals(trxs.get(i).getCategory(), "TV")){
                tvNum++;
            }else if (Objects.equals(trxs.get(i).getCategory(), "Airtime")){
                airtimeNum++;
            }else if (Objects.equals(trxs.get(i).getCategory(), "Internet")){
                internetNum++;
            }else if (Objects.equals(trxs.get(i).getCategory(), "Mobile Money")){
                mmNum++;
            }else if (Objects.equals(trxs.get(i).getCategory(), "Utilities")){
                utilityNum++;
            }else if (Objects.equals(trxs.get(i).getCategory(), "Loans")){
                loansNum++;
            }else if (Objects.equals(trxs.get(i).getCategory(), "Taxes")){
                taxesNum++;
            }
        }

        Log.e(TAG, "TV NUM = "+tvNum);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "Category";

        //initializing data
        Map<String, Integer> categoryMap = new HashMap<>();
        categoryMap.put("TV",tvNum);
        categoryMap.put("Airtime",airtimeNum);
        categoryMap.put("Internet",internetNum);
        categoryMap.put("Mobile Money",mmNum);
        categoryMap.put("Utilities",utilityNum);
        categoryMap.put("Loans",loansNum);
        categoryMap.put("Taxes",taxesNum);

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF6F6F"));
        colors.add(Color.parseColor("#558b2f"));
        colors.add(Color.parseColor("#26ae90"));
        colors.add(Color.parseColor("#428bca"));
        colors.add(Color.parseColor("#5e6266"));
        colors.add(Color.parseColor("#3700B3"));
        colors.add(Color.parseColor("#3ca567"));

        //input data and fit data into pie chart entry
        for(String type: categoryMap.keySet()){
            pieEntries.add(new PieEntry(categoryMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());

        categoryPieChart.setData(pieData);
        categoryPieChart.invalidate();
    }

    //show service usage pie chart
    private void showServiceBarChart(List<Transaction> trxs){
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = "Transaction Types";
        int depositNum = 0, withdrawNum = 0;

        int goTvNum = 0, mtnUgNum = 0, airtelIntNum = 0, lyncNum = 0,
                mtnMmNum = 0, airtelMonNum = 0, umemeNum = 0,
                airtelUgNum = 0, waterNum = 0, starNum = 0,
                simbaNum = 0, mogoNum = 0, uraNum = 0,
                tugendeNum = 0, zukuNum = 0, dstvNum = 0,
                azamNum = 0, mtnIntNum = 0;
        //input data
        for(int i = 0; i < trxs.size(); i++){
            if (Objects.equals(trxs.get(i).getService(), "GOtv")){
                goTvNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "MTN Uganda")){
                mtnUgNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "Airtel Internet")){
                airtelIntNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "Lycamobile")){
                lyncNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "MTN Mobile Money")){
                mtnMmNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "Airtel Money")){
                airtelMonNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "UMEME Yaka")){
                umemeNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "Airtel Uganda")){
                airtelUgNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "National Water")){
                waterNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "StarTimes TV")){
                starNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "SIMBATV")){
                simbaNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "MOGO")){
                mogoNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "URA")){
                uraNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "Tugende")){
                tugendeNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "Zuku TV")){
                zukuNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "DSTV Multichoice")){
                dstvNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "Azam Media")){
                azamNum++;
            }else if (Objects.equals(trxs.get(i).getService(), "MTN Mobile Internet")){
                mtnIntNum++;
            }
        }

        valueList.add((double)goTvNum);
        valueList.add((double)mtnUgNum);
        valueList.add((double)airtelIntNum);
        valueList.add((double)mtnIntNum);
        valueList.add((double)lyncNum);
        valueList.add((double)mtnMmNum);
        valueList.add((double)airtelMonNum);
        valueList.add((double)umemeNum);
        valueList.add((double)airtelUgNum);
        valueList.add((double)waterNum);
        valueList.add((double)starNum);
        valueList.add((double)simbaNum);
        valueList.add((double)mogoNum);
        valueList.add((double)uraNum);
        valueList.add((double)tugendeNum);
        valueList.add((double)zukuNum);
        valueList.add((double)dstvNum);
        valueList.add((double)azamNum);

        //fit the data into a bar
        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i+1, (float)valueList.get(i).floatValue());
            entries.add(barEntry);
        }

        //initialize x Axis Labels
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("GOtv");
        xAxisLabel.add("MTN Uganda");
        xAxisLabel.add("Airtel Internet");
        xAxisLabel.add("Lycamobile");
        xAxisLabel.add("MTN Mobile Money");
        xAxisLabel.add("Airtel Money");
        xAxisLabel.add("MTN Mobile Internet");
        xAxisLabel.add("UMEME Yaka");
        xAxisLabel.add("Airtel Uganda");
        xAxisLabel.add("National Water");
        xAxisLabel.add("StarTimes TV");
        xAxisLabel.add("SIMBATV");
        xAxisLabel.add("MOGO");
        xAxisLabel.add("URA");
        xAxisLabel.add("Tugende");
        xAxisLabel.add("Zuku TV");
        xAxisLabel.add("DSTV Multichoice");
        xAxisLabel.add("Azam Media");
        xAxisLabel.add("");

        //initialize xAxis
        XAxis xAxis = serviceBarChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(14);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0 + 0.5f); //to center the bars inside the vertical grid lines we need + 0.5 step
        xAxis.setAxisMaximum(entries.size() + 0.5f); //to center the bars inside the vertical grid lines we need + 0.5 step

        xAxis.setLabelCount(xAxisLabel.size(), true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(0f); //labels x offset in dps
        xAxis.setYOffset(0f); //labels y offset in dps
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        });

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF6F6F"));
        colors.add(Color.parseColor("#558b2f"));
        colors.add(Color.parseColor("#26ae90"));
        colors.add(Color.parseColor("#428bca"));
        colors.add(Color.parseColor("#5e6266"));
        colors.add(Color.parseColor("#3700B3"));
        colors.add(Color.parseColor("#3ca567"));
        colors.add(Color.parseColor("#F92FFF"));
        colors.add(Color.parseColor("#0F0323"));
        colors.add(Color.parseColor("#FF5722"));
        colors.add(Color.parseColor("#FFEB3B"));
        colors.add(Color.parseColor("#1EA9E9"));
        colors.add(Color.parseColor("#683100"));
        colors.add(Color.parseColor("#370980"));
        colors.add(Color.parseColor("#00C348"));
        colors.add(Color.parseColor("#593634"));
        colors.add(Color.parseColor("#009688"));
        colors.add(Color.parseColor("#370980"));

        BarDataSet barDataSet = new BarDataSet(entries, title);
        barDataSet.setColors(colors);
        barDataSet.setFormSize(15f);
        barDataSet.setDrawValues(false);
        barDataSet.setValueTextSize(12f);

        BarData data = new BarData(barDataSet);
        serviceBarChart.setData(data);
        //serviceBarChart.animateXY(2000, 2000);
        serviceBarChart.setScaleEnabled(false);
        serviceBarChart.getLegend().setEnabled(false);
        serviceBarChart.setDrawBarShadow(false);
        serviceBarChart.getDescription().setEnabled(false);
        serviceBarChart.setPinchZoom(false);
        serviceBarChart.setDrawGridBackground(true);
        serviceBarChart.setVisibleXRangeMaximum(3);
        serviceBarChart.invalidate();
    }

    private void initPieChart(){
        //using percentage as values instead of amount
        categoryPieChart.setUsePercentValues(true);

        //remove the description label on the lower left corner, default true if not set
        categoryPieChart.getDescription().setEnabled(false);

        //enabling the user to rotate the chart, default true
        categoryPieChart.setRotationEnabled(true);
        //adding friction when rotating the pie chart
        categoryPieChart.setDragDecelerationFrictionCoef(0.9f);
        //setting the first entry start from right hand side, default starting from top
        categoryPieChart.setRotationAngle(0);

        //highlight the entry when it is tapped, default true if not set
        categoryPieChart.setHighlightPerTapEnabled(true);
        //adding animation so the entries pop up from 0 degree
        //categoryPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        //setting the color of the hole in the middle, default white
        //categoryPieChart.setHoleColor(Color.parseColor("#000000"));

    }

    private void initializeDailyTrxsBar(){
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    private void setData(int count, float range) {
        float start = 1f;
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));
            if (Math.random() * 100 < 25) {
                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
            } else {
                values.add(new BarEntry(i, val));
            }
    }
    BarDataSet set1;
        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "The year 2017");
            set1.setDrawIcons(false);
            int startColor1 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(getActivity(), android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(getActivity(), android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(getActivity(), android.R.color.holo_red_light);
            int endColor1 = ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark);
            int endColor2 = ContextCompat.getColor(getActivity(), android.R.color.holo_purple);
            int endColor3 = ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark);
            int endColor4 = ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark);
            int endColor5 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_dark);
            List<GradientColor> gradientFills = new ArrayList<>();
            gradientFills.add(new GradientColor(startColor1, endColor1));
            gradientFills.add(new GradientColor(startColor2, endColor2));
            gradientFills.add(new GradientColor(startColor3, endColor3));
            gradientFills.add(new GradientColor(startColor4, endColor4));
            gradientFills.add(new GradientColor(startColor5, endColor5));
            set1.setGradientColors(gradientFills);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            barChart.setData(data);
        }
    }

    private void fetchTransactionsList(){

        dashboardViewModel.getAllTrxs().observe(getActivity(),
                new Observer<List<Transaction>>() {
                    @Override
                    public void onChanged(@Nullable final List<Transaction> transactionList) {
                        transactionsAdapter.swapTransactionList(transactionList);
                    }
                });
    }

    /*private void setTransactionsAdapter(View view)
    {
        transactionsRecyclerView = view.findViewById(R.id.daily_trxs_bar);
        transactionsAdapter = new HomeTransactionsAdapter(getActivity(),this);

        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL, false);
        transactionsRecyclerView.setLayoutManager(mLayoutManager);
        transactionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        transactionsRecyclerView.setAdapter(transactionsAdapter);
    }*/

    public void onHomeTransactionItemClicked(int position) {
        Transaction transaction = transactionList.get(position);
        mTransactionsCallback.transactionRowListClick(transaction.getTransaction_id(), transaction.getService());
    }

    public interface OnHomeTransactionListClickListener {
        void transactionRowListClick(int transactionId, String payeeName);
    }

    public interface OnHomeProListClickListener {
        void homeProListClick(int heritageId);
    }

    //method to check for internet connection
    private static boolean isNetworkAvailable(Context context) {
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
}
