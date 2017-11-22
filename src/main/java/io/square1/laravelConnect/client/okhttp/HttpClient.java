package io.square1.laravelConnect.client.okhttp;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.square1.laravelConnect.client.LaravelConnectClient;
import io.square1.laravelConnect.client.LaravelConnectSettings;
import io.square1.laravelConnect.client.retrofit.RetrofitApiClient;
import io.square1.laravelConnect.requests.LaravelRequest;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by roberto on 17/11/2017.
 */

public class HttpClient  {



    private class AddHeadersInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            okhttp3.Request.Builder ongoing = chain.request().newBuilder();

            HashMap<String,String> headers = mLaravelClient.getExtraHeaders();

            Set<String> headerNames = headers.keySet();

            for(String headerName : headerNames){
                ongoing.addHeader(headerName, headers.get(headerName));
            }

            return chain.proceed(ongoing.build());
        }
    }

    private LaravelConnectClient mLaravelClient;
    private LaravelConnectSettings mSettings;
    private final OkHttpClient mClient;

    public HttpClient(LaravelConnectClient laravel, LaravelConnectSettings settings){
        mLaravelClient = laravel;
        mSettings = settings;
        mClient = setupClient();
    }

    private OkHttpClient setupClient() {

        return new OkHttpClient.Builder()
                .followRedirects(true)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new AddHeadersInterceptor())
                .followRedirects(true)
                .build();

    }



    public void executePOST(LaravelRequest request){

    }

    public void executeGET(LaravelRequest request){

    }

    private void executeDELETE(LaravelRequest request){

    }


    private static class InternalCallback implements  Callback {

        private final LaravelRequest mRequest;

        public InternalCallback(LaravelRequest request){
            mRequest = request;
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

        }
    };
}
