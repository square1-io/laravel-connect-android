package io.square1.laravelConnect.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.square1.laravelConnect.client.gjson.GSonModelFactory;
import io.square1.laravelConnect.model.BaseModel;

/**
 * Created by roberto on 11/07/2017.
 */

public class Auth {

    public interface AuthStateChangedObserver {
        void onLoginStateChanged();
    }

    private static Auth sInstance;


    synchronized static Auth init(Context context, Gson gson, Class userClass){

        if(sInstance == null){
            sInstance = new Auth(context, userClass, gson);
        }
        return sInstance;
    }

    private static final String PREF_JWT_TOKEN =  "PREF_JWT_TOKEN";

    private Gson mGson;
    private Class mCurrentUserClass;
    private BaseModel mCurrentUser;
    private Context mApplicationContext;
    private ArrayList<AuthStateChangedObserver> mObservers;

    private Auth(Context context, Class currentUserClass, Gson gson){
        mApplicationContext = context.getApplicationContext();
        mCurrentUserClass = currentUserClass;
        mGson = gson;
        mObservers = new ArrayList<>();
    }

    private String loadToken(){

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mApplicationContext);
        return sharedPreferences.getString(PREF_JWT_TOKEN, "");

    }

    private void storeToken(String token){

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mApplicationContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_JWT_TOKEN, token);
        editor.commit();

        ArrayList<AuthStateChangedObserver> observers = new ArrayList<>(mObservers);
        for(AuthStateChangedObserver observer : observers){
            observer.onLoginStateChanged();
        }
    }
    private BaseModel parseUser(JsonObject object){
        BaseModel user = GSonModelFactory.getModelInstance(mGson, object, mCurrentUserClass);
        setCurrentUser(user);
        return user;
    }

    private BaseModel loadCurrentUser(){
        return mCurrentUser;
    }

    private void storeCurrentUser(BaseModel user){
        mCurrentUser = user;
    }

    public static boolean isLoggedIn(){
        String token =  sInstance.getToken();
        return TextUtils.isEmpty(token) == false;
    }

    public static void setToken(String token){
        sInstance.storeToken(token);
    }

    public static BaseModel setCurrentUser(JsonObject object){
      return sInstance.parseUser(object);
    }

    public static void clearToken(){
        sInstance.storeToken("");
    }

    public static String getToken(){
        return sInstance.loadToken();
    }

    public static void setCurrentUser(BaseModel baseModel){
        sInstance.storeCurrentUser(baseModel);
    }

    public static BaseModel getCurrentUser(){
        return sInstance.loadCurrentUser();
    }

    public static void registerObserver(AuthStateChangedObserver observer){
        if(sInstance.mObservers.contains(observer) == false){
            sInstance.mObservers.add(observer);
        }
    }
    public static void unregisterObserver(AuthStateChangedObserver observer){
        sInstance.mObservers.remove(observer);
    }

    public static ApiRequest login(String email, String password, LaravelConnectClient.Observer observer){
        LaravelConnectClient connectClient = LaravelConnectClient.getInstance();
        return connectClient.login(email, password, observer);
    }

    public static ApiRequest refresh(LaravelConnectClient.Observer observer){
        if(isLoggedIn() == true) {
            LaravelConnectClient connectClient = LaravelConnectClient.getInstance();
            return connectClient.refreshCurrentUser(observer);
        }
        return null;
    }

    public static void logout(){
        setCurrentUser((BaseModel)null);
        clearToken();
    }

}
