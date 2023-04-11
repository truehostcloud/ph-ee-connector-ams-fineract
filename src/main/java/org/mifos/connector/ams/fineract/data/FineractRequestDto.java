package org.mifos.connector.ams.fineract.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import static org.mifos.connector.ams.fineract.util.ConnectionUtils.convertCustomData;

/**
 * Class representing the request body of Fineract validation API
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
     * @param channelRequest  contains data related to the transaction
     * @param transactionId the transaction identifier
     * @return {@link FineractRequestDto}
     */
    public static FineractRequestDto fromChannelRequest(JSONObject channelRequest,
            String transactionId) {
        FineractRequestDto dto = new FineractRequestDto();

        String phoneNumber = channelRequest.getJSONObject("payer")
                .getJSONObject("partyIdInfo").getString("partyIdentifier");
        String accountId = channelRequest.getJSONObject("payee")
                .getJSONObject("partyIdInfo").getString("partyIdentifier");
        JSONObject amountJson = channelRequest.getJSONObject("amount");

        dto.setRemoteTransactionId(transactionId);
        dto.setAmount( amountJson.getLong("amount"));
        dto.setPhoneNumber(phoneNumber);
        dto.setCurrency(amountJson.getString("currency"));
        dto.setAccount(accountId);

        return dto;
    }

    public static FineractRequestDto convertPayBillPayloadToAmsPayload(JSONObject payload) {
        String transactionId = convertCustomData(payload.getJSONArray("customData"), "transactionId");
        String currency = convertCustomData(payload.getJSONArray("customData"), "currency");
        String walletMsisdn=payload.getJSONObject("secondaryIdentifier").getString("value");
        String accountID=payload.getJSONObject("primaryIdentifier").getString("value");
        FineractRequestDto validationRequestDTO = new FineractRequestDto();
        validationRequestDTO.setAccount(accountID);
        validationRequestDTO.setAmount(1L);
        validationRequestDTO.setCurrency(currency);
        validationRequestDTO.setRemoteTransactionId(transactionId);
        validationRequestDTO.setPhoneNumber(walletMsisdn);
        return validationRequestDTO;
    }

    @Override
    public String toString() {
        return "FineractConfirmationRequestDto{" +
                "remoteTransactionId='" + remoteTransactionId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", account='" + account + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
