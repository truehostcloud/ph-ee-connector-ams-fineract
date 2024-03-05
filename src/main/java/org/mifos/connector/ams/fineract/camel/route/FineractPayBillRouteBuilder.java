package org.mifos.connector.ams.fineract.camel.route;

import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME;
import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.CLIENT_NAME_VARIABLE_NAME;
import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.CUSTOM_DATA_VARIABLE_NAME;
import static org.mifos.connector.ams.fineract.zeebe.ZeebeVariables.PARTY_LOOKUP_FAILED;
import static org.mifos.connector.ams.fineract.zeebe.ZeebeVariables.TRANSACTION_ID;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.json.JSONObject;
import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Route handlers for Fineract PayBill flow.
 *
 */
@Slf4j
@Component
public class FineractPayBillRouteBuilder extends ErrorHandlerRouteBuilder {

    @Override
    public void configure() {
        from("rest:POST:/api/v1/paybill/validate/fineract").id("validate-user")
                .log(LoggingLevel.INFO, "## Fineract user validation").setBody(e -> {
                    String body = e.getIn().getBody(String.class);
                    log.debug("Body : {}", body);
                    String accountHoldingInstitutionId = String
                            .valueOf(e.getIn().getHeader(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME));
                    e.setProperty(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME, accountHoldingInstitutionId);
                    return body;
                }).to("direct:transfer-validation-base").process(e -> {
                    String transactionId = e.getProperty(TRANSACTION_ID).toString();
                    log.debug("Transaction Id : {}", transactionId);
                    log.debug("Response received from validation base : {}", e.getIn().getBody());
                    // Building the response
                    JSONObject responseObject = new JSONObject();
                    responseObject.put("reconciled", e.getProperty(PARTY_LOOKUP_FAILED).equals(false));
                    responseObject.put("amsName", "fineract");
                    responseObject.put(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME,
                            e.getProperty("accountHoldingInstitutionId"));
                    responseObject.put(TRANSACTION_ID, e.getProperty(TRANSACTION_ID));
                    responseObject.put("amount", e.getProperty("amount"));
                    responseObject.put("currency", e.getProperty("currency"));
                    responseObject.put("msisdn", e.getProperty("msisdn"));
                    responseObject.put(CLIENT_NAME_VARIABLE_NAME, e.getProperty(CLIENT_NAME_VARIABLE_NAME));
                    responseObject.put(CUSTOM_DATA_VARIABLE_NAME, e.getProperty(CUSTOM_DATA_VARIABLE_NAME));
                    log.debug("response object: {} ", responseObject);
                    e.getIn().setBody(responseObject.toString());
                });
    }
}
