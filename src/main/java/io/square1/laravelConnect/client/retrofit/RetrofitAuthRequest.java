package io.square1.laravelConnect.client.retrofit;

import com.google.gson.JsonObject;

import io.square1.laravelConnect.client.ApiRequest;
import io.square1.laravelConnect.client.Auth;
import io.square1.laravelConnect.client.LaravelConnectClient;
import io.square1.laravelConnect.client.results.ResultFactory;
import io.square1.laravelConnect.client.results.ResultObj;
import io.square1.laravelConnect.model.BaseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by roberto on 11/07/2017.
 */

public class RetrofitAuthRequest implements ApiRequest, Callback<JsonObject> {


    private Call<JsonObject> mCall;
    private LaravelConnectClient.Observer mObserver;

    public RetrofitAuthRequest(Call call, LaravelConnectClient.Observer observer){
        mCall = call;
        mObserver = observer;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void execute() {
        mCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

        //"token_type":"Bearer","expires_in":31536000,"":"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjhjNGVkNWM3YWM4YWVjZGRjZGNlNWFhYWFmNTk2MzZjYThlYmYyMTI0OGY0YjY3MTA1YjdjMjYyNmRlNDBkMzhjZmMyM2E1YTA4MGFhOGE2In0.eyJhdWQiOiIyIiwianRpIjoiOGM0ZWQ1YzdhYzhhZWNkZGNkY2U1YWFhYWY1OTYzNmNhOGViZjIxMjQ4ZjRiNjcxMDViN2MyNjI2ZGU0MGQz
        JsonObject data = response.body().getAsJsonObject("data");
        String token = data.get("access_token").getAsString();
        Auth.setToken(token);

        //parse current user
        String reference = data.get("reference").getAsString();

        JsonObject userJson = data.getAsJsonObject("user");
        BaseModel user = Auth.setCurrentUser(userJson);

        if(mObserver != null){
            ResultObj resultObj = new ResultObj<>();
            resultObj.setData(user);
            mObserver.onRequestCompleted(resultObj);
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Auth.clearToken();

        if(mObserver != null){
            mObserver.onRequestCompleted(ResultFactory.getInstance(t));
        }
    }
}
