# Fineract AMS Connector

An Account Management System (AMS) connector for Fineract. This connector gets invoked after a
collection
has been made on a Fineract client's M-PESA wallet. Its primary role is to notify the Fineract service
about a successful
payment made by a client.

This AMS connector is a Zeebe project that is part of the OAF Payment Hub EE setup. See
the [Payment Hub EE documentation](https://mifos.gitbook.io/docs/payment-hub-ee/overview)
for more information about Zeebe projects and Payment Hub in general.

## Badges

[![Build Status](https://dev.azure.com/OAFDev/prd-pipelines/_apis/build/status/one-acre-fund.ph-ee-connector-ams-fineract?branchName=main)](https://dev.azure.com/OAFDev/prd-pipelines/_build/latest?definitionId=171&branchName=main)

## Tech Stack

- Java 17
- Spring Boot
- Apache Camel
- Zeebe Java Client

## Getting Started

Clone the project

  ```bash
    git clone https://github.com/one-acre-fund/ph-ee-connector-ams-fineract.git
    cd ph-ee-connector-ams-fineract
  ```

This connector is expected to be run alongside other connectors/services. It depends on some of
those services being up
and healthy. For local development, the services that are most critical for running this project
have been included in
the `docker-compose.yml` file. The following components are included:

- Zeebe: A workflow engine for microservices orchestration. This must be running in a healthy state
  otherwise errors
  will occur when the services below attempt to connect to it.
- Zeebe-ops: Provides APIs for carrying out certain operations on zeebe such as uploading a bpmn
  file
- Channel-connector: Provides APIs for initiating collection requests
- Mpesa-connector: Contains workers that handle the connection to M-PESA for making the actual
  collection from a
  client's wallet

A lot more services can be added to the above based on your needs, but to run the Fineract ams
connector locally,
the ones listed above are the required minimum.
Please note that the `docker-compose.yml` file in this repository should NOT be used in a production
environment.

## Running with Docker

Some images listed in the `docker-compose.yml` are available on OAF's Azure Container Registry (
ACR). To be able to pull
them, certain permissions must be granted to your azure account. Follow the steps below to
successfully run the project:

- Ensure [Docker](https://docs.docker.com/get-docker/) is installed on your machine

- Authenticate with
  azure. [Install the Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli)
  on your machine if it's not already available, and then run the command below

  ```bash
      az acr login -n oaftech # Log in to OAF's ACR through the Docker CLI.
   ```

- Run the project:

  Update `src/main/resources/application.yml` with the appropriate values where necessary, or
  provide the
  values as environment variables in the `services.fineract-connector.environment` section of
  the `docker-compose.yml`
  file, and run the command below:

  ```bash
      docker compose up -d
   ```

## Usage

To initiate a collection request, and have the Fineract connector notify the Fineract AMS about the
payment, follow the
steps below:

- Upload the Fineract bpmn (found in `src/main/resources/mpesa_flow_fineract-oaf.bpmn`) through *
  *zeebe-ops** by sending a
  POST
  request to `http://localhost:5001/zeebe/upload` with the file attached.

- Send a collection request through the **channel-connector** by sending a POST request
  to `http://localhost:5002/channel/collection`
  with a sample body as shown below:
  ```json
  {
    "payer": [
        {
            "key": "MSISDN",
            "value": "254113888031"
        },
        {
            "key": "fineractAccountID",
            "value": "60649568"
        }
    ],
    "amount": {
        "amount": "1",
        "currency": "KES"
    },
    "transactionType": {
        "scenario": "MPESA",
        "subScenario": "BUYGOODS",
        "initiator": "PAYEE",
        "initiatorType": "BUSINESS"
    }
  }
  ```
- Check the logs in the **fineract-connector** container to see that settlement has been handled, and
  see the result of
  calling the Fineract service

## Troubleshooting

If an error occurs while carrying out any of the steps above, check if the zeebe container is in a
healthy state by
either viewing its state through `docker ps` or sending a GET request
to `http://localhost:9600/health`.
If the zeebe container shows state as unhealthy or the health endpoint doesn't return a 204 status
response, restart the
zeebe container.

## Contributing

See `CONTRIBUTING.md` for ways to get started.