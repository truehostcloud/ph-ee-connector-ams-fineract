package org.mifos.connector.ams.fineract.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;

/**
 * Class representing the request body of Fineract confirmation API.
 *
 * @author amy.muhimpundu
 */
@Getter
@Setter
@NoArgsConstructor
public class FineractConfirmationRequestDto extends FineractRequestDto {

    @JsonProperty("Status")
    private String status;

    @JsonProperty("ReceiptId")
    private String receiptId;

    /**
     * Creates a {@link FineractConfirmationRequestDto} using data in the channel request.
     *
     * @param channelRequest
     *            contains data related to the transaction
     * @param transactionId
     *            the transaction identifier
     * @return {@link FineractConfirmationRequestDto}
     */
    public static FineractConfirmationRequestDto fromChannelRequest(JSONObject channelRequest, String transactionId) {
        FineractConfirmationRequestDto dto = new FineractConfirmationRequestDto();

        BeanUtils.copyProperties(FineractRequestDto.fromChannelRequest(channelRequest, transactionId, null), dto);

        return dto;
    }

    /**
     * Convert the paybill payload to AMS payload.
     *
     * @param payload
     *            the JSON payload
     * @return {@link FineractRequestDto}
     */
    public static FineractConfirmationRequestDto convertPayBillPayloadToAmsPayload(JSONObject payload) {
        FineractConfirmationRequestDto dto = new FineractConfirmationRequestDto();

        BeanUtils.copyProperties(FineractRequestDto.convertPayBillPayloadToAmsPayload(payload), dto);

        return dto;
    }

    @Override
    public String toString() {
        return "FineractConfirmationRequestDto{" + "remoteTransactionId='" + super.getRemoteTransactionId() + '\''
                + ", phoneNumber='" + super.getPhoneNumber() + '\'' + ", account='" + super.getAccount() + '\''
                + ", amount=" + super.getAmount() + ", currency='" + super.getCurrency() + '\'' + ", status='" + status
                + '\'' + ", receiptId='" + receiptId + '\'' + '}';
    }
}
