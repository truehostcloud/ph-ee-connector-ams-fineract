package org.mifos.connector.ams.fineract.camel.config;

/**
 * Contains properties related to camel.
 */
public class CamelProperties {

    private CamelProperties() {
    }

    public static final String CHANNEL_REQUEST = "channelRequest";
    public static final String CAMEL_HTTP_RESPONSE_CODE = "CamelHttpResponseCode";

    // Fields names
    public static final String AMOUNT_VARIABLE_NAME = "amount";
    public static final String CURRENCY_VARIABLE_NAME = "currency";
    public static final String MSISDN_VARIABLE_NAME = "msisdn";
    public static final String ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME = "accountHoldingInstitutionId";

}
