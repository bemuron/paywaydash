package com.payway.dashboard.data.network.api;

import com.payway.dashboard.app.AppController;
import com.payway.dashboard.data.network.api.APIService;
import com.payway.dashboard.data.network.api.APIUrl;
import com.payway.dashboard.helpers.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class LocalRetrofitApi {
  Retrofit retrofit1;
  SessionManager sessionManager;

  public LocalRetrofitApi() {
    // Session manager
    sessionManager = new SessionManager(AppController.getInstance().getApplicationContext());

    //Here a logging interceptor is created
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

    //The logging interceptor will be added to the http client
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    httpClient.retryOnConnectionFailure(true);

    httpClient.addInterceptor(logging)
            .connectTimeout(80, TimeUnit.SECONDS)
            .readTimeout(80, TimeUnit.SECONDS);

    //The Retrofit builder will have the client attached, in order to get connection logs
    this.retrofit1 = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(APIUrl.BASE_URL)
            .build();
  }

  public Retrofit getRetrofit1() {
    return retrofit1;
  }

  public APIService getRetrofitService(){
    return this.getRetrofit1().create(APIService.class);
  }
}
