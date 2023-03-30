package org.mifos.connector.ams.fineract.util;

/**
 * Contains utility methods for connection.
 */
public class ConnectionUtils {

    private ConnectionUtils(){}

    /**
     * returns camel dsl for applying connection timeout.
     *
     * @param timeout timeout value in ms
     * @return camel dsl string with timeout
     */
    public static String getConnectionTimeoutDsl(final int timeout) {
        String base = "httpClient.connectTimeout={}&httpClient.connectionRequestTimeout={}"
            + "&httpClient.socketTimeout={}";
        return base.replace("{}", "" + timeout);
    }
}
