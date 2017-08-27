package io.square1.laravelConnect.client.results;

/**
 * Created by roberto on 10/05/2017.
 */

public abstract class Result {




    public Result(){

    }

    public abstract  boolean isSuccessful();

    public abstract Object getData();

}
