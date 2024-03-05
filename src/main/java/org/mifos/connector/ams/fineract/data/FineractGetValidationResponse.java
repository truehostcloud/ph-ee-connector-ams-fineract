package org.mifos.connector.ams.fineract.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mifos.connector.common.gsma.dto.CustomData;

/**
 * Holds fields to be returned by the payments/validations api
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FineractGetValidationResponse {

    private String transactionId;
    private BigDecimal amount;
    private String accountNumber;
    private String phoneNumber;
    private String provider;
    private String clientFirstname;
    private String clientLastname;
    private String clientAccountNumber;
    private String clientMobileNo;

    /**
     * Converts the {@link FineractGetValidationResponse} to a list of {@link CustomData}.
     *
     * @param fineractGetValidationResponse
     *            the response from the Fineract API
     * @return a list of {@link CustomData}
     */
    public static List<CustomData> convertToCustomData(FineractGetValidationResponse fineractGetValidationResponse) {

        List<CustomData> customDataList = new ArrayList<>();

        customDataList.add(new CustomData("transactionId", fineractGetValidationResponse.getTransactionId()));
        customDataList.add(new CustomData("amount", fineractGetValidationResponse.getAmount()));
        customDataList.add(new CustomData("accountNumber", fineractGetValidationResponse.getAccountNumber()));
        customDataList.add(new CustomData("phoneNumber", fineractGetValidationResponse.getPhoneNumber()));
        customDataList.add(new CustomData("provider", fineractGetValidationResponse.getProvider()));
        customDataList.add(new CustomData("clientFirstname", fineractGetValidationResponse.getClientFirstname()));
        customDataList.add(new CustomData("clientLastname", fineractGetValidationResponse.getClientLastname()));
        customDataList
                .add(new CustomData("clientAccountNumber", fineractGetValidationResponse.getClientAccountNumber()));
        customDataList.add(new CustomData("clientMobileNo", fineractGetValidationResponse.getClientMobileNo()));

        return customDataList;
    }
}
