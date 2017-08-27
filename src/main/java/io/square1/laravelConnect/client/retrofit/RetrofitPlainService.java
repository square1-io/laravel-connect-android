package io.square1.laravelConnect.client.retrofit;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by roberto on 26/05/2017.
 */

public interface RetrofitPlainService {

    @GET
    Call<JsonObject> get(@Url String url, @QueryMap Map<String, String> options);

    @DELETE
    Call<JsonObject> delete(@Url String url, @Body HashMap<String, String> request);

    @Multipart
    @POST
    Call<JsonObject> post(@Url String url, @PartMap Map<String, RequestBody> params);



}
