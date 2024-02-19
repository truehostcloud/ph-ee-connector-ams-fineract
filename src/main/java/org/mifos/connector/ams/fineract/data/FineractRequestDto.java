package org.mifos.connector.ams.fineract.data;

import static org.mifos.connector.ams.fineract.camel.config.CamelProperties.CURRENCY_VARIABLE_NAME;
import static org.mifos.connector.ams.fineract.util.ConnectionUtils.convertCustomData;
import static org.mifos.connector.ams.fineract.zeebe.ZeebeVariables.CUSTOM_DATA;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class representing the request body of Fineract validation API.
 *
 * @author amy.muhimpundu
 */
@Getter
@Setter
@NoArgsConstructor
public class FineractRequestDto {

    @JsonProperty("RemoteTransactionId")
    private String remoteTransactionId;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("Account")
    private String account;

    @JsonProperty("Amount")
    private BigDecimal amount;

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("LoanId")
    private Long loanId;

    /**
     * Creates a {@link FineractRequestDto} using data in the channel request.
     *
     * @param channelRequest
     *            contains data related to the transaction
     * @param transactionId
     *            the transaction identifier
     * @param customData
     *            holds custom data such as loanId
     * @return {@link FineractRequestDto}
     */
    public static FineractRequestDto fromChannelRequest(JSONObject channelRequest, String transactionId,
            JSONArray customData) {
        FineractRequestDto dto = new FineractRequestDto();

        String phoneNumber = getPartyIdIdentifier(channelRequest, "payer");
        String accountId = getPartyIdIdentifier(channelRequest, "payee");
        Object amountObj = channelRequest.get("amount");

        if (amountObj != null) {
            if (amountObj instanceof String amount) {

                dto.setAmount(new BigDecimal(amount));
                dto.setCurrency(String.valueOf(channelRequest.get(CURRENCY_VARIABLE_NAME)));
            } else {
                JSONObject amountJson = (JSONObject) amountObj;
                dto.setAmount(new BigDecimal(amountJson.getString("amount")));
                dto.setCurrency(amountJson.getString(CURRENCY_VARIABLE_NAME));
            }
        }

        dto.setPhoneNumber(phoneNumber);
        dto.setAccount(accountId);
        dto.setRemoteTransactionId(transactionId);
        dto.setLoanIdFromCustomData(customData);

        return dto;
    }

    /**
     * Convert the paybill payload to AMS payload.
     *
     * @param payload
     *            the JSON payload
     * @return {@link FineractRequestDto}
     */
    public static FineractRequestDto convertPayBillPayloadToAmsPayload(JSONObject payload) {
        String transactionId = convertCustomData(payload.getJSONArray("customData"), "transactionId");
        String currency = convertCustomData(payload.getJSONArray("customData"), CURRENCY_VARIABLE_NAME);
        String walletMsisdn = payload.getJSONObject("secondaryIdentifier").getString("value");
        String accountId = payload.getJSONObject("primaryIdentifier").getString("value");
        FineractRequestDto validationRequestDto = new FineractRequestDto();
        validationRequestDto.setAccount(accountId);
        validationRequestDto.setAmount(BigDecimal.valueOf(1L));
        validationRequestDto.setCurrency(currency);
        validationRequestDto.setRemoteTransactionId(transactionId);
        validationRequestDto.setPhoneNumber(walletMsisdn);
        validationRequestDto.setLoanIdFromCustomData(payload.getJSONArray(CUSTOM_DATA));
        return validationRequestDto;
    }

    /**
     * Sets the loan ID to the value retrieved from custom data.
     *
     * @param customData
     *            {@link JSONArray} containing the loanId value
     */
    private void setLoanIdFromCustomData(JSONArray customData) {
        if (customData != null) {
            String loanId = convertCustomData(customData, "loanId");
            if (loanId != null && !loanId.isBlank()) {
                this.loanId = Long.valueOf(loanId);
            }
        }
    }

    private static String getPartyIdIdentifier(JSONObject party, String partyType) {
        Object type = party.get(partyType);
        if (type instanceof JSONArray jsonArray) {
            return jsonArray.getJSONObject(0).getString("partyIdIdentifier");
        } else {
            return ((JSONObject) type).getJSONObject("partyIdInfo").getString("partyIdentifier");
        }
    }

    @Override
    public String toString() {
        return "FineractRequestDto{" + "remoteTransactionId='" + remoteTransactionId + '\'' + ", phoneNumber='"
                + phoneNumber + '\'' + ", account='" + account + '\'' + ", amount=" + amount + ", currency='" + currency
                + '\'' + ", loanId=" + loanId + '}';
    }
}
