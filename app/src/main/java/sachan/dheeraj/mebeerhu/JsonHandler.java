package sachan.dheeraj.mebeerhu;

/**
 * Created by dheeraj on 18/2/15.
 */
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class JsonHandler {

    private static final String TAG = JsonHandler.class.getSimpleName();

    private static ObjectMapper underScoreToCamelCaseMapper;
    private static ObjectMapper normalMapper;

    static {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        underScoreToCamelCaseMapper = new ObjectMapper();
        underScoreToCamelCaseMapper.setDateFormat(df);
        underScoreToCamelCaseMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        underScoreToCamelCaseMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        underScoreToCamelCaseMapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        normalMapper = new ObjectMapper();
        normalMapper.setDateFormat(df);
        normalMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        normalMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    private JsonHandler() {
    }

    /**
     * parses String in json format and maps it to
     * an object of given class
     *
     * @param json     String
     * @param classOfT class to map the json to
     * @return T
     */
    public static <T> T parseUnderScoredToBaseResponse(String json, Class<T> classOfT, Class<?>... innerClassTypes) {
        try {
            if (json == null) {
                return null;
            }
            JavaType parametricType = underScoreToCamelCaseMapper.getTypeFactory().constructParametricType(classOfT, innerClassTypes);
            return underScoreToCamelCaseMapper.readValue(json, parametricType);

        } catch (JsonParseException e) {
            Log.e(TAG, "JsonParseException Failed to parse json correctly. " , e);
        } catch (JsonMappingException e) {
            Log.e(TAG,"JsonMappingException Failed to parse json correctly. " , e);
        } catch (IOException e) {
            Log.e(TAG,"IOException Failed to parse json correctly. " , e);
        }
        return null;
    }

    /**
     * * parses String in json format and maps it to
     * an object of given class
     *
     * @param json     String
     * @param classOfT class to map the json to
     * @return T
     */
    public static <T> T parseUnderScoredResponse(String json, Class<T> classOfT) {
        try {
            if (json == null) {
                return null;
            }
            return underScoreToCamelCaseMapper.readValue(json, classOfT);
        } catch (JsonParseException e) {
            Log.e(TAG,"JsonParseException Failed to parse json correctly. ", e);
        } catch (JsonMappingException e) {
            Log.e(TAG,"JsonMappingException Failed to parse json correctly. ", e);
        } catch (IOException e) {
            Log.e(TAG,"IOException Failed to parse json correctly. " , e);
        }
        return null;
    }

    public static <T> T parseNormal(String json,Class<T> classOfT){
        try {
            if (json == null) {
                return null;
            }
            return normalMapper.readValue(json, classOfT);

        } catch (JsonParseException e) {
            Log.e(TAG,"JsonParseException Failed to parse json correctly. ", e);
        } catch (JsonMappingException e) {
            Log.e(TAG,"JsonMappingException Failed to parse json correctly. ", e);
        } catch (IOException e) {
            Log.e(TAG,"IOException Failed to parse json correctly. " , e);
        }
        return null;
    }

    public static String stringify(Object object) {
        try {
            return underScoreToCamelCaseMapper.writeValueAsString(object);

        } catch (JsonProcessingException e) {
            Log.e(TAG,"JsonProcessingException Failed to stringify json correctly. " , e);
            return null;
        }
    }

    public static String stringifyNormal(Object object) {
        try {
            return normalMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.e(TAG,"JsonProcessingException Failed to stringify json correctly. " , e);
            return null;
        }
    }
}
