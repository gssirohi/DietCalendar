package com.techticz.networking.retrofit;

import android.content.Context;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.Logger;
import com.ihsanbal.logging.LoggingInterceptor;
import com.techticz.networking.BuildConfig;
import com.techticz.networking.livedata.LiveDataCallAdapterFactory;
import com.techticz.powerkit.constant.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RetrofitProvider {
    public static Retrofit provideDefaultRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(provideOkHttpClient())
                .build();
    }

    public static Retrofit provideMockRetrofit(String baseUrl,Context context) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(provideOkHttpClientWithMockData(context))
                .build();
    }

    public static OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);
        if (Environment.Companion.getShowNetworkLogs()) {
/*
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
*/

            LoggingInterceptor secondInterceptor = new LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("Request")
                    .response("ResponseContainer")
              /*.logger(new Logger() {
                  @Override
                  public void log(int level, String tag, String msg) {
                      Log.w(tag, msg);
                  }
              })*/
                    .build();
                    Timber.d("---Network Interceptor Attached---");
            builder.addInterceptor(secondInterceptor);
        }
        return builder.build();
    }

    public static OkHttpClient provideOkHttpClientWithMockData(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);
        if (Environment.Companion.getShowNetworkLogs()) {
            Interceptor mockResponseInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Timber.i("--------------Mocking Intercepted----------");
                    Request request = chain.request();
                    HttpUrl url = request.url();
                    if(url.toString().contains("PaymentOptions")){
                        Response response = prepareResponse(context,chain,"payment_options.json");
                        return response;
                    } else {
                        Response response = chain.proceed(request);
                        return response;
                    }
                }
            };

            LoggingInterceptor secondInterceptor = new LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("Request")
                    .response("ResponseContainer")
              /*.logger(new Logger() {
                  @Override
                  public void log(int level, String tag, String msg) {
                      Log.w(tag, msg);
                  }
              })*/
                    .build();
            Timber.d("---Logging Interceptor Attached---");
           // builder.addInterceptor(secondInterceptor);
            Timber.d("---Mocking Interceptor Attached---");
            builder.addInterceptor(mockResponseInterceptor);
        }
        return builder.build();
    }

    private static Response prepareResponse(Context context,Interceptor.Chain chain,String jsonFileName) throws IOException{
        InputStream is = context.getAssets().open(jsonFileName);
        String responseString = convertStreamToString(is);
        Timber.i("Delivering Mocked Response from ["+jsonFileName+"] :\n" + responseString);
        return new Response.Builder()
                .code(200)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                .addHeader("content-type", "application/json")
                .build();

    }

    public static String convertStreamToString(InputStream inputStream) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String streamLine;
            try {
                while ((streamLine = reader.readLine()) != null) {
                    stringBuilder.append(streamLine + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
