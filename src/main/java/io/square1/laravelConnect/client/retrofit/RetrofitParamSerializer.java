package io.square1.laravelConnect.client.retrofit;

import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.square1.laravelConnect.client.LaravelConnectSettings;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelProperty;
import io.square1.laravelConnect.model.UploadedFile;
import io.square1.laravelConnect.requests.Param;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by roberto on 11/08/2017.
 */

public class RetrofitParamSerializer {


    public static String serializeRouteParam(Param param, LaravelConnectSettings settings){

        Object value = param.getValue();

        if( param.getValue() == null){
            return null;
        }

        if(value instanceof Date){
            SimpleDateFormat dateFormat = new SimpleDateFormat(settings.dateFormat());
            return dateFormat.format((Date)value);
        }

        if(value instanceof Boolean){
            return ((Boolean)value) ? "1" : "0";
        }

        if(value instanceof BaseModel){

            BaseModel baseModel = (BaseModel)value;
            if (TextUtils.isEmpty(param.getKey())) {
                return String.valueOf( baseModel.getIdValue() );
            }else {
                ModelProperty modelProperty = baseModel.getProperty(param.getKey());
                return serializeRouteParam(new Param(param.getName(), modelProperty.getValue()), settings);
            }

        }

        return String.valueOf(param.getValue());

    }

    public static  TreeMap<String, RequestBody> serializeForMultipartPost(Map<String, Param> parameters, LaravelConnectSettings settings){

        TreeMap<String, RequestBody> parameterValues = new TreeMap<>();

        Set<Map.Entry<String, Param>> entries = parameters.entrySet();

        for(Map.Entry<String, Param> entry : entries) {
            Param param = entry.getValue();
            serializeParamToMap(parameterValues, param.getName(), param.getValue(), settings);
        }

        return parameterValues;
    }

    private static  void serializeParamToMap(Map<String, RequestBody> out, String paramName, Object value, LaravelConnectSettings settings){

        if(value != null){

           if(value instanceof File){
                RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), (File)value );
                out.put(paramName, body);
            }
            else if(value instanceof String ||
                    value instanceof Number ){
                RequestBody body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(value));
                out.put(paramName, body);
            }
            else if(value instanceof Boolean ){
                RequestBody body = RequestBody.create(MediaType.parse("text/plain"), ((Boolean)value) ? "1" : "0");
                out.put(paramName, body);
            }
            else if (value instanceof BaseModel){
                int objectId = ((BaseModel)value).getIdValue();
                RequestBody body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(objectId));
                out.put(paramName, body);
            }
            else if (value.getClass().isArray() ){
                int length = Array.getLength(value);
                for (int index = 0; index < length; index ++){
                    Object currentValue = Array.get(value, index);
                    String arrayName = paramName + "["+index+"]";
                    serializeParamToMap(out, arrayName, currentValue,settings);
                }
            }else if(value instanceof Iterable){
                int index = 1;
                Iterator iterator = ((Iterable)value).iterator();
                while (iterator.hasNext()){
                    String arrayName = paramName + "["+index+"]";
                    serializeParamToMap(out, arrayName, iterator.next(),settings);
                    index ++;
                }
            }
        }
    }

}
