package org.mifos.connector.ams.fineract.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.util.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

/** Contains utility methods for connection. */
@Slf4j
public class ConnectionUtils {

    private ConnectionUtils() {}

    /**
     * returns camel dsl for applying connection timeout.
     *
     * @param timeout
     *            timeout value in ms
     * @return camel dsl string with timeout
     */
    public static String getConnectionTimeoutDsl(final int timeout) {
        String base = "httpClient.connectTimeout={}&httpClient.connectionRequestTimeout={}" + "&httpClient.socketTimeout={}";
        return base.replace("{}", "" + timeout);
    }

    public static String parseErrorDescriptionFromJsonPayload(String errorJson) {
        if (errorJson == null || errorJson.isEmpty()) {
            return "Internal Server Error";
        }
        try {
            JsonObject jsonObject = (new Gson()).fromJson(errorJson, JsonObject.class);
            String[] keyList = { "Message", "error", "errorDescription", "errorMessage", "description" };
            for (String s : keyList) {
                String data = jsonObject.getString(s);
                if (data != null && !data.isEmpty()) {
                    return data;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "Internal Server Error";
    }

    public static String convertCustomData(JSONArray customData, String key) {
        for (Object obj : customData) {
            JSONObject item = (JSONObject) obj;
            try {
                String filter = item.getString("key");
                if (filter != null && filter.equalsIgnoreCase(key)) {
                    return item.getString("value");
                }
            } catch (Exception e) {
                log.error("Error while converting custom data: {}", e);
            }
        }
        return null;
    }
}
