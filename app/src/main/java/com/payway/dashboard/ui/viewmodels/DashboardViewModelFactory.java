package com.payway.dashboard.ui.viewmodels;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.payway.dashboard.data.DashboardRepository;

public class DashboardViewModelFactory extends ViewModelProvider.NewInstanceFactory
implements ViewModelProvider.Factory{
    private final DashboardRepository mRepository;
    private Context mContext;

    public DashboardViewModelFactory(DashboardRepository repository, Context context) {
        this.mRepository = repository;
        this.mContext = context;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        //return (T) new DashboardViewModel(mContext);
        return null;
    }
}
