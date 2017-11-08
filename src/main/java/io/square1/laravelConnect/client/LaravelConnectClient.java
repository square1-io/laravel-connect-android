package io.square1.laravelConnect.client;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.square1.laravelConnect.client.gjson.GsonConverterFactory;
import io.square1.laravelConnect.client.results.Result;
import io.square1.laravelConnect.client.retrofit.RetrofitApiClient;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.Filter;
import io.square1.laravelConnect.requests.Param;
import io.square1.laravelConnect.requests.ParamSerializer;

/**
 * Created by roberto on 27/06/2017.
 */

public class LaravelConnectClient {



    public interface Observer {
        void onRequestCompleted(Result result);
    }

    private static LaravelConnectClient sInstance;


    private LaravelConnectSettings mSettings;
    private RetrofitApiClient mClientImplementation;
    private Context mApplicationContext;
    private String mApplicationVersion;

    public synchronized static LaravelConnectClient init(Context context, LaravelConnectSettings settings) {

        if(sInstance == null){
            sInstance = new LaravelConnectClient(context, settings);
        }

        return sInstance;
    }

    public synchronized static LaravelConnectClient getInstance() {

        if(sInstance == null){
            throw  new RuntimeException("api client not initialised , call init at application start");
        }

        return sInstance;
    }


    private LaravelConnectClient(Context context, LaravelConnectSettings settings){

        Auth.init(context,
                GsonConverterFactory.create().getGson(),
                settings.getUserModelClass());

        mSettings = settings;
        mApplicationContext = context.getApplicationContext();
        mApplicationVersion = getApplicationVersion();
        mClientImplementation = new RetrofitApiClient(context, this, settings);
    }

    public HashMap<String, String> getExtraHeaders(){

        HashMap<String, String> headers = new HashMap<>();

        // ongoing.addHeader("Accept", "application/json;versions=1");
        if (Auth.isLoggedIn()) {
            headers.put("Authorization", "Bearer " + Auth.getToken());
        }
        if(TextUtils.isEmpty(mApplicationVersion) == false){
            headers.put("X-Connect-Client", mApplicationVersion);
        }

        String apiKey = mSettings.getApiKey();
        if(TextUtils.isEmpty(apiKey) == false){
            headers.put("X-Api-Key", apiKey);
        }


        return headers;
    }


    public ApiRequest list(Class<? extends BaseModel> tModel,
                           int page,
                           int perPage,
                           Observer observer){


        return list(tModel, page, perPage, observer, null, null);

    }
    public ApiRequest list(Class<? extends BaseModel> tModel,
                           int page,
                           int perPage,
                           Observer observer,
                           Filter filter,
                           Sort... orderBy){

        ApiRequest request =  mClientImplementation.list(tModel, page, perPage, observer, filter, orderBy);
        request.execute();
        return request;
    }

    public ApiRequest list(Class<? extends BaseModel> tModel,
                           int parentId,
                           Class<? extends BaseModel> tRelationClass,
                           String relationName,
                           int page,
                           int perPage,
                           Observer observer,
                           Filter filter,
                           Sort... orderBy){


        ApiRequest request =  mClientImplementation.list(tModel,
                parentId,
                tRelationClass,
                relationName,
                page,
                perPage,
                observer,
                filter,
                orderBy);

        request.execute();

        return request;
    }


    public ApiRequest show(BaseModel model, Observer observer){
        return show(model.getClass(), model.getIdValue(), observer);

    }

    public ApiRequest show(Class<? extends BaseModel> tModel, int id, Observer observer){

        ApiRequest request =  mClientImplementation.show(tModel, id, observer);
        request.execute();
        return request;

    }

    public ApiRequest update(BaseModel model, Observer observer){

        ApiRequest request =  mClientImplementation.edit(model, observer);
        request.execute();
        return request;
    }

    public void create(BaseModel model, Observer observer){

    }

    public ApiRequest post(Class model, String route, HashMap<String, Param>  parameters, LaravelConnectClient.Observer observer ) {
        return mClientImplementation.generalPostRequest(model, route, parameters, mSettings, observer);
    }

    public ApiRequest get(Class model,String route, HashMap<String, Param>  parameters, LaravelConnectClient.Observer observer) {
        TreeMap<String, String> parameterValues = ParamSerializer.serializeforUrlEncodedRequest(parameters, mSettings);
        return mClientImplementation.generalGetRequest(model, route, parameterValues, observer);
    }

    public ApiRequest delete(String route, HashMap<String, Param>  parameters, LaravelConnectClient.Observer observer) {
        return null;
    }

    public ApiRequest head(String route, HashMap<String, Param>  parameters, LaravelConnectClient.Observer observer) {
        return null;
    }

    public ApiRequest login(String email, String password, LaravelConnectClient.Observer observer){

        ApiRequest request = mClientImplementation.login(email,
                password,
                mSettings.getClientGrantType() ,
                mSettings.getClientId(),
                mSettings.getClientSecret(), observer);
        request.execute();
        return request;
    }

    public ApiRequest refreshCurrentUser(LaravelConnectClient.Observer observer){
        ApiRequest request = mClientImplementation.refreshCurrentUser(observer);
        request.execute();
        return request;
    }


    public ApiRequest executeGenericRequest(Class modelClass, String method, String route, HashMap<String, Param> params, Observer observer) {

        //parse route and resolve route params
        ArrayList<String> routeParams = extractRouteParams(route);

        for(String param : routeParams){

            String value = null;
            //do we have a param in the supplied params ?
            Param paramValue = params.get(param);
            if(paramValue != null) {
                value = ParamSerializer.serializeRouteParam(paramValue, mSettings);
            }else { //try with some default
                value = mSettings.getDefaultParamValue(param);
            }

            if(TextUtils.isEmpty(value) == false){
                route = route.replaceAll("\\{"+param+"\\}",ParamSerializer.urlEncode(value));
            }

        }

        ApiRequest request = null;
        if("POST".equalsIgnoreCase(method)) {
            request =  post(modelClass, route, params, observer);
        }
        else  if("GET".equalsIgnoreCase(method)) {
            request =  get(modelClass, route, params, observer);
        }

        if(request != null){
            request.execute();
        }

        return request;
    }

    public ArrayList<String> extractRouteParams(String route) {

        Pattern pattern = Pattern.compile("\\{(\\w+?)?\\}");
        ArrayList<String> list = new ArrayList<String>();
        Matcher m = pattern.matcher(route);
        while (m.find()) {
            list.add(m.group(1));
        }

        return list;

    }

    public String getApplicationVersion(){


        String packageName = mApplicationContext.getPackageName();

        StringBuilder stringBuilder = new StringBuilder(packageName);
        stringBuilder.append("_v");

        try {
            PackageManager packageManager = mApplicationContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            stringBuilder.append(packageInfo.versionName);
            stringBuilder.append("_");
            stringBuilder.append(packageInfo.versionCode);
        }catch (Exception e){
            stringBuilder.append("00");
        }

        return stringBuilder.toString();
    }


}
