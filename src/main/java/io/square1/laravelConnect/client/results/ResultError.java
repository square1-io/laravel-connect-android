package io.square1.laravelConnect.client.results;

/**
 * Created by roberto on 05/06/2017.
 */

public class ResultError extends Result {


    private String mMessage;

    public ResultError(){
        this("");
    }

    public ResultError(String message){
        super();
        mMessage = message;
    }

    public String getMessage(){
        return mMessage;
    }

    public  boolean isSuccessful() {
        return false;
    }

    @Override
    public Object getData() {
        return null;
    }
}
