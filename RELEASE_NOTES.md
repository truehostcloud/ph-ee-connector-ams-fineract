# Release Notes

## Version 0.0.8
    * Paybill flow
            * [SER-2514] - Fix exception when useWorkflowIdAsTransactionId field is in not in the channel request
## Version 0.0.7
    * Paybill flow
            * [SER-2514] - Refactor settlement flow to use TNM reference number as transaction id
## Version 0.0.6
    * Fineract AMS connector
            * [SER-2304] - Add transaction logs
            * [SER-2444] - Fix Successful fineract paybill transactions showing on Ops app and Kibana as failed transactions

## Version 0.0.5
    * Paybill flow
            * [SER-2040] - Expose validation endpoint for paybill transactions
            * [SER-2233] - Add BPMN for fineract paybill transactions

## Version 0.0.4

    * Fineract AMS connector
            * [SER-2031] - Update the BPMN files to skip the validation step
            * [SER-2000] - End payment hub transaction flow when an exception occurs
            * [SER-2001] - Fineract AMS Connector throws error when amount is in decimal format

## Version 0.0.3

    * Fineract AMS connector
            * [SER-1954] - Create BPMN files for processing Airtel and MTN transactions on Fineract

## Version 0.0.2

    * Fineract AMS connector
            * [SER-1742] - Include loan ID in payload sent to fineract validation endpoint by the fineract AMS connector

## Version 0.0.1

    * Fineract AMS connector
            * [SER-1568] - Project setup + Adding workers for Fineract verification and confirmation
            * [SER-1729] - Fix error that occurs when validation endpoint is called
