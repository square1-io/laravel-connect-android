package io.square1.laravelConnect.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by roberto on 11/08/2017.
 */

public class ParamListBuilder {


    public static HashMap<String, Param> buildParamsList(Object... objects){

        HashMap<String, Param> paramHashMap = new HashMap<>();

        final int pairs = objects == null ? 0 : (objects.length / 2);

        for(int index = 0; index < pairs; index ++ ){

            int nameIndex = index * 2;
            int valueIndex = nameIndex + 1;

            Param param = new Param<>(((String)objects[nameIndex]), objects[valueIndex]);
            paramHashMap.put(param.getName(), param);

        }

        return paramHashMap;
    }


}
