package io.square1.laravelConnect.client;

import android.net.Uri;
import android.webkit.URLUtil;

import io.square1.laravelConnect.model.BaseModel;

/**
 * Created by roberto on 09/07/2017.
 */

public abstract class LaravelConnectSettings {

    public abstract String getBasePath();
    public abstract String getApiAddress();

    public abstract String getApiKey();

    public String dateFormat(){
        return "yyyy-mm-dd HH:mm:ss";
    }

    public abstract String getDefaultParamValue(String paramValue);

    public abstract <T extends BaseModel> Class<T> getUserModelClass();

    public final String buildApiPath() throws RuntimeException{

        if(URLUtil.isNetworkUrl(getApiAddress())){
            Uri.Builder builder = Uri.parse(getApiAddress()).buildUpon();
            builder.appendEncodedPath(getBasePath());

            String path = builder.build().toString();

            if(URLUtil.isNetworkUrl(path) == true) {
                return path;
            }else {
                throw new RuntimeException("api address is not valid -> " + path);
            }

        }
        else {
            throw new RuntimeException("api address is not valid -> " + getApiAddress());
        }
    }


}
