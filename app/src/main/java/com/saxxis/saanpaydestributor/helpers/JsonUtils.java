package com.saxxis.saanpaydestributor.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static org.json.JSONObject.wrap;

/**
 * Created by saxxis25 on 3/25/2017.
 */

public class JsonUtils {

    public static JSONObject mapToJson(Map<String, String> data)
    {
        JSONObject object = new JSONObject();

        for (Map.Entry<String,String> entry : data.entrySet())
        {
            /*
             * Deviate from the original by checking that keys are non-null and
             * of the proper type. (We still defer validating the values).
             */
            String key = (String) entry.getKey();
            if (key == null)
            {
                throw new NullPointerException("key == null");
            }
            try
            {
                object.put(key, wrap(entry.getValue()));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return object;
    }
}
