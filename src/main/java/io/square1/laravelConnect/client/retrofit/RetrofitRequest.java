package io.square1.laravelConnect.client.retrofit;

import io.square1.laravelConnect.client.ApiRequest;
import retrofit2.Call;

/**
 * Created by roberto on 30/06/2017.
 */

public class RetrofitRequest implements ApiRequest {

    private RetrofitCallHandler mHandler;
    private Call mCall;

    public RetrofitRequest(Call call, RetrofitCallHandler handler){
        mCall = call;
        mHandler = handler;

    }

    @Override
    public void cancel() {
        mCall.cancel();
    }

    @Override
    public void execute() {
        mCall.enqueue(mHandler);
    }
}
