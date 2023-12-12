/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.payway.dashboard.utilities;

import android.content.Context;

import com.payway.dashboard.data.AppExecutors;
import com.payway.dashboard.data.DashboardRepository;
import com.payway.dashboard.ui.viewmodels.DashboardViewModelFactory;


/**
 * The purpose of InjectorUtils is to provide static methods for dependency injection.
 * Dependency injection is the idea that you should make required components available for a class,
 * instead of creating them within the class itself.
 */
public class InjectorUtils {
    private static final String TAG = InjectorUtils.class.getSimpleName();

    public static DashboardRepository provideRepository(Context context) {

        AppExecutors executors = AppExecutors.getInstance();

        return DashboardRepository.getInstance();
    }

    public static DashboardViewModelFactory provideTransactionsViewModelFactory(Context context) {
        DashboardRepository repository = provideRepository(context.getApplicationContext());
        return new DashboardViewModelFactory(repository,context);
    }
}