package com.example.googlemapdummysk.retrofit;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
  //  private static final String Base_URL = "http://mobuloustech.com/pantry/api/";
    private static final String Base_URL = "https://maps.googleapis.com/maps/api/";

    private static ApiClient apiClientConnection = null;
    private static APIServiceInterface apiServiceInterface = null;

    public static ApiClient getInstance() {
        if (apiClientConnection == null) {
            apiClientConnection = new ApiClient();
        }
        return apiClientConnection;
    }

    public APIServiceInterface getClient() {
        if (apiServiceInterface == null) {

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS);


            httpBuilder.addNetworkInterceptor(loggingInterceptor);
            httpBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                           // .header("Authorization", AllAPIs.Authorization_TOKEN)
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(Base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpBuilder.build());

            Retrofit retrofit = builder.build();
            apiServiceInterface = retrofit.create(APIServiceInterface.class);
        }
        return apiServiceInterface;
    }


}

