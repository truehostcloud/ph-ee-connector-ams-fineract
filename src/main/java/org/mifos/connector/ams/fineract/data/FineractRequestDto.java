package org.mifos.connector.ams.fineract.data;

import static org.mifos.connector.ams.fineract.util.ConnectionUtils.convertCustomData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private Long amount;

    @JsonProperty("Currency")
    private String currency;

    /**
     * Creates a {@link FineractRequestDto} using data in the channel request.
     *
     * @param channelRequest
     *            contains data related to the transaction
     * @param transactionId
     *            the transaction identifier
     * @return {@link FineractRequestDto}
     */
    public static FineractRequestDto fromChannelRequest(JSONObject channelRequest, String transactionId) {
        FineractRequestDto dto = new FineractRequestDto();

        String phoneNumber = channelRequest.getJSONObject("payer").getJSONObject("partyIdInfo")
                .getString("partyIdentifier");
        String accountId = channelRequest.getJSONObject("payee").getJSONObject("partyIdInfo")
                .getString("partyIdentifier");
        JSONObject amountJson = channelRequest.getJSONObject("amount");

        dto.setRemoteTransactionId(transactionId);
        dto.setAmount(amountJson.getLong("amount"));
        dto.setPhoneNumber(phoneNumber);
        dto.setCurrency(amountJson.getString("currency"));
        dto.setAccount(accountId);

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
        String currency = convertCustomData(payload.getJSONArray("customData"), "currency");
        String walletMsisdn = payload.getJSONObject("secondaryIdentifier").getString("value");
        String accountId = payload.getJSONObject("primaryIdentifier").getString("value");
        FineractRequestDto validationRequestDto = new FineractRequestDto();
        validationRequestDto.setAccount(accountId);
        validationRequestDto.setAmount(1L);
        validationRequestDto.setCurrency(currency);
        validationRequestDto.setRemoteTransactionId(transactionId);
        validationRequestDto.setPhoneNumber(walletMsisdn);
        return validationRequestDto;
    }

    @Override
    public String toString() {
        return "FineractConfirmationRequestDto{" + "remoteTransactionId='" + remoteTransactionId + '\''
                + ", phoneNumber='" + phoneNumber + '\'' + ", account='" + account + '\'' + ", amount=" + amount
                + ", currency='" + currency + '\'' + '}';
    }
}
