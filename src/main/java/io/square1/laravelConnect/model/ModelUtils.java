package io.square1.laravelConnect.model;

import android.content.Context;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * Created by roberto on 05/06/2017.
 */

public class ModelUtils {


    public static <T extends BaseModel> void copyValues(T destination, T origin){

        Class<T> clazz = (Class<T>) origin.getClass();

        List<Field> fields = getAllModelFields(clazz);

        if (fields != null) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(destination, field.get(origin));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static List<Field> getAllModelFields(Class aClass) {

        List<Field> fields = new ArrayList<>();
        do {
            Collections.addAll(fields, aClass.getDeclaredFields());
            aClass = aClass.getSuperclass();
        } while (aClass != null);
        return fields;
    }

    public static <T extends BaseModel> String pathForModel(Class<T> model){
        try {
            String path = (String) model.getMethod("getModelPath").invoke(null);
            return path;
        }catch (Exception e){
            return "";
        }
    }

    public static <T extends BaseModel> String privateKeyForModel(Class<T> model){
        try {
            String path = (String) model.getField("PRIMARY_KEY").get(null);
            return path;
        }catch (Exception e){
            return "";
        }
    }

    public static  Map<String, Object> updateParamsForModel(BaseModel model){

        HashMap<String, Object> updateFiels = new HashMap<>();

        Collection<ModelProperty> properties = model.mProperties.values();
        for ( ModelProperty property : properties ){

            if(property.getChanged() == true){
                updateFiels.put(property.getName(), property.getValue());
            }
        }

        return updateFiels;

    }

    public static BaseModel mockModelInstance(Class<BaseModel> parentClass) {

        try{

            return parentClass.newInstance();

        }catch (Exception e){
            return null;
        }
    }

    public static ArrayList getClassesForPackage(Context context, String packageName) {

        ArrayList<Class> classes = new ArrayList<Class>();
        try {
            String packageCodePath = context.getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
                String className = iter.nextElement();
                if (className.contains(packageName)) {
                    Class currentClass =  getModelClassForName(className);
                    if(currentClass != null) {
                        classes.add(currentClass);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  classes;
    }

    private static Class getModelClassForName(String className){

        try{
            Class currentClass = Class.forName(className);

            if(BaseModel.class.isAssignableFrom(currentClass)){
                return currentClass;
            }

        }catch (Exception e){

        }

        return null;

    }
}
