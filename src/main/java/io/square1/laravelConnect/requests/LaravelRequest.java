package io.square1.laravelConnect.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.square1.laravelConnect.client.ApiRequest;
import io.square1.laravelConnect.client.LaravelConnectClient;
import io.square1.laravelConnect.model.BaseModel;

/**
 * Created by roberto on 11/08/2017.
 */

public class LaravelRequest {

    public static class Builder {

        private HashMap<String, Param> mParams;
        private String mMethod;
        private String mRoute;
        private Class mBaseClass;
        private boolean mPaginated;

        public Builder(){
            mParams = new HashMap<>();
        }

        public Builder params(Object... nameValuePairs){
            mParams.putAll( ParamListBuilder.buildParamsList(nameValuePairs) );
            return this;
        }

        public Builder modelClass(Class modelClass){
            mBaseClass = modelClass;
            return this;
        }

        public Builder paginated(boolean paginated){
            mPaginated = paginated;
            return this;
        }

        public Builder param(String name, Object value){
            Param param = new Param(name, value);
            mParams.put(param.getName(), param);
            return this;
        }

        public <T extends BaseModel> Builder param(String name, String key, List<T> values){
            Param param = new Param(name, key, values);
            mParams.put(param.getName(), param );
            return this;
        }

        public Builder method(String method){
            mMethod = method;
            return this;
        }

        public Builder route(String route){
            mRoute = route;
            return this;
        }

        public LaravelRequest build(){
            return new LaravelRequest(mMethod, mRoute, mBaseClass, mParams);
        }


    }

    private HashMap<String, Param> mParams;
    private String mMethod;
    private String mRoute;
    private Class mModelClass;


    LaravelRequest(String method, String route, Class modelClass, HashMap<String, Param>  params){
        mMethod = method;
        mRoute = route;
        mParams = params;
        mModelClass = modelClass;
    }

    public LaravelRequest param(String name, Object value){
        Param param = new Param(name, value);
        mParams.put(param.getName(), param);
        return this;
    }

    public final ApiRequest execute(LaravelConnectClient.Observer observer){
        LaravelConnectClient laravelConnectClient = LaravelConnectClient.getInstance();
        return laravelConnectClient.executeGenericRequest(mModelClass, mMethod, mRoute, mParams, observer);
    }


}
