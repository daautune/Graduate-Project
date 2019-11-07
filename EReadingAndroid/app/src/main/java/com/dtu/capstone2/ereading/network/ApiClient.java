package com.dtu.capstone2.ereading.network;

import com.dtu.capstone2.ereading.App;
import com.dtu.capstone2.ereading.BuildConfig;
import com.dtu.capstone2.ereading.network.utils.CustomCallAdapterFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ApiClient {
    private static final long API_TIMEOUT = 15000L;// Time out = 15s
    private static ApiClient sApiClient;

    public static ApiClient getInstants() {
        if (sApiClient == null) {
            sApiClient = new ApiClient();
        }
        return sApiClient;
    }

    public ApiServer createServer() {
        String mBaseUrl = "http://10.1.1.139:8000/api/";

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        //Show log request
        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .serializeNulls()
                .create();

        // Header for request
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = (original).newBuilder()
                        .method((original).method(), (original).body());

                // Request customization: add request headers
                requestBuilder.addHeader("Authorization", App.Companion.getInstant().localRepository.getTokenUser());
                requestBuilder.addHeader("app-version", BuildConfig.VERSION_NAME);
                requestBuilder.addHeader("User-Agent", "Android");

                return chain.proceed(requestBuilder.build());
            }
        });

        // Set time out for request
        OkHttpClient client = httpClientBuilder.connectTimeout(API_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(API_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.MILLISECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();

        //Pares data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CustomCallAdapterFactory.Companion.create())
                .client(client)
                .build();
        return retrofit.create(ApiServer.class);
    }

    public ApiServer createServerXml(String baseUrl) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        //Show log request
        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        // Set time out for request
        OkHttpClient client = httpClientBuilder.connectTimeout(API_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(API_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.MILLISECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy())))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(ApiServer.class);
    }
}
