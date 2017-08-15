package io.square1.laravelConnect.client.retrofit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import io.square1.laravelConnect.client.ApiRequest;
import io.square1.laravelConnect.client.Auth;
import io.square1.laravelConnect.client.LaravelConnectClient;
import io.square1.laravelConnect.client.LaravelConnectSettings;
import io.square1.laravelConnect.client.Sort;
import io.square1.laravelConnect.client.gjson.GsonConverterFactory;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.Criteria;
import io.square1.laravelConnect.model.Filter;
import io.square1.laravelConnect.model.ModelUtils;
import io.square1.laravelConnect.requests.Param;
import io.square1.laravelConnect.requests.ParamSerializer;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;


/**
 * Created by roberto on 10/05/2017.
 */

public class RetrofitApiClient {


    private class AddHeadersInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            okhttp3.Request.Builder ongoing = chain.request().newBuilder();

            HashMap<String,String> headers = mApiClient.getExtraHeaders();

            Set<String> headerNames = headers.keySet();

            for(String headerName : headerNames){
                ongoing.addHeader(headerName, headers.get(headerName));
            }

            return chain.proceed(ongoing.build());
        }
    }


    private Context mApplicationContext;

    private RetrofitService mRetrofitService;
    private LaravelConnectSettings mSettings;
    private LaravelConnectClient mApiClient;

    private RetrofitPlainService mRetrofitPlainService;


    public RetrofitApiClient(Context context, LaravelConnectClient client, LaravelConnectSettings settings){

        mApiClient = client;
        mSettings = settings;
        mApplicationContext = context.getApplicationContext();

        mRetrofitService = getRetrofitService(settings);
        mRetrofitPlainService = getPlainServiceForRequest();

    }

    private RetrofitService getRetrofitService(LaravelConnectSettings settings){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String apiPath = settings.buildApiPath();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(apiPath)
                .client(httpClient.connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .addInterceptor(new AddHeadersInterceptor())
                        .followRedirects(true)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitService.class);
    }

    private RetrofitPlainService getPlainServiceForRequest(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String url = mSettings.getApiAddress();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url)
                .client(httpClient.connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .addInterceptor(new AddHeadersInterceptor())
                        .followRedirects(true)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return  retrofit.create(RetrofitPlainService.class);

    }



    public ApiRequest list(Class<? extends BaseModel> tModel,
                           int page,
                           int perPage,
                           LaravelConnectClient.Observer observer,
                           Filter filter,
                           Sort... orderBy){

        String modelPath = ModelUtils.pathForModel(tModel);
        HashMap<String,String> requestParameters = new HashMap<>();
        parseFilter(requestParameters, "filter", filter);
        parseSort(requestParameters, "sort_by", orderBy);
        Call call = mRetrofitService.get(modelPath, page, perPage, requestParameters);
        RetrofitRequest retrofitRequest = new RetrofitRequest(call, new RetrofitCallHandler(tModel, observer));
        return retrofitRequest;

    }

    public ApiRequest list(Class<? extends BaseModel> tParentModel,
                           int parentId,
                           Class<? extends BaseModel> tRelationModel,
                           String relationName,
                           int page,
                           int perPage,
                           LaravelConnectClient.Observer observer,
                           Filter filter,
                           Sort... orderBy){

        String modelPath = ModelUtils.pathForModel(tParentModel);
        HashMap<String,String> requestParameters = new HashMap<>();
        parseFilter(requestParameters, "filter", filter);
        parseSort(requestParameters, "sort_by", orderBy);
        Call call = mRetrofitService.get( modelPath, parentId, relationName, page, perPage, requestParameters);

        RetrofitRequest retrofitRequest = new RetrofitRequest(call, new RetrofitCallHandler(tRelationModel, observer));
        return retrofitRequest;

    }

    public ApiRequest show(Class<? extends BaseModel> tModel,
                           int id,
                           LaravelConnectClient.Observer observer){

        String modelPath = ModelUtils.pathForModel(tModel);
        Call call = mRetrofitService.get( modelPath, id);
        RetrofitRequest retrofitRequest = new RetrofitRequest(call, new RetrofitCallHandler(tModel, observer));
        return retrofitRequest;
    }

    public ApiRequest edit(BaseModel model, LaravelConnectClient.Observer observer){

        String modelPath = ModelUtils.pathForModel(model.getClass());
        Map<String, Object> maps = ModelUtils.updateParamsForModel(model);
        Call call = mRetrofitService.edit(modelPath, model.getIdValue(), maps);
        RetrofitRequest retrofitRequest = new RetrofitRequest(call, new RetrofitCallHandler(model.getClass(), observer));
        return retrofitRequest;
    }

    public ApiRequest show(Class<? extends BaseModel> tParentModel,
                           int parentId,
                           Class<? extends BaseModel> tRelationModel,
                           String relationName,
                           int relationId,
                           LaravelConnectClient.Observer observer){

        String modelPath = ModelUtils.pathForModel(tParentModel);
        Call call = mRetrofitService.get( modelPath, parentId, relationName, relationId);
        RetrofitRequest retrofitRequest = new RetrofitRequest(call, new RetrofitCallHandler(tRelationModel, observer));
        return retrofitRequest;

    }

    public ApiRequest login(String email, String password, String grantType, int clientId, String clientSecret, LaravelConnectClient.Observer observer){
        Call call = mRetrofitService.login(email, password, grantType, clientId, clientSecret);
        RetrofitAuthRequest retrofitRequest = new RetrofitAuthRequest(call, observer);
        return retrofitRequest;
    }


    public ApiRequest generalPostRequest(Class model,  String route, Map<String, String> params , LaravelConnectClient.Observer observer ) {

        Call call = mRetrofitPlainService.post(route);
        RetrofitRequest retrofitRequest = new RetrofitRequest(call, new RetrofitCallHandler(model, observer));
        return retrofitRequest;
    }

    public ApiRequest generalGetRequest(Class model,  String route, Map<String, String> params , LaravelConnectClient.Observer observer) {
        Call call = mRetrofitPlainService.get(route, params);
        RetrofitRequest retrofitRequest = new RetrofitRequest(call, new RetrofitCallHandler(model, observer));
        return retrofitRequest;
    }

    public ApiRequest generalDeleteRequest( String route, Map<String, Param> params , LaravelConnectClient.Observer observer ) {
        return null;
    }

    public ApiRequest generalHeadRequest( String route, Map<String, Param> params, LaravelConnectClient.Observer observer ) {
        return null;
    }


    public static void parseFilter(HashMap<String,String> map, String name, Filter[] filters){

        if(filters == null || filters.length == 0){
            return;
        }
        final String filterArrayName  = name + "[]";
        for(Filter filter : filters){
            parseFilter(map, filterArrayName, filter);
        }

    }

    public static void parseFilter(HashMap<String,String> map, String name, Filter filter){

        if(filter == null){
            return;
        }

        ArrayList<String> params = filter.getNames();
        for (String paramName : params){
            ArrayList<Criteria> criterias = filter.getCriteria(paramName);

            for(Criteria criteria : criterias){
                map.put(name+"["+paramName+"]["+criteria.getVerb()+"][]", criteria.getPropertyValue());
            }
        }
    }

    public static void parseSort(HashMap<String,String> map, String name, Sort[] sort){

//        ArrayList<String> params = filter.getNames();
//        for (String paramName : params){
//            ArrayList<Criteria> criterias = filter.getCriteria(paramName);
//
//            for(Criteria criteria : criterias){
//                map.put(name+"["+paramName+"]["+criteria.getVerb()+"]", criteria.getPropertyValue());
//            }
//
//        }

    }





}
