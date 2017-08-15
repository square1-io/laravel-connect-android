package io.square1.laravelConnect.requests;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.square1.laravelConnect.client.LaravelConnectSettings;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelProperty;

/**
 * Created by roberto on 11/08/2017.
 */

public class ParamSerializer {


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

    public static  TreeMap<String, String> serializeforUrlEncodedRequest(Map<String, Param> parameters, LaravelConnectSettings settings){

        TreeMap<String, String> parameterValues = new TreeMap<>();

        Set<Map.Entry<String, Param>> entries = parameters.entrySet();

        for(Map.Entry<String, Param> entry : entries) {
            Param param = entry.getValue();
            serializeParamToMap(parameterValues, param.getName(), param.getValue(), settings);
        }

        return parameterValues;
    }

    private static  void serializeParamToMap(TreeMap<String, String> out, String paramName, Object value, LaravelConnectSettings settings){

        if(value != null){

            if(value instanceof String){
                out.put(paramName, (String)value);
            }
            else if(value instanceof Number){
                out.put(paramName, String.valueOf(value));
            }
            else if (value instanceof BaseModel){
                int objectId = ((BaseModel)value).getIdValue();
                out.put(paramName, String.valueOf(objectId));
            }
            else if (value.getClass().isArray() ){
                String arrayName = paramName + "[]";
                int length = Array.getLength(value);
                for (int index = 0; index < length; index ++){
                    Object currentValue = Array.get(value, index);
                    serializeParamToMap(out, arrayName, currentValue,settings);
                }
            }else if(value instanceof Iterable){
                String arrayName = paramName + "[]";
                Iterator iterator = ((Iterable)value).iterator();
                while (iterator.hasNext()){
                    serializeParamToMap(out, arrayName, iterator.next(),settings);
                }
            }
        }
    }

    public final static String urlEncode(String value){

        try {
            return URLEncoder.encode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }

    }

}
