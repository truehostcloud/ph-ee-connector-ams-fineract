package org.mifos.connector.ams.fineract.camel.route;

import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME;
import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.AMOUNT_VARIABLE_NAME;
import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.CAMEL_HTTP_RESPONSE_CODE;
import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.CHANNEL_REQUEST;
import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.CURRENCY_VARIABLE_NAME;
import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.MSISDN_VARIABLE_NAME;
import static org.mifos.connector.ams.fineract.zeebe.ZeebeVariables.EXTERNAL_ID;
import static org.mifos.connector.ams.fineract.zeebe.ZeebeVariables.PARTY_LOOKUP_FAILED;
import static org.mifos.connector.ams.fineract.zeebe.ZeebeVariables.TRANSACTION_FAILED;
import static org.mifos.connector.ams.fineract.zeebe.ZeebeVariables.TRANSACTION_ID;
import static org.mifos.connector.ams.fineract.zeebe.ZeebeVariables.TRANSFER_SETTLEMENT_FAILED;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.json.JSONObject;
import org.mifos.connector.ams.fineract.data.FineractConfirmationRequestDto;
import org.mifos.connector.ams.fineract.data.FineractRequestDto;
import org.mifos.connector.ams.fineract.util.ConnectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Route handlers for Fineract flow. */
@Slf4j
@Component
public class FineractRouteBuilder extends RouteBuilder {

    @Value("${fineract.base-url}")
    private String fineractBaseUrl;

    @Value("${fineract.endpoint.validation}")
    private String validationEndpoint;

    @Value("${fineract.endpoint.confirmation}")
    private String confirmationEndpoint;

    @Value("${ams.timeout}")
    private Integer amsTimeout;

    @Override
    public void configure() throws Exception {
        from("direct:transfer-validation-base").id("transfer-validation-base")
                .log(LoggingLevel.INFO, "## Starting transfer Validation base route").to("direct:transfer-validation")
                .choice().when(header(CAMEL_HTTP_RESPONSE_CODE).isEqualTo("200"))
                .log(LoggingLevel.INFO, "Validation successful").process(exchange -> {
                    // processing success case
                    exchange.setProperty(PARTY_LOOKUP_FAILED, false);
                    exchange.setProperty(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME,
                            exchange.getProperty(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME));
                    exchange.setProperty(TRANSACTION_ID, exchange.getProperty(TRANSACTION_ID));
                    exchange.setProperty(AMOUNT_VARIABLE_NAME, exchange.getProperty(AMOUNT_VARIABLE_NAME));
                    exchange.setProperty(CURRENCY_VARIABLE_NAME, exchange.getProperty(CURRENCY_VARIABLE_NAME));
                    exchange.setProperty(MSISDN_VARIABLE_NAME, exchange.getProperty(MSISDN_VARIABLE_NAME));
                    log.debug("Fineract Validation Success");
                }).otherwise().log(LoggingLevel.ERROR, "Validation unsuccessful").process(exchange -> {
                    // processing unsuccessful case
                    exchange.setProperty(PARTY_LOOKUP_FAILED, true);
                    exchange.setProperty(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME,
                            exchange.getProperty(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME));
                    exchange.setProperty(TRANSACTION_ID, exchange.getProperty(TRANSACTION_ID));
                    exchange.setProperty(AMOUNT_VARIABLE_NAME, exchange.getProperty(AMOUNT_VARIABLE_NAME));
                    exchange.setProperty(CURRENCY_VARIABLE_NAME, exchange.getProperty(CURRENCY_VARIABLE_NAME));
                    exchange.setProperty(MSISDN_VARIABLE_NAME, exchange.getProperty(MSISDN_VARIABLE_NAME));
                    log.debug("Fineract Validation Failure");
                });
        from("direct:transfer-validation").id("transfer-validation")
                .log(LoggingLevel.INFO, "## Starting transfer Validation route").removeHeader("*")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("application/json")).setBody(exchange -> {
                    FineractRequestDto verificationRequestDto;
                    if (exchange.getProperty(CHANNEL_REQUEST) != null) {
                        JSONObject channelRequest = (JSONObject) exchange.getProperty(CHANNEL_REQUEST);

                        verificationRequestDto = FineractRequestDto.fromChannelRequest(channelRequest,
                                exchange.getProperty(TRANSACTION_ID, String.class));
                    } else {
                        JSONObject payBillRequest = new JSONObject(exchange.getIn().getBody(String.class));

                        verificationRequestDto = FineractRequestDto.convertPayBillPayloadToAmsPayload(payBillRequest);
                        exchange.setProperty(TRANSACTION_ID, verificationRequestDto.getRemoteTransactionId());
                        exchange.setProperty(AMOUNT_VARIABLE_NAME, verificationRequestDto.getAmount());
                        exchange.setProperty(CURRENCY_VARIABLE_NAME, verificationRequestDto.getCurrency());
                        exchange.setProperty(MSISDN_VARIABLE_NAME, verificationRequestDto.getPhoneNumber());
                        exchange.setProperty(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME,
                                exchange.getProperty(ACCT_HOLDING_INSTITUTION_ID_VARIABLE_NAME));
                    }
                    log.debug("Validation request DTO: {}", verificationRequestDto);
                    return verificationRequestDto;
                }).marshal().json(JsonLibrary.Jackson)
                .toD(getValidationUrl() + "?" + ConnectionUtils.getConnectionTimeoutDsl(amsTimeout))
                .log(LoggingLevel.INFO, "Fineract verification api response: \n\n..\n\n..\n\n.. ${body}");

        from("direct:transfer-settlement-base").id("transfer-settlement-base")
                .log(LoggingLevel.INFO, "## Transfer Settlement route").to("direct:transfer-settlement").choice()
                .when(header(CAMEL_HTTP_RESPONSE_CODE).isEqualTo("200"))
                .log(LoggingLevel.INFO, "Call to Fineract AMS for settlement was successful").process(exchange -> {
                    // processing success case
                    // check if actual transaction was also successful
                    Boolean transactionFailed = exchange.getProperty(TRANSACTION_FAILED, Boolean.class);
                    boolean transferSettlementFailed = !Boolean.FALSE.equals(transactionFailed);
                    exchange.setProperty(TRANSFER_SETTLEMENT_FAILED, transferSettlementFailed);
                }).otherwise().log(LoggingLevel.ERROR, "Call to  Fineract AMS for settlement was unsuccessful")
                .process(exchange ->
                // processing unsuccessful case
                exchange.setProperty(TRANSFER_SETTLEMENT_FAILED, true));

        from("direct:transfer-settlement").id("transfer-settlement")
                .log(LoggingLevel.INFO, "## Starting transfer settlement route").removeHeader("*")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("application/json")).setBody(exchange -> {
                    FineractConfirmationRequestDto confirmationRequestDto;
                    if (exchange.getProperty(CHANNEL_REQUEST) != null) {
                        JSONObject channelRequest = (JSONObject) exchange.getProperty(CHANNEL_REQUEST);
                        String transactionId = exchange.getProperty(TRANSACTION_ID, String.class);
                        String externalId = exchange.getProperty(EXTERNAL_ID, String.class);
                        confirmationRequestDto = FineractConfirmationRequestDto.fromChannelRequest(channelRequest,
                                transactionId);
                        confirmationRequestDto.setStatus("successful");
                        confirmationRequestDto.setReceiptId(externalId);

                    } else {
                        JSONObject payBillRequest = new JSONObject(exchange.getIn().getBody(String.class));

                        confirmationRequestDto = FineractConfirmationRequestDto
                                .convertPayBillPayloadToAmsPayload(payBillRequest);
                        exchange.setProperty(TRANSACTION_ID, confirmationRequestDto.getRemoteTransactionId());
                    }
                    log.info("Fineract Confirmation request DTO: \n\n\n {}", confirmationRequestDto);
                    return confirmationRequestDto;
                }).marshal().json(JsonLibrary.Jackson)
                .toD(getConfirmationUrl() + "?bridgeEndpoint=true&throwExceptionOnFailure=false&"
                        + ConnectionUtils.getConnectionTimeoutDsl(amsTimeout))
                .log(LoggingLevel.INFO, "Fineract confirmation api response: \n ${body}");
    }

    /**
     * Combines Fineract base url and the validation endpoint.
     *
     * @return the full url to be used in Fineract confirmation requests
     */
    private String getValidationUrl() {
        return fineractBaseUrl + validationEndpoint;
    }

    /**
     * Combines Fineract base url and the confirmation endpoint.
     *
     * @return the full url to be used in Fineract confirmation requests
     */
    private String getConfirmationUrl() {
        return fineractBaseUrl + confirmationEndpoint;
    }
}
