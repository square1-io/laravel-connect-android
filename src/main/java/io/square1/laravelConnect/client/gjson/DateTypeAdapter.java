
package io.square1.laravelConnect.client.gjson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeAdapter implements JsonDeserializer<Date> {

        public Date deserialize(JsonElement json,
                                   Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException
        {
            Date result = null;
            String value = json.getAsString();

            SimpleDateFormat dateFormat = new SimpleDateFormat(GsonConverterFactory.DATE_FORMAT);
            try {
              result = dateFormat.parse(value);
            } catch (Exception e){
                /// date format is not working
            }
            // is it a timestamp
            if(result == null) {
                try {
                    Integer seconds = Integer.parseInt(value);
                    result = new Date(seconds * 1000);
                } catch (Exception e) {

                }
            }

            return result;
        }
    }