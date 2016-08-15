package com.ityadi.app.tourmate.Common;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by taslim on 8/12/2016.
 */

public class Network {

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(okHttpClient).build();

        return retrofit.create(serviceClass);
    }
}